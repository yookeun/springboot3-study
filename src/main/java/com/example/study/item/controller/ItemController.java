package com.example.study.item.controller;


import com.example.study.item.dto.ItemDto;
import com.example.study.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@RequestBody @Valid ItemDto itemDto) {
        return itemService.save(itemDto);
    }
}
