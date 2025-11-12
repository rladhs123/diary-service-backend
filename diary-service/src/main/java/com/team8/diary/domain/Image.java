package com.team8.diary.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl; // GCS URL 저장

    // "여러 개의 이미지(Many)는 하나의 일기(One)에 속한다"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    public Image(String imageUrl, Diary diary) {
        this.imageUrl = imageUrl;
        this.diary = diary;
    }
}
