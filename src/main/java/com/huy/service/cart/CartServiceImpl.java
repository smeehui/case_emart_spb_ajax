package com.huy.service.cart;

import com.huy.model.*;
import com.huy.repository.CartDetailRepository;
import com.huy.repository.CartRepository;
import com.huy.repository.OrderDetailRepository;
import com.huy.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<Cart> findAll() {
        return null;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public Cart createIfNotExist(User user, Product product) {
        Cart cart = new Cart();
        cart.setUser(user);

        String productTitle = product.getTitle();
        BigDecimal productPrice = product.getPrice();
        long quantity = 1L;
        BigDecimal productAmount = productPrice.multiply(BigDecimal.valueOf(quantity));

        cart.setTotalAmount(productAmount);


        CartDetail cartDetail = new CartDetail();
        cartDetail.setProduct(product);
        cartDetail.setTitle(productTitle);
        cartDetail.setPrice(productPrice);
        cartDetail.setQuantity(quantity);
        cartDetail.setAmount(productAmount);
        cartDetail.setCart(cart);

        cartDetailRepository.save(cartDetail);

        return cartRepository.save(cart);
    }

    @Override
    public Cart createIfExist(User user, Cart cart, Product product) {
        Optional<CartDetail> cartDetailOptional = cartDetailRepository.findByCartAndProduct(cart, product);

        String productTitle = product.getTitle();
        BigDecimal productPrice = product.getPrice();
        long quantity = 1L;
        BigDecimal productAmount = productPrice.multiply(BigDecimal.valueOf(quantity));

        if (cartDetailOptional.isEmpty()) {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            cartDetail.setTitle(productTitle);
            cartDetail.setPrice(productPrice);
            cartDetail.setQuantity(quantity);
            cartDetail.setAmount(productAmount);
            cartDetailRepository.save(cartDetail);
        }
        else {
            CartDetail cartDetail = cartDetailOptional.get();
            long newQuantity = cartDetail.getQuantity() + quantity;
            BigDecimal newAmount = productPrice.multiply(BigDecimal.valueOf(newQuantity));
            cartDetail.setPrice(productPrice);
            cartDetail.setQuantity(newQuantity);
            cartDetail.setAmount(newAmount);
            cartDetailRepository.save(cartDetail);
        }

        BigDecimal newTotalAmount = cartDetailRepository.getTotalAmountByCart(cart);
        cart.setTotalAmount(newTotalAmount);
        return cartRepository.save(cart);
    }

    @Override
    public void checkout(User user, Cart cart) {
        Order order = cart.toOrder();
        order.setId(null);
        orderRepository.save(order);

        List<CartDetail> cartDetails = cartDetailRepository.findAllByCart(cart);

        for (CartDetail item : cartDetails) {
            OrderDetail orderDetail = item.toOrderDetail(order);
            orderDetail.setId(null);
            orderDetailRepository.save(orderDetail);
        }

        cartDetailRepository.deleteAllByCart(cart);
        cartRepository.deleteById(cart.getId());
    }

    @Override
    public Cart decreaseProductCartDetail(Cart cart, CartDetail cartDetail, Product product) {

        Long oldQuantity = cartDetail.getQuantity();
        BigDecimal prodPrice = product.getPrice();
        Long cartDetailId = cartDetail.getId();
        Long quantity  = 1L;

        if (oldQuantity==1) {
            cartDetailRepository.deleteById(cartDetailId);
        }
        else {
            long newQuantity = oldQuantity - quantity;
            BigDecimal newAmount = prodPrice.multiply(BigDecimal.valueOf(newQuantity));

            cartDetail.setQuantity(newQuantity);
            cartDetail.setAmount(newAmount);
            cartDetail.setPrice(prodPrice);
            cartDetailRepository.save(cartDetail);
        }

        BigDecimal newTotalAmount = cartDetailRepository.getTotalAmountByCart(cart);
        if (newTotalAmount == null) {
            cartRepository.deleteById(cart.getId());
            return new Cart();
        } else {
            cart.setTotalAmount(newTotalAmount);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public Cart adjustCartDetailQuantity(Cart cart, CartDetail cartDetail, Product product, Long quantity) {

        BigDecimal prodPrice = product.getPrice();
        Long cartDetailId = cartDetail.getId();

        if (quantity == 0) {
            cartDetailRepository.deleteById(cartDetailId);
        }
        else {
            BigDecimal newAmount = prodPrice.multiply(BigDecimal.valueOf(quantity));

            cartDetail.setQuantity(quantity);
            cartDetail.setAmount(newAmount);
            cartDetail.setPrice(prodPrice);
            cartDetailRepository.save(cartDetail);
        }

        BigDecimal newTotalAmount = cartDetailRepository.getTotalAmountByCart(cart);
        if (newTotalAmount == null) {
            cartRepository.deleteById(cart.getId());
            return new Cart();
        } else {
            cart.setTotalAmount(newTotalAmount);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public Cart save(Cart cart) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(Cart cart) {

    }
}
