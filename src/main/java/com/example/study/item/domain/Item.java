package com.example.study.item.domain;

import com.example.study.common.BaseEntity;
import com.example.study.converter.YnToBooleanConverter;
import com.example.study.member.enums.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "ITEM")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "PRICE")
    private Long price;

    @Column(name = "ITEM_TYPE")
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column(name = "USED_COUNT")
    @Default
    private Integer usedCount = 0;

    @Column(name = "IS_USED")
    @Convert(converter = YnToBooleanConverter.class)
    private String isUsed;

    public void updateItemName(String itemName) {
        this.itemName = itemName;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void updateUsedCount(Integer usedCount) {
        this.usedCount += usedCount;
    }

    public void updateIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

}
