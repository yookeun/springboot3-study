package com.example.study.common;

import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import com.example.study.member.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestToken {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenHandler jwtTokenHandler;
    private final MemberRepository memberRepository;

    public final String AUTHORIZATION = "Authorization";
    public final String BEARER = "Bearer ";

    public String getAccessToken(Authority authority) {
        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(authority)
                .build();

        Member member = Member.builder()
                .userId("admin")
                .password(passwordEncoder.encode("1234"))
                .name("test")
                .gender(Gender.MALE)
                .build();

        member.getMemberAuthorityList().add(memberAuthority);
        memberRepository.save(member);
        return jwtTokenHandler.generateToken(member);
    }

}
