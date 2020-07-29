package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lsimas on 10/04/2017.
 */

public class AppUtils {

    private AppUtils() {
    }

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
            }
            for (Number n : lista) {
                listaInteiros.add(new Integer( n.intValue()));
            }
        }
        return listaInteiros;
    }

    public static ArrayList<ArrayList<Integer>> converteMatrizInteiros(List<List<Integer>> lista){
        ArrayList<ArrayList<Integer>> listaInteiros = new ArrayList<>();
        for (List<Integer> coluna : lista) {
            if(coluna.get(0) instanceof Integer){
                listaInteiros.add(new ArrayList<>(coluna));
            } else {
                List<Integer> l = converteListaInteiros(coluna);
                listaInteiros.add(new ArrayList<>(l));
            }

        }
        return listaInteiros;
    }

    public static void fechaTeclado(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void requestFingerprintPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, requestCode);
    }

    public static boolean checkFingerprintPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED;
    }

    public static String capitalizeFirstChar(String string) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase(new Locale("pt", "BR")).toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String removeMasckCPF(String cpf) {
        return cpf.replaceAll("[.-]", "").trim();
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

    public static List<String> listDezenasSurpresinha(int size) {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                lineSB.append("XX");
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                lineSB.append("XX");
            } else {
                lineSB.append(String.format(" - %s", "XX"));
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }

}
