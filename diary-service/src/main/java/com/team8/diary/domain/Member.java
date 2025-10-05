package com.team8.diary.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long memberId;

    @Column(nullable = false, unique = true)
    private String memberEmail;

    @Column(nullable = false)
    private String password;

    @Setter
    private String memberName;


    //new로 객체 생성 안됨. 그대신 Member.build()로 생성가능
    @Builder
    private Member(String memberEmail, String password) {
        this.memberEmail = memberEmail;
        this.password = password;
    }

    public static Member signUp(String email, String encodedPassword) {
        return Member.builder()
                .memberEmail(email)
                .password(encodedPassword)
                .build();
    }
}
