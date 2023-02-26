package com.huy.service.cartDetail;

import com.huy.model.Cart;
import com.huy.model.CartDetail;
import com.huy.model.Product;
import com.huy.service.IGeneralService;

import java.util.Optional;

public interface ICartDetailService extends IGeneralService<CartDetail> {
    Optional<CartDetail> findByCartAndProduct(Cart cart, Product product);
}
