package com.example.study.order.repository;

import com.example.study.order.domain.Order;
import java.util.Optional;
import org.springframework.data.repository.Repository;


@org.springframework.stereotype.Repository
public interface OrderRepository extends Repository<Order, Long>, OrderRepositoryCustom {

    Optional<Order> findById(Long id);

    Order save(Order order);

    void delete(Order order);
}
