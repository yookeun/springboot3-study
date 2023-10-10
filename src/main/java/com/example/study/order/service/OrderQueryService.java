package com.example.study.order.service;

import com.example.study.order.dto.OrderStatisticDto;
import com.example.study.order.dto.OrderStatisticDto.OrderStatisticsDto;
import com.example.study.order.repository.OrderQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    public OrderStatisticDto getOrderStatisticsDto(String startDate, String endDate) {
        return orderQueryRepository.getOrderStatisticsDto(startDate, endDate);
    }

    public List<OrderStatisticsDto> getOrderStatisticsDtoList(String startDate, String endDate) {
        return orderQueryRepository.getOrderStatisticsDtoList(startDate, endDate);
    }
}
