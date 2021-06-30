package com.example.budget.service;


import com.example.budget.domain.dto.ManyDto;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.entity.FamilyAdminLimitations;
import com.example.budget.domain.entity.User;
import com.example.budget.domain.entity.UserTransactions;
import com.example.budget.repository.FamilyAdminLimitationsRepository;
import com.example.budget.repository.UserRepository;
import com.example.budget.repository.UserTransactionsRepository;
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
public class ChekFamilyAdminLimitations {

    private final UserRepository userRepository;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;
    private final CalendarDateService calendarDateService;
    private final UserTransactionsRepository userTransactionsRepository;

    public ChekFamilyAdminLimitations(UserRepository userRepository, FamilyAdminLimitationsRepository familyAdminLimitationsRepository, CalendarDateService calendarDateService, UserTransactionsRepository userTransactionsRepository) {
        this.userRepository = userRepository;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
        this.calendarDateService = calendarDateService;
        this.userTransactionsRepository = userTransactionsRepository;
    }


    public MessageTransferObject chekWithdrawMoneyOnAccountLimitationFamilyAdmin(ManyDto manyDto, String login) throws ParseException {
        MessageTransferObject messageTransferObject = new MessageTransferObject();
        User user = userRepository.findFamilyMemberByMemberLogin(login);
        Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(user.getFamilyMemberId()));
        if (familyAdminLimitationsOptional.isPresent()) {
            FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsOptional.get();
            int maximumOneTimeWithdrawalPerDay = familyAdminLimitations.getMaximumOneTimeWithdrawalPerDay();
            int maximumWithdrawalPerDay = familyAdminLimitations.getMaximumWithdrawalPerDay();
            Date dateStart = familyAdminLimitations.getDateStartLimitation();
            Date dateEnd = familyAdminLimitations.getDateEndLimitation();
            int many = calendarDateService.parseData(manyDto.getManyCount());

            Date date = new Date();

            long millsStart = calendarDateService.parseCalendarDateToLong(dateStart);
            long millsEnd = calendarDateService.parseCalendarDateToLong(dateEnd);
            long milsNow = calendarDateService.parseCalendarDateToLong(date);


            if (millsStart == 0 && millsEnd == 0 && maximumOneTimeWithdrawalPerDay != 0 || millsStart < milsNow && millsEnd > milsNow && maximumOneTimeWithdrawalPerDay != 0 || millsEnd > milsNow && maximumOneTimeWithdrawalPerDay != 0) {
                if (many > maximumOneTimeWithdrawalPerDay) {
                    messageTransferObject.setMessage("Вы привысили сумму единоразового снятия !!!");
                    messageTransferObject.setChekResult(false);
                    return messageTransferObject;
                }
            }
            int sumTransactions = 0;

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            if (millsStart == 0 && millsEnd == 0 && maximumWithdrawalPerDay != 0 || millsStart < milsNow && millsEnd > milsNow && maximumWithdrawalPerDay != 0 || millsEnd > milsNow && maximumWithdrawalPerDay != 0) {
                Optional<List<UserTransactions>> optionalUserTransactionsList = Optional.ofNullable(userTransactionsRepository.findAllByDateTransactionAndFamilyMemberIdAndTypeOfTransaction(new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp), user.getFamilyMemberId(), "delete"));
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
}
