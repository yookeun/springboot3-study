package com.example.study.member.service;

import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.respository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenHandler jwtTokenHandler;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDto save(MemberDto memberDto) {
        Member saveMember = memberRepository.save(memberDto.toEntity());
        saveMember.addAuthorities(memberDto.getAuthorities());
        return MemberDto.fromEntity(saveMember);
    }

    public LoginDto loginProcess(LoginDto loginDto) {
        Optional<Member> optionalMember = memberRepository.findByUserId(loginDto.getUserId());
        if (optionalMember.isEmpty() || !isMatchPassword(loginDto.getPassword(),
                optionalMember.get().getPassword())) {
            loginDto.setResult(false);
            loginDto.setMsg("ID and password do not match");
            return loginDto;
        }
        loginDto.setAccessToken(jwtTokenHandler.generateToken(optionalMember.get()));
        loginDto.setResult(true);
        loginDto.setMsg("SUCCESS");
        return loginDto;
    }

    public Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getAllMembers(condition, pageable);
    }


    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }
}
