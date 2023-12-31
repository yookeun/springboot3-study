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
import com.example.study.member.dto.MemberOrderDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.dto.RefreshTokenDto;
import com.example.study.member.dto.UserTokenInfo;
import com.example.study.member.respository.MemberRepository;
import com.example.study.order.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Transactional
    public MemberDto save(MemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findByUserId(requestDto.getUserId());
        if (optionalMember.isPresent()) {
            throw new IllegalArgumentException("This userId is already in use.");
        }
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
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
        member.updatePhone(requestDto.getPhone());
        if (!CollectionUtils.isEmpty(requestDto.getAuthorities())) {
            member.updateAuthorities(requestDto.getAuthorities());
        }
        return MemberDto.fromEntity(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This ID does not exist."));
        memberRepository.delete(member);
        orderRepository.deleteAllByMemberId(member.getId());
    }

    public Page<MemberOrderDto> getAllMemberAndOrderCount(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getAllMemberAndOrderCount(condition, pageable);
    }

    public String getRenewAccessToken(RefreshTokenDto refreshTokenDto) {

        UserTokenInfo userTokenInfo = checkToken(refreshTokenDto);

        //Create a new access token.
        Member member = memberRepository.findByUserId(userTokenInfo.getUserId())
                .orElseThrow(() -> new AuthException(JwtResultType.UNUSUAL_REQUEST.name()));
        String renewAccessToken =  jwtTokenHandler.generateToken(member);
        userTokenInfo.setAccessToken(renewAccessToken);

        //Update to redis
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

        //1. Throw an error if userId has no stored key value
        if (optionalUserTokenInfo.isEmpty()) {
            log.warn("Not found Redis key = {}", refreshTokenDto.getUserId());
            throw new AuthException(JwtResultType.TOKEN_EXPIRED.name());
        }

        UserTokenInfo userTokenInfo = optionalUserTokenInfo.get();

        //2.If the received refreshToken is different from the stored refreshToken, a 401 error is thrown.
        if (!refreshTokenDto.getRefreshToken().equals(userTokenInfo.getRefreshToken())) {
            log.warn("requested refreshToken != saved refreshToken");
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        //3. If the refreshToken is an invalid or expired token, delete the key and throw a 401 error.
        jwtResult = jwtTokenHandler.extractAllClaims(userTokenInfo.getRefreshToken());
        if (jwtResult.getJwtResultType() != JwtResultType.TOKEN_SUCCESS) {
            redisTokenHandler.deleteRedis(userTokenInfo.getUserId());
            log.warn("refreshToken invalid or expired: {}", jwtResult.getJwtResultType().name());
            throw new AuthException(jwtResult.getJwtResultType().name());
        }

        //4.If the received accessToken is different from the stored accessToken, delete the key and handle a 401 error.
        if (!refreshTokenDto.getAccessToken().equals(userTokenInfo.getAccessToken())) {
            redisTokenHandler.deleteRedis(userTokenInfo.getUserId());
            log.warn("requested accessToken != saved accessToken");
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        //5.If the existing accessToken is not yet an expired token, delete the key as an abnormal request and throw a 401 error.
        jwtResult = jwtTokenHandler.extractAllClaims(userTokenInfo.getAccessToken());
        if (jwtResult.getJwtResultType() == JwtResultType.TOKEN_SUCCESS) {
            redisTokenHandler.deleteRedis(userTokenInfo.getUserId());
            log.warn("accessToken has not expired yet : {}", jwtResult.getJwtResultType().name());
            throw new AuthException(JwtResultType.UNUSUAL_REQUEST.name());
        }

        return userTokenInfo;
    }

    private boolean isMatchPassword(String rawPassword, String dbPassword) {
        return passwordEncoder.matches(rawPassword, dbPassword);
    }

}
