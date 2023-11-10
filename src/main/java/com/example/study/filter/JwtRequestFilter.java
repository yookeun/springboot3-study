package com.example.study.filter;

import com.example.study.config.UserDetailService;
import com.example.study.exception.ErrorResponse;
import com.example.study.handler.JwtResult;
import com.example.study.handler.JwtResult.JwtResultType;
import com.example.study.handler.JwtTokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenHandler jwtTokenHandler;
    private final UserDetailService  userDetailService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {"/favicon", "/member", "/docs"};
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username;
        String token = null;
        //HttpSession session = request.getSession();

        //Parse the token attached below the Bearer part of the Header.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        JwtResult jwtResult = jwtTokenHandler.extractAllClaims(token);

        if (jwtResult == null)  {
            ErrorResponse.exceptionCall(HttpStatus.BAD_REQUEST, response, JwtResultType.UNUSUAL_REQUEST.name());
            return;
        }

        if (jwtResult.getJwtResultType() == JwtResultType.TOKEN_EXPIRED) {
            ErrorResponse.exceptionCall(HttpStatus.UNAUTHORIZED, response, JwtResultType.TOKEN_EXPIRED.name());
            return;
        }
        if (jwtResult.getJwtResultType() == JwtResultType.TOKEN_INVALID) {
            ErrorResponse.exceptionCall(HttpStatus.UNAUTHORIZED, response, JwtResultType.TOKEN_INVALID.name());
            return;
        }

        username = jwtResult.getClaims().get("username").toString();

        if (username == null) {
            ErrorResponse.exceptionCall(HttpStatus.UNAUTHORIZED, response);
            return;
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            //session.setAttribute("userId", username);
        }

        filterChain.doFilter(request, response);
    }

}
