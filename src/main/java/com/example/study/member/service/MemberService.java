package com.example.study.member.service;

import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
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
    public MemberDto save(MemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findByUserId(requestDto.getUserId());
        if (optionalMember.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 userId 입니다");
        }
        
        Member saveMember = memberRepository.save(requestDto.toEntity());
        saveMember.addAuthorities(requestDto.getAuthorities());
        return MemberDto.fromEntity(saveMember);
    }

    public LoginDto loginProcess(LoginDto loginDto) {
        Optional<Member> optionalMember = memberRepository.findByUserId(loginDto.getUserId());
        if (optionalMember.isEmpty() || !isMatchPassword(loginDto.getPassword(),
                optionalMember.get().getPassword())) {
            throw new IllegalArgumentException("아이디 혹은 패스워드가 잘못되었습니다.");
        }
        loginDto.setAccessToken(jwtTokenHandler.generateToken(optionalMember.get()));
        return loginDto;
    }

    public Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getAllMembers(condition, pageable);
    }

    public MemberDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));
        return MemberDto.fromEntity(member);
    }


    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }
}
