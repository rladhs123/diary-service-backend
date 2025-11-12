package com.team8.diary.controller;

import com.team8.diary.domain.Member;
import com.team8.diary.dto.DiaryCreateRequestDto;
import com.team8.diary.security.CustomUserDetails;
import com.team8.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "2. Diary API", description = "그림일기 (생성/조회) 관련 API")
@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "그림일기 생성 (핵심)", description = "일기 내용(content)을 받아 AI가 3개의 이미지 URL을 생성하고 DB에 저장합니다. (JWT 인증 필요)")
    @ApiResponse(
            responseCode = "200",
            description = "생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = @ExampleObject(
                            name = "3개 URL 예시",
                            value = "[\"https://storage.googleapis.com/diary-image-bucket/generated-image/1.png\", " +
                                    "\"https://storage.googleapis.com/diary-image-bucket/generated-image/2.png\", " +
                                    "\"https://storage.googleapis.com/diary-image-bucket/generated-image/3.png\"]"
                    )
            )
    )
    @PostMapping("/generate-images")
    public ResponseEntity<?> generateDiaryImages(
            @RequestBody DiaryCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String diaryText = request.getContent();
        Member member = userDetails.getMember();

        if (member == null) {
            return new ResponseEntity<>("Invalid user token.", HttpStatus.UNAUTHORIZED);
        }

        if (diaryText == null || diaryText.trim().isEmpty()) {
            return new ResponseEntity<>("Diary text cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        try {
            // 서비스 계정 키를 사용한다면, 아래 코드를 사용하여 키 파일 경로를 환경 변수로 설정합니다.
            // System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "/path/to/your/service-account-key.json");

            List<String> generatedImagesUrl = diaryService.createDiary(diaryText, member);

            return ResponseEntity.ok(generatedImagesUrl);

        } catch (IOException e) {
            return new ResponseEntity<>("Failed to generate images: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //일기 조회

    //일기 삭제

}
