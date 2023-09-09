package com.example.study.member;

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

    @NotBlank(message = "ID is required.")
    private String userId;

    @NotBlank(message = "PASSWORD is required.")
    private String password;

    private String accessToken;
    private Boolean result;
    private String msg;
}