package com.huy.utils.product;


import com.huy.exception.DataInputException;
import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.ProductAvatar;
import com.huy.model.dto.ProductResponseDTO;
import com.huy.repository.UserAvatarRepository;
import com.huy.model.dto.ProductCreateReqDTO;
import com.huy.model.dto.ProductCreateResDTO;
import com.huy.model.dto.ProductEditReqDTO;
import com.huy.repository.ProductAvatarRepository;
import com.huy.repository.ProductRepository;
import com.huy.repository.ProductTypeRepository;
import com.huy.service.upload.IUploadService;
import com.huy.utils.UploadUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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
    @Autowired
    private UserAvatarRepository userAvatarRepository;

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

        return modelMapper.map(product, ProductCreateResDTO.class);
    }

    @Override
    public ProductCreateResDTO update(ProductEditReqDTO productEditReqDTO, Product product) {

        MultipartFile file = productEditReqDTO.getFile();

        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        product.setProductType(productEditReqDTO.getProdType());
        product.setDescription(productEditReqDTO.getDescription());
        product.setTitle(productEditReqDTO.getDescription());
        product.setPrice(BigDecimal.valueOf(Long.parseLong(productEditReqDTO.getPrice())));
        product.setProdCategory(productEditReqDTO.getProdCategory());

        if (file != null&&!file.isEmpty()) {

            destroyProductImageOnCloud(product, product.getProductAvatar());

            ProductAvatar productAvatar = new ProductAvatar();
            productAvatarRepository.save(productAvatar);
            uploadAndSaveProductImageUpdate(productEditReqDTO, productAvatar);
            product.setProductAvatar(productAvatar);

        }
        productRepository.save(product);
        ProductCreateResDTO productCreateResDTO = modelMapper.map(product, ProductCreateResDTO.class);
        return productCreateResDTO;
    }

    @Override
    public List<Product> findAllByProductType(ProdType prodType) {
        List<Product> allByProductType = productRepository.findAllByProductType(prodType);
        return allByProductType;
    }
    private void uploadAndSaveProductImage(ProductCreateReqDTO productCreateReqDTO, ProductAvatar productAvatar) {
        try {
            MultipartFile file = productCreateReqDTO.getFile();
            Map params = uploadUtils.buildImageUploadParamsProduct(productAvatar);
            Map uploadResult = uploadService.uploadImage(file, params);
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

    private void destroyProductImageOnCloud(Product product,ProductAvatar productAvatar){
        String publicId = String.format("%s/%s", productAvatar.getFileFolder(), productAvatar.getId());
        Map params = uploadUtils.buildImageDestroyParams(product, publicId);

        try {
            uploadService.destroyImage(publicId, params);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Destroy image failed");
        }


    }


    private void uploadAndSaveProductImageUpdate(ProductEditReqDTO productEditReqDTO, ProductAvatar productAvatar) {
        try {
            Map uploadResult = uploadService.uploadImage(productEditReqDTO.getFile(), uploadUtils.buildImageUploadParamsProduct(productAvatar));
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
        try {
            ProductAvatar productAvatar = product.getProductAvatar();
            productRepository.deleteById(product.getId());
            productAvatarRepository.deleteById(productAvatar.getId());
            destroyProductImageOnCloud(product, productAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
