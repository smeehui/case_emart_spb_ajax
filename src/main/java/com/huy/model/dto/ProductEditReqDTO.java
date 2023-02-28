package com.huy.model.dto;

import com.huy.model.ProdCategory;
import com.huy.model.ProdType;
import com.huy.model.enums.EProdCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductEditReqDTO implements Validator {

    private Long id;
    private String title;
    private String price;
    private String description;

    private String prodTypeStr;

    private String prodCateStr;

    private ProdCategory prodCategory;

    private ProdType prodType;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductEditReqDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductEditReqDTO productEditReqDTO = (ProductEditReqDTO) target;
        String title = productEditReqDTO.getTitle();
        String priceStr = productEditReqDTO.getPrice();
        String prodType = productEditReqDTO.getProdTypeStr();
        String prodCate = productEditReqDTO.getProdCateStr();

        if (title==null||title.trim().equals("")) {
            errors.rejectValue("title", "title.empty","Product title is required");
        }
        if (priceStr==null|| priceStr.trim().equals("")) {
            errors.rejectValue("price", "price.empty","Product price is required");
        }
        else if (!priceStr.matches("[0-9]+")) {
            errors.rejectValue("price", "price.format", "Product price is invalid");
        }
        else {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(priceStr));
            BigDecimal minP = BigDecimal.valueOf(100L);
            BigDecimal maxP = BigDecimal.valueOf(10000L);
            if (price.compareTo(minP) < 0 || price.compareTo(maxP) > 0) {
                errors.rejectValue("price", "price.range", "Product price is from 100$ to 10000$");
            }
        }
        if (prodType == null) {
            errors.rejectValue("prodTypeStr", "prodTypeStr.empty", "Product type is required");
        }else {
            if (prodType.trim().equals("")) {
                errors.rejectValue("prodTypeStr", "prodTypeStr.empty", "Product type is required");
            } else if (!prodType.matches("[0-9]")) {
                errors.rejectValue("prodTypeStr", "prodTypeStr.format", "Product type is invalid");
            }
        }
        if (prodCate == null) {
            errors.rejectValue("prodCateStr", "prodTypeStr.empty", "Product category is required");
        }else {
            if (prodCate.trim().equals("")) {
                errors.rejectValue("prodCateStr", "prodTypeStr.empty", "Product category is required");
            } else {
                try {
                    EProdCategory.valueOf(prodCate);
                } catch (Exception e) {
                    errors.rejectValue("prodCateStr", "prodCateStr.empty", "Product category is not valid");
                }
            }
        }

    }
}
