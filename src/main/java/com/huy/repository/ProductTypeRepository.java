package com.huy.repository;

import com.huy.model.ProdType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProdType, Long> {
}
