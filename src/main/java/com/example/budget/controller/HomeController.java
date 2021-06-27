package com.example.budget.controller;



import com.example.budget.domain.dto.CreateNewUserFamilyDto;
import com.example.budget.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final MainService mainService;

    public HomeController(MainService mainService) {
        this.mainService = mainService;
    }



    @GetMapping("/")
    public String startPage() {
        return mainService.globalAdminPresent();
    }

    @PostMapping("/")
    public String saveGlobalAdmin(@Validated @ModelAttribute CreateNewUserFamilyDto createNewUserFamilyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return mainService.saveGlobalAdmin(createNewUserFamilyDto);
        }
        return "redirect:/home";
    }

}
