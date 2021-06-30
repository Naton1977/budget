package com.example.budget.service;


import com.example.budget.domain.dto.FamilyAdminLimitationsDto;
import com.example.budget.domain.dto.GetClientMessage;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.entity.Family;
import com.example.budget.domain.entity.FamilyAdminLimitations;
import com.example.budget.domain.entity.User;
import com.example.budget.repository.FamilyAdminLimitationsRepository;
import com.example.budget.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ApplyFamilyAdminLimitationsService {

    private final UserRepository userRepository;
    private final ChekGlobalAdminLimitations chekGlobalAdminLimitations;
    private final CalendarDateService calendarDateService;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;

    public ApplyFamilyAdminLimitationsService(UserRepository userRepository, ChekGlobalAdminLimitations chekGlobalAdminLimitations, CalendarDateService calendarDateService, FamilyAdminLimitationsRepository familyAdminLimitationsRepository) {
        this.userRepository = userRepository;
        this.chekGlobalAdminLimitations = chekGlobalAdminLimitations;
        this.calendarDateService = calendarDateService;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
    }


    public GetClientMessage applyAdminFamilyRestrictions(FamilyAdminLimitationsDto familyAdminLimitationsDto, String login) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        User adminFamily = userRepository.findFamilyMemberByMemberLogin(login);
        Family family = adminFamily.getFamily();
        MessageTransferObject messageTransferObject;

        messageTransferObject = chekGlobalAdminLimitations.chekGlobalAdminFamilyLimitations(family, familyAdminLimitationsDto);
        if (messageTransferObject.isChekResult()) {

            messageTransferObject = calendarDateService.chekDate(familyAdminLimitationsDto);
            if (messageTransferObject.isChekResult()) {

                List<User> userList = userRepository.findFamilyMemberByFamily_FamilyId(adminFamily.getFamily().getFamilyId());
                for (User faml : userList) {
                    if (faml.getMemberLogin().equals(familyAdminLimitationsDto.getFamilyMemberLogin()) || familyAdminLimitationsDto.getFamilyMemberLogin().equals("На всех")) {
                        messageTransferObject = chekGlobalAdminLimitations.chekGlobalAdminPersonalLimitations(faml.getFamilyMemberId(), familyAdminLimitationsDto);
                        if (messageTransferObject.isChekResult()) {
                            Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(faml.getFamilyMemberId()));
                            if (familyAdminLimitationsOptional.isEmpty()) {
                                FamilyAdminLimitations familyAdminLimitations = new FamilyAdminLimitations();
                                familyAdminLimitations.setFamilyMemberId(faml.getFamilyMemberId());
                                familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(calendarDateService.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay()));
                                familyAdminLimitations.setMaximumWithdrawalPerDay(calendarDateService.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay()));
                                familyAdminLimitations.setDateStartLimitation(calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation()));
                                familyAdminLimitations.setDateEndLimitation(calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation()));

                                familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                            } else {
                                FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsOptional.get();
                                if (familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay().equals("")) {
                                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(0);
                                } else {
                                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(calendarDateService.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay()));
                                }

                                if (familyAdminLimitationsDto.getMaximumWithdrawalPerDay().equals("")) {
                                    familyAdminLimitations.setMaximumWithdrawalPerDay(0);
                                } else {
                                    familyAdminLimitations.setMaximumWithdrawalPerDay(calendarDateService.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay()));
                                }

                                if (!familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay().equals("") || !familyAdminLimitationsDto.getMaximumWithdrawalPerDay().equals("")) {

                                    if (familyAdminLimitationsDto.getDateStartLimitation().equals("")) {
                                        familyAdminLimitations.setDateStartLimitation(null);
                                    }
                                    familyAdminLimitations.setDateStartLimitation(calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation()));


                                    if (familyAdminLimitationsDto.getDateEndLimitation().equals("")) {
                                        familyAdminLimitations.setDateEndLimitation(null);
                                    }
                                    familyAdminLimitations.setDateEndLimitation(calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation()));

                                    familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                                }
                            }

                        } else {
                            getClientMessage.setMessage(messageTransferObject.getMessage());
                            return getClientMessage;
                        }
                    }
                }
                getClientMessage.setMessage("Данные обновленны !!!");
                return getClientMessage;
            }
        }
        getClientMessage.setMessage(messageTransferObject.getMessage());
        return getClientMessage;
    }
}
