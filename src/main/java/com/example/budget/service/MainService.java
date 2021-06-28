package com.example.budget.service;

import com.example.budget.domain.dto.*;
import com.example.budget.domain.entity.*;
import com.example.budget.methods.Methods;
import com.example.budget.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class MainService {


    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final FamilyAdminLimitationsRepository familyAdminLimitationsRepository;
    private final UserTransactionsRepository userTransactionsRepository;
    private final GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository;
    private final Methods methods;
    private final GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository;

    public MainService(FamilyMemberRepository familyMemberRepository, FamilyRepository familyRepository, FamilyAdminLimitationsRepository familyAdminLimitationsRepository, UserTransactionsRepository userTransactionsRepository, GlobalAdminAllFamilyLimitationsRepository globalAdminAllFamilyLimitationsRepository, Methods methods, GlobalAdminPersonalLimitationsRepository globalAdminPersonalLimitationsRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyRepository = familyRepository;
        this.familyAdminLimitationsRepository = familyAdminLimitationsRepository;
        this.userTransactionsRepository = userTransactionsRepository;
        this.globalAdminAllFamilyLimitationsRepository = globalAdminAllFamilyLimitationsRepository;
        this.methods = methods;
        this.globalAdminPersonalLimitationsRepository = globalAdminPersonalLimitationsRepository;
    }

    @Autowired
    PasswordEncoder passwordEncoder;


    public String globalAdminPresent() {
        List<FamilyMember> familyMemberList = familyMemberRepository.findAll();
        if (familyMemberList.size() > 0) {
            return "redirect:/home";
        }
        return "GlobalAdminRegisterPage";
    }


    public String saveGlobalAdmin(CreateNewUserFamilyDto createNewUserFamilyDto) {
        String globalAdminPassword = passwordEncoder.encode(createNewUserFamilyDto.getGlobalAdminPassword());
        FamilyMember familyMember = new FamilyMember();
        familyMember.setMemberLogin(createNewUserFamilyDto.getGlobalAdminLogin());
        familyMember.setMemberPassword(globalAdminPassword);
        familyMember.setMemberRole("ROLE_GLOBAL_ADMIN");
        familyMemberRepository.save(familyMember);
        return "redirect:/home";
    }


    public void saveNewFamily(CreateNewFamilyDto createNewFamilyDto) {
        String adminPassword = passwordEncoder.encode(createNewFamilyDto.getMemberPassword());
        Family family = new Family();
        family.setFamilyLogin(createNewFamilyDto.getFamilyLogin());
        family.setFamilyPassword(createNewFamilyDto.getFamilyPassword());
        Family family1 = familyRepository.save(family);
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFirstName(createNewFamilyDto.getFirstName());
        familyMember.setLastName(createNewFamilyDto.getLastName());
        familyMember.setPatronymic(createNewFamilyDto.getPatronymic());
        familyMember.setMemberLogin(createNewFamilyDto.getMemberLogin());
        familyMember.setMemberPassword(adminPassword);
        familyMember.setMemberRole("ROLE_ADMIN");
        familyMember.setFamily(family1);
        familyMemberRepository.save(familyMember);
    }


    public void createNewUserFamily(CreateNewUserFamilyDto createNewUserFamilyDto) {
        String userPassword = passwordEncoder.encode(createNewUserFamilyDto.getMemberPassword());
        Family family = familyRepository.findFamilyByFamilyLogin(createNewUserFamilyDto.getFamilyLogin());
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFirstName(createNewUserFamilyDto.getFirstName());
        familyMember.setLastName(createNewUserFamilyDto.getLastName());
        familyMember.setPatronymic(createNewUserFamilyDto.getPatronymic());
        familyMember.setMemberLogin(createNewUserFamilyDto.getMemberLogin());
        familyMember.setMemberPassword(userPassword);
        familyMember.setMemberRole("ROLE_USER");
        familyMember.setFamily(family);
        familyMemberRepository.save(familyMember);
    }


    public List<FamilyMember> findAllFamilyMember() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername());
        List<FamilyMember> familyMemberList = familyMemberRepository.findFamilyMemberByFamily_FamilyId(familyMember.getFamily().getFamilyId());
        LinkedList<FamilyMember> familyMemberListDto = new LinkedList<>();
        for (FamilyMember member : familyMemberList) {
            FamilyMember familyMember1 = new FamilyMember();
            familyMember1.setFirstName(member.getFirstName());
            familyMember1.setLastName(member.getLastName());
            familyMember1.setPatronymic(member.getPatronymic());
            familyMember1.setMemberRole(member.getMemberRole());
            familyMember1.setMemberLogin(member.getMemberLogin());
            familyMemberListDto.add(familyMember1);
        }
        FamilyMember familyMember1 = null;
        Collections.sort(familyMemberListDto);
        ListIterator<FamilyMember> iter = (ListIterator<FamilyMember>) familyMemberListDto.iterator();
        while (iter.hasNext()) {
            familyMember1 = iter.next();
            if (familyMember1.getMemberRole().equals("ROLE_ADMIN")) {
                iter.remove();
                break;
            }
        }
        familyMemberListDto.add(0, familyMember1);


        return familyMemberListDto;
    }


    public FamilyMember findUserByLogin(String login) {
        return familyMemberRepository.findFamilyMemberByMemberLogin(login);
    }

    public GetClientMessage putManyOnAccount(ManyDto manyDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername());
        Family family = familyMember.getFamily();
        int many = methods.parseData(manyDto.getManyCount());
        int account = family.getFamilyAccount();
        account += many;
        family.setFamilyAccount(account);
        Family family1 = familyRepository.saveAndFlush(family);
        UserTransactions userTransactions = new UserTransactions();
        userTransactions.setFamilyId(family1.getFamilyId());
        userTransactions.setFamilyMemberId(familyMember.getFamily_member_id());
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

    public GetClientMessage withdrawMoneyOnAccount(ManyDto manyDto) throws ParseException {
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject;
        messageTransferObject = methods.chekWithdrawManyLimitationsGlobalAdminAllFAmily(manyDto);
        if (messageTransferObject.isChekResult()) {
            messageTransferObject = methods.chekWithdrawManyLimitationsGlobalAdminPersonal(manyDto);
            if (messageTransferObject.isChekResult()) {
                messageTransferObject = methods.chekWithdrawMoneyOnAccountLimitationFamilyAdmin(manyDto);
                if (messageTransferObject.isChekResult()) {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    FamilyMember familyMember = familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername());
                    Family family = familyMember.getFamily();
                    int familyAccount = family.getFamilyAccount();
                    int withdrawSum = methods.parseData(manyDto.getManyCount());
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
                        userTransactions.setFamilyMemberId(familyMember.getFamily_member_id());
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


    public GetClientMessage applyAdminFamilyRestrictions(FamilyAdminLimitationsDto familyAdminLimitationsDto) throws
            ParseException {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GetClientMessage getClientMessage = new GetClientMessage();
        FamilyMember adminFamily = familyMemberRepository.findFamilyMemberByMemberLogin(principal.getUsername());
        Family family = adminFamily.getFamily();
        MessageTransferObject messageTransferObject;

        messageTransferObject = methods.chekGlobalAdminFamilyLimitations(family, familyAdminLimitationsDto);
        if (messageTransferObject.isChekResult()) {

            messageTransferObject = methods.chekDate(familyAdminLimitationsDto);
            if (messageTransferObject.isChekResult()) {

                List<FamilyMember> familyMemberList = familyMemberRepository.findFamilyMemberByFamily_FamilyId(adminFamily.getFamily().getFamilyId());
                for (FamilyMember faml : familyMemberList) {
                    if (faml.getMemberLogin().equals(familyAdminLimitationsDto.getFamilyMemberLogin()) || familyAdminLimitationsDto.getFamilyMemberLogin().equals("На всех")) {
                        messageTransferObject = methods.chekGlobalAdminPersonalLimitations(faml.getFamily_member_id(), familyAdminLimitationsDto);
                        if (messageTransferObject.isChekResult()) {
                            Optional<FamilyAdminLimitations> familyAdminLimitationsOptional = Optional.ofNullable(familyAdminLimitationsRepository.findByFamilyMemberId(faml.getFamily_member_id()));
                            if (familyAdminLimitationsOptional.isEmpty()) {
                                FamilyAdminLimitations familyAdminLimitations = new FamilyAdminLimitations();
                                familyAdminLimitations.setFamilyMemberId(faml.getFamily_member_id());
                                familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(methods.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay()));
                                familyAdminLimitations.setMaximumWithdrawalPerDay(methods.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay()));
                                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                                familyAdminLimitations.setDateStartLimitation(methods.parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation()));
                                familyAdminLimitations.setDateEndLimitation(methods.parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation()));

                                familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                            } else {
                                FamilyAdminLimitations familyAdminLimitations = familyAdminLimitationsOptional.get();
                                if (familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay().equals("")) {
                                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(0);
                                } else {
                                    familyAdminLimitations.setMaximumOneTimeWithdrawalPerDay(methods.parseData(familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay()));
                                }

                                if (familyAdminLimitationsDto.getMaximumWithdrawalPerDay().equals("")) {
                                    familyAdminLimitations.setMaximumWithdrawalPerDay(0);
                                } else {
                                    familyAdminLimitations.setMaximumWithdrawalPerDay(methods.parseData(familyAdminLimitationsDto.getMaximumWithdrawalPerDay()));
                                }

                                if (!familyAdminLimitationsDto.getMaximumOneTimeWithdrawalPerDay().equals("") || !familyAdminLimitationsDto.getMaximumWithdrawalPerDay().equals("")) {

                                    if (familyAdminLimitationsDto.getDateStartLimitation().equals("")) {
                                        familyAdminLimitations.setDateStartLimitation(null);
                                    }
                                    familyAdminLimitations.setDateStartLimitation(methods.parseStringToCalendarDate(familyAdminLimitationsDto.getDateStartLimitation()));


                                    if (familyAdminLimitationsDto.getDateEndLimitation().equals("")) {
                                        familyAdminLimitations.setDateEndLimitation(null);
                                    }
                                    familyAdminLimitations.setDateEndLimitation(methods.parseStringToCalendarDate(familyAdminLimitationsDto.getDateEndLimitation()));

                                    familyAdminLimitationsRepository.saveAndFlush(familyAdminLimitations);
                                }
                            }
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


    public List<AllFamilyDto> allFamily() {
        List<AllFamilyDto> allFamilyDtos = new ArrayList<>();
        List<Family> familyList = familyRepository.findAll();
        for (Family fam : familyList) {
            AllFamilyDto allFamilyDto = new AllFamilyDto();
            allFamilyDto.setFamilyLogin(fam.getFamilyLogin());
            allFamilyDto.setFamilyAccount(fam.getFamilyAccount());
            List<FamilyMember> familyMemberList = familyMemberRepository.findFamilyMemberByFamily_FamilyId(fam.getFamilyId());
            for (FamilyMember memb : familyMemberList) {
                if (memb.getMemberRole().equals("ROLE_ADMIN")) {
                    allFamilyDto.setFirstName(memb.getFirstName());
                    allFamilyDto.setLastName(memb.getLastName());
                    allFamilyDto.setPatronymic(memb.getPatronymic());
                }
            }
            allFamilyDtos.add(allFamilyDto);
        }
        return allFamilyDtos;
    }

    public List<FamilyMember> findAllUsersFamily(FamilyLoginDto familyLoginDto) {
        Family family = familyRepository.findFamilyByFamilyLogin(familyLoginDto.getFamilyLogin());
        return findAllFamilyMemberByFamilyId(family.getFamilyId());
    }

    public ManyDto findFamilyAccount(FamilyLoginDto familyLoginDto) {
        Family family = familyRepository.findFamilyByFamilyLogin(familyLoginDto.getFamilyLogin());
        ManyDto manyDto = new ManyDto();
        manyDto.setManyCount(Integer.toString(family.getFamilyAccount()));
        return manyDto;
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

    public GetClientMessage imposeRestrictionsOnFamilyGlobalAdmin(GlobalAdminFamilyLimitationsDto
                                                                          globalAdminFamilyLimitationsDto) throws ParseException {
        String familyLogin = globalAdminFamilyLimitationsDto.getFamilyLogin();
        GetClientMessage getClientMessage = new GetClientMessage();
        MessageTransferObject messageTransferObject = methods.chekDate(globalAdminFamilyLimitationsDto);
        if (messageTransferObject.isChekResult()) {
            if (familyLogin.equals("All")) {
                List<Family> familyList = familyRepository.findAll();
                List<GlobalAdminAllFamilyLimitations> globalAdminAllFamilyLimitationsList = globalAdminAllFamilyLimitationsRepository.findAll();
                if (globalAdminAllFamilyLimitationsList.size() == 0) {
                    for (Family family : familyList) {
                        GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                        getClientMessage = methods.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                        methods.cancelPersonalLimitation(family);
                        methods.cancelFamilyAdminLimitations(family);
                    }
                } else {
                    for (Family family : familyList) {
                        Optional<GlobalAdminAllFamilyLimitations> globalAdminLimitationsOptional = Optional.ofNullable(globalAdminAllFamilyLimitationsRepository.findByFamilyId(family.getFamilyId()));
                        if (globalAdminLimitationsOptional.isPresent()) {
                            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = globalAdminLimitationsOptional.get();
                            int maxOneDay = methods.parseData(globalAdminFamilyLimitationsDto.getMaximumOneTimeWithdrawalPerDay());

                            globalAdminAllFamilyLimitations.setMaximumOneTimeWithdrawalPerDay(maxOneDay);
                            int maxPreDay = methods.parseData(globalAdminFamilyLimitationsDto.getMaximumWithdrawalPerDay());

                            globalAdminAllFamilyLimitations.setMaximumWithdrawalPerDay(maxPreDay);

                            Date dateStart = methods.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateStartLimitation());

                            globalAdminAllFamilyLimitations.setDateStartLimitation(dateStart);
                            Date dateEnd = methods.parseStringToCalendarDate(globalAdminFamilyLimitationsDto.getDateEndLimitation());

                            globalAdminAllFamilyLimitations.setDateEndLimitation(dateEnd);

                            globalAdminAllFamilyLimitationsRepository.saveAndFlush(globalAdminAllFamilyLimitations);
                            getClientMessage.setMessage("операция успешна");
                            methods.cancelPersonalLimitation(family);
                            methods.cancelFamilyAdminLimitations(family);
                        } else {
                            GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                            getClientMessage = methods.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                            methods.cancelPersonalLimitation(family);
                            methods.cancelFamilyAdminLimitations(family);
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
                    getClientMessage = methods.createGlobalAdminAllFamilyLimitations(family, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                    methods.cancelPersonalLimitation(family);
                    methods.cancelFamilyAdminLimitations(family);
                    return getClientMessage;
                } else {
                    Family family1 = new Family();
                    GlobalAdminAllFamilyLimitations globalAdminAllFamilyLimitations = new GlobalAdminAllFamilyLimitations();
                    getClientMessage = methods.createGlobalAdminAllFamilyLimitations(family1, globalAdminFamilyLimitationsDto, globalAdminAllFamilyLimitations);
                    methods.cancelPersonalLimitation(family);
                    methods.cancelFamilyAdminLimitations(family);
                    return getClientMessage;
                }
            }

        }
        getClientMessage.setMessage(messageTransferObject.getMessage());
        return getClientMessage;
    }


    public String familyAccount() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FamilyMember familyMember = findUserByLogin(principal.getUsername());
        Family family = familyMember.getFamily();
        return Integer.toString(family.getFamilyAccount());
    }

    public GetClientMessage personalLimitationsGlobalAdmin(FamilyAdminLimitationsDto familyAdminLimitationsDto) throws
            ParseException {
        GetClientMessage getClientMessage;
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberByMemberLogin(familyAdminLimitationsDto.getFamilyMemberLogin());
        Optional<GlobalAdminPersonalLimitations> globalAdminPersonalLimitationsOptional = Optional.ofNullable(globalAdminPersonalLimitationsRepository.findByMemberId(familyMember.getFamily_member_id()));
        if (globalAdminPersonalLimitationsOptional.isPresent()) {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = globalAdminPersonalLimitationsOptional.get();
            getClientMessage = methods.createGlobalAdminPersonalLimitations(globalAdminPersonalLimitations, familyAdminLimitationsDto, familyMember);
            methods.cancelAllFamilyLimitation(familyMember);
            methods.cancelFamilyAdminPersonalLimitations(familyMember);
        } else {
            GlobalAdminPersonalLimitations globalAdminPersonalLimitations = new GlobalAdminPersonalLimitations();
            getClientMessage = methods.createGlobalAdminPersonalLimitations(globalAdminPersonalLimitations, familyAdminLimitationsDto, familyMember);
            methods.cancelAllFamilyLimitation(familyMember);
            methods.cancelFamilyAdminPersonalLimitations(familyMember);
        }
        return getClientMessage;
    }

    public String returnGlobalAdminPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().toString();
        if (role.equals("[ROLE_GLOBAL_ADMIN]")) {
            return "redirect:/globalAdmin/admin";
        }
        return "FamilyPage";
    }


    public List<FamilyMember> findAllFamilyMemberByFamilyId(int familyId) {
        List<FamilyMember> familyMemberList = familyMemberRepository.findFamilyMemberByFamily_FamilyId(familyId);
        List<FamilyMember> familyMemberListDto = new ArrayList<>();
        for (FamilyMember member : familyMemberList) {
            FamilyMember familyMember1 = new FamilyMember();
            familyMember1.setFirstName(member.getFirstName());
            familyMember1.setLastName(member.getLastName());
            familyMember1.setPatronymic(member.getPatronymic());
            familyMember1.setMemberRole(member.getMemberRole());
            familyMember1.setMemberLogin(member.getMemberLogin());
            familyMemberListDto.add(familyMember1);
        }
        return familyMemberListDto;
    }
}

