package com.example.budget.domain.dto;

import lombok.Data;

@Data
public class CreateNewUserFamilyDto {

    private String firstName;

    private String lastName;

    private String patronymic;

    private String memberLogin;

    private String memberPassword;

    private String confirmMemberPassword;

    private String familyLogin;

    private String familyPassword;

    private String globalAdminLogin;

    private String globalAdminPassword;

    private String confirmPassword;


}
