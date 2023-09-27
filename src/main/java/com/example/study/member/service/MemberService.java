package com.example.study.member.service;

import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.LoginDto.LoginRequestDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.dto.MemberDto.MemberUpdateDto;
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
            throw new IllegalArgumentException("This userId is already in use.");
        }
        
        Member saveMember = memberRepository.save(requestDto.toEntity());
        saveMember.addAuthorities(requestDto.getAuthorities());
        return MemberDto.fromEntity(saveMember);
    }

    public LoginDto loginProcess(LoginRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findByUserId(requestDto.getUserId());
        if (optionalMember.isEmpty() || !isMatchPassword(requestDto.getPassword(),
                optionalMember.get().getPassword())) {
            throw new IllegalArgumentException("The ID or password is incorrect.");
        }
        return LoginDto.builder()
                .userId(optionalMember.get().getUserId())
                .password(optionalMember.get().getPassword())
                .name(optionalMember.get().getName())
                .accessToken(jwtTokenHandler.generateToken(optionalMember.get()))
                .build();
    }

    public Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getAllMembers(condition, pageable).map(MemberDto::fromEntity);
    }

    public MemberDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This ID does not exist."));
        return MemberDto.fromEntity(member);
    }

    @Transactional
    public MemberDto updateMember(Long id, MemberUpdateDto requestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This ID does not exist."));

        member.updateName(requestDto.getName());
        member.updateGender(requestDto.getGender());
        member.updatePassword(passwordEncoder.encode(requestDto.getPassword()));
        member.updateAuthorities(requestDto.getAuthorities());
        return MemberDto.fromEntity(member);
    }


    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }
}
