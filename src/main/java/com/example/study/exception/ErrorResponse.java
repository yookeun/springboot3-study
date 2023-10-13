package com.example.study.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private Integer code;
    private String msg;

    public static void exceptionCall(HttpStatus httpStatus, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(httpStatus.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.builder()
                .code(httpStatus.value())
                .msg(httpStatus.name())
                .build()));
    }

    public static void exceptionCall(HttpStatus httpStatus, HttpServletResponse response, String msg) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(httpStatus.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.builder()
                .code(httpStatus.value())
                .msg(msg)
                .build()));
    }


}
