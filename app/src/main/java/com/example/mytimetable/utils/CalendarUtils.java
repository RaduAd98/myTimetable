package com.example.mytimetable.utils;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import android.content.Context;

import com.example.mytimetable.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarUtils {

    public static final int FIRST_SEMESTER = 1;
    public static final int SECOND_SEMESTER = 2;
    public static final int THIRD_WEEK_IN_MONTH = 3;
    private static final SimpleDateFormat simpleFormatter = new SimpleDateFormat("dd/MM/yyyy");

    //Method that generates the 14 days when a Module takes place
    public static List<String> generateSemesterDates(int semester, int dayOfWeekSpinnerPosition) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(MONTH, getMonthForSemester(semester));
        calendar.set(DAY_OF_MONTH, getFirstDayOfSemesterModule(semester, dayOfWeekSpinnerPosition));

        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            dates.add(simpleFormatter.format(calendar.getTime()));
            calendar.add(WEEK_OF_MONTH, 1);
        }
        return dates;
    }

    /**
     * Method that returns the first day of a Module
     *
     * @param semester                 the first or second Semester
     * @param dayOfWeekSpinnerPosition used to see in which day the Module takes place
     * @return generates the first day of Module
     */
    public static String getFirstWeek(int semester, int dayOfWeekSpinnerPosition) {
        return generateSemesterDates(semester, dayOfWeekSpinnerPosition).get(0);
    }

    //Method that returns the available hours by removing the occupied hours from the hours list
    public static List<String> getAvailableHours(Context context, List<String> occupiedHours) {
        List<String> availableHours = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.hours)));
        availableHours.removeAll(occupiedHours);
        return availableHours;
    }

    //Method to show the date of Module from the edit Module menu
    public static Calendar getCalendarFromString(String stringCalendar) {
        String[] values = stringCalendar.split("/");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(DAY_OF_MONTH, Integer.parseInt(values[0]));
            calendar.set(MONTH, (Integer.parseInt(values[1]) - 1));
            calendar.set(YEAR, Integer.parseInt(values[2]));
            return calendar;

        } catch (Exception e) {
            return Calendar.getInstance();
        }
    }

    public static String getStringDate(Calendar calendar) {
        return simpleFormatter.format(calendar.getTime());
    }

    public static String getDayOfWeek(Calendar calendar) {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
    }

    //Method that returns the first day in which the selected module takes place
    private static int getFirstDayOfSemesterModule(int semester, int dayOfWeekSpinnerPosition) {

        //Setting the starting month (either September or January)
        Calendar calendar = getInstance();
        calendar.set(MONTH, getMonthForSemester(semester));

        //To ensure that a week's first day is Monday
        calendar.setFirstDayOfWeek(MONDAY);

        /* Set the current day to Sunday to ensure that the second week is always the second
         (if 1st of September is Saturday and the course is on a Friday, the second Friday would be
         in the third week of September (which is not what we want) */
        calendar.set(DAY_OF_WEEK, SUNDAY);

        calendar.set(WEEK_OF_MONTH, THIRD_WEEK_IN_MONTH);

        //Add 2 to the Spinner position value since position 0 means Saturday
        calendar.set(DAY_OF_WEEK, dayOfWeekSpinnerPosition + 2);

        return calendar.get(DAY_OF_MONTH);
    }

    //Method that checks if the Spinner Semester has the first or second semester selected
    private static int getMonthForSemester(int semester) {
        if (semester == FIRST_SEMESTER) {
            return Calendar.SEPTEMBER;
        }
        return Calendar.JANUARY;
    }
}
