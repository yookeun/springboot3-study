package com.example.study.order.controller;

import com.example.study.order.dto.OrderDto;
import com.example.study.order.dto.OrderDto.OrderRequestDto;
import com.example.study.order.dto.OrderSearchCondition;
import com.example.study.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@PreAuthorize("hasAuthority('ORDER')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderDto> getAllOrders(OrderSearchCondition condition, Pageable pageable) {
        return orderService.getAllOrders(condition, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOneOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOneOrder(id));
    }

    @PostMapping
    public ResponseEntity<OrderDto> saveOrder(@RequestBody @Valid OrderRequestDto requestDto) {
        return ResponseEntity.ok(orderService.saveOrder(requestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable("id") Long id,
            @RequestBody @Valid OrderRequestDto requestDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
