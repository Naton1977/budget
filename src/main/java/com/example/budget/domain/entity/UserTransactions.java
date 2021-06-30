package com.example.budget.domain.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "user_transactions")
@Entity
@Data
public class UserTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactions_id")
    private int transactionsId;

    @Column(name = "family_id")
    private int familyId;

    @Column(name = "family_member_id")
    private int familyMemberId;

    @Column(name = "type_of_transaction")
    private String typeOfTransaction;

    @Column(name = "sum_transaction")
    private int sumTransaction;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_transaction")
    private Date dateTransaction;

}
