package com.monjaz.baiedu.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekDataUtils {

    public static List<String> getDateStringFromWeek(int curWeek, int targetWeek) {
        Calendar calendar = Calendar.getInstance();
        if (targetWeek == curWeek)
            return getDateStringFromCalendar(calendar);
        int amount = targetWeek - curWeek;
        calendar.add(Calendar.WEEK_OF_YEAR, amount);
        return getDateStringFromCalendar(calendar);
    }

    private static List<String> getDateStringFromCalendar(Calendar calendar) {
        List<String> dateList = new ArrayList<>();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        dateList.add((calendar.get(Calendar.MONTH) + 1) + "");
        for (int i = 0; i < 7; i++) {
            dateList.add(calendar.get(Calendar.DAY_OF_MONTH) + "");
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateList;
    }
}
