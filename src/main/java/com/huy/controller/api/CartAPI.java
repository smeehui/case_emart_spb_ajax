package com.huy.controller.api;

import com.huy.exception.DataInputException;
import com.huy.model.*;
import com.huy.service.cart.ICartService;
import com.huy.service.cartDetail.ICartDetailService;
import com.huy.utils.product.IProductService;
import com.huy.service.user.IUserService;
import com.huy.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/carts")
public class CartAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ICartDetailService cartDetailService;

    @Autowired
    private IProductService productService;

    @Autowired
    private AppUtils appUtils;


    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addCart(@PathVariable Long productId) {

        String username = appUtils.getUsernamePrincipal();

        Optional<User> userOptional = userService.findByUsername(username);

        User user = userOptional.get();

        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product not valid");
        }

        Product product = productOptional.get();

        Optional<Cart> cartOptional = cartService.findByUser(user);

        if (cartOptional.isEmpty()) {
            cartService.createIfNotExist(user, product);
        } else {
            Cart cart = cartOptional.get();
            cartService.createIfExist(user, cart, product);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/minus/{productId}")
    public ResponseEntity<?> minusCart(@PathVariable Long productId) {

        String username = appUtils.getUsernamePrincipal();

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }

        User user = userOptional.get();

        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product not found");
        }

        Optional<Cart> cartOptional = cartService.findByUser(user);

        if (cartOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Cart not found)");
        }

        Cart cart = cartOptional.get();
        Product product = productOptional.get();
        Optional<CartDetail> cartDetailOptional = cartDetailService.findByCartAndProduct(cart, product);

        if (cartDetailOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Product's cart detail not found)");
        }

        cartService.decreaseProductCartDetail(cart, cartDetailOptional.get(), product);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> addCart() {
        String username = appUtils.getUsernamePrincipal();

        Optional<User> userOptional = userService.findByUsername(username);

        User user = userOptional.get();

        Optional<Cart> cartOptional = cartService.findByUser(user);

        if (!cartOptional.isPresent()) {
            throw new DataInputException("Please buy something before checkout");
        } else {
            Cart cart = cartOptional.get();

            cartService.checkout(user, cart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/adjust/{productId}/{quantity}")
    public ResponseEntity<?> adjustCartDetailQuantity( @PathVariable Long productId, @PathVariable Long quantity) {

        if (productId < 0 || quantity < 0) {
            throw new DataInputException("Quantity or product id is not valid");
        }
        if (quantity > 20) {
            throw new DataInputException("Quantity excess required number (less than 20)");
        }

        String username = appUtils.getUsernamePrincipal();

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }

        User user = userOptional.get();

        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product not found");
        }

        Optional<Cart> cartOptional = cartService.findByUser(user);

        if (cartOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Cart not found)");
        }

        Cart cart = cartOptional.get();
        Product product = productOptional.get();
        Optional<CartDetail> cartDetailOptional = cartDetailService.findByCartAndProduct(cart, product);

        if (cartDetailOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Product's cart detail not found)");
        }

        cartService.adjustCartDetailQuantity(cart, cartDetailOptional.get(), product, quantity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteCartDetail(@PathVariable Long productId) {

        String username = appUtils.getUsernamePrincipal();

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }

        User user = userOptional.get();

        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty()) {
            throw new DataInputException("Product not found");
        }

        Optional<Cart> cartOptional = cartService.findByUser(user);

        if (cartOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Cart not found)");
        }

        Cart cart = cartOptional.get();
        Product product = productOptional.get();
        Optional<CartDetail> cartDetailOptional = cartDetailService.findByCartAndProduct(cart, product);

        if (cartDetailOptional.isEmpty()) {
            throw new DataInputException("Invalid process (Product's cart detail not found)");
        }

        cartService.adjustCartDetailQuantity(cart, cartDetailOptional.get(), product, 0l);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/{a}/{b}")
    public ResponseEntity<?> test(@PathVariable Long a, @PathVariable Long b) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
