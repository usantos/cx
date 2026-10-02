package br.gov.caixa.loterias.apostas.utils;

import com.android.volley.VolleyError;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public final class AppCenterManager {
    private static final int MAX_VALUE = 120;
    private static final int MAX_LINES = 20;

    // constants para evento
    public static final String ERRO_GENERICO = "ERRO";
    public static final String ERRO_RESPONSE = "ERRO_RESPONSE";
    public static final String ERRO_GENERICO_SERIALIZACAO = "ERRO_SERIALIZACAO";
    public static final String ERRO_UNSUPPORTED_ENCODING = "ERRO_UNSUPPORTED_ENCODING";
    public static final String ERRO_SERVICO_SEM_RESPONSEDATA = "ERRO_SERVICO_SEM_RESPONSEDATA";
    public static final String ERRO_BOLOES_DISPONIVEIS = "ERRO_SERVICO_SEM_RESPONSEDATA";
    public static final String ERRO_DETALHE_BOLAO = "ERRO_SERVICO_DETALHE_BOLAO";

    // constants para tag

    public static void registraEvento(String title) {
        Analytics.trackEvent(title);
    }

    public static void registraErro(String codigoErro, String endpoint) {
        Map<String, String> properties = new HashMap();
        properties.put("CODIGOERROO + ENDPOINT", codigoErro + "-" + endpoint);
        Analytics.trackEvent("ERRO_WEBSERVICE", properties, Flags.CRITICAL);
    }

    public static void registraEventoDeviceMP(String evento, String jsonString) {
        Map<String, String> properties = new HashMap<>();
        try {
            JSONObject          jsonObject = new JSONObject(jsonString);
            String              deviceString    = jsonObject.getString("device");

            properties = getPropertiesByString("DEVICE", deviceString);
        } catch (JSONException e) {
            properties = getPropetyErroPadrao();
        }

        Analytics.trackEvent(evento + "_" + DadosUsuarioBO.obterCpf() + "_" + getDate(), properties, Flags.CRITICAL);
    }

    public static void registraEventoErro(String evento, VolleyError error) {
        registraEventoErro(evento, error, null);
    }

    public static void registraEventoErro(String evento, ErrorResponse error) {
        Map<String, String> properties = new HashMap<>();
        properties.put(evento, error.toString());
        Analytics.trackEvent(evento, properties, Flags.CRITICAL);
    }
    
    private static String createStringProperties(VolleyError error, String body){
        StringBuilder stringProperties = new StringBuilder();

        if (body != null && !body.isEmpty()){
            stringProperties.append("BODY: ");
            stringProperties.append(body + " | ");
        }

        if (error != null){
            if (error.networkResponse != null) {
                stringProperties.append(" | STATUSCODE: ");
                stringProperties.append(error.networkResponse.statusCode);
            }
            stringProperties.append(" | GETCAUSE: ");
            if (error.getCause() != null){
                stringProperties.append(error.getCause());
            }else {
                stringProperties.append(error.toString());
            }

            if (error.getStackTrace() != null) {
                stringProperties.append(" | STACKTRACER: ");
                StackTraceElement[] stackTrace = error.getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    stringProperties.append(element.toString() + " | ");
                }
            }
        }
        
        return stringProperties.toString();
    }
    
    private static Map<String, String> getPropertiesByString(String tag, String stringProperties) {
        Map<String, String> properties = new HashMap<>();

        if (stringProperties != null && !stringProperties.isEmpty()) {
            int count = 0;
            for (int i = 0; i < stringProperties.length(); i += MAX_VALUE) {
                count++;
                if (count <= MAX_LINES) {
                    String value    = null;
                    int    restante = stringProperties.length() - i;
                    if (restante < MAX_VALUE) {
                        value = stringProperties.substring(i, stringProperties.length());
                    } else {
                        value = stringProperties.substring(i, MAX_VALUE + i);
                    }
                    properties.put(tag + (count), value);
                } else {
                    break;
                }
            }
        }else {
            properties = getPropetyErroPadrao();
        }
        return properties;
    }

    private static Map<String, String> getPropetyErroPadrao() {
        Map<String, String> properties = new HashMap<>();
        
        properties.put(ERRO_GENERICO, "Nao foi possivel montar a propety do evento devido ha uma string vazia");
        
        return properties;
    }

    public static void registraEventoErro(String evento, VolleyError error, String body) {
        String stringProperties = createStringProperties(error, body);

        Map<String, String> properties = getPropertiesByString(ERRO_GENERICO, stringProperties);

        Analytics.trackEvent(evento + "_" + getDate(), properties, Flags.CRITICAL);
    }

    private static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


}
