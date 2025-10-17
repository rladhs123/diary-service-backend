package com.team8.diary.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ImageGenerateAI {

    @Value("${ai.api.url}")
    private String aiApiUrl;
    @Value("${ai.api.key}")
    private String aiApiKey;

    public String generateImage(String prompt) {
        try {
            System.out.println(aiApiKey);

            WebClient webClient = WebClient.builder()
                    .baseUrl(aiApiUrl + "?key=" + aiApiKey)
                    .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .build();


            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            Map response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println(response);

            Map candidate = ((List<Map>) response.get("candidates")).get(0);
            Map content = (Map) candidate.get("content");
            Map part = ((List<Map>) content.get("parts")).get(0);
//            Map fileData = (Map) part.get("fileData");

            //            String imageUrl = (String) fileData.get("fileUri");

            return (String) part.get("text");
        } catch (WebClientResponseException e) {
            log.error("api 호출실패 (상태코드: {}), (오류 메세지: {}) ", e.getStatusCode(), e.getStatusText());
            System.out.println("💥 Response Body: " + e.getResponseBodyAsString());
        }

        return null;
    }
}
