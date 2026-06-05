package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by lsimas on 10/04/2017.
 */

public class AppUtils {

    public static void applyInsets(Activity activity, boolean consumeInsets) {
        // Ativa edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        View rootView = activity.getWindow().getDecorView();

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.topMargin = systemInsets.top;
                params.bottomMargin = systemInsets.bottom;
                params.leftMargin = systemInsets.left;
                params.rightMargin = systemInsets.right;
                v.setLayoutParams(params);
            } else {
                v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            }

            if (!consumeInsets) {
                return insets;
            }
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.requestApplyInsets(rootView);
    }


    public static boolean isNetworkAvailable(Context context) {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected();
        }catch (Exception e){
            return false;
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // Obtém todas as redes
            Network[] networks = connectivityManager.getAllNetworks();

            for (Network network : networks) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

                if (capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))) {
                    // Há uma conexão de dados móveis ou Wi-Fi ativa ou via cabo
                    return true;
                }
            }
        }

        // Sem conexão ativa
        return false;
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

    public static String listaIntToString(List<Integer> integerList) {
        StringBuilder sb = new StringBuilder();

        integerList = converteListaInteiros(integerList);
        for (Integer numero : integerList) {
            if (numero == 100) {
                sb.append("00 ");
            } else {
                sb.append(String.format("%02d ",numero));
            }
        }
        return sb.toString().trim();
    }


}
