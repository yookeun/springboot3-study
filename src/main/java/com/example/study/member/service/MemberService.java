package com.example.study.member.service;

import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.LoginDto;
import com.example.study.member.MemberDto;
import com.example.study.member.domain.Member;
import com.example.study.member.respository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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

    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }
}
