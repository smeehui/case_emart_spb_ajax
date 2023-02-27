package com.huy.repository;

import com.huy.model.ProdCategory;
import com.huy.model.enums.EProdCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdCategoryRepository extends JpaRepository<ProdCategory, Long> {
    Optional<ProdCategory> findByName(EProdCategory name);
}