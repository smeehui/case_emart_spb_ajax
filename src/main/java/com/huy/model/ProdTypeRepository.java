package com.huy.model;

import com.huy.model.enums.EProdType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProdTypeRepository extends JpaRepository<ProdType, Long> {
    @Query("select p from ProdType p where p.name = ?1")
    ProdType findByName(EProdType name);
}