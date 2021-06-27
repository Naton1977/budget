package com.example.budget.domain.entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "global_admin_personal_limitations")
@Data
public class GlobalAdminPersonalLimitations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "global_admin_personal_limitations_id")
    private int globalAdminPersonalLimitationsId;

    @Column(name = "family_id")
    private int familyId;

    @Column(name = "member_id")
    private int memberId;

    @Column(name = "maximum_one_time_withdrawal_per_day")
    private int maximumOneTimeWithdrawalPerDay;


    @Column(name = "maximum_withdrawal_per_day")
    private int maximumWithdrawalPerDay;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_start_limitation")
    private Date dateStartLimitation;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_end_limitation")
    private Date dateEndLimitation;

}
