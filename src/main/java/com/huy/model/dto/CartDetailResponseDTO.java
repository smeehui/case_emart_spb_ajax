package com.huy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartDetailResponseDTO {

    private String title;

    private BigDecimal price;

    private Long quantity;

    private BigDecimal amount;

    private ProductResponseDTO product;
}
