package com.example.study.member.dto;

import jakarta.validation.constraints.NotBlank;
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
public class LoginDto {

    private String userId;
    private String name;
    private String password;
    private String accessToken;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDto {

        @NotBlank(message = "ID is required.")
        private String userId;

        @NotBlank(message = "PASSWORD is required.")
        private String password;
    }
}
