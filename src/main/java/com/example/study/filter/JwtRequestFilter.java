package com.example.study.filter;

import com.example.study.config.UserDetailService;
import com.example.study.handler.JwtTokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        //This filter does not apply to the paths below.
        if (path.startsWith("/member")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username;
        String token = null;
        HttpSession session = request.getSession();

        //Parse the token attached below the Bearer part of the Header.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        username = jwtTokenHandler.extractUsername(token);
        if (username == null) {
            exceptionCall(response);
            return;
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            session.setAttribute("userId", username);
        }

        filterChain.doFilter(request, response);
    }

    private void exceptionCall(HttpServletResponse response) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("error", "INVALID TOKEN");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(map));
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
    }
}
