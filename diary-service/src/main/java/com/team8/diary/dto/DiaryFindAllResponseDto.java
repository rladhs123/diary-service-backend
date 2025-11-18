package com.team8.diary.dto;

import com.team8.diary.domain.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DiaryFindAllResponseDto {
    private Long id;
    private String content;

    public static DiaryFindAllResponseDto from(Diary diary) {
        return DiaryFindAllResponseDto.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .build();
    }
}
