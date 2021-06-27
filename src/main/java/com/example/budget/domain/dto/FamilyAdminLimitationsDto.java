package com.example.budget.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


@Data
public class FamilyAdminLimitationsDto {

    private String familyMemberLogin;

    private String maximumOneTimeWithdrawalPerDay;

    private String maximumWithdrawalPerDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy",timezone = "Europe/Kiev")
    private String dateStartLimitation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy",timezone = "Europe/Kiev")
    private String dateEndLimitation;


}
