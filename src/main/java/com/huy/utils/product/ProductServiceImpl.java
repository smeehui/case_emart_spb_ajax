package com.huy.utils.product;


import com.huy.exception.DataInputException;
import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.ProductAvatar;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.repository.ProductAvatarRepository;
import com.huy.repository.ProductRepository;
import com.huy.repository.ProductTypeRepository;
import com.huy.service.upload.IUploadService;
import com.huy.utils.UploadUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductAvatarRepository productAvatarRepository;

    @Autowired
    private IUploadService uploadService;

    @Autowired
    private UploadUtils uploadUtils;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductCreateResDTO> findAllProductCreateResDTO() {
        return productRepository.findAllProductCreateResDTO();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Override
    public ProductCreateResDTO create(ProductCreateReqDTO productCreateReqDTO) {

        ProductAvatar productAvatar = new ProductAvatar();
        productAvatarRepository.save(productAvatar);

        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Product product = modelMapper.map(productCreateReqDTO, Product.class);



        uploadAndSaveProductImage(productCreateReqDTO, productAvatar);

        product.setProductAvatar(productAvatar);

        product.setId(null);
        productRepository.save(product);

        ProductCreateResDTO productCreateResDTO = product.toProductCreateResDTO();

        return productCreateResDTO;
    }

    private void uploadAndSaveProductImage(ProductCreateReqDTO productCreateReqDTO, ProductAvatar productAvatar) {
        try {
            Map uploadResult = uploadService.uploadImage(productCreateReqDTO.getFile(), uploadUtils.buildImageUploadParamsProduct(productAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            productAvatar.setFileName(productAvatar.getId() + "." + fileFormat);
            productAvatar.setFileUrl(fileUrl);
            productAvatar.setFileFolder(UploadUtils.IMAGE_UPLOAD_FOLDER_PRODUCT);
            productAvatar.setCloudId(productAvatar.getFileFolder() + "/" + productAvatar.getId());
            productAvatarRepository.save(productAvatar);

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(Product product) {

    }
}
