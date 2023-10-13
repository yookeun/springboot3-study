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
    private String name;
    private Long memberCount;


    @QueryProjection
    public MemberOrderDto(Long memberId, String name, Long memberCount) {
        this.memberId = memberId;
        this.name = name;
        this.memberCount = memberCount;
    }
}
