package com.example.study.order.dto;

import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemDto;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.enums.OrderStatus;
import com.example.study.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private MemberDto memberDto;
    private ItemDto itemDto;
    private Integer orderCount;
    private OrderStatus orderStatus;

    public static OrderDto fromEntity(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .memberDto(MemberDto.fromEntity(order.getMember()))
                .itemDto(ItemDto.fromEntity(order.getItem()))
                .orderCount(order.getOrderCount())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRequestDto {

        private Long memberId;
        private Long itemId;

        @JsonIgnore
        private Member member;

        @JsonIgnore
        private Item item;

        private Integer orderCount;
        private OrderStatus orderStatus;

        public Order toEntity() {
            return Order.builder()
                    .member(member)
                    .item(item)
                    .orderCount(orderCount)
                    .orderStatus(orderStatus)
                    .build();
        }
    }
}
