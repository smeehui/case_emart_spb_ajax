package com.huy.repository;

import com.huy.model.ProdCategory;
import com.huy.model.ProdType;
import com.huy.model.Product;
import com.huy.model.dto.ProductCreateResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalse();

    List<Product> findAllByProductTypeAndDeletedIsFalse(ProdType productType);

    @Query("SELECT NEW com.huy.model.dto.ProductCreateResDTO (" +
                "p.id, " +
                "p.title, " +
                "p.price, " +
                "p.description, " +
                "p.productAvatar" +
            ") " +
            "FROM Product AS p"
    )
    List<ProductCreateResDTO> findAllProductCreateResDTO();

    List<Product> findAllByProdCategory(ProdCategory prodCategory);
}
