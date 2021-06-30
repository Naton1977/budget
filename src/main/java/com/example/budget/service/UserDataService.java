package com.example.budget.service;

import com.example.budget.domain.dto.AllFamilyDto;
import com.example.budget.domain.dto.CreateNewUserFamilyDto;
import com.example.budget.domain.dto.FamilyLoginDto;
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
import java.util.*;


@Transactional
@Service
public class UserDataService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserDataService(UserRepository userRepository, FamilyRepository familyRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }




    public List<User> findAllFamilyMember(String login) {
        User user = userRepository.findFamilyMemberByMemberLogin(login);
        List<User> userList = userRepository.findFamilyMemberByFamily_FamilyId(user.getFamily().getFamilyId());
        LinkedList<User> userListDto = new LinkedList<>();
        for (User member : userList) {
            User user1 = new User();
            user1.setFirstName(member.getFirstName());
            user1.setLastName(member.getLastName());
            user1.setPatronymic(member.getPatronymic());
            user1.setRoles(member.getRoles());
            user1.setMemberLogin(member.getMemberLogin());
            userListDto.add(user1);
        }

        boolean rolePresent = false;
        User user1 = null;
        Collections.sort(userListDto);
        ListIterator<User> iter = (ListIterator<User>) userListDto.iterator();
        while (iter.hasNext()) {
            user1 = iter.next();
            Set<Role> roles = user1.getRoles();
            for (Role rol : roles) {
                if (rol.getName().equals(ERole.ROLE_ADMIN)) {
                    iter.remove();
                    rolePresent = true;
                    break;
                }
            }
            if(rolePresent){
                break;
            }
        }
        userListDto.add(0, user1);


        return userListDto;
    }

    public List<AllFamilyDto> allFamily() {
        List<AllFamilyDto> allFamilyDtos = new ArrayList<>();
        List<Family> familyList = familyRepository.findAll();
        for (Family fam : familyList) {
            AllFamilyDto allFamilyDto = new AllFamilyDto();
            allFamilyDto.setFamilyLogin(fam.getFamilyLogin());
            allFamilyDto.setFamilyAccount(fam.getFamilyAccount());
            List<User> userList = userRepository.findFamilyMemberByFamily_FamilyId(fam.getFamilyId());
            for (User memb : userList) {
                Set<Role> roles = memb.getRoles();
                for (Role rol : roles) {
                    if (rol.getName().equals(ERole.ROLE_ADMIN)) {
                        allFamilyDto.setFirstName(memb.getFirstName());
                        allFamilyDto.setLastName(memb.getLastName());
                        allFamilyDto.setPatronymic(memb.getPatronymic());
                    }
                }
            }
            allFamilyDtos.add(allFamilyDto);
        }
        return allFamilyDtos;
    }


    public List<User> findAllFamilyMemberByFamilyId(int familyId) {
        List<User> userList = userRepository.findFamilyMemberByFamily_FamilyId(familyId);
        List<User> userListDto = new ArrayList<>();
        for (User member : userList) {
            User user1 = new User();
            user1.setFirstName(member.getFirstName());
            user1.setLastName(member.getLastName());
            user1.setPatronymic(member.getPatronymic());
            user1.setRoles(member.getRoles());
            user1.setMemberLogin(member.getMemberLogin());
            userListDto.add(user1);
        }
        return userListDto;
    }

    public User createNewUserInFamily(CreateNewUserFamilyDto createNewUserFamilyDto) {
        String userPassword = passwordEncoder.encode(createNewUserFamilyDto.getMemberPassword());
        Family family = familyRepository.findFamilyByFamilyLogin(createNewUserFamilyDto.getFamilyLogin());
        User user = new User();
        user.setFirstName(createNewUserFamilyDto.getFirstName());
        user.setLastName(createNewUserFamilyDto.getLastName());
        user.setPatronymic(createNewUserFamilyDto.getPatronymic());
        user.setMemberLogin(createNewUserFamilyDto.getMemberLogin());
        user.setMemberPassword(userPassword);
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        roleRepository.saveAndFlush(role);
        Set<Role> roles = new HashSet<>();;
        roles.add(role);
        user.setRoles(roles);
        user.setFamily(family);
        userRepository.save(user);
        return user;
    }

    public List<User> findAllUsersFamily(FamilyLoginDto familyLoginDto) {
        Family family = familyRepository.findFamilyByFamilyLogin(familyLoginDto.getFamilyLogin());
        return findAllFamilyMemberByFamilyId(family.getFamilyId());
    }

    public User findUserByLogin(String login) {
        return userRepository.findFamilyMemberByMemberLogin(login);
    }


}
