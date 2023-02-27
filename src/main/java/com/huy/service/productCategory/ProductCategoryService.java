package com.huy.service.productCategory;

import com.huy.model.ProdCategory;
import com.huy.model.enums.EProdCategory;
import com.huy.repository.ProdCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService implements IProdCategoryService{
    @Autowired
    ProdCategoryRepository prodCategoryRepository;

    @Override
    public List<ProdCategory> findAll() {
        return prodCategoryRepository.findAll();
    }

    @Override
    public Optional<ProdCategory> findById(Long id) {
        return prodCategoryRepository.findById(id);
    }
    @Override
    public Optional<ProdCategory> findAllByName(EProdCategory name) {
        return prodCategoryRepository.findAllByName(name);
    }

    @Override
    public ProdCategory save(ProdCategory prodCategory) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(ProdCategory prodCategory) {

    }

}
