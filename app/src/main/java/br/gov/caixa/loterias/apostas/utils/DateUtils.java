package br.gov.caixa.loterias.apostas.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.DataHoraServidorSingleton;

/**
 * Created by ramluz on 15/12/2017.
 */

public class DateUtils {

    public static final String PATTERN_DDMM_YYYY = "dd/MM/yyyy";
    public static final String PATTERN_DD_MM = "dd/MM";
    public static final String PATTERN_DD_MMM_YYYY = "dd MMM yyyy";
    public static final String PATTERN_HH_MM_SS = "hh:mm:ss";
    public static final String PATTERN_HH_MM = "hh:mm";
    public static final String PATTERN_DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
    public static String TAG = DateUtils.class.getSimpleName();

    public static String[] getAllMonthsOfYear() {
        DateFormatSymbols symbols = new DateFormatSymbols();
        return symbols.getMonths();
    }

    public static int parseMonthName(String monthName) {
        try {
            Date date = new SimpleDateFormat("MMMM", Locale.getDefault()).parse(monthName);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH);
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isLegalDate(String date, String paternDate) {
        SimpleDateFormat sdf = getSimpleDateFormat(paternDate);
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public static Date stringToDate(String dateString, String paternDate) {
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
        final Locale myLocale = new Locale("pt", "BR");
        return new SimpleDateFormat(paternDate, myLocale);
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static int getBetweenAges(Calendar current, Calendar born) {

        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH);
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        int bornYear = born.get(Calendar.YEAR);
        int bornMonth = born.get(Calendar.MONTH);
        int bornDay = born.get(Calendar.DAY_OF_MONTH);

        if (bornMonth > currentMonth || (bornMonth == currentMonth && bornDay > currentDay)) {
            return currentYear - bornYear - 1;
        }
        return currentYear - bornYear;
    }

    public static String getDateToString(Date date, String pattern) {
        SimpleDateFormat dt = getSimpleDateFormat(pattern);
        return dt.format(date);
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long getDiffSeconds(Date date) {
        return (new Date().getTime() - date.getTime()) / 1000;
    }

    public static long diffMillisSecondsTimerZone(String dataHoraExpiracao) {
        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        //DateTime agora = DateTime.now().withZone(timeZone);
        DateTime agora = DataHoraServidorSingleton.getInstance().getCurrentDateTime();

        //Alterado o formato para atender o Servico Nuvem
        //DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        //DateTime expiracao = formatter.parseDateTime(dataHoraExpiracao);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(timeZone);
        DateTime expiracao = formatter.parseDateTime(dataHoraExpiracao.substring(0,19));

        Duration duration = new Duration(agora, expiracao);
        long diffInSeconds = duration.getStandardSeconds();

        return diffInSeconds * 1000;
    }

    public static String calculaDataHoraFutura(int horas) {
        DateTime agora = DataHoraServidorSingleton.getInstance().getCurrentDateTime();
        DateTime futura = agora.plusHours(horas);
        DateTimeFormatter formato = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        return formato.print(futura);
    }

    public static DateTime criaDataHoraFuso(String dataHora, String formatoDataHora, String fusoHorario) {

        String fusoHorarioFinal = "America/Sao_Paulo";
        if (fusoHorario != null) {
            fusoHorarioFinal = fusoHorario;
        }
        try {
            DateTimeZone fusoDesejado = DateTimeZone.forID(fusoHorarioFinal);
            DateTimeFormatter formato = DateTimeFormat.forPattern(formatoDataHora).withZone(fusoDesejado);
            DateTime resultado = formato.parseDateTime(dataHora);
            return resultado;
        } catch (Exception e) {
            return null;
        }
    }

    //org.joda.time.DateTime Armazena o TimeZone
    public static DateTime criaDataHora(String dataHora, String formatoDataHora) {
        return criaDataHoraFuso(dataHora, formatoDataHora, null);
    }
}
