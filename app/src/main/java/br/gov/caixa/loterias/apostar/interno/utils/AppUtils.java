package br.gov.caixa.loterias.apostar.interno.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    public static void requestFingerprintPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, requestCode);
    }

    public static boolean checkFingerprintPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED;
    }


    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getApplicationVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }

    public static String capitalizeFirstChar(String string) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
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

    public static boolean isStringEmpty(String string) {
        // Null-safe, short-circuit evaluation.
        return string == null || string.trim().isEmpty();
    }

    public static String removeMasckCPF(String cpf) {
        return cpf.replaceAll("[.-]", "").trim();
    }

    public static String dateFormatteAaaaMmDd(String data, String formato){

        SimpleDateFormat dateFormatter = new SimpleDateFormat(formato);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormatter.format(df.parse(data));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getEditTextValue(EditText editText) {
        assert editText != null;

        if (editText.getText() != null) {
            return editText.getText().toString();
        }
        return null;
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
