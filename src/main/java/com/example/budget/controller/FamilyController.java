package com.example.budget.controller;

import com.example.budget.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FamilyController {
    private final MainService mainService;

    public FamilyController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/family")
    public String family() {
        return mainService.returnGlobalAdminPage();
    }

}
