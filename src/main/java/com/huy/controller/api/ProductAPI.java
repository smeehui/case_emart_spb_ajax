package com.huy.controller.api;

import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.utils.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductAPI {

    @Autowired
    private IProductService productService;


    @GetMapping
    public ResponseEntity<?> getALl() {

        List<ProductCreateResDTO> productCreateResDTOS = productService.findAllProductCreateResDTO();

        return new ResponseEntity<>(productCreateResDTOS, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(ProductCreateReqDTO productCreateReqDTO) {

        ProductCreateResDTO productCreateResDTO = productService.create(productCreateReqDTO);

        return new ResponseEntity<>(productCreateResDTO, HttpStatus.CREATED);
    }
}
