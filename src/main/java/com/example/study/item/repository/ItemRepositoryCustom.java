package com.example.study.item.repository;

import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAllItems(ItemSearchCondition condition, Pageable pageable);
}
