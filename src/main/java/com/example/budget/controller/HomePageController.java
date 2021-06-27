package com.example.budget.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {



    @GetMapping("/home")
    public String index() {
        return "Home";
    }


}
