package br.gov.caixa.loterias.apostas.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cedesbr450 on 18/05/18.
 */

public class ValidacaoUtils {

    public static boolean validarEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
