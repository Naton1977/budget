package com.example.budget.methods;

import com.example.budget.domain.dto.*;
import com.example.budget.domain.entity.*;
import com.example.budget.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional
@Service
public class Methods {

    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;
    private final UserTransactionsRepository userTransactionsRepository;

    public Methods(GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository, FamilyMemberRepository familyMemberRepository, FamilyAdminLimitationsRepository familyAdminLimitationsRepository, UserTransactionsRepository userTransactionsRepository) {
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
        this.userTransactionsRepository = userTransactionsRepository;
    }


    public GetClientMessage createGlobalAdminAllFamilyLimitations(Family family, GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto, GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        globalAdminAllFamilyLimitations.setFamilyId(family.getFamilyId());

        int maxOneDay = 0;
        try {
            maxOneDay = Integer.parseInt(globalAdminFamilyLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
        } catch (Exception ignored) {

        }
        globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);

        int maxWithdr = 0;
        try{
            maxWithdr = Integer.parseInt(globalAdminFamilyLimitationsDto.getMaximumWithdrawalPerDay());
        } catch (Exception ignored){

        }
        globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(maxWithdr);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

        Date start = null;

        try{
            start = formatDate.parse(globalAdminFamilyLimitationsDto.getDateStartLimitation());
        } catch (Exception ignored){

        }

        Date end = null;

        try{
            end = formatDate.parse(globalAdminFamilyLimitationsDto.getDateEndLimitation());
        } catch (Exception ignored){

        }

        globalAdminAllFamilyLimitations.setDateStartLimitation(start);
        globalAdminAllFamilyLimitations.setDateEndLimitation(end);
        globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
        getClientMessage.setMessage("операция успешна");
        return getClientMessage;
    }

    public GetClientMessage createGlobalAdminPersonalLimitations(GlobalAdminPersonalLimitations globalAdminPersonalLimitations, FamilyAdminLimitationsDto familyAdminLimitationsDto, FamilyMember familyMember) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        globalAdminPersonalLimitations.setFamilyId(familyMember.getFamily().getFamilyId());
        globalAdminPersonalLimitations.setMemberId(familyMember.getFamily_member_id());
        globalAdminPersonalLimitations.setMaximumOneTimeWithdrawalPerDay(Integer.parseInt(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay()));
        globalAdminPersonalLimitations.setMaximumWithdrawalPerDay(Integer.parseInt(familyAdminLimitationsDto.getMaximumWithdrawalPerDay()));
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        globalAdminPersonalLimitations.setDateStartLimitation(formatDate.parse(familyAdminLimitationsDto.getDateStartLimitation()));
        globalAdminPersonalLimitations.setDateEndLimitation(formatDate.parse(familyAdminLimitationsDto.getDateEndLimitation()));
        globalAdminPersonalLimitationsRepository.saveAndFlush(globalAdminPersonalLimitations);
        getClientMessage.setMessage("Операция прошла успешно");
        return getClientMessage;
    }

    public void cancelPersonalLimitation(Family family) {
        List<FamilyMember> familyMemberList = familyMemberRepository.findFamilyMemberByFamily_FamilyId(family.getFamilyId());
        for (FamilyMember familyMember : familyMemberList) {
            Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(familyMember.getFamily_member_id()));
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

    public void cancelAllFamilyLimitation(FamilyMember familyMember) {
        Family family = familyMember.getFamily();
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

    public MessageTransferObject chekWithdrawMoneyOnAccount(ManyDto manyDto) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername());
        Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(familyMember.getFamily_member_id()));
        if (familyAdminLimitationsOptional.isPresent()) {
            FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsOptional.get();
            int maximumOneTimeWithdrawalPerDay = familyAdminLimitations.getMaximumOneTimeWithdrawalPerDay();
            int maximumWithdrawalPerDay = familyAdminLimitations.getMaximumWithdrawalPerDay();
            Date dateStart = familyAdminLimitations.getDateStartLimitation();
            Date dateEnd = familyAdminLimitations.getDateEndLimitation();
            int many = 0;
            try {
                many = Integer.parseInt(manyDto.getManyCount());
            } catch (Exception ignored) {

            }
            Date date = new Date();

            long millsStart = 0;
            long millsEnd = 0;

            try {
                millsStart = dateStart.getTime();
            } catch (Exception ignored) {

            }
            try {
                millsEnd = dateEnd.getTime();
            } catch (Exception ignored) {

            }

            long milsNow = date.getTime();


            if (millsStart == 0 && millsEnd == 0 || millsStart < milsNow && millsEnd > milsNow || millsEnd > milsNow) {

                if (maximumOneTimeWithdrawalPerDay != 0 && maximumOneTimeWithdrawalPerDay < many) {
                    messageTransferObject.setMessage("Вы привысили сумму единоразового снятия !!!");
                    messageTransferObject.setChekResult(false);
                }
                int sumTransactions = 0;

                Optional<List<UserTransactions>> optionalUserTransactionsList = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse("2021-06-27"), "delete"));
                if (optionalUserTransactionsList.isPresent()) {
                    List<UserTransactions> userTransactionsList = optionalUserTransactionsList.get();
                    for (UserTransactions tr : userTransactionsList) {
                        sumTransactions += tr.getSumTransaction();
                    }
                }
                if (sumTransactions + many > maximumWithdrawalPerDay) {
                    messageTransferObject.setMessage("Вы привысили сумму снятия за день !!!");
                    messageTransferObject.setChekResult(false);
                }
            }
        }
        return messageTransferObject;
    }

    public MessageTransferObject chekDate(FamilyAdminLimitationsDto familyAdminLimitationsDto) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

        Date start = null;
        try {
            start = formatDate.parse(familyAdminLimitationsDto.getDateStartLimitation());
        } catch (Exception ignored) {

        }
        Date end = null;
        try {
            end = formatDate.parse(familyAdminLimitationsDto.getDateEndLimitation());
        } catch (Exception ignored) {

        }


        long millsStart = 0;
        long millsEnd = 0;

        try {
            millsStart = start.getTime();
        } catch (Exception ignored) {

        }
        try {
            millsEnd = end.getTime();
        } catch (Exception ignored) {

        }
        if (millsEnd < millsStart) {
            messageTransferObject.setMessage("Дата начала ограничения не должна быть меньше даты конца");
            messageTransferObject.setChekResult(false);
            return messageTransferObject;
        }

        messageTransferObject.setChekResult(true);
        return messageTransferObject;
    }

    public void cancelFamilyAdminLimitations(Family family) {
        Optional<List<FamilyMember>> optionalFamilyMemberList = Optional.ofNullable(familyMemberRepository.findFamilyMemberByFamily_FamilyId(family.getFamilyId()));
        if (optionalFamilyMemberList.isPresent()) {
            List<FamilyMember> familyMemberList = optionalFamilyMemberList.get();
            for (FamilyMember fam : familyMemberList) {
                Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(fam.getFamily_member_id()));
                if (familyAdminLimitationsOptional.isPresent()) {
                    FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsRepository.findByFamilyMemberId(fam.getFamily_member_id());
                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(0);
                    familyAdminLimitations.setMaximumWithdrawalPerDay(0);
                    familyAdminLimitations.setDateStartLimitation(null);
                    familyAdminLimitations.setDateEndLimitation(null);
                    familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                }
            }
        }
    }

    public void cancelFamilyAdminPersonalLimitations(FamilyMember familyMember) {
        Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(familyMember.getFamily_member_id()));
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
