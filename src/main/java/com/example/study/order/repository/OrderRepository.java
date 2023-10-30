package com.example.study.order.repository;

import com.example.study.order.domain.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;


@org.springframework.stereotype.Repository
public interface OrderRepository extends Repository<Order, Long>, OrderRepositoryCustom {

    Optional<Order> findById(Long id);

    Order save(Order order);

    void delete(Order order);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Order o
        where o.member.id = :memberId
    """)
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
