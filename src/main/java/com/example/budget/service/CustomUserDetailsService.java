package com.example.budget.service;

import com.example.budget.domain.entity.FamilyMember;
import com.example.budget.repository.FamilyMemberRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final FamilyMemberRepository familyMemberRepository;


    public CustomUserDetailsService(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<FamilyMember> familyMemberOptional = Optional.ofNullable(familyMemberRepository.findFamilyMemberByMemberLogin(userName));
        if (familyMemberOptional.isEmpty())
            throw new UsernameNotFoundException("Not found by " + userName);
            FamilyMember familyMember = familyMemberOptional.get();
            return new User(familyMember.getMemberLogin(), familyMember.getMemberPassword(),
                    AuthorityUtils.createAuthorityList(familyMember.getMemberRole()));

    }
}
