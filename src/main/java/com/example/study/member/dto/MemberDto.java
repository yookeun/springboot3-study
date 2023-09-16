package com.example.study.member.dto;

import com.example.study.member.domain.Member;
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

    private Long memberId;
    private String userId;
    private String password;
    private String name;
    private Gender gender;

    @QueryProjection
    public MemberDto(Long memberId, String userId, String name, Gender gender) {
        this.memberId = memberId;
        this.userId = userId;
        this.name = name;
        this.gender = gender;
    }

    @Default
    private List<MemberAuthorityDto> authorities = new ArrayList<>();

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .userId(userId)
                .password(password)
                .name(name)
                .gender(gender)
                .build();
    }

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .memberId(member.getMemberId())
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

}
