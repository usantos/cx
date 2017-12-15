package caixa.gov.br.loterias.utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ramluz on 15/12/2017.
 */

public class DateUtils {

    public static String PATTERN_DDMM_YYYY = "dd/MM/yyyy";


    public static boolean isLegalDate(String date, String paternDate) {
        SimpleDateFormat sdf = getSimpleDateFormat(paternDate);
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public static Date fomatDate(String dateString, String paternDate) throws ParseException {
        SimpleDateFormat dt = getSimpleDateFormat(paternDate);
        Date date = dt.parse(dateString);
        return date;
    }

    @NonNull
    public static SimpleDateFormat getSimpleDateFormat(String paternDate) {
        return new SimpleDateFormat(paternDate);
    }

    public static Calendar DateToCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static int getBetweenAges(Calendar current, Calendar born){
//        if(){
//
//        }

    }



}
