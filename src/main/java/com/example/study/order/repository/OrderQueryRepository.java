package com.example.study.order.repository;

import com.example.study.order.dto.OrderStatisticDto;
import com.example.study.order.dto.OrderStatisticDto.OrderStatisticsDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderQueryRepository {

    @PersistenceContext
    private EntityManager em;


    public OrderStatisticDto getOrderStatisticsDto(String startDate, String endDate) {
        String sql = new StringBuilder()
                .append(" SELECT")
                .append(" COUNT(O.ORDER_ID) AS totalCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'WAITING' THEN 1 ELSE 0 END ) AS waitingCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'DOING' THEN 1 ELSE 0 END ) AS doingCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'DONE' THEN 1 ELSE 0 END ) AS doneCount")
                .append(" FROM ORDERS AS O")
                .append(" WHERE O.ORDER_DATE >= :startDate AND O.ORDER_DATE <= :endDate")
                .toString();

        JpaResultMapper jpaResultMapper = new JpaResultMapper();

        Query nativeQuery = em.createNativeQuery(sql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        return jpaResultMapper.uniqueResult(nativeQuery, OrderStatisticDto.class);
    }

    public List<OrderStatisticsDto> getOrderStatisticsDtoList(String startDate, String endDate) {
        String sql = new StringBuilder()
                .append(" SELECT")
                .append(" O.ORDER_DATE AS orderDate")
                .append(" ,COUNT(O.ORDER_ID) AS totalCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'WAITING' THEN 1 ELSE 0 END ) AS waitingCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'DOING' THEN 1 ELSE 0 END ) AS doingCount")
                .append(" ,SUM(CASE WHEN O.ORDER_STATUS = 'DONE' THEN 1 ELSE 0 END ) AS doneCount")
                .append(" FROM ORDERS AS O")
                .append(" WHERE O.ORDER_DATE >= :startDate AND O.ORDER_DATE <= :endDate")
                .append(" GROUP BY O.ORDER_DATE")
                .toString();

        JpaResultMapper jpaResultMapper = new JpaResultMapper();

        Query nativeQuery = em.createNativeQuery(sql)
                .setParameter("startDate", startDate)
                .setParameter("endDate",endDate);

        return jpaResultMapper.list(nativeQuery, OrderStatisticsDto.class);
    }
}
