package com.example.study.item.service;

import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemDto;
import com.example.study.item.dto.ItemDto.ItemRequestDto;
import com.example.study.item.dto.ItemSearchCondition;
import com.example.study.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public ItemDto save(ItemRequestDto requestDto) {
        Item item = itemRepository.save(requestDto.toEntity());
        return ItemDto.fromEntity(item);
    }

    public ItemDto getOneItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This Member Item ID does not exist"));
        return ItemDto.fromEntity(item);
    }

    public Page<ItemDto> getAllItems(ItemSearchCondition condition, Pageable pageable) {
        return itemRepository.getAllItems(condition, pageable).map(ItemDto::fromEntity);
    }

    @Transactional
    public ItemDto updateItem(Long id, ItemRequestDto requestDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This Member Item ID does not exist"));
        item.updateItemName(requestDto.getItemName());
        item.updateItemType(requestDto.getItemType());
        item.updatePrice(requestDto.getPrice());
        return ItemDto.fromEntity(item);
    }

}
