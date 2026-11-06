package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SilceCrypto {
    private static final Bin DEFAULT_KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    private static final Bin DEFAULT_IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    private Crypto crypto;

    private SilceCrypto(Bin keyBase64, Bin ivBase64) {
        crypto = new Crypto(keyBase64, ivBase64);
    }

    public Bin encryptBody(String jsonString) {
        try {
            return crypto.encrypt(Bin.fromIso8859_1(jsonString));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar criptografar o corpo", e);
        }
    }

    public Bin encryptParams(Map<String, String> params) {
        try {
            return crypto.encrypt(Bin.fromUtf8(concatenateParams(params)));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar descriptografar os parâmetros", e);
        }
    }
    public Bin encryptParams(List<Map.Entry<String, String>> params) {
        try {
            return crypto.encrypt(Bin.fromUtf8(concatenateParams(params)));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar descriptografar os parâmetros", e);
        }
    }
    public String decryptBody(String encryptedBody) {
        try {
            return crypto.decrypt(Bin.fromBase64(encryptedBody)).toUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar descriptografar o corpo", e);
        }
    }

    public Map<String, String> decryptParams(Bin encryptedParams) {
        try {
            String singleParam = crypto.decrypt(encryptedParams).toUtf8();
            String[] paramStrs = singleParam.split("&&");
            Map<String, String> params = new LinkedHashMap<>(paramStrs.length);
            for (String paramStr : paramStrs) {
                String[] components = paramStr.split("==");
                String key = components[0];
                String value = components[1];
                params.put(key, value);
            }

            return params;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar descriptografar parâmetros", e);
        }
    }

    private String concatenateParams(Map<String, String> params) {
        StringBuilder singleParam = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getValue() != null) {
                singleParam.append(param.getKey()).append("==").append(param.getValue()).append("&&");
            }
        }

        return singleParam.toString();
    }

    private String concatenateParams(List<Map.Entry<String, String>> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null) {
                if (!first) {
                    sb.append("&&");
                }
                sb.append(key).append("==").append(value);
                first = false;
            }
        }

        return sb.toString();
    }

    public static SilceCrypto create() {
        return new SilceCrypto(DEFAULT_KEY, DEFAULT_IV);
    }

    public static SilceCrypto create(Bin key, Bin iv) {
        return new SilceCrypto(key, iv);
    }

    public static SilceCrypto create(String keyBase64, String ivBase64) {
        return new SilceCrypto(Bin.fromBase64(keyBase64), Bin.fromBase64(ivBase64));
    }
}
