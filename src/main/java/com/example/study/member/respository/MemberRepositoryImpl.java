package com.example.study.member.respository;

import static com.example.study.member.domain.QMember.member;

import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.dto.QMemberDto;
import com.example.study.member.enums.Gender;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberDto> getAllMembers(MemberSearchCondition condition, Pageable pageable) {

        List<MemberDto> result = queryFactory
                .select(new QMemberDto(
                        member.memberId,
                        member.userId,
                        member.name,
                        member.gender
                ))
                .from(member)
                .where(
                        containsSearchName(condition.getSearchName()),
                        eqIsGender(condition.getGender())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(member.count())
                .from(member);

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression containsSearchName(String searchName) {
        return StringUtils.isNullOrEmpty(searchName) ? null :
                member.userId.contains(searchName).or(member.name.contains(searchName));

    }

    private BooleanExpression eqIsGender(Gender gender) {
        return gender == null ? null : member.gender.eq(gender);
    }
}
