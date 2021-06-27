package com.example.budget.controller;

import com.example.budget.domain.dto.CreateNewFamilyDto;
import com.example.budget.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NewFamilyController {

    private final MainService mainService;

    public NewFamilyController(MainService mainService) {
        this.mainService = mainService;
    }


    @GetMapping("/createFamily")
    private String createFamily() {
        return "CreateFamily";
    }


    @PostMapping("/createFamily")
    private String saveFamily(@Validated @ModelAttribute CreateNewFamilyDto createNewFamilyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            mainService.saveNewFamily(createNewFamilyDto);
        }
        return "redirect:/";
    }


}
