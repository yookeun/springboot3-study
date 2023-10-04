package com.example.study.item.controller;

import com.example.study.item.dto.ItemDto;
import com.example.study.item.dto.ItemDto.ItemRequestDto;
import com.example.study.item.dto.ItemSearchCondition;
import com.example.study.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @PreAuthorize("hasAuthority('ITEM')")
    public ItemDto saveItem(@RequestBody @Valid ItemRequestDto requestDto) {
        return itemService.save(requestDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEM')")
    public ItemDto getItem(@PathVariable("id") Long id) {
        return itemService.getOneItem(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ITEM')")
    public Page<ItemDto> getAllItems(ItemSearchCondition condition, Pageable pageable) {
        return itemService.getAllItems(condition, pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEM')")
    public ItemDto updateItem(@PathVariable("id") Long id, @RequestBody @Valid ItemRequestDto requestDto) {
        return itemService.updateItem(id, requestDto);
    }
}

