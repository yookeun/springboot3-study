package com.example.study.handler;

import com.example.study.member.domain.Member;
import io.jsonwebtoken.Claims;
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
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private String createToken(Map<String, Object> claims) {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(jwtSecretKey.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .compact();
    }

    private Claims extractAllClaims(String token) {
        if (StringUtils.isEmpty(token)) return null;
        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSecretKey.getBytes(), sa.getJcaName());
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(secretKeySpec).build()
                    .parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            log.error(e.toString());
            claims = null;
        }
        return claims;
    }

    public String extractUsername(String token) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) return null;
        else return claims.get("username",String.class);
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
}
