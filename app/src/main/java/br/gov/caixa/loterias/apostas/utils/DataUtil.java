package br.gov.caixa.loterias.apostas.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static String getStringDataFormatted(String patter, String stringData) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patter);
		Date             date             = new Date(stringData);
		return simpleDateFormat.format(date);
	}
}
