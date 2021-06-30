package com.example.budget.service;

import com.example.budget.domain.dto.GetClientMessage;
import com.example.budget.domain.dto.ManyDto;
import com.example.budget.domain.dto.MessageTransferObject;
import com.example.budget.domain.dto.PutManyDto;
import com.example.budget.domain.entity.Family;
import com.example.budget.domain.entity.User;
import com.example.budget.domain.entity.UserTransactions;
import com.example.budget.repository.FamilyRepository;
import com.example.budget.repository.UserRepository;
import com.example.budget.repository.UserTransactionsRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Date;


@Transactional
@Service
public class ManyService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final UserTransactionsRepository userTransactionsRepository;
    private final CalendarDateService calendarDateService;
    private final ChekGlobalAdminLimitations chekGlobalAdminLimitations;
    private final ChekFamilyAdminLimitations chekFamilyAdminLimitations;

    public ManyService(UserRepository userRepository, FamilyRepository familyRepository, UserTransactionsRepository userTransactionsRepository, CalendarDateService calendarDateService, ChekGlobalAdminLimitations chekGlobalAdminLimitations, ChekFamilyAdminLimitations chekFamilyAdminLimitations) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.userTransactionsRepository = userTransactionsRepository;
        this.calendarDateService = calendarDateService;
        this.chekGlobalAdminLimitations = chekGlobalAdminLimitations;
        this.chekFamilyAdminLimitations = chekFamilyAdminLimitations;
    }




    public GetClientMessage putManyOnAccount(ManyDto manyDto, String login) {
        User user = userRepository.findFamilyMemberByMemberLogin(login);
        Family family = user.getFamily();
        int many = calendarDateService.parseData(manyDto.getManyCount());
        int account = family.getFamilyAccount();
        account += many;
        family.setFamilyAccount(account);
        Family family1 = familyRepository.saveAndFlush(family);
        UserTransactions userTransactions = new UserTransactions();
        userTransactions.setFamilyId(family1.getFamilyId());
        userTransactions.setFamilyMemberId(user.getFamilyMemberId());
        userTransactions.setTypeOfTransaction("put");
        userTransactions.setSumTransaction(many);
        Date date = new Date();
        userTransactions.setDateTransaction(date);
        userTransactionsRepository.saveAndFlush(userTransactions);
        Family sendFamily = new Family();
        sendFamily.setFamilyId(family1.getFamilyId());
        sendFamily.setFamilyLogin(family.getFamilyLogin());
        sendFamily.setFamilyAccount(family1.getFamilyAccount());
        GetClientMessage getClientMessage = new GetClientMessage();
        getClientMessage.setMessage("Деньги успешно добавленны на счет");
        getClientMessage.setFamily(sendFamily);
        return getClientMessage;
    }

    public GetClientMessage withdrawMoneyOnAccount(ManyDto manyDto, String login) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject;
        messageTransferObject = chekGlobalAdminLimitations.chekWithdrawManyLimitationsGlobalAdminAllFAmily(manyDto, login);
        if (messageTransferObject.isChekResult()) {
            messageTransferObject = chekGlobalAdminLimitations.chekWithdrawManyLimitationsGlobalAdminPersonal(manyDto, login);
            if (messageTransferObject.isChekResult()) {
                messageTransferObject = chekFamilyAdminLimitations.chekWithdrawMoneyOnAccountLimitationFamilyAdmin(manyDto, login);
                if (messageTransferObject.isChekResult()) {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    User user = userRepository.findFamilyMemberByMemberLogin(principal.getUsername());
                    Family family = user.getFamily();
                    int familyAccount = family.getFamilyAccount();
                    int withdrawSum = calendarDateService.parseData(manyDto.getManyCount());
                    if (familyAccount < withdrawSum) {
                        getClientMessage.setMessage("На вашем счету не достаточно денег");
                        return getClientMessage;
                    } else {
                        familyAccount -= withdrawSum;
                        family.setFamilyAccount(familyAccount);
                        Family family1 = familyRepository.saveAndFlush(family);
                        Family sendFamily = new Family();
                        sendFamily.setFamilyId(family1.getFamilyId());
                        sendFamily.setFamilyLogin(family.getFamilyLogin());
                        sendFamily.setFamilyAccount(family1.getFamilyAccount());
                        getClientMessage.setMessage("Деньги успешно сняты со счета");
                        UserTransactions userTransactions = new UserTransactions();
                        userTransactions.setFamilyId(family1.getFamilyId());
                        userTransactions.setFamilyMemberId(user.getFamilyMemberId());
                        userTransactions.setTypeOfTransaction("delete");
                        userTransactions.setSumTransaction(withdrawSum);
                        Date date = new Date();
                        userTransactions.setDateTransaction(date);
                        userTransactionsRepository.saveAndFlush(userTransactions);
                        getClientMessage.setFamily(sendFamily);
                        return getClientMessage;
                    }
                }
                getClientMessage.setMessage(messageTransferObject.getMessage());
                return getClientMessage;
            }
            getClientMessage.setMessage(messageTransferObject.getMessage());
            return getClientMessage;
        }
        getClientMessage.setMessage(messageTransferObject.getMessage());
        return getClientMessage;
    }

    public String familyAccount(String login) {
        User user = userRepository.findFamilyMemberByMemberLogin(login);
        Family family = user.getFamily();
        return Integer.toString(family.getFamilyAccount());
    }

    public GetClientMessage withdrawManyFromFamilyAccount(PutManyDto putManyDto) {
        GetClientMessage getClientMessage = new GetClientMessage();
        Family family = familyRepository.findFamilyByFamilyLogin(putManyDto.getFamilyLogin());
        int familyAccount = family.getFamilyAccount();
        int withdrawMany = Integer.parseInt(putManyDto.getMany());
        if (familyAccount >= withdrawMany) {
            familyAccount -= withdrawMany;
            family.setFamilyAccount(familyAccount);
            familyRepository.saveAndFlush(family);
            getClientMessage.setMessage("Операция успешна");
            Family family1 = new Family();
            family1.setFamilyAccount(family.getFamilyAccount());
            getClientMessage.setFamily(family1);
            return getClientMessage;
        }
        getClientMessage.setMessage("на счету не достаточно денег");
        Family family1 = new Family();
        family1.setFamilyAccount(family.getFamilyAccount());
        getClientMessage.setFamily(family1);
        return getClientMessage;
    }

    public GetClientMessage putManyForFamilyAccount(PutManyDto putManyDto) {
        Family family = familyRepository.findFamilyByFamilyLogin(putManyDto.getFamilyLogin());
        int familyAccount = family.getFamilyAccount();
        familyAccount += Integer.parseInt(putManyDto.getMany());
        family.setFamilyAccount(familyAccount);
        familyRepository.saveAndFlush(family);
        GetClientMessage getClientMessage = new GetClientMessage();
        getClientMessage.setMessage("Операция успешна");
        Family family1 = new Family();
        family1.setFamilyAccount(family.getFamilyAccount());
        getClientMessage.setFamily(family1);
        return getClientMessage;
    }

    public GetClientMessage withdrawManyFromUserAccount(PutManyDto putManyDto) {
        GetClientMessage getClientMessage = new GetClientMessage();
        Family family = familyRepository.findFamilyByFamilyLogin(putManyDto.getFamilyLogin());
        int familyAccount = family.getFamilyAccount();
        int withdrawMany = Integer.parseInt(putManyDto.getMany());
        if (familyAccount >= withdrawMany) {
            familyAccount -= withdrawMany;
            family.setFamilyAccount(familyAccount);
            familyRepository.saveAndFlush(family);
            getClientMessage.setMessage("Операция успешна");
            Family family1 = new Family();
            family1.setFamilyAccount(family.getFamilyAccount());
            getClientMessage.setFamily(family1);
            return getClientMessage;
        }
        getClientMessage.setMessage("на счету не достаточно денег");
        Family family1 = new Family();
        family1.setFamilyAccount(family.getFamilyAccount());
        getClientMessage.setFamily(family1);
        return getClientMessage;
    }
}
