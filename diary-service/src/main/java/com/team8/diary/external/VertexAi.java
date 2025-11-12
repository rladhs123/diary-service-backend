package com.team8.diary.external;

import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import com.google.protobuf.Struct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.aiplatform.v1.*;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class VertexAi {

    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.vertexai.location}")
    private String location;

    @Value("${gcp.vertexai.image.model-id}")
    private String modelId;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    private Storage storage;

    // 생성자에서 Storage 클라이언트 초기화
    // (gcloud auth application-default login으로 인증 (자동))
    public VertexAi(@Value("${gcp.project.id}") String projectId) {
        this.storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

    // Vertex AI 서비스 엔드포인트 URL
    private String getEndpoint() {
        return String.format("%s-aiplatform.googleapis.com:443", location);
    }

    public String generateImageAndGetUrls(String diaryText) throws IOException {
        // 1. AI -> Base64 이미지 1장
        String rawBase64Images = generateImagesFromDiary(diaryText);
        // 2. Image -> Storage -> Url
        String publicUrl = uploadToStorage(rawBase64Images);

        return publicUrl;
    }

    /**
     * 사용자 일기 내용을 바탕으로 Vertex AI Imagen 모델을 통해 3장의 이미지를 생성합니다.
     *
     * @param diaryText 사용자가 입력한 일기 내용
     * @return 생성된 이미지의 Base64 인코딩 문자열 리스트
     * @throws IOException Vertex AI API 호출 중 오류 발생 시
     */
    public String generateImagesFromDiary(String diaryText) throws IOException {
        log.info("image-generation...");
        // 일기 내용을 이미지 생성 프롬프트로 변환 (핵심 로직)
        String prompt = createPromptFromDiary(diaryText);

        // Vertex AI 클라이언트 설정
        PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
                .setEndpoint(getEndpoint())
                .build();

        String generatedImageBase64 = "";

        try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {
            // Vertex AI Model Endpoint 경로
            String endpointName = String.format("projects/%s/locations/%s/publishers/google/models/%s",
                    projectId, location, modelId);

            // 이미지 생성 요청 파라미터 설정
            com.google.protobuf.Value.Builder parametersValue = com.google.protobuf.Value.newBuilder()
                    .setStructValue(Struct.newBuilder()
                            .putFields("sampleCount", com.google.protobuf.Value.newBuilder().setNumberValue(1).build())
                            .build());

//            // sampleCount: 생성할 이미지 개수 (3장)
//            String parametersJson = "{\"sampleCount\": 3, \"width\": 256, \"height\": 256, \"negativePrompt\": \"blue\"}";
//            com.google.protobuf.Value.Builder parametersBuilder = com.google.protobuf.Value.newBuilder();
//            JsonFormat.parser().merge(parametersJson, parametersBuilder);

            // 이미지 생성 인스턴스(프롬프트) 설정
            // prompt: AI에게 이미지 생성을 요청할 텍스트
            String instanceJson = String.format("{\"prompt\": \"%s\"}",
                    prompt.replace("\"", "\\\"")); // 프롬프트 내 "는 이스케이프 처리
            com.google.protobuf.Value.Builder instanceBuilder = com.google.protobuf.Value.newBuilder();
            JsonFormat.parser().merge(instanceJson, instanceBuilder);

            // 예측 요청 객체 생성
            PredictRequest request = PredictRequest.newBuilder()
                    .setEndpoint(endpointName)
                    .addInstances(instanceBuilder.build())
                    .setParameters(parametersValue.build())
                    .build();

            // API 호출 및 응답 받기
            PredictResponse response = client.predict(request);
            log.info("response success");

            // 응답에서 이미지 데이터 추출
            for (com.google.protobuf.Value prediction : response.getPredictionsList()) {
                var fieldsMap = prediction.getStructValue().getFieldsMap();
                if (fieldsMap.containsKey("bytesBase64Encoded")) {
                    com.google.protobuf.Value base64Value = fieldsMap.get("bytesBase64Encoded");
                    if (base64Value != null && base64Value.getKindCase() != com.google.protobuf.Value.KindCase.NULL_VALUE) {
                        return base64Value.getStringValue();
                    }
                } else {
                    log.warn("Image generation was BLOCKED by safety filters.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to generate images from Vertex AI.", e);
        }
        return null; // 실패 시 null 반환
    }

    /**
     * 일기 내용을 기반으로 프롬프트를 생성합니다.
     * 이 부분은 서비스의 핵심적인 '프롬프트 엔지니어링' 로직입니다.
     *
     * @param diaryText 사용자 일기 내용
     * @return 이미지 생성에 사용될 상세 프롬프트
     */
    private String createPromptFromDiary(String diaryText) {
        String style = "cartoon style"; //ex

        return style + diaryText;
    }

    //생성된 이미지를 Google Cloud Storage 업로드
    private String uploadToStorage(String base64Data) {
        // 1. Base64 문자열을 byte 배열로 디코딩
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // 2. 고유한 파일 이름 생성 (예: /generated-image/123e4567-e89b.png)
        String fileName = "generated-image/" + UUID.randomUUID().toString() + ".png";

        // 3. GCS에 업로드할 파일 정보(BlobInfo) 생성
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/png")
                .build();

        // 4. 스토리지에 파일 업로드
        Blob blob = storage.create(blobInfo, imageBytes);

        // 5. 공개 URL 반환
        String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

        return publicUrl;
    }
}
