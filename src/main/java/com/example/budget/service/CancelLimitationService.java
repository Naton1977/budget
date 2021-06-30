package com.example.budget.service;


import com.example.budget.domain.entity.*;
import com.example.budget.repository.FamilyAdminLimitationsRepository;
import com.example.budget.repository.GlobalAdminAllFamilyLimitationsRepository;
import com.example.budget.repository.GlobalAdminPersonalLimitationsRepository;
import com.example.budget.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class CancelLimitationService {

    private final UserRepository userRepository;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;
    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;

    public CancelLimitationService(UserRepository userRepository, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository, GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, FamilyAdminLimitationsRepository familyAdminLimitationsRepository) {
        this.userRepository = userRepository;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
    }


    public void cancelPersonalLimitation(Family family) {
        List<User> userList = userRepository.findFamilyMemberByFamily_FamilyId(family.getFamilyId());
        for (User user : userList) {
            Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(user.getFamilyMemberId()));
            if (globalAdminPersonalLimitationsOptional.isPresent()) {
                GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();
                globalAdminPersonalLimitations.setMaximumOneTimeWithdrawalPerDay(0);
                globalAdminPersonalLimitations.setMaximumWithdrawalPerDay(0);
                globalAdminPersonalLimitations.setDateStartLimitation(null);
                globalAdminPersonalLimitations.setDateEndLimitation(null);
                globalAdminPersonalLimitationsRepository.saveAndFlush(globalAdminPersonalLimitations);
            }
        }
    }

    public void cancelAllFamilyLimitation(User user) {
        Family family = user.getFamily();
        Optional<GlobalAdminAllFamilyLimitations> familyOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
        if (familyOptional.isPresent()) {
            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = familyOptional.get();
            globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(0);
            globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(0);
            globalAdminAllFamilyLimitations.setDateStartLimitation(null);
            globalAdminAllFamilyLimitations.setDateEndLimitation(null);
            globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
        }
    }

    public void cancelFamilyAdminLimitations(Family family) {
        Optional<List<User>> optionalFamilyMemberList = Optional.ofNullable(userRepository.findFamilyMemberByFamily_FamilyId(family.getFamilyId()));
        if (optionalFamilyMemberList.isPresent()) {
            List<User> userList = optionalFamilyMemberList.get();
            for (User fam : userList) {
                Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(fam.getFamilyMemberId()));
                if (familyAdminLimitationsOptional.isPresent()) {
                    FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsRepository.findByFamilyMemberId(fam.getFamilyMemberId());
                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(0);
                    familyAdminLimitations.setMaximumWithdrawalPerDay(0);
                    familyAdminLimitations.setDateStartLimitation(null);
                    familyAdminLimitations.setDateEndLimitation(null);
                    familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                }
            }
        }
    }

    public void cancelFamilyAdminPersonalLimitations(User user) {
        Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(user.getFamilyMemberId()));
        if (familyAdminLimitationsOptional.isPresent()) {
            FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsOptional.get();
            familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(0);
            familyAdminLimitations.setMaximumWithdrawalPerDay(0);
            familyAdminLimitations.setDateStartLimitation(null);
            familyAdminLimitations.setDateEndLimitation(null);
            familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
        }
    }
}
