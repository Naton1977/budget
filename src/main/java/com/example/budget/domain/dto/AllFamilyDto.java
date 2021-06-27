package com.example.budget.domain.dto;


import lombok.Data;

@Data
public class AllFamilyDto {

    private String familyLogin;

    private int familyAccount;

    private String firstName;

    private String lastName;

    private String patronymic;
}
