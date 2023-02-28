package com.huy.controller.api;

import com.huy.exception.DataInputException;
import com.huy.model.ProdCategory;
import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.model.dto.ProductEditReqDTO;
import com.huy.model.dto.ProductResponseDTO;
import com.huy.model.enums.EProdCategory;
import com.huy.model.enums.EProdType;
import com.huy.repository.ProdCategoryRepository;
import com.huy.service.productCategory.IProdCategoryService;
import com.huy.service.productType.IProductTypeService;
import com.huy.utils.AppUtils;
import com.huy.service.product.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private IProdCategoryService prodCategoryService;

    @Autowired
    AppUtils appUtils;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ProdCategoryRepository prodCategoryRepository;


    @GetMapping
    public ResponseEntity<?> getALl() {

        List<ProductCreateResDTO> productCreateResDTOS =
                productService.findAll()
                        .stream()
                        .map(product -> modelMapper.map(product, ProductCreateResDTO.class))
                        .collect(Collectors.toList());
        return new ResponseEntity<>(productCreateResDTOS, HttpStatus.OK);
    }

    @GetMapping("/{productId}")

    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product not found");
        }
        return new ResponseEntity<>(modelMapper.map(productOptional.get(), ProductResponseDTO.class), HttpStatus.OK);
    }

    @GetMapping("/best_sellers")
    public ResponseEntity<?> getBestSellerProduct() {
        ProdType prodType = productTypeService.findByName(EProdType.BEST_SELLER);
//        ProdType prodType2 = productTypeService.findByName("BEST_SELLER");
        List<ProductResponseDTO> productResponseDTOS =
                productService.findAllByProductTypeAndDeletedIsFalse(prodType)
                        .stream()
                        .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                        .toList();
        return new ResponseEntity<>(productResponseDTOS,HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<?> create(@Validated ProductCreateReqDTO productCreateReqDTO, BindingResult bindingResult) {

//        productCreateReqDTO.setProdType(new ProdType());
        new ProductCreateReqDTO().validate(productCreateReqDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }


        Long prodTypeId = Long.parseLong(productCreateReqDTO.getProdTypeStr());
        String prodCateName = productCreateReqDTO.getProdCategoryStr();


        Optional<ProdType> prodTypeOptional = productTypeService.findById(prodTypeId);

        if (prodTypeOptional.isEmpty()) {
            throw new DataInputException("Invalid product type");
        }
        Optional<ProdCategory> prodCategoryOptional = prodCategoryService.findByName(EProdCategory.valueOf(prodCateName));

        if (prodCategoryOptional.isEmpty()) {
            throw new DataInputException("Invalid product type");
        }
        productCreateReqDTO.setProdCategory(prodCategoryOptional.get());
        productCreateReqDTO.setProdType(prodTypeOptional.get());
//        ProductCreateResDTO productCreateResDTO = new ProductCreateResDTO();
        ProductCreateResDTO productCreateResDTO = productService.create(productCreateReqDTO);

        return new ResponseEntity<>(productCreateResDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{productId}")
    public ResponseEntity<?> update(@PathVariable Long productId, @Validated ProductEditReqDTO productEditReqDTO, BindingResult bindingResult, MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            long fileSize = file.getSize();

            if (fileSize > 512000) {
                throw new DataInputException("Max file size is 500KB");
            }
        }
        productEditReqDTO.setProdType(new ProdType());
        new ProductEditReqDTO().validate(productEditReqDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product is not found");
        }

        Long prodTypeId = Long.parseLong(productEditReqDTO.getProdTypeStr());
        Optional<ProdType> prodTypeOptional = productTypeService.findById(prodTypeId);

        if (prodTypeOptional.isEmpty()) {
            throw new DataInputException("Invalid product type");
        }

        String prodCatStr = productEditReqDTO.getProdCateStr();

        Optional<ProdCategory> prodCategoryOptional = prodCategoryService.findByName(EProdCategory.valueOf(prodCatStr));
        if (prodCategoryOptional.isEmpty()) {
            throw new DataInputException("Invalid product category");
        }

        productEditReqDTO.setProdCategory(prodCategoryOptional.get());
        productEditReqDTO.setProdType(prodTypeOptional.get());

        ProductCreateResDTO productCreateResDTO = productService.update(productEditReqDTO,productOptional.get(),file);


        return new ResponseEntity<>(productCreateResDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product is not found");
        }

        productService.delete(productOptional.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
