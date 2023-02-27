package com.huy.repository;

import com.huy.model.ProdType;
import com.huy.model.enums.EProdType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProdType, Long> {
    ProdType findByName(EProdType name);
    ProdType findByName(String name);
}
