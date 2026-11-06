package br.gov.caixa.loterias.apostas.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DataUtil {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String DD_MM_YYYY = "dd/MM/yyyy";
	public static final String HH_MM_SS = "HH:mm:ss";

	public static String getStringDataFormatted(String patter, String stringData) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patter);
		Date             date             = new Date(stringData);
		return simpleDateFormat.format(date);
	}

	public static String formatStringData(String patter, String stringData) {

		Date date = parseIsoDateString(stringData);
		return formatToNewPattern(date, patter);
	}

	private static Date parseIsoDateString(String isoDateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

		try {
			return sdf.parse(isoDateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String formatToNewPattern(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(date);
	}
}
