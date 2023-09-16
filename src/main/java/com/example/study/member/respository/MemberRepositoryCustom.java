package com.example.study.member.respository;

import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable);
}
