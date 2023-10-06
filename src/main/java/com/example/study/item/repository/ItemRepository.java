package com.example.study.item.repository;

import com.example.study.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface ItemRepository extends Repository<Item, Long>, ItemRepositoryCustom {
    Optional<Item> findById(Long id);
    Item save(Item item);

    @Modifying
    @Query("""
        UPDATE Item m
            SET m.usedCount = m.usedCount + 1
        WHERE m.id = :itemId
    """)
    void updateUsedCount(@Param("itemId") Long itemId);
}
