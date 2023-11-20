package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by lsimas on 10/04/2017.
 */

public class AppUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    public static List<Integer> converteListaInteiros(List<Integer> lista){
        ArrayList<Integer> listaInteiros = new ArrayList<>();
        if (lista != null && lista.size() > 0) {
            if(lista.get(0) instanceof Integer){
                return lista;
            } else if (lista.get(0) instanceof Number) {
                for (Number n : lista) {
                    listaInteiros.add(new Integer( n.intValue()));
                }
            } else {
                for (int a = 0; a < lista.size(); a++){
                    String tmp  = String.valueOf(lista.get(a)).replace("[","").replace("]", "");

                    Double dNumero = Double.parseDouble(tmp);
                    listaInteiros.add(dNumero.intValue());
                }
            }

        }
        return listaInteiros;
    }

    public static ArrayList<ArrayList<Integer>> converteMatrizInteiros(List<List<Integer>> lista){
        ArrayList<ArrayList<Integer>> listaInteiros = new ArrayList<>();
        if(lista != null){
            for (List<Integer> coluna : lista) {
                if (coluna.get(0) instanceof Integer) {
                    listaInteiros.add(new ArrayList<>(coluna));
                } else {
                    List<Integer> l = converteListaInteiros(coluna);
                    listaInteiros.add(new ArrayList<>(l));
                }
            }
        }

        return listaInteiros;
    }

    public static void fechaTeclado(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static String capitalizeFirstChar(String string) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String removeMasckCPF(String cpf) {
        return cpf.replaceAll("[.-]", "").trim();
    }

    public static String mascaraCPF(String cpf) {
        if(cpf != null && cpf.length() == 11){
            return  cpf.substring(0,3) + "." + cpf.substring(3,6) + "." + cpf.substring(6,9) + "-" + cpf.substring(9,11);
        } else {
            return "";
        }
    }

    public static List<String> listIntegerToListString(List<Integer> lista) {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            if (i == 0) {
                lineSB.append(ViewUtils.getNumberIntegerToString(lista.get(i)));
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                lineSB.append(ViewUtils.getNumberIntegerToString(lista.get(i)));
            } else {
                lineSB.append(String.format(" - %s", ViewUtils.getNumberIntegerToString(lista.get(i))));
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }

}
