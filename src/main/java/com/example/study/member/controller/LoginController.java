package com.example.study.member.controller;

import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.LoginDto.LoginRequestDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.dto.RefreshTokenDto;
import com.example.study.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<MemberDto> create(@Valid @RequestBody MemberRequestDto requestDto) {
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return ResponseEntity.ok(memberService.save(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(memberService.loginProcess(requestDto));
    }


    @PostMapping("/token/reissue")
    public ResponseEntity<Map<String, String>> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", memberService.getRenewAccessToken(refreshTokenDto));
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

}
