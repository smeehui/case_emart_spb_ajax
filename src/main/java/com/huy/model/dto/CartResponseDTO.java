package com.huy.model.dto;


import com.huy.model.CartDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponseDTO {
    private List<CartDetailResponseDTO> cartDetails;
    private BigDecimal totalAmount;
}