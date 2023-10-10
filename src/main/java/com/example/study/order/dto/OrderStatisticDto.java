package com.example.study.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderStatisticDto {
    private Long totalCount;
    private BigDecimal waitingCount;
    private BigDecimal doingCount;
    private BigDecimal doneCount;


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class OrderStatisticsDto {
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        private Date orderDate;
        private Long totalCount;
        private BigDecimal waitingCount;
        private BigDecimal doingCount;
        private BigDecimal doneCount;
    }

}
