package com.huy.service.product;

import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.model.dto.ProductEditReqDTO;
import com.huy.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService extends IGeneralService<Product> {

    ProductCreateResDTO create(ProductCreateReqDTO productCreateReqDTO);

    ProductCreateResDTO update(ProductEditReqDTO productEditReqDTO, Product product, MultipartFile file);

    List<Product> findAllByProductTypeAndDeletedIsFalse(ProdType prodType);
}
