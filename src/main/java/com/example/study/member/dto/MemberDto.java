package com.example.study.member.dto;

import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.enums.Gender;
import com.querydsl.core.annotations.QueryProjection;
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
    private String password;
    private String name;
    private Gender gender;

    @QueryProjection
    public MemberDto(Long id, String userId, String name, Gender gender) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.gender = gender;
    }

    @Default
    private List<MemberAuthorityDto> authorities = new ArrayList<>();



    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .password(member.getPassword())
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


        private String userId;
        private String password;
        private String name;
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

}
