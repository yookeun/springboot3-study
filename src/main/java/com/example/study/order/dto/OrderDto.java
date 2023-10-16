package com.example.study.order.dto;

import com.example.study.annotation.ItemCheck;
import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemDto;
import com.example.study.member.domain.Member;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.enums.OrderStatus;
import com.example.study.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private MemberDto memberDto;
    @ItemCheck
    private ItemDto itemDto;
    private Integer orderCount;
    private OrderStatus orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    public static OrderDto fromEntity(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .memberDto(MemberDto.fromEntity(order.getMember()))
                .itemDto(ItemDto.fromEntity(order.getItem()))
                .orderCount(order.getOrderCount())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRequestDto {

        @NotNull(message = "required")
        private Long memberId;

        @NotNull(message = "required")
        private Long itemId;

        @JsonIgnore
        private Member member;

        @JsonIgnore
        private Item item;

        @NotNull(message = "required")
        private Integer orderCount;

        @NotNull(message = "required")
        private OrderStatus orderStatus;

        @NotNull(message = "required")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate orderDate;

        public Order toEntity() {
            return Order.builder()
                    .member(member)
                    .item(item)
                    .orderCount(orderCount)
                    .orderStatus(orderStatus)
                    .orderDate(orderDate)
                    .build();
        }
    }

}
