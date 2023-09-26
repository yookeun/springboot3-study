package com.example.study.item.repository;

import com.example.study.item.domain.Item;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ItemRepository extends Repository<Item, Long>, ItemRepositoryCustom {
    Item save(Item item);
}
