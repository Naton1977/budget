package com.example.budget.controller;


import com.example.budget.domain.dto.CreateNewUserFamilyDto;
import com.example.budget.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NewMemberController {

private final MainService mainService;

    public NewMemberController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/newMember")
    public String newUser() {
        return "NewUserFamily";
    }


    @PostMapping("/newMember")
    public String createNewUserFamily(@Validated @ModelAttribute CreateNewUserFamilyDto createNewUserFamilyDto, BindingResult bindingResult) {
        mainService.createNewUserFamily(createNewUserFamilyDto);
        return "redirect:/home";
    }



}
