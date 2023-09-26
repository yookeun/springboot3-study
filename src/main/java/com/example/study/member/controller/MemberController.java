package com.example.study.member.controller;

import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberService.getAllMembers(condition, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MemberDto> getMember(@PathVariable("id") Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

}
