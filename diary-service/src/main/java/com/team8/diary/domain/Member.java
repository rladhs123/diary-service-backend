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

    @Column(name = "member_email", nullable = false, unique = true)
    private String memberEmail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "member_name", nullable = false)
    private String memberName;


    //new로 객체 생성 안됨. 그대신 Member.build()로 생성가능
    @Builder
    private Member(String memberEmail, String password, String memberName) {
        this.memberEmail = memberEmail;
        this.password = password;
        this.memberName = memberName;
    }

    public static Member signUp(String email, String encodedPassword) {
        return Member.builder()
                .memberEmail(email)
                .password(encodedPassword)
                .build();
    }
}
