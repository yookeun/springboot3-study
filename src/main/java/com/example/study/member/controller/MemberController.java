package com.example.study.member.controller;

import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberUpdateDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MemberDto> updateMember(@PathVariable("id") Long id,
            @Valid @RequestBody MemberUpdateDto requestDto) {
        return ResponseEntity.ok(memberService.updateMember(id, requestDto));
    }


}
