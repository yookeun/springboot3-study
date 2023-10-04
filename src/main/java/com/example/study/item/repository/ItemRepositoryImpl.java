package com.example.study.item.repository;

import static com.example.study.item.domain.QItem.item;

import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemSearchCondition;
import com.example.study.member.enums.ItemType;
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
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> getAllItems(ItemSearchCondition condition, Pageable pageable) {

        List<Item> result = queryFactory.select(item)
                .from(item)
                .where(
                        containsSearchName(condition.getItemName()),
                        inItemTypes(condition.getItemType())
                )
                .fetch();

        JPAQuery<Long> count = queryFactory.select(item.count()).from(item);

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression containsSearchName(String searchName) {
        return StringUtils.isNullOrEmpty(searchName) ? null : item.itemName.contains(searchName);
    }

    private BooleanExpression inItemTypes(String itemType) {
        if (StringUtils.isNullOrEmpty(itemType)) {
            return null;
        }
        String[] itemTypes = itemType.split(",");
        List<ItemType> itemTypeList = new ArrayList<>();
        Arrays.stream(itemTypes).forEach(c -> itemTypeList.add(ItemType.valueOf(c.trim())));
        return item.itemType.in(itemTypeList);
    }

}