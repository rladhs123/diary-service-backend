package com.team8.diary.dto;

import com.team8.diary.domain.Member;
import lombok.Getter;

@Getter
public class DiaryCreateRequestDto {
    private String content;
    private Member member;
}
