package com.example.budget.service;

import com.example.budget.domain.dto.*;
import com.example.budget.domain.entity.ERole;
import com.example.budget.domain.entity.Family;
import com.example.budget.domain.entity.Role;
import com.example.budget.domain.entity.User;
import com.example.budget.repository.FamilyRepository;
import com.example.budget.repository.RoleRepository;
import com.example.budget.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class FamilyService {

    private final PasswordEncoder passwordEncoder;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public FamilyService(PasswordEncoder passwordEncoder, FamilyRepository familyRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public GetClientMessage saveNewFamily(CreateNewFamilyDto createNewFamilyDto) {
        GetClientMessage getClientMessage = new GetClientMessage();
        Family family = new Family();
        family.setFamilyLogin(createNewFamilyDto.getFamilyLogin());
        family.setFamilyPassword(createNewFamilyDto.getFamilyPassword());
        Family family1 = familyRepository.saveAndFlush(family);
        String adminPassword = passwordEncoder.encode(createNewFamilyDto.getMemberPassword());
        User user = new User();
        user.setFirstName(createNewFamilyDto.getFirstName());
        user.setLastName(createNewFamilyDto.getLastName());
        user.setPatronymic(createNewFamilyDto.getPatronymic());
        user.setMemberLogin(createNewFamilyDto.getMemberLogin());
        user.setMemberPassword(adminPassword);
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        roleRepository.saveAndFlush(role);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setFamily(family);
        userRepository.save(user);
        getClientMessage.setMessage("Семья и администратор семьи созданы успешно !!!");
        return getClientMessage;
    }


    public ManyDto findFamilyAccount(FamilyLoginDto familyLoginDto) {
        Family family = familyRepository.findFamilyByFamilyLogin(familyLoginDto.getFamilyLogin());
        ManyDto manyDto = new ManyDto();
        manyDto.setManyCount(Integer.toString(family.getFamilyAccount()));
        return manyDto;
    }
}
