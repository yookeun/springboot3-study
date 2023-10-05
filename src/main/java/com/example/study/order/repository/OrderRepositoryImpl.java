package com.example.study.order.repository;

import static com.example.study.order.domain.QOrder.order;

import com.example.study.member.enums.OrderStatus;
import com.example.study.order.domain.Order;
import com.example.study.order.dto.OrderSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> getAllOrders(OrderSearchCondition condition, Pageable pageable) {
        Predicate[] where = {
                containsMultiSearchName(condition.getSearchName()),
                inOrderStatus(condition.getOrderStatus())
        };

        List<Order> result = queryFactory.select(order)
                .from(order)
                .where(where)
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(order.count())
                .from(order)
                .where(where);

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    //multiSearch  : memberName, itemName
    private BooleanBuilder containsMultiSearchName(String searchName) {
        if (StringUtils.isNullOrEmpty(searchName)) {
            return null;
        }

        BooleanBuilder builder = new BooleanBuilder();
        String[] searchNames = searchName.split(",");
        for (String str : searchNames) {
            builder.and(order.member.name.contains(str.trim()))
                    .or(order.item.itemName.contains(str.trim()));
        }
        return builder;
    }

    private BooleanExpression inOrderStatus(String orderStatus) {
        if (StringUtils.isNullOrEmpty(orderStatus)) {
            return null;
        }
        String[] orderStatues = orderStatus.split(",");
        List<OrderStatus> orderStatusList = new ArrayList<>();
        Arrays.stream(orderStatues).forEach(c -> orderStatusList.add(OrderStatus.valueOf(c.trim())));
        return order.orderStatus.in(orderStatusList);
    }

}
