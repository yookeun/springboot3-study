package com.example.study.item.service;

import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemDto;
import com.example.study.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public ItemDto save(ItemDto itemDto) {
        Item item = itemRepository.save(itemDto.toEntity());
        return ItemDto.fromEntity(item);
    }
}
