package com.example.study.handler;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResult {

    private JwtResultType jwtResultType;
    private Claims claims;

    public enum JwtResultType {
        TOKEN_EXPIRED,
        TOKEN_INVALID,
        TOKEN_SUCCESS,
        UNUSUAL_REQUEST
    }

}
