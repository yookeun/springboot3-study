package com.example.study.order.repository;

import com.example.study.order.domain.Order;
import com.example.study.order.dto.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Order> getAllOrders(OrderSearchCondition condition, Pageable pageable);

}
