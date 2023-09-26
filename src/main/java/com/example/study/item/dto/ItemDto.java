package com.example.study.item.dto;

import com.example.study.item.domain.Item;
import com.example.study.member.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDto {

    private Long id;
    private String itemName;
    private Long price;
    private ItemType itemType;

    public static ItemDto fromEntity(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .price((item.getPrice()))
                .itemType(item.getItemType())
                .build();
    }

    public Item toEntity() {
        return Item.builder()
                .itemName(itemName)
                .price(price)
                .itemType(itemType)
                .build();
    }
}
