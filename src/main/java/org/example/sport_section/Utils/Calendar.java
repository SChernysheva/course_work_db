package org.example.sport_section.Utils;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Calendar {
    public static String getDayOfWeekStringDate(Date date) {
        LocalDate localDate = date.toLocalDate();
        // Получаем день недели
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.toString().toLowerCase();
    }
    public static String getDayOfWeekStringDate(LocalDate localDate) {
        // Получаем день недели
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.toString().toLowerCase();
    }
}
