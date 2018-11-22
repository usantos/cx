package br.gov.caixa.loterias.apostar.utils;

public class StringUtils {

    public static String capitalizer(String texto){
        texto = texto.toLowerCase();
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

}
