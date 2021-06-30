package com.example.budget.service;

import com.example.budget.domain.dto.FamilyAdminLimitationsDto;
import com.example.budget.domain.dto.GetClientMessage;
import com.example.budget.domain.dto.GlobalAdminFamilyLimitationsDto;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.entity.Family;
import com.example.budget.domain.entity.GlobalAdminAllFamilyLimitations;
import com.example.budget.domain.entity.GlobalAdminPersonalLimitations;
import com.example.budget.domain.entity.User;
import com.example.budget.repository.FamilyRepository;
import com.example.budget.repository.GlobalAdminAllFamilyLimitationsRepository;
import com.example.budget.repository.GlobalAdminPersonalLimitationsRepository;
import com.example.budget.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;



@Transactional
@Service
public class LimitationsService {

    private final CalendarDateService calendarDateService;
    private final FamilyRepository familyRepository;
    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final CreateGlobalAdminLimitations createGlobalAdminAllFamilyLimitations;
    private final CancelLimitationService cancelLimitationService;
    private final UserRepository userRepository;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;

    public LimitationsService(CalendarDateService calendarDateService, FamilyRepository familyRepository, GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, CreateGlobalAdminLimitations createGlobalAdminAllFamilyLimitations, CancelLimitationService cancelLimitationService, UserRepository userRepository, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository) {
        this.calendarDateService = calendarDateService;
        this.familyRepository = familyRepository;
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.createGlobalAdminAllFamilyLimitations = createGlobalAdminAllFamilyLimitations;
        this.cancelLimitationService = cancelLimitationService;
        this.userRepository = userRepository;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
    }


    public GetClientMessage imposeRestrictionsOnFamilyGlobalAdmin(GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto) throws ParseException {
        String familyLogin = globalAdminFamilyLimitationsDto.getFamilyLogin();
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject = calendarDateService.chekDate(globalAdminFamilyLimitationsDto);
        if (messageTransferObject.isChekResult()) {
            if (familyLogin.equals("All")) {
                List<Family> familyList = familyRepository.findAll();
                List<GlobalAdminAllFamilyLimitations> globalAdminAllFamilyLimitationsList = globalAdminAllFamilyLimitationsRepository.findAll();
                if (globalAdminAllFamilyLimitationsList.size() == 0) {
                    for (Family family : familyList) {
                        GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                        getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                        cancelLimitationService.cancelPersonalLimitation(family);
                        cancelLimitationService.cancelFamilyAdminLimitations(family);
                    }
                } else {
                    for (Family family : familyList) {
                        Optional<GlobalAdminAllFamilyLimitations> globalAdminLimitationsOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
                        if (globalAdminLimitationsOptional.isPresent()) {
                            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = globalAdminLimitationsOptional.get();
                            int maxOneDay = calendarDateService.parseData(globalAdminFamilyLimitationsDto.getMaximumOneTimeWithdrawalPerDay());

                            globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);
                            int maxPreDay = calendarDateService.parseData(globalAdminFamilyLimitationsDto.getMaximumWithdrawalPerDay());

                            globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(maxPreDay);

                            Date dateStart = calendarDateService.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateStartLimitation());

                            globalAdminAllFamilyLimitations.setDateStartLimitation(dateStart);
                            Date dateEnd = calendarDateService.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateEndLimitation());

                            globalAdminAllFamilyLimitations.setDateEndLimitation(dateEnd);

                            globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
                            getClientMessage.setMessage("операция успешна");
                            cancelLimitationService.cancelPersonalLimitation(family);
                            cancelLimitationService.cancelFamilyAdminLimitations(family);
                        } else {
                            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                            getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                            cancelLimitationService.cancelPersonalLimitation(family);
                            cancelLimitationService.cancelFamilyAdminLimitations(family);
                        }
                    }
                    return getClientMessage;
                }
                return getClientMessage;
            } else {
                Family family = familyRepository.findFamilyByFamilyLogin(globalAdminFamilyLimitationsDto.getFamilyLogin());
                Optional<GlobalAdminAllFamilyLimitations> globalAdminAllFamilyLimitationsOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
                if (globalAdminAllFamilyLimitationsOptional.isPresent()) {
                    GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = globalAdminAllFamilyLimitationsOptional.get();
                    getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                    cancelLimitationService.cancelPersonalLimitation(family);
                    cancelLimitationService.cancelFamilyAdminLimitations(family);
                    return getClientMessage;
                } else {
                    GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                    getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                    cancelLimitationService.cancelPersonalLimitation(family);
                    cancelLimitationService.cancelFamilyAdminLimitations(family);
                    return getClientMessage;
                }
            }

        }
        getClientMessage.setMessage(messageTransferObject.getMessage());
        return getClientMessage;
    }


    public GetClientMessage personalLimitationsGlobalAdmin(FamilyAdminLimitationsDto familyAdminLimitationsDto) throws
            ParseException {
        GetClientMessage getClientMessage;
        User user = userRepository.findFamilyMemberByMemberLogin(familyAdminLimitationsDto.getFamilyMemberLogin());
        Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(user.getFamilyMemberId()));
        if (globalAdminPersonalLimitationsOptional.isPresent()) {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();
            getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminPersonalLimitations(globalAdminPersonalLimitations, familyAdminLimitationsDto, user);
            cancelLimitationService.cancelAllFamilyLimitation(user);
            cancelLimitationService.cancelFamilyAdminPersonalLimitations(user);
        } else {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = new GlobalAdminPersonalLimitations();
            getClientMessage = createGlobalAdminAllFamilyLimitations.createGlobalAdminPersonalLimitations(globalAdminPersonalLimitations, familyAdminLimitationsDto, user);
            cancelLimitationService.cancelAllFamilyLimitation(user);
            cancelLimitationService.cancelFamilyAdminPersonalLimitations(user);
        }
        return getClientMessage;
    }

}
