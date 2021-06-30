package com.example.budget.service;


import com.example.budget.domain.dto.FamilyAdminLimitationsDto;
import com.example.budget.domain.dto.GetClientMessage;
import com.example.budget.domain.dto.GlobalAdminFamilyLimitationsDto;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.entity.Family;
import com.example.budget.domain.entity.GlobalAdminAllFamilyLimitations;
import com.example.budget.domain.entity.GlobalAdminPersonalLimitations;
import com.example.budget.domain.entity.User;
import com.example.budget.repository.GlobalAdminAllFamilyLimitationsRepository;
import com.example.budget.repository.GlobalAdminPersonalLimitationsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Date;


@Transactional
@Service
public class CreateGlobalAdminLimitations {
    private final CalendarDateService calendarDateService;
    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;

    public CreateGlobalAdminLimitations(CalendarDateService calendarDateService, GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository) {
        this.calendarDateService = calendarDateService;
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
    }


    public GetClientMessage createGlobalAdminAllFamilyLimitations(Family family, GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto, GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        globalAdminAllFamilyLimitations.setFamilyId(family.getFamilyId());

        int maxOneDay = calendarDateService.parseData(globalAdminFamilyLimitationsDto.getMaximumOneTimeWithdrawalPerDay());

        globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);

        int maxWithdr = calendarDateService.parseData(globalAdminFamilyLimitationsDto.getMaximumWithdrawalPerDay());

        globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(maxWithdr);

        Date start = calendarDateService.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateStartLimitation());

        Date end = calendarDateService.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateEndLimitation());


        globalAdminAllFamilyLimitations.setDateStartLimitation(start);
        globalAdminAllFamilyLimitations.setDateEndLimitation(end);
        globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
        getClientMessage.setMessage("операция успешна");
        return getClientMessage;
    }

        public GetClientMessage createGlobalAdminPersonalLimitations(GlobalAdminPersonalLimitations globalAdminPersonalLimitations, FamilyAdminLimitationsDto familyAdminLimitationsDto, User user) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject = calendarDateService.chekDate(familyAdminLimitationsDto);
        if (messageTransferObject.isChekResult()) {
            globalAdminPersonalLimitations.setFamilyId(user.getFamily().getFamilyId());
            globalAdminPersonalLimitations.setMemberId(user.getFamilyMemberId());

            int maxOneDay = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            globalAdminPersonalLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);

            int maxPerDay = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

            globalAdminPersonalLimitations.setMaximumWithdrawalPerDay(maxPerDay);

            Date start = calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation());

            globalAdminPersonalLimitations.setDateStartLimitation(start);

            Date end = calendarDateService.parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation());

            globalAdminPersonalLimitations.setDateEndLimitation(end);

            globalAdminPersonalLimitationsRepository.saveAndFlush(globalAdminPersonalLimitations);
            getClientMessage.setMessage("Операция прошла успешно");
            return getClientMessage;
        }
        getClientMessage.setMessage("Дата начала ограничения не должна быть больше даты конца ограничения");
        return getClientMessage;
    }
}
