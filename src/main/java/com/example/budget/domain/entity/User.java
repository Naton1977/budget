package com.example.budget.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@Data
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_login"),

        })
@Entity
@ToString(exclude = "family")
public class User implements Comparable<User>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_member_id")
    private int familyMemberId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;


    @NotBlank
    @Size(max = 20)
    @Column(name = "user_login")
    private String memberLogin;


    @NotBlank
    @Size(max = 120)
    @Column(name = "member_password")
    private String memberPassword;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "family_member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "familyId")
    private Family family;

    @Override
    public int compareTo(User o) {
        return lastName.compareTo(o.lastName);
    }
}
