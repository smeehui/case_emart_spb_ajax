package com.huy.service.productAvatar;

import com.huy.model.ProductAvatar;
import com.huy.repository.ProductAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ProductAvatarServiceImpl implements IProductAvatarService{

    @Autowired
    private ProductAvatarRepository productAvatarRepository;

    @Override
    public List<ProductAvatar> findAll() {
        return null;
    }

    @Override
    public Optional<ProductAvatar> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public ProductAvatar save(ProductAvatar productAvatar) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(ProductAvatar productAvatar) {

    }

}
