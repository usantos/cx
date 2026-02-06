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

    public static String capitalizerNovo(String texto){
        // Palavras que não devem ser capitalizadas (exceto se forem a primeira palavra)
        String[] palavrasMinusculas = {"de", "da", "do", "das", "dos", "a", "o", "as", "os",
                "e", "em", "no", "na", "nos", "nas", "por", "para"};

        texto = texto.toLowerCase(new Locale("pt", "BR"));
        StringBuilder stringBuilder = new StringBuilder();
        String[] palavras = texto.split(" ");

        for(int i = 0; i < palavras.length; i++){
            String palavra = palavras[i];
            boolean deveCapitalizar = true;

            // Não capitaliza palavras da lista, exceto se for a primeira palavra
            if(i > 0){
                for(String palavraMinuscula : palavrasMinusculas){
                    if(palavra.equals(palavraMinuscula)){
                        deveCapitalizar = false;
                        break;
                    }
                }
            }

            if(deveCapitalizar && !palavra.isEmpty()){
                palavra = palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
            }

            stringBuilder.append(palavra);
            if(i < palavras.length - 1){
                stringBuilder.append(" ");
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

    public static String getApostasDePalpites(int aposta, int dezena) {
        String apostaText = (aposta <= 1) ? "aposta" : "apostas";
        return "_"+aposta+"_ "+apostaText+" de _"+dezena+"_ palpites";
    }
    public static String getCotaDeTotal(int cota, int total) {
        String cotaText = (cota <= 1) ? "cota disponível" : "cotas disponíveis";
        return "_"+cota+"_ "+cotaText+" de _"+total+"_";
    }

    public static String transformToHorahMinuto(String hh_mm_ss) {
        return hh_mm_ss.substring(0,2) + "h" +
               hh_mm_ss.substring(3,5);
    }
}
