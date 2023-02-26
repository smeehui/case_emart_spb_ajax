package com.huy.controller.api;

import com.huy.exception.DataInputException;
import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.model.dto.ProductEditReqDTO;
import com.huy.service.productType.IProductTypeService;
import com.huy.utils.AppUtils;
import com.huy.utils.product.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/products")
public class ProductAPI {

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    AppUtils appUtils;

    @Autowired
    ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<?> getALl() {

        List<ProductCreateResDTO> productCreateResDTOS =
                productService.findAll()
                        .stream()
                        .map(product -> modelMapper.map(product, ProductCreateResDTO.class))
                        .collect(Collectors.toList());
        return new ResponseEntity<>(productCreateResDTOS, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Validated ProductCreateReqDTO productCreateReqDTO, BindingResult bindingResult) {

        productCreateReqDTO.setProdType(new ProdType());
        new ProductCreateReqDTO().validate(productCreateReqDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }


        Long prodTypeId = Long.parseLong(productCreateReqDTO.getProdTypeStr());

        Optional<ProdType> prodTypeOptional = productTypeService.findById(prodTypeId);

        if (prodTypeOptional.isEmpty()) {
            throw new DataInputException("Invalid product type");
        }

        productCreateReqDTO.setProdType(prodTypeOptional.get());
//        ProductCreateResDTO productCreateResDTO = new ProductCreateResDTO();
        ProductCreateResDTO productCreateResDTO = productService.create(productCreateReqDTO);

        return new ResponseEntity<>(productCreateResDTO, HttpStatus.CREATED);
    }

    @PostMapping("/edit/{productId}")
    public ResponseEntity<?> update(@PathVariable Long productId, @Validated ProductEditReqDTO productEditReqDTO, BindingResult bindingResult) {

        productEditReqDTO.setProdType(new ProdType());
        new ProductEditReqDTO().validate(productEditReqDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Optional<Product> product = productService.findById(productId);

        if (product.isEmpty()) {
            throw new DataInputException("Product is not found");
        }

        Long prodTypeId = Long.parseLong(productEditReqDTO.getProdTypeStr());
        Optional<ProdType> prodTypeOptional = productTypeService.findById(prodTypeId);

        if (prodTypeOptional.isEmpty()) {
            throw new DataInputException("Invalid product type");
        }

        productEditReqDTO.setProdType(prodTypeOptional.get());

        //TODO add edit product API

//        ProductCreateResDTO productCreateResDTO = new ProductCreateResDTO();
//        ProductCreateResDTO productCreateResDTO = productService.create(productCreateReqDTO);

        return new ResponseEntity<>(productCreateResDTO, HttpStatus.CREATED);
    }
}
