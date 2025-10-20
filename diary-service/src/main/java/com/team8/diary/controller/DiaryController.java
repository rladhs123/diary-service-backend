package com.team8.diary.controller;

import com.team8.diary.domain.Diary;
import com.team8.diary.dto.DiaryCreateRequestDto;
import com.team8.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    //일기 생성
    @PostMapping("/create")
    public Diary createDiary(@RequestBody DiaryCreateRequestDto request) {
        return diaryService.createDiary(request.getContent(), request.getMember());
    }
    //일기 조회

    //일기 삭제

}
