package com.example.budget.security.services;

import com.example.budget.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private int familyMemberId;

    private String username;

    @JsonIgnore
    private String memberPassword;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(int familyMemberId, String memberLogin, String memberPassword,
                           Collection<? extends GrantedAuthority> authorities) {
        this.familyMemberId = familyMemberId;
        this.username = memberLogin;
        this.memberPassword = memberPassword;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getFamilyMemberId(),
                user.getMemberLogin(),
                user.getMemberPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public int getFamilyMemberId() {
        return familyMemberId;
    }



    @Override
    public String getPassword() {
        return memberPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(familyMemberId, user.familyMemberId);
    }
}