package br.gov.caixa.loterias.apostas.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
        // Palavras que devem permanecer totalmente em maiúsculas
        String[] palavrasMaiusculas = {"s.a."};

        texto = texto.toLowerCase(new Locale("pt", "BR"));
        StringBuilder stringBuilder = new StringBuilder();
        String[] palavras = texto.split(" ");

        for(int i = 0; i < palavras.length; i++){
            String palavra = palavras[i];
            boolean deveCapitalizar = true;

            for(String palavraMaiuscula : palavrasMaiusculas){
                if(palavra.equals(palavraMaiuscula)){
                    palavra = palavraMaiuscula.toUpperCase(new Locale("pt", "BR"));
                    deveCapitalizar = false;
                    break;
                }
            }

            // Não capitaliza palavras da lista, exceto se for a primeira palavra
            if(deveCapitalizar && i > 0){
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
    public static String getCotaListaColapsada(int cota, int total) {
        String cotaText = (cota <= 1) ? " cota " : " cotas ";
        return "Restam "+cota+" de "+total+cotaText;
    }

    public static String transformToHorahMinuto(String hh_mm_ss) {
        return hh_mm_ss.substring(0,2) + "h" +
               hh_mm_ss.substring(3,5);
    }

    public static List<String> formatToStringList(List<? extends Number> listtNumeros) {
        List<String> listString = new ArrayList<>();
        if (listtNumeros == null) {
            return listString;
        }

        //cria cópia para não alterar lista original
        List<Number> ordenada = new ArrayList<>(listtNumeros);
        Collections.sort(ordenada, (a, b) -> Integer.compare(a.intValue(), b.intValue()));

        for (Number numero : ordenada) {
            long longNumero = numero.longValue();
            if (longNumero == 100) {
                longNumero = 0L;
            }
            listString.add(String.format("%02d", longNumero));
        }
        return listString;
    }

    public static List<String> formatToStringList1Digito(List<? extends Number> listtNumeros) {
        List<String> listString = new ArrayList<>();
        if (listtNumeros == null) {
            return listString;
        }

        //cria cópia para não alterar lista original
        List<Number> ordenada = new ArrayList<>(listtNumeros);
        Collections.sort(ordenada, (a, b) -> Integer.compare(a.intValue(), b.intValue()));

        for (Number numero : ordenada) {
            long longNumero = numero.longValue();
            listString.add(String.format("%1d", longNumero));
        }
        return listString;
    }

//    public static List<String> formatToStringListFlexPrimeiro(List<?> input) {
//        List<String> out = new ArrayList<>();
//        if (input == null) return out;
//
//        for (Object item : input) {
//            if (item == null) continue;
//
//            if (item instanceof Number) {
//                Number n = (Number) item;
//                long v = Math.round(n.doubleValue()); // use n.longValue() para truncar
//                out.add(String.format("%01d", v));
//            } else if (item instanceof List<?>) {
//                List<?> inner = (List<?>) item;
//                if (inner.isEmpty()) continue;
//
//                Object first = inner.get(0);
//                if (first instanceof Number) {
//                    Number n0 = (Number) first;
//                    long v = Math.round(n0.doubleValue());
//                    out.add(String.format("%01d", v));
//                }
//                // Se "first" não for Number, ignora.
//            }
//            // Se não for Number nem List, ignora.
//        }
//        return out;
//    }

    public static List<String> formatToStringListFlexS7(List<?> input) {
        if (input == null || input.size() != 7) {
            return null;
        }

        List<String> out = new ArrayList<>();

        // Descobrir o maior tamanho das sublistas (quantas "camadas" existem)
        int maxDepth = 1;

        for (Object item : input) {
            if (item instanceof List<?>) {
                maxDepth = Math.max(maxDepth, ((List<?>) item).size());
            } else {
                maxDepth = Math.max(maxDepth, 1);
            }
        }

        // Para cada "camada" (posição interna)
        for (int i = 0; i < maxDepth; i++) {

            for (Object item : input) {
                String value = null; //""; // default

                if (item instanceof Number) {
                    // Só participa da camada 0
                    if (i == 0) {
                        long v = Math.round(((Number) item).doubleValue());
                        value = String.format("%01d", v);
                    }
                } else if (item instanceof List<?>) {
                    List<?> inner = (List<?>) item;

                    if (i < inner.size()) {
                        Object obj = inner.get(i);

                        if (obj instanceof Number) {
                            long v = Math.round(((Number) obj).doubleValue());
                            value = String.format("%01d", v);
                        }
                    }
                }

                out.add(value);
            }
        }

        return out;
    }

}
