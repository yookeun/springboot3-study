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
public class RefreshTokenDto {

    @NotBlank(message = "required")
    private String userId;

    @NotBlank(message = "required")
    private String accessToken;

    @NotBlank(message = "required")
    private String refreshToken;

}
