package com.huy.model.dto;

import com.huy.model.ProdCategory;
import com.huy.model.ProdType;
import com.huy.model.enums.EProdCategory;
import jakarta.validation.constraints.NotNull;
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
public class ProductCreateReqDTO implements Validator {

    private Long id;
    private String title;
    private String price;
    private String description;

    @NotNull(message = "Product image is required")
    private MultipartFile file;

    private String prodTypeStr;

    private ProdType prodType;

    private String prodCategoryStr;

    private ProdCategory prodCategory;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductCreateReqDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductCreateReqDTO productCreateReqDTO = (ProductCreateReqDTO) target;
        String title = productCreateReqDTO.getTitle();
        String priceStr = productCreateReqDTO.getPrice();
        MultipartFile file = productCreateReqDTO.getFile();
        String prodType = productCreateReqDTO.getProdTypeStr();
        String prodCategory = productCreateReqDTO.getProdCategoryStr();

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
        if (file == null) {
            errors.rejectValue("file", "file.empty", "Product image is required");
        }
        if (prodType == null||prodType.trim().equals("")) {
            errors.rejectValue("prodTypeStr", "prodTypeStr.empty", "Product type is required");
        }else {
            if (prodType.trim().equals("")) {
                errors.rejectValue("prodTypeStr", "prodTypeStr.empty", "Product type is required");
            } else if (!prodType.matches("[0-9]")) {
                errors.rejectValue("prodTypeStr", "prodTypeStr.format", "Product type is invalid");
            }
        }

        if (prodCategory == null||prodCategory.trim().equals("")) {
            errors.rejectValue("prodCategoryStr", "prodTypeStr.empty", "Product category is required");
        }else{
            try {
               EProdCategory.valueOf(prodCategory);
            } catch (Exception e) {
                e.printStackTrace();
                errors.rejectValue("prodCategoryStr", "prodTypeStr.format", "Product category is not valid");
            }
        }

    }
}
