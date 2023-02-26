package com.huy.utils.product;

import com.huy.model.Product;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.service.IGeneralService;

import java.util.List;

public interface IProductService extends IGeneralService<Product> {

    List<ProductCreateResDTO> findAllProductCreateResDTO();

    ProductCreateResDTO create(ProductCreateReqDTO productCreateReqDTO);
}