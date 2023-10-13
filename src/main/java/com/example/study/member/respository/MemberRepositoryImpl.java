package com.example.study.member.respository;

import static com.example.study.member.domain.QMember.member;
import static com.example.study.order.domain.QOrder.order;


import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberOrderDto;
import com.example.study.member.dto.MemberSearchCondition;
import com.example.study.member.dto.QMemberOrderDto;
import com.example.study.member.enums.Gender;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
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
    public Page<Member> getAllMembers(MemberSearchCondition condition, Pageable pageable) {

        Predicate[] where = {
                containsSearchName(condition.getSearchName()),
                eqIsGender(condition.getGender())
        };

        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(member.count())
                .from(member)
                .where(where);

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    @Override
    public Page<MemberOrderDto> getAllMemberAndOrderCount(MemberSearchCondition condition,
            Pageable pageable) {

        Predicate[] where = {
                containsSearchName(condition.getSearchName()),
                eqIsGender(condition.getGender())
        };

        List<MemberOrderDto> result = queryFactory.select(
                    new QMemberOrderDto(
                            member.id.as("memberId"),
                            member.name.as("memberName"),
                            ExpressionUtils.as(
                                    JPAExpressions
                                            .select(order.count())
                                            .from(order)
                                            .where(
                                                    order.member.eq(member)
                                            ),"orderCount"
                            )
                    )
                )
                .from(member)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        JPAQuery<Long> count = queryFactory
                .select(member.count())
                .from(member)
                .where(where);

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
