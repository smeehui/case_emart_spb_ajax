package com.huy.service.productType;

import com.huy.model.ProdType;
import com.huy.model.ProdTypeRepository;
import com.huy.model.enums.EProdType;
import com.huy.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductTypeService implements IProductTypeService {

    @Autowired
    ProductTypeRepository productTypeRepository;
    @Autowired
    private ProdTypeRepository prodTypeRepository;

    @Override
    public List<ProdType> findAll() {
        return productTypeRepository.findAll();
    }

    @Override
    public Optional<ProdType> findById(Long id) {
        return productTypeRepository.findById(id);
    }

    @Override
    public ProdType save(ProdType prodType) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(ProdType prodType) {

    }

    @Override
    public ProdType findByName(EProdType name) {
        return prodTypeRepository.findByName(name);
    }
}
