package com.huy.service.cartDetail;

import com.huy.model.Cart;
import com.huy.model.CartDetail;
import com.huy.model.Product;
import com.huy.repository.CartDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CartDetailServiceImpl implements ICartDetailService{

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Override
    public List<CartDetail> findAll() {
        return null;
    }

    @Override
    public Optional<CartDetail> findById(Long id) {
        return Optional.empty();
    }

    public Optional<CartDetail> findByCartAndProduct(Cart cart, Product product){
        return cartDetailRepository.findByCartAndProduct(cart, product);
    };

    @Override
    public CartDetail save(CartDetail cartDetail) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

        cartDetailRepository.deleteById(id);
    }

    @Override
    public void delete(CartDetail cartDetail) {

    }

}
