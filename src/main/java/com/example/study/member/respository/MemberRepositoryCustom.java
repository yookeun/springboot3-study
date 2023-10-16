package com.example.study.member.respository;

import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberOrderDto;
import com.example.study.member.dto.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<Member> getAllMembers(MemberSearchCondition condition, Pageable pageable);

    Page<MemberOrderDto> getAllMemberAndOrderCount(MemberSearchCondition condition, Pageable pageable);

}
