package com.example.study.handler;

import com.example.study.handler.JwtResult.JwtResultType;
import com.example.study.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenHandler {


    private final String jwtSecretKey;
    private final Long accessTokenExpiredMin;
    private final Long refreshTokenExpiredDays;

    public JwtTokenHandler(@Value("${jwt.secret-key}") String jwtSecretKey,
            @Value("${jwt.access-token-expired-min}") Long accessTokenExpiredMin,
            @Value("${jwt.refresh-token-expired-days}") Long refreshTokenExpiredDays) {
        this.jwtSecretKey = jwtSecretKey;
        this.accessTokenExpiredMin = accessTokenExpiredMin;
        this.refreshTokenExpiredDays = refreshTokenExpiredDays;
    }

    private String createToken(Map<String, Object> claims) {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(jwtSecretKey.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * (accessTokenExpiredMin)))
                .compact();
    }

    private String createRefreshToken(Map<String, Object> claims) {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(jwtSecretKey.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * (refreshTokenExpiredDays)))
                .compact();
    }

    public JwtResult extractAllClaims(String token) {
        if (StringUtils.isEmpty(token)) return null;
        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSecretKey.getBytes(), sa.getJcaName());
        Claims claims;
        JwtResult jwtResult = new JwtResult();
        try {
            claims = Jwts.parserBuilder().setSigningKey(secretKeySpec).build()
                    .parseClaimsJws(token).getBody();            ;
            jwtResult.setJwtResultType(JwtResultType.TOKEN_SUCCESS);
            jwtResult.setClaims(claims);

        } catch (ExpiredJwtException e) {
            jwtResult.setJwtResultType(JwtResultType.TOKEN_EXPIRED);
            jwtResult.setClaims(null);

        } catch (JwtException e) {
            jwtResult.setJwtResultType(JwtResultType.TOKEN_INVALID);
            jwtResult.setClaims(null);
        }
        return jwtResult;
    }




    public String generateToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUserId());
        String auths = member.getMemberAuthorityList().stream()
                .map(memberAuthority -> memberAuthority.getAuthority().name())
                .collect(Collectors.joining(","));
        claims.put("authorities", auths);
        return createToken(claims);
    }

    public String generateRefreshToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUserId());
        return createRefreshToken(claims);
    }

}
