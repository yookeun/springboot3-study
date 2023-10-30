package com.example.study.order.service;

import com.example.study.annotation.ItemCheck;
import com.example.study.item.domain.Item;
import com.example.study.item.repository.ItemRepository;
import com.example.study.member.domain.Member;
import com.example.study.member.respository.MemberRepository;
import com.example.study.order.domain.Order;
import com.example.study.order.dto.OrderDto;
import com.example.study.order.dto.OrderDto.OrderRequestDto;
import com.example.study.order.dto.OrderSearchCondition;
import com.example.study.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Page<OrderDto> getAllOrders(OrderSearchCondition condition, Pageable pageable) {
        return orderRepository.getAllOrders(condition, pageable).map(OrderDto::fromEntity);
    }

    public OrderDto getOneOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This Order ID does not exist"));
        return OrderDto.fromEntity(order);
    }

    @Transactional
    @ItemCheck
    public OrderDto saveOrder(OrderRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("This Member ID does not exist"));
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("This Item ID does not exist"));
        requestDto.setItem(item);
        requestDto.setMember(member);
        Order order = orderRepository.save(requestDto.toEntity());
        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This Order ID does not exist"));
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("This Item ID does not exist"));

        order.updateItem(item);
        order.updateOrderStatus(requestDto.getOrderStatus());
        order.updateOrderCount(requestDto.getOrderCount());
        order.updateOrderDate(requestDto.getOrderDate());

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This Order ID does not exist"));
        orderRepository.delete(order);
    }
}
