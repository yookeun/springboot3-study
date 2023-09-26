package com.example.study.member.dto;

import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.enums.Authority;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MemberAuthorityDto {
    private Long id;
    private Authority authority;
    @JsonIgnore
    private Member member;



    public static MemberAuthorityDto fromEntity(MemberAuthority memberAuthority) {
        return MemberAuthorityDto.builder()
                .id(memberAuthority.getId())
                .authority(memberAuthority.getAuthority())
                .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberAuthorityRequestDto {

        private Authority authority;
        @JsonIgnore
        private Member member;

        public MemberAuthority toEntity() {
            return MemberAuthority.builder()
                    .authority(authority)
                    .member(member)
                    .build();
        }
    }
}
