package com.example.budget.domain.entity;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name = "family")
@Entity
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
    private Set<User> userSet = new HashSet<>();


    public void familyMemberSetAdd(User user){
        userSet.add(user);
    }


    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public String getFamilyLogin() {
        return familyLogin;
    }

    public void setFamilyLogin(String familyLogin) {
        this.familyLogin = familyLogin;
    }

    public String getFamilyPassword() {
        return familyPassword;
    }

    public void setFamilyPassword(String familyPassword) {
        this.familyPassword = familyPassword;
    }

    public int getFamilyAccount() {
        return familyAccount;
    }

    public void setFamilyAccount(int familyAccount) {
        this.familyAccount = familyAccount;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    @Override
    public String toString() {
        return "Family{" +
                "familyId=" + familyId +
                ", familyLogin='" + familyLogin + '\'' +
                ", familyPassword='" + familyPassword + '\'' +
                ", familyAccount=" + familyAccount +
                '}';
    }
}
