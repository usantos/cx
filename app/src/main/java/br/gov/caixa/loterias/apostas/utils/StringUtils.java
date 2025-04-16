package br.gov.caixa.loterias.apostas.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringUtils {

    public static String capitalizer(String texto){
        texto = texto.toLowerCase(new Locale("pt", "BR"));
        StringBuilder stringBuilder = new StringBuilder();
        char caracteres[] = texto.toCharArray();
        boolean primeiroCaracter = true;
        for(char caracter : caracteres){
            String caracterString = String.valueOf(caracter);
            if(primeiroCaracter){
                primeiroCaracter = false;
                caracterString = caracterString.toUpperCase();
            }
            stringBuilder.append(caracterString);
            if(caracter == ' '){
                primeiroCaracter = true;
            }
        }
        return stringBuilder.toString();
    }

    public static String formatDouble(Double valor){
        return String.format("%.2f", valor);
    }

    public static String firstWord(String texto) {
        if (texto != null && !texto.isEmpty() && texto.contains(" ")){
            return texto.substring(0, texto.indexOf(" "));
        }
        return texto;
    }

    public static String padZeroLeft(int valor, int tamanho) {
        return String.format("%0" + tamanho + "d", valor);
    }

    public static String formatToCurrency(double value) {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
        return df.format(value);
    }

    public static String getApostasDeDezenas(int aposta, int dezena) {
        String apostaText = (aposta <= 1) ? "aposta" : "apostas";
        return "_"+aposta+"_ "+apostaText+" de _"+dezena+"_ dezenas";
    }

    public static String getCotaDeTotal(int cota, int total) {
        String cotaText = (cota <= 1) ? "cota disponível" : "cotas disponíveis";
        return "_"+cota+"_ "+cotaText+" de _"+total+"_";
    }
}
