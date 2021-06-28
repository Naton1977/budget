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

        int maxOneDay = parseData(globalAdminFamilyLimitationsDto.getMaximumOneTimeWithdrawalPerDay());

        globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);

        int maxWithdr = parseData(globalAdminFamilyLimitationsDto.getMaximumWithdrawalPerDay());

        globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(maxWithdr);

        Date start = parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateStartLimitation());

        Date end = parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateEndLimitation());


        globalAdminAllFamilyLimitations.setDateStartLimitation(start);
        globalAdminAllFamilyLimitations.setDateEndLimitation(end);
        globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
        getClientMessage.setMessage("операция успешна");
        return getClientMessage;
    }

    public GetClientMessage createGlobalAdminPersonalLimitations(GlobalAdminPersonalLimitations globalAdminPersonalLimitations, FamilyAdminLimitationsDto familyAdminLimitationsDto, FamilyMember familyMember) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject = chekDate(familyAdminLimitationsDto);
        if (messageTransferObject.isChekResult()) {
            globalAdminPersonalLimitations.setFamilyId(familyMember.getFamily().getFamilyId());
            globalAdminPersonalLimitations.setMemberId(familyMember.getFamily_member_id());

            int maxOneDay = parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            globalAdminPersonalLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);

            int maxPerDay = parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

            globalAdminPersonalLimitations.setMaximumWithdrawalPerDay(maxPerDay);

            Date start = parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation());

            globalAdminPersonalLimitations.setDateStartLimitation(start);

            Date end = parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation());

            globalAdminPersonalLimitations.setDateEndLimitation(end);

            globalAdminPersonalLimitationsRepository.saveAndFlush(globalAdminPersonalLimitations);
            getClientMessage.setMessage("Операция прошла успешно");
            return getClientMessage;

        }
        getClientMessage.setMessage("Дата начала ограничения не должна быть больше даты конца граничения");
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

    public MessageTransferObject chekWithdrawMoneyOnAccountLimitationFamilyAdmin(ManyDto manyDto) throws ParseException {
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
            int many = parseData(manyDto.getManyCount());

            Date date = new Date();

            long millsStart = parseCalendarDateToLong(dateStart);
            long millsEnd = parseCalendarDateToLong(dateEnd);
            long milsNow = parseCalendarDateToLong(date);


            if (millsStart == 0 && millsEnd == 0 || millsStart < milsNow && millsEnd > milsNow || millsEnd > milsNow) {

                if (maximumOneTimeWithdrawalPerDay != 0 && maximumOneTimeWithdrawalPerDay < many) {
                    messageTransferObject.setMessage("Вы привысили сумму единоразового снятия !!!");
                    messageTransferObject.setChekResult(false);
                }
                int sumTransactions = 0;

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                Optional<List<UserTransactions>> optionalUserTransactionsList = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp), "delete"));
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

        Date start = parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation());

        Date end = parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation());

        long millsStart = parseCalendarDateToLong(start);
        long millsEnd = parseCalendarDateToLong(end);

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


    public MessageTransferObject chekDate(GlobalAdminFamilyLimitationsDto globalAdminFamilyLimitationsDto) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();

        Date start = parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateStartLimitation());

        Date end = parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateEndLimitation());

        long millsStart = parseCalendarDateToLong(start);
        long millsEnd = parseCalendarDateToLong(end);

        if (millsEnd < millsStart) {
            messageTransferObject.setMessage("Дата начала ограничения не должна быть меньше даты конца");
            messageTransferObject.setChekResult(false);
            return messageTransferObject;
        }

        messageTransferObject.setChekResult(true);
        return messageTransferObject;
    }


    public MessageTransferObject chekGlobalAdminFamilyLimitations(Family family, FamilyAdminLimitationsDto familyAdminLimitationsDto) {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        Optional<GlobalAdminAllFamilyLimitations> optionalGlobalAdminAllFamilyLimitations = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
        if (optionalGlobalAdminAllFamilyLimitations.isPresent()) {
            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = optionalGlobalAdminAllFamilyLimitations.get();

            int oneTime = globalAdminAllFamilyLimitations.getMaximumOneTimeWithdrawalPerDay();
            int perDay = globalAdminAllFamilyLimitations.getMaximumWithdrawalPerDay();

            long startTime = parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateStartLimitation());
            long endTime = parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateEndLimitation());

            Date date = new Date();
            long today = date.getTime();

            int oneTimeFamilyAdmin = parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            int maxWithdrawFamilyAdmin = parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

            if (oneTime > 0 && oneTimeFamilyAdmin > 0 && startTime < today && endTime > today || startTime == 0 && endTime == 0 && oneTime > 0 && oneTimeFamilyAdmin > 0 || endTime > today && oneTime > 0 && oneTimeFamilyAdmin > 0) {
                messageTransferObject.setMessage("Вы не можите наложить такие ограничения, такие ограничения уже наложены глобальным администратором !!!");
                messageTransferObject.setChekResult(false);
                return messageTransferObject;
            }
            if (perDay > 0 && maxWithdrawFamilyAdmin > 0 && startTime < today && endTime > today || startTime == 0 && endTime == 0 && perDay > 0 && maxWithdrawFamilyAdmin > 0 || endTime > today && perDay > 0 && maxWithdrawFamilyAdmin > 0) {
                messageTransferObject.setMessage("Вы не можите наложить такие ограничения, такие ограничения уже наложены глобальным администратором !!!");
                messageTransferObject.setChekResult(false);
                return messageTransferObject;
            }
        }
        return messageTransferObject;
    }

    public int parseData(Object data) {
        int parseData = 0;
        try {
            parseData = Integer.parseInt((String) data);
            return parseData;
        } catch (Exception ignored) {

        }
        return parseData;
    }


    public long parseCalendarDateToLong(Date calendarDate) {
        long parseDate = 0;
        try {
            parseDate = calendarDate.getTime();
            return parseDate;
        } catch (Exception ignored) {

        }
        return parseDate;
    }

    public Date parseStringToCalendarDate(String date) {
        Date date1 = null;
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = formatDate.parse(date);
            return date1;
        } catch (Exception ignored) {

        }
        return date1;
    }

    public MessageTransferObject chekGlobalAdminPersonalLimitations(int familyMemberId, FamilyAdminLimitationsDto familyAdminLimitationsDto) {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(familyMemberId));
        if (globalAdminPersonalLimitationsOptional.isPresent()) {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();

            int oneTime = globalAdminPersonalLimitations.getMaximumOneTimeWithdrawalPerDay();
            int perDay = globalAdminPersonalLimitations.getMaximumWithdrawalPerDay();

            long startTime = parseCalendarDateToLong(globalAdminPersonalLimitations.getDateStartLimitation());
            long endTime = parseCalendarDateToLong(globalAdminPersonalLimitations.getDateEndLimitation());

            Date date = new Date();
            long today = date.getTime();

            int oneTimeFamilyAdmin = parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            int maxWithdrawFamilyAdmin = parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

            if (oneTime > 0 && oneTimeFamilyAdmin > 0 && startTime < today && endTime > today || startTime == 0 && endTime == 0 && oneTime > 0 && oneTimeFamilyAdmin > 0 || endTime > today && oneTime > 0 && oneTimeFamilyAdmin > 0) {
                messageTransferObject.setMessage("Вы не можите наложить такие ограничения, такие ограничения уже наложены глобальным администратором !!!");
                messageTransferObject.setChekResult(false);
                return messageTransferObject;
            }
            if (perDay > 0 && maxWithdrawFamilyAdmin > 0 && startTime < today && endTime > today || startTime == 0 && endTime == 0 && perDay > 0 && maxWithdrawFamilyAdmin > 0 || endTime > today && perDay > 0 && maxWithdrawFamilyAdmin > 0) {
                messageTransferObject.setMessage("Вы не можите наложить такие ограничения, такие ограничения уже наложены глобальным администратором !!!");
                messageTransferObject.setChekResult(false);
                return messageTransferObject;
            }

        }
        return messageTransferObject;
    }


    public MessageTransferObject chekWithdrawManyLimitationsGlobalAdminAllFAmily(ManyDto manyDto) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<FamilyMember> familyMemberOptional = Optional.ofNullable(familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername()));
        if (familyMemberOptional.isPresent()) {
            FamilyMember familyMember = familyMemberOptional.get();

            Optional<Family> familyOptional = Optional.ofNullable(familyMember.getFamily());
            if (familyOptional.isPresent()) {
                Family family = familyOptional.get();
                Optional<GlobalAdminAllFamilyLimitations> globalAdminAllFamilyLimitationsOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));

                if (globalAdminAllFamilyLimitationsOptional.isPresent()) {
                    GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = globalAdminAllFamilyLimitationsOptional.get();

                    int oneTime = globalAdminAllFamilyLimitations.getMaximumOneTimeWithdrawalPerDay();
                    int perDay = globalAdminAllFamilyLimitations.getMaximumWithdrawalPerDay();

                    long startTime = parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateStartLimitation());
                    long endTime = parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateEndLimitation());

                    Date date = new Date();
                    long today = date.getTime();

                    int userPerDay = 0;

                    int many = parseData(manyDto.getManyCount());

                    if (many > oneTime && startTime == 0 && endTime == 0 || many > oneTime && today < endTime || many > oneTime && startTime < today && endTime > today) {
                        messageTransferObject.setMessage("Вы привысили сумму единоразового снятия");
                        messageTransferObject.setChekResult(false);
                        return messageTransferObject;
                    }

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    Optional<List<UserTransactions>> userTransactionsOptional = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndFamilyIdAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp), family.getFamilyId(), "delete"));
                    if (userTransactionsOptional.isPresent()) {
                        List<UserTransactions> userTransactionsList = userTransactionsOptional.get();
                        for (UserTransactions ustr : userTransactionsList) {
                            userPerDay += ustr.getSumTransaction();
                        }
                    }

                    if ((many + userPerDay) > perDay && startTime == 0 && endTime == 0 || (many + userPerDay) > perDay && today < endTime || (many + userPerDay) > perDay && startTime < today && endTime > today) {
                        messageTransferObject.setMessage("Вы привысили сумму снятия за день");
                        messageTransferObject.setChekResult(false);
                        return messageTransferObject;
                    }
                }
            }
        }
        return messageTransferObject;
    }

    public MessageTransferObject chekWithdrawManyLimitationsGlobalAdminPersonal(ManyDto manyDto) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<FamilyMember> familyMemberOptional = Optional.ofNullable(familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername()));
        if (familyMemberOptional.isPresent()) {
            FamilyMember familyMember = familyMemberOptional.get();
            Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(familyMember.getFamily_member_id()));
            if (globalAdminPersonalLimitationsOptional.isPresent()) {
                GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();
                int oneTime = globalAdminPersonalLimitations.getMaximumOneTimeWithdrawalPerDay();
                int perDay = globalAdminPersonalLimitations.getMaximumWithdrawalPerDay();

                long startTime = parseCalendarDateToLong(globalAdminPersonalLimitations.getDateStartLimitation());
                long endTime = parseCalendarDateToLong(globalAdminPersonalLimitations.getDateEndLimitation());

                Date date = new Date();
                long today = date.getTime();

                int userPerDay = 0;

                int many = parseData(manyDto.getManyCount());

                if (many > oneTime && startTime == 0 && endTime == 0 || many > oneTime && today < endTime || many > oneTime && startTime < today && endTime > today) {
                    messageTransferObject.setMessage("Вы привысили сумму единоразового снятия");
                    messageTransferObject.setChekResult(false);
                    return messageTransferObject;
                }

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Optional<List<UserTransactions>> userTransactionsOptional = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndFamilyMemberIdAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp), familyMember.getFamily_member_id(), "delete"));
                if (userTransactionsOptional.isPresent()) {
                    List<UserTransactions> userTransactionsList = userTransactionsOptional.get();
                    for (UserTransactions ustr : userTransactionsList) {
                        userPerDay += ustr.getSumTransaction();
                    }
                }

                if ((many + userPerDay) > perDay && startTime == 0 && endTime == 0 || (many + userPerDay) > perDay && today < endTime || (many + userPerDay) > perDay && startTime < today && endTime > today) {
                    messageTransferObject.setMessage("Вы привысили сумму снятия за день");
                    messageTransferObject.setChekResult(false);
                    return messageTransferObject;
                }
            }
        }
        return messageTransferObject;
    }

}
