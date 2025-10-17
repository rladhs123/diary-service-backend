// AuthService.java
package com.team8.diary.service;

import com.team8.diary.dto.AuthDtos.*;
import com.team8.diary.domain.Member;
import com.team8.diary.dto.AuthDtos;
import com.team8.diary.repository.MemberRepository;
import com.team8.diary.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore; // 아래 구현해놈

    public void signUp(AuthDtos.SignUpRequest req) {
        if (memberRepository.existsByMemberEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        String hash = passwordEncoder.encode(req.password());
        memberRepository.save(Member.signUp(req.email(), hash));
    }

    @Transactional(readOnly = true)
    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
        Member m = memberRepository.findByMemberEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(req.password(), m.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        String access = jwtTokenProvider.generateAccessToken(m.getMemberEmail());
        String refresh = jwtTokenProvider.generateRefreshToken(m.getMemberEmail());
        refreshTokenStore.save(m.getMemberEmail(), refresh); // 저장/교체
        return new AuthDtos.TokenResponse(access, refresh);
    }

    public void logout(String email, String accessToken) {
        refreshTokenStore.delete(email);
        refreshTokenStore.blacklistAccess(accessToken);
    }

    public TokenResponse refresh(RefreshRequest req) {
        String refreshToken = req.refreshToken();

        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }
        String email = jwtTokenProvider.getSubject(refreshToken);

        if (!refreshTokenStore.isSame(email, refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었거나 일치하지 않습니다.");
        }

        String newAccess = jwtTokenProvider.generateAccessToken(email);
        String newRefresh = jwtTokenProvider.generateRefreshToken(email);

        refreshTokenStore.save(email, newRefresh);
        return new TokenResponse(newAccess, newRefresh);
    }
}