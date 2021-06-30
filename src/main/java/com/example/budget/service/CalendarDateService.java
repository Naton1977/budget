package com.example.budget.service;


import com.example.budget.domain.dto.FamilyAdminLimitationsDto;
import com.example.budget.domain.dto.GlobalAdminFamilyLimitationsDto;
import com.example.budget.domain.dto.MessageTransferObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Transactional
@Service
public class CalendarDateService {


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

    public int parseData(Object data) {
        int parseData = 0;
        try {
            parseData = Integer.parseInt((String) data);
            return parseData;
        } catch (Exception ignored) {

        }
        return parseData;
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


}
