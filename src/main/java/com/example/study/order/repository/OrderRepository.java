package com.example.study.order.repository;

import com.example.study.order.domain.Order;
import org.springframework.data.repository.Repository;


@org.springframework.stereotype.Repository
public interface OrderRepository extends Repository<Order, Long>, OrderRepositoryCustom {
    Order save(Order order);
}
