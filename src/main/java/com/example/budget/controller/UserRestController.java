package com.example.budget.controller;

import com.example.budget.domain.dto.*;
import com.example.budget.domain.entity.FamilyMember;
import com.example.budget.methods.Methods;
import com.example.budget.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class UserRestController {

    private final MainService mainService;
    private final Methods methods;

    public UserRestController(MainService mainService, Methods methods) {
        this.mainService = mainService;
        this.methods = methods;
    }


    @GetMapping("/findAllUsers")
    public ResponseEntity<List<FamilyMember>> findAllFamilyMember() {
        List<FamilyMember> familyMemberList = mainService.findAllFamilyMember();
        if (familyMemberList.size() > 0) {
            return ResponseEntity.ok(familyMemberList);
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/findFamilyAccount")
    public String findCountFamily() {
        return mainService.familyAccount();
    }

    @PutMapping("/putMany")
    public ResponseEntity<GetClientMessage> putMoney(@Validated @ModelAttribute ManyDto manyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.putManyOnAccount(manyDto));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdrawMoney")
    public ResponseEntity<GetClientMessage> withdrawMoney(@Validated @ModelAttribute ManyDto manyDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.withdrawMoneyOnAccount(manyDto));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/applyLimitations")
    public ResponseEntity<GetClientMessage> applyLimitations(@Validated @ModelAttribute FamilyAdminLimitationsDto familyAdminLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.applyAdminFamilyRestrictions(familyAdminLimitationsDto));
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/allFamilyList")
    public ResponseEntity<List<AllFamilyDto>> allFamilyList() {
        List<AllFamilyDto> allFamilyDtoList = mainService.allFamily();
        if (allFamilyDtoList.size() > 0) {
            return ResponseEntity.ok(allFamilyDtoList);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/findByFamilyLogin")
    public ResponseEntity<List<FamilyMember>> findFamilyByFamilyLogin(@Validated @ModelAttribute FamilyLoginDto familyLoginDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            List<FamilyMember> familyMemberList = mainService.findAllUsersFamily(familyLoginDto);
            if (familyMemberList.size() > 0) {
                return ResponseEntity.ok(familyMemberList);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/familyAccount")
    public ResponseEntity<ManyDto> getFamilyAccount(@Validated @ModelAttribute FamilyLoginDto familyLoginDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.findFamilyAccount(familyLoginDto));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/putManyGlobalAdmin")
    public ResponseEntity<GetClientMessage> putManyForFamilyAccount(@Validated @ModelAttribute PutManyDto putManyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.putManyForFamilyAccount(putManyDto));
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/withdrawManyGlobalAdmin")
    public ResponseEntity<GetClientMessage> withdrawManyFromFamilyAccount(@Validated @ModelAttribute PutManyDto putManyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.withdrawManyFromUserAccount(putManyDto));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/globAdmLimAllFamily")
    public ResponseEntity<GetClientMessage> imposeRestrictionsOnFamilyGlobalAdmin(@Validated @ModelAttribute GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.imposeRestrictionsOnFamilyGlobalAdmin(globalAdminFamilyLimitationsDto));
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/globAdmPersLim")
    public ResponseEntity<GetClientMessage> personalLimitationGlobalAdmin(@Validated @ModelAttribute FamilyAdminLimitationsDto familyAdminLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(mainService.personalLimitationsGlobalAdmin(familyAdminLimitationsDto));
        }
        return ResponseEntity.notFound().build();
    }

}
