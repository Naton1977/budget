package com.example.budget.domain.entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Table(name = "global_admin_all_family_limitations")
@Entity
@Data
public class GlobalAdminAllFamilyLimitations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "global_admin_limitations_id")
    private int globalAdminLimitationsId;

    @Column(name = "family_id")
    private int familyId;

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
