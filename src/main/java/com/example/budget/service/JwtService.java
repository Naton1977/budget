package com.example.budget.service;

import com.example.budget.domain.dto.CreateNewUserFamilyDto;
import com.example.budget.domain.dto.JwtResponse;
import com.example.budget.security.jwt.JwtUtils;
import com.example.budget.security.services.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public JwtService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    public JwtResponse getUser(CreateNewUserFamilyDto createNewUserFamilyDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(createNewUserFamilyDto.getGlobalAdminLogin(), createNewUserFamilyDto.getGlobalAdminPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return (new JwtResponse(jwt,
                userDetails.getFamilyMemberId(),
                userDetails.getUsername(),
                roles));
    }
}
