package com.example.budget.domain.entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Table(name = "family_admin_limitations")
@Entity
@Data
public class FamilyAdminLimitations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limitations_id")
    private int limitationsId;

    @Column(name = "family_member_id")
    private int familyMemberId;

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
