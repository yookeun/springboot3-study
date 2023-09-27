package com.example.study.member.dto;

import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String userId;
    private String name;
    private Gender gender;
    private List<MemberAuthorityDto> authorities;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .name(member.getName())
                .gender(member.getGender())
                .authorities(getAuthorities(member))
                .build();
    }

    public static List<MemberAuthorityDto> getAuthorities(Member member) {
        return member.getMemberAuthorityList().stream()
                .map(MemberAuthorityDto::fromEntity)
                .collect(Collectors.toList());

    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRequestDto {


        @NotBlank(message = "required")
        private String userId;

        @NotBlank(message = "required")
        private String password;

        @NotBlank(message = "required")
        private String name;

        @NotNull(message = "required")
        private Gender gender;

        @Default
        private List<MemberAuthorityRequestDto> authorities = new ArrayList<>();

        public Member toEntity() {
            return Member.builder()
                    .userId(userId)
                    .password(password)
                    .name(name)
                    .gender(gender)
                    .build();
        }
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberUpdateDto {

        @NotBlank(message = "required")
        private String password;

        @NotBlank(message = "required")
        private String name;

        @NotNull(message = "required")
        private Gender gender;

        @Default
        private List<MemberAuthorityRequestDto> authorities = new ArrayList<>();

    }

}
