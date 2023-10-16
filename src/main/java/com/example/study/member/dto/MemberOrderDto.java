package com.example.study.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MemberOrderDto {

    private Long memberId;
    private String memberName;
    private Long memberCount;

    @QueryProjection
    public MemberOrderDto(Long memberId, String memberName, Long memberCount) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberCount = memberCount;
    }

}
