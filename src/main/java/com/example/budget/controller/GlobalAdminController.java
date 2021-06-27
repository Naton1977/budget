package com.example.budget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/globalAdmin")
public class GlobalAdminController {

    @GetMapping("/admin")
    public String getAdminPage(){
        return "GlobalAdminPage";
    }


}
