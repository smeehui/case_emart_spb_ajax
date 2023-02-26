package com.huy.model.dto;

import com.huy.model.Product;
import com.huy.model.ProductAvatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductCreateReqDTO {

    private Long id;
    private String title;
    private BigDecimal price;
    private String description;

    private MultipartFile file;

    public Product toProduct(ProductAvatar productAvatar) {
        return new Product()
                .setId(id)
                .setTitle(title)
                .setPrice(price)
                .setDescription(description)
                .setProductAvatar(productAvatar)
                ;
    }
}
