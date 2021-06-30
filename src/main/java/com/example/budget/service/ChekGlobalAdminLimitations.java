package com.example.budget.service;

import com.example.budget.domain.dto.FamilyAdminLimitationsDto;
import com.example.budget.domain.dto.ManyDto;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.entity.*;
import com.example.budget.repository.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class ChekGlobalAdminLimitations {

    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final CalendarDateService calendarDateService;
    private final UserRepository userRepository;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;
    private final UserTransactionsRepository userTransactionsRepository;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;

    public ChekGlobalAdminLimitations(GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, CalendarDateService calendarDateService, UserRepository userRepository, FamilyAdminLimitationsRepository familyAdminLimitationsRepository, UserTransactionsRepository userTransactionsRepository, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository) {
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.calendarDateService = calendarDateService;
        this.userRepository = userRepository;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
        this.userTransactionsRepository = userTransactionsRepository;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
    }


    public MessageTransferObject chekGlobalAdminFamilyLimitations(Family family, FamilyAdminLimitationsDto familyAdminLimitationsDto) {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        Optional<GlobalAdminAllFamilyLimitations> optionalGlobalAdminAllFamilyLimitations = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
        if (optionalGlobalAdminAllFamilyLimitations.isPresent()) {
            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = optionalGlobalAdminAllFamilyLimitations.get();

            int oneTime = globalAdminAllFamilyLimitations.getMaximumOneTimeWithdrawalPerDay();
            int perDay = globalAdminAllFamilyLimitations.getMaximumWithdrawalPerDay();

            long startTime = calendarDateService.parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateStartLimitation());
            long endTime = calendarDateService.parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateEndLimitation());

            Date date = new Date();
            long today = date.getTime();

            int oneTimeFamilyAdmin = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            int maxWithdrawFamilyAdmin = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

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




    public MessageTransferObject chekGlobalAdminPersonalLimitations(int familyMemberId, FamilyAdminLimitationsDto familyAdminLimitationsDto) {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(familyMemberId));
        if (globalAdminPersonalLimitationsOptional.isPresent()) {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();

            int oneTime = globalAdminPersonalLimitations.getMaximumOneTimeWithdrawalPerDay();
            int perDay = globalAdminPersonalLimitations.getMaximumWithdrawalPerDay();

            long startTime =calendarDateService.parseCalendarDateToLong(globalAdminPersonalLimitations.getDateStartLimitation());
            long endTime = calendarDateService.parseCalendarDateToLong(globalAdminPersonalLimitations.getDateEndLimitation());

            Date date = new Date();
            long today = date.getTime();

            int oneTimeFamilyAdmin = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay());
            int maxWithdrawFamilyAdmin = calendarDateService.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay());

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

    public MessageTransferObject chekWithdrawManyLimitationsGlobalAdminAllFAmily(ManyDto manyDto, String login) throws ParseException {
        Optional<User> familyMemberOptional = Optional.ofNullable(userRepository.findFamilyMemberByMemberLogin(login));
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        if (familyMemberOptional.isPresent()) {
            User user = familyMemberOptional.get();

            Optional<Family> familyOptional = Optional.ofNullable(user.getFamily());
            if (familyOptional.isPresent()) {
                Family family = familyOptional.get();
                Optional<GlobalAdminAllFamilyLimitations> globalAdminAllFamilyLimitationsOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));

                if (globalAdminAllFamilyLimitationsOptional.isPresent()) {
                    GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = globalAdminAllFamilyLimitationsOptional.get();

                    int oneTime = globalAdminAllFamilyLimitations.getMaximumOneTimeWithdrawalPerDay();
                    int perDay = globalAdminAllFamilyLimitations.getMaximumWithdrawalPerDay();

                    long startTime = calendarDateService.parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateStartLimitation());
                    long endTime = calendarDateService.parseCalendarDateToLong(globalAdminAllFamilyLimitations.getDateEndLimitation());

                    Date date = new Date();
                    long today = date.getTime();

                    int userPerDay = 0;

                    int many = calendarDateService.parseData(manyDto.getManyCount());

                    if (many > oneTime && startTime == 0 && endTime == 0  && oneTime != 0|| many > oneTime && today < endTime && oneTime != 0 || many > oneTime && startTime < today && endTime > today && oneTime != 0) {
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

                    if ((many + userPerDay) > perDay && startTime == 0 && endTime == 0 && perDay != 0 || (many + userPerDay) > perDay && today < endTime && perDay !=0 || (many + userPerDay) > perDay && startTime < today && endTime > today && perDay != 0) {
                        messageTransferObject.setMessage("Вы привысили сумму снятия за день");
                        messageTransferObject.setChekResult(false);
                        return messageTransferObject;
                    }
                }
            }
        }
        return messageTransferObject;
    }

    public MessageTransferObject chekWithdrawManyLimitationsGlobalAdminPersonal(ManyDto manyDto, String login) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        Optional<User> familyMemberOptional = Optional.ofNullable(userRepository.findFamilyMemberByMemberLogin(login));
        if (familyMemberOptional.isPresent()) {
            User user = familyMemberOptional.get();
            Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(user.getFamilyMemberId()));
            if (globalAdminPersonalLimitationsOptional.isPresent()) {
                GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();
                int oneTime = globalAdminPersonalLimitations.getMaximumOneTimeWithdrawalPerDay();
                int perDay = globalAdminPersonalLimitations.getMaximumWithdrawalPerDay();

                long startTime = calendarDateService.parseCalendarDateToLong(globalAdminPersonalLimitations.getDateStartLimitation());
                long endTime = calendarDateService.parseCalendarDateToLong(globalAdminPersonalLimitations.getDateEndLimitation());

                Date date = new Date();
                long today = date.getTime();

                int userPerDay = 0;

                int many = calendarDateService.parseData(manyDto.getManyCount());

                if (many > oneTime && startTime == 0 && endTime == 0 || many > oneTime && today < endTime || many > oneTime && startTime < today && endTime > today) {
                    messageTransferObject.setMessage("Вы привысили сумму единоразового снятия");
                    messageTransferObject.setChekResult(false);
                    return messageTransferObject;
                }

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Optional<List<UserTransactions>> userTransactionsOptional = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndFamilyMemberIdAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp), user.getFamilyMemberId(), "delete"));
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
