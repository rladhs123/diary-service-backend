package com.team8.diary.service;

import com.team8.diary.domain.Diary;
import com.team8.diary.domain.Member;
import com.team8.diary.external.ImageGenerateAI;
import com.team8.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageGenerateAI imageGenerateAi;

    @Transactional
    public Diary createDiary(String content, Member member) {
        String prompt = "그냥 적당한 url 3개 작성해줘" + content;
        String imageUrl = imageGenerateAi.generateImage(prompt);

        Diary diary = Diary.builder()
                .content(content)
                .imageUrl(imageUrl)
                .member(member)
                .build();

        diaryRepository.save(diary);

        return diary;
    }

//    @Transactional
//    public List<Diary> findDiaryByMemberId(Long memberId) {
//        diaryRepository.findDiaryList();
//    }
}
