package com.example.budget.service;


import com.example.budget.domain.dto.CreateNewUserFamilyDto;
import com.example.budget.domain.entity.ERole;
import com.example.budget.domain.entity.Role;
import com.example.budget.domain.entity.User;
import com.example.budget.repository.RoleRepository;
import com.example.budget.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Transactional
@Service
public class GlobalAdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public GlobalAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    public boolean globalAdminPresent() {
        List<User> userList = userRepository.findAll();
        return userList.size() == 0;
    }

    public User saveGlobalAdmin(CreateNewUserFamilyDto createNewUserFamilyDto) {
        if(globalAdminPresent()){
            String globalAdminPassword = passwordEncoder.encode(createNewUserFamilyDto.getGlobalAdminPassword());
            User user = new User();
            user.setMemberLogin(createNewUserFamilyDto.getGlobalAdminLogin());
            user.setMemberPassword(globalAdminPassword);
            Role role = new Role();
            role.setName(ERole.ROLE_GLOBAL_ADMIN);
            Role role1 = roleRepository.saveAndFlush(role);
            Set<Role> roles = new HashSet<>();
            roles.add(role1);
            user.setRoles(roles);
            userRepository.save(user);
            return user;
        }
        return null;
    }
}
