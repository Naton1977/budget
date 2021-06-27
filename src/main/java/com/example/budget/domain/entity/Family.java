package com.example.budget.domain.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name = "family")
@Data
@Entity
@ToString(exclude = "familyMemberSet")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private int familyId;

    @Column(name = "family_login", unique = true)
    private String familyLogin;

    @Column(name = "family_password")
    private String familyPassword;


    @Column(name = "family_account")
    private int familyAccount;

    @OneToMany(mappedBy = "family")
    private Set<FamilyMember> familyMemberSet = new HashSet<>();


    public void familyMemberSetAdd(FamilyMember familyMember){
        familyMemberSet.add(familyMember);
    }


}
