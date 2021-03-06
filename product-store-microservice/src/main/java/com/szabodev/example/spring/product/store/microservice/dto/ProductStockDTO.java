package com.szabodev.example.spring.product.store.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDTO {

    private Long id;

    private Long productId;

    private Integer available;

    private Integer requiredAmount;

    private Integer version;
}
