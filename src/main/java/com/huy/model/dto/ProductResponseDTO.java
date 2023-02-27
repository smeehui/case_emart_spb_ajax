package com.huy.model.dto;

import com.huy.model.ProdType;
import com.huy.model.ProductAvatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponseDTO {

    private Long id;
    private String title;
    private BigDecimal price;
    private String description;

    private ProductAvatarDTO avatar;

    private ProdType prodType;

    private ProdCategoryDTO prodCategory;

}
