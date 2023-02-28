package com.huy.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class SiteController {

    @GetMapping("/index")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/shop")
    public String showShopPage() {
        return "shop/shopping";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(){
        return "shop/checkout";
    }

}
