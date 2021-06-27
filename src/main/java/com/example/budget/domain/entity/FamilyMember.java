package com.example.budget.domain.entity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
@Data
@Table(name = "family_member")
@Entity
@ToString(exclude = "family")
public class FamilyMember implements Comparable<FamilyMember>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int family_member_id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "member_login", unique = true)
    private String memberLogin;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_role")
    private String memberRole;


    @ManyToOne
    @JoinColumn(name = "familyId")
    private Family family;

    @Override
    public int compareTo(FamilyMember o) {
        return lastName.compareTo(o.lastName);
    }
}
