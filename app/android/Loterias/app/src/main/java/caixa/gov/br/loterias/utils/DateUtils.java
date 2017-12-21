package caixa.gov.br.loterias.utils;

import android.support.annotation.NonNull;
import android.util.Log;

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
    public static String TAG = DateUtils.class.getSimpleName();


    public static boolean isLegalDate(String date, String paternDate) {
        SimpleDateFormat sdf = getSimpleDateFormat(paternDate);
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public static Date stringToDate(String dateString, String paternDate){
        SimpleDateFormat dt = getSimpleDateFormat(paternDate);
        Date date = null;
        try {
            date = dt.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
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

        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH);
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        int bornYear = born.get(Calendar.YEAR);
        int bornMonth = born.get(Calendar.MONTH);
        int bornDay = born.get(Calendar.DAY_OF_MONTH);

        if(bornMonth > currentMonth || (bornMonth == currentMonth && bornDay > currentDay)){
            return currentYear - bornYear -1;
        }
        return currentYear - bornYear;
    }


}
