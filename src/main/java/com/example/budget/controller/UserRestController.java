package com.example.budget.controller;


import com.example.budget.domain.dto.*;
import com.example.budget.domain.entity.User;
import com.example.budget.security.jwt.JwtUtils;
import com.example.budget.security.services.UserAuthenticateService;
import com.example.budget.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class UserRestController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final GlobalAdminService globalAdminService;
    private final JwtService jwtService;
    private final FamilyService familyService;
    private final UserDataService userDataService;
    private final UserAuthenticateService userAuthenticateService;
    private final ManyService manyService;
    private final ApplyFamilyAdminLimitationsService applyAdminFamilyRestrictions;
    private final LimitationsService limitationsService;

    public UserRestController(AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtUtils jwtUtils, GlobalAdminService globalAdminService, JwtService jwtService, FamilyService familyService, UserDataService userDataService, UserAuthenticateService userAuthenticateService, ManyService manyService, ApplyFamilyAdminLimitationsService applyAdminFamilyRestrictions, LimitationsService limitationsService) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.globalAdminService = globalAdminService;
        this.jwtService = jwtService;
        this.familyService = familyService;
        this.userDataService = userDataService;
        this.userAuthenticateService = userAuthenticateService;
        this.manyService = manyService;
        this.applyAdminFamilyRestrictions = applyAdminFamilyRestrictions;
        this.limitationsService = limitationsService;
    }


    @PostMapping("/signupGlobalAdmin")
    public ResponseEntity<JwtResponse> registerUser(@Validated @ModelAttribute CreateNewUserFamilyDto createNewUserFamilyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Optional<User> optionalUser = Optional.ofNullable(globalAdminService.saveGlobalAdmin(createNewUserFamilyDto));
            if (optionalUser.isPresent()) {
                JwtResponse jwtResponse = jwtService.getUser(createNewUserFamilyDto);
                return ResponseEntity.ok(jwtResponse);
            }
        }
        return ResponseEntity.notFound().build();

    }

    @PostMapping("/creNewFamily")
    public ResponseEntity<GetClientMessage> createNewFamily(@Validated @ModelAttribute CreateNewFamilyDto createNewFamilyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            GetClientMessage getClientMessage = familyService.saveNewFamily(createNewFamilyDto);
            return ResponseEntity.ok(getClientMessage);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/createNewUserFamily")
    public ResponseEntity<JwtResponse> createNewUserFamily(@Validated @ModelAttribute CreateNewUserFamilyDto createNewUserFamilyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Optional<User> optionalUser = Optional.ofNullable(userDataService.createNewUserInFamily(createNewUserFamilyDto));
            if (optionalUser.isPresent()) {
                JwtResponse jwtResponse = jwtService.getUser(createNewUserFamilyDto);
                return ResponseEntity.ok(jwtResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @ModelAttribute LoginDto loginDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Optional<JwtResponse> optionalJwt = Optional.ofNullable(userAuthenticateService.userAuthenticate(loginDto));
            if (optionalJwt.isPresent()) {
                JwtResponse jwtResponse = optionalJwt.get();
                return ResponseEntity.ok(jwtResponse);
            }
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/findAllUsers")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')or hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<List<User>> findAllFamilyMember() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> userList = userDataService.findAllFamilyMember(principal.getUsername());
        if (userList.size() > 0) {
            return ResponseEntity.ok(userList);
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/findFamilyAccount")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')or hasRole('GLOBAL_ADMIN') ")
    public String findCountFamily() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return manyService.familyAccount(principal.getUsername());
    }

    @PutMapping("/putMany")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')or hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> putMoney(@Validated @ModelAttribute ManyDto manyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(manyService.putManyOnAccount(manyDto, principal.getUsername()));
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/withdrawMoney")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')or hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> withdrawMoney(@Validated @ModelAttribute ManyDto manyDto, BindingResult bindingResult) throws ParseException, ParseException {
        if (!bindingResult.hasErrors()) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(manyService.withdrawMoneyOnAccount(manyDto, principal.getUsername()));
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/applyLimitations")

    public ResponseEntity<GetClientMessage> applyLimitations(@Validated @ModelAttribute FamilyAdminLimitationsDto familyAdminLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(applyAdminFamilyRestrictions.applyAdminFamilyRestrictions(familyAdminLimitationsDto, principal.getUsername()));
        }
        return ResponseEntity.notFound().build();
    }



    @GetMapping("/allFamilyList")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<List<AllFamilyDto>> allFamilyList() {
        List<AllFamilyDto> allFamilyDtoList = userDataService.allFamily();
        if (allFamilyDtoList.size() > 0) {
            return ResponseEntity.ok(allFamilyDtoList);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/findByFamilyLogin")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<List<User>> findFamilyByFamilyLogin(@Validated @ModelAttribute FamilyLoginDto familyLoginDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            List<User> userList = userDataService.findAllUsersFamily(familyLoginDto);
            if (userList.size() > 0) {
                return ResponseEntity.ok(userList);
            }
        }
        return ResponseEntity.notFound().build();
    }



    @PostMapping("/familyAccount")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<ManyDto> getFamilyAccount(@Validated @ModelAttribute FamilyLoginDto familyLoginDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(familyService.findFamilyAccount(familyLoginDto));
        }
        return ResponseEntity.notFound().build();
    }



    @PutMapping("/putManyGlobalAdmin")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> putManyForFamilyAccount(@Validated @ModelAttribute PutManyDto putManyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(manyService.putManyForFamilyAccount(putManyDto));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdrawManyGlobalAdmin")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> withdrawManyFromFamilyAccount(@Validated @ModelAttribute PutManyDto putManyDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(manyService.withdrawManyFromUserAccount(putManyDto));
        }
        return ResponseEntity.notFound().build();
    }



    @PostMapping("/globAdmLimAllFamily")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> imposeRestrictionsOnFamilyGlobalAdmin(@Validated @ModelAttribute GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(limitationsService.imposeRestrictionsOnFamilyGlobalAdmin(globalAdminFamilyLimitationsDto));
        }
        return ResponseEntity.notFound().build();
    }



    @PostMapping("/globAdmPersLim")
    @PreAuthorize("hasRole('GLOBAL_ADMIN') ")
    public ResponseEntity<GetClientMessage> personalLimitationGlobalAdmin(@Validated @ModelAttribute FamilyAdminLimitationsDto familyAdminLimitationsDto, BindingResult bindingResult) throws ParseException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(limitationsService.personalLimitationsGlobalAdmin(familyAdminLimitationsDto));
        }
        return ResponseEntity.notFound().build();
    }
}

