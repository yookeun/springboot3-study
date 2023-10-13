package com.example.study.member.service;

import com.example.study.exception.AuthException;
import com.example.study.handler.JwtResult;
import com.example.study.handler.JwtResult.JwtResultType;
import com.example.study.handler.JwtTokenHandler;
import com.example.study.handler.RedisTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.LoginDto.LoginRequestDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.dto.MemberDto.MemberUpdateDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.dto.RefreshTokenDto;
import com.example.study.member.dto.UserTokenInfo;
import com.example.study.member.respository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenHandler jwtTokenHandler;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenHandler redisTokenHandler;

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
        LoginDto loginDto =  LoginDto.builder()
                .userId(optionalMember.get().getUserId())
                .password(optionalMember.get().getPassword())
                .name(optionalMember.get().getName())
                .accessToken(jwtTokenHandler.generateToken(optionalMember.get()))
                .refreshToken(jwtTokenHandler.generateRefreshToken(optionalMember.get()))
                .build();
        //Save to redis
        try {
            redisTokenHandler.saveToken(loginDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return loginDto;
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
        if (!CollectionUtils.isEmpty(requestDto.getAuthorities())) {
            member.updateAuthorities(requestDto.getAuthorities());
        }
        return MemberDto.fromEntity(member);
    }

    public String getRenewAccessToken(RefreshTokenDto refreshTokenDto) {

        UserTokenInfo userTokenInfo = checkToken(refreshTokenDto);

        //access token를 새로 만들어 준다.
        Member member = memberRepository.findByUserId(userTokenInfo.getUserId())
                .orElseThrow(() -> new AuthException(JwtResultType.UNUSUAL_REQUEST.name()));
        String renewAccessToken =  jwtTokenHandler.generateToken(member);
        userTokenInfo.setAccessToken(renewAccessToken);

        //레디스에 업데이트
        try {
            redisTokenHandler.updateRedis(userTokenInfo);
        } catch (JsonProcessingException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
        return renewAccessToken;
    }

    private UserTokenInfo checkToken(RefreshTokenDto refreshTokenDto) {
        JwtResult jwtResult ;

        Optional<UserTokenInfo> optionalUserTokenInfo;
        try {
            optionalUserTokenInfo = redisTokenHandler.findSavedAccessToken(
                    refreshTokenDto.getUserId());
        } catch (JsonProcessingException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }

        //1.userId에 저장된 키값이 없다면 에러 처리
        if (optionalUserTokenInfo.isEmpty()) {
            log.warn("Not found Redis key = {}", refreshTokenDto.getUserId());
            throw new AuthException(JwtResultType.TOKEN_EXPIRED.name());
        }

        UserTokenInfo userTokenInfo = optionalUserTokenInfo.get();

        //2.전달받은 refreshToken과 저장된 refreshToken이 다르다면 401 에러 처리한다.
        if (!refreshTokenDto.getRefreshToken().equals(userTokenInfo.getRefreshToken())) {
            log.warn("requested refreshToken != saved refreshToken");
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        //3.refreshToken이 유효하지 않거나 만료된 토큰이라면 해당 키 삭제후 401 에러 처리한다.
        jwtResult = jwtTokenHandler.extractAllClaims(userTokenInfo.getRefreshToken());
        if (jwtResult.getJwtResultType() != JwtResultType.TOKEN_SUCCESS) {
            redisTokenHandler.deleteUserToken(userTokenInfo.getUserId());
            log.warn("refreshToken invalid or expired: {}", jwtResult.getJwtResultType().name());
            throw new AuthException(jwtResult.getJwtResultType().name());
        }

        //4. 전달받은 accessToken과 저장된 accessToken이 다르다면 해당 키 삭제후 401 에러 처리한다.
        if (!refreshTokenDto.getAccessToken().equals(userTokenInfo.getAccessToken())) {
            redisTokenHandler.deleteUserToken(userTokenInfo.getUserId());
            log.warn("requested accessToken != saved accessToken");
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        //5.기존 accessToken이 아직 만료되지 않은 토큰이라면 비정상적인 요청으로 해당 키 삭제 후 401 에러 처리한다.
        jwtResult = jwtTokenHandler.extractAllClaims(userTokenInfo.getAccessToken());
        if (jwtResult.getJwtResultType() == JwtResultType.TOKEN_SUCCESS) {
            redisTokenHandler.deleteUserToken(userTokenInfo.getUserId());
            log.warn("accessToken has not expired yet : {}", jwtResult.getJwtResultType().name());
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        return userTokenInfo;
    }


    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }
}
