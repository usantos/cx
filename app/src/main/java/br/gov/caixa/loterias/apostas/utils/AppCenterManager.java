package br.gov.caixa.loterias.apostas.utils;

import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public final class AppCenterManager {
    private static final int MAX_VALUE = 120;
    private static final int MAX_LINES = 20;

    public static void registraEvento(String title) {
        Analytics.trackEvent(title);
    }

    public static void registraErro(String codigoErro, String endpoint) {

    }

    public static void registraEventoDeviceMP(String evento, String jsonString) {
        Map<String, String> properties = new HashMap<>();
        try {
            JSONObject          jsonObject = new JSONObject(jsonString);
            String              deviceString    = jsonObject.getString("device");

            int count = 0;
            for (int i = 0; i < deviceString.length(); i+=MAX_VALUE) {
                count++;
                if (count <= MAX_LINES) {
                    String value    = null;
                    int    restante = deviceString.length() - i;
                    if (restante < MAX_VALUE) {
                        value = deviceString.substring(i, deviceString.length());
                    } else {
                        value = deviceString.substring(i, MAX_VALUE + i);
                    }
                    properties.put("DEVICE" + (count), value);
                }else {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Analytics.trackEvent(evento + "_" + DadosUsuarioBO.obterCpf() + "_" + getDate(), properties, Flags.CRITICAL);
    }

    private static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return simpleDateFormat.format(new Date());
    }


}
