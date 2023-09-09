package com.example.study.member.controller;

import com.example.study.member.LoginDto;
import com.example.study.member.MemberDto;
import com.example.study.member.service.MemberService;
import lombok.RequiredArgsConstructor;
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
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<MemberDto> create(@RequestBody MemberDto memberDto) {
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        return ResponseEntity.ok(memberService.save(memberDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(memberService.loginProcess(loginDto));
    }

    @GetMapping
    public String helloMember() {
        return "Hello Member";
    }

}
