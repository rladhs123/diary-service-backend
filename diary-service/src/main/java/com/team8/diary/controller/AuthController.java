// AuthController.java
package com.team8.diary.controller;

import com.team8.diary.dto.AuthDtos.*;
import com.team8.diary.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Auth API", description = "인증 (회원가입, 로그인) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 Access/Refresh 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(summary = "로그아웃", description = "서버에서 Refresh 토큰을 만료시킵니다. (Access 토큰 필요)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization,
                                    @AuthenticationPrincipal(expression = "username") String email) {
        String access = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7) : null;
        authService.logout(email, access);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 갱신", description = "Refresh 토큰으로 새 Access/Refresh 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }
}