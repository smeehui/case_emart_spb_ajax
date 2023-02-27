package com.huy.service.cart;

import com.huy.model.Cart;
import com.huy.model.CartDetail;
import com.huy.model.Product;
import com.huy.model.User;
import com.huy.service.IGeneralService;

import java.util.Optional;

public interface ICartService extends IGeneralService<Cart> {

    Optional<Cart> findByUser(User user);

    Cart createIfNotExist(User user, Product product);

    Cart createIfExist(User user, Cart cart, Product product);

    void checkout(User user, Cart cart);

    Cart decreaseProductCartDetail(Cart cart, CartDetail cartDetail, Product product);

    Cart adjustCartDetailQuantity(Cart cart, CartDetail cartDetail, Product product, Long quantity);
}
