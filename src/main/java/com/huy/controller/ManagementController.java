package com.huy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
public class ManagementController {


    @GetMapping("/user")
    public String showUserManagementPage() {
        return "shop/manage/user_management";
    }


    @GetMapping("/product")
    public String showProductManagementPage() {
        return "shop/manage/product_management";
    }
}
