package com.huy.repository;

import com.huy.model.Cart;
import com.huy.model.CartDetail;
import com.huy.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    Optional<CartDetail> findByCartAndProduct(Cart cart, Product product);

    List<CartDetail> findAllByCart(Cart cart);

    @Query("SELECT SUM(cd.amount) FROM CartDetail AS cd WHERE cd.cart = :cart")
    BigDecimal getTotalAmountByCart(@Param("cart") Cart cart);

    void deleteAllByCart(Cart cart);
}
