package com.team8.diary.service;

import com.team8.diary.domain.Diary;
import com.team8.diary.domain.Image;
import com.team8.diary.domain.Member;
import com.team8.diary.external.TextGenerationAi;
import com.team8.diary.external.VertexAi;
import com.team8.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final VertexAi vertexAi;
    private final TextGenerationAi textGenerationAi;

    @Transactional
    public List<String> createDiary(String content, Member member) throws IOException {
        Diary diary = Diary.builder()
                .content(content)
                .member(member)
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        List<String> generatedPrompts = textGenerationAi.summarizeDiaryIntoThreePrompts(content);
        List<String> imageUrls = new ArrayList<>();
        for (String prompt : generatedPrompts) {
            log.info("generated prompt = {}", prompt);
            imageUrls.add(vertexAi.generateImageAndGetUrls(prompt.trim()));
        }


        for (String url : imageUrls) {
            Image image = new Image(url, diary);
            diary.getImageUrls().add(image);
        }


        return imageUrls;
    }

//    @Transactional
//    public List<Diary> findDiaryByMemberId(Long memberId) {
//        diaryRepository.findDiaryList();
//    }
}
