package com.example.study.order.service;

import com.example.study.item.domain.Item;
import com.example.study.item.repository.ItemRepository;
import com.example.study.member.domain.Member;
import com.example.study.member.respository.MemberRepository;
import com.example.study.order.domain.Order;
import com.example.study.order.dto.OrderDto;
import com.example.study.order.dto.OrderDto.OrderRequestDto;
import com.example.study.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public OrderDto save(OrderRequestDto orderRequestDto) {
        Member member = memberRepository.findById(orderRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("This Member ID does not exist"));
        Item item = itemRepository.findById(orderRequestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("This Order ID does not exist"));
        orderRequestDto.setItem(item);
        orderRequestDto.setMember(member);
        Order order = orderRepository.save(orderRequestDto.toEntity());
        return OrderDto.fromEntity(order);
    }

}
