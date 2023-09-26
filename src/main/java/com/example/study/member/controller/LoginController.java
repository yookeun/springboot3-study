package com.example.study.member.controller;

import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<MemberDto> create(@RequestBody MemberRequestDto requestDto) {
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return ResponseEntity.ok(memberService.save(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(memberService.loginProcess(loginDto));
    }

}
