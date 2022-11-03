package br.gov.caixa.loterias.apostas.bo.silce;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.SilceCrypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SilceCryptoTest {
    private static final String TAG = SilceCryptoTest.class.getName();
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    public static final String CLEARTEXT_PARAM_STR = "quantidadeTotalNumeros==6&&numerosMarcados==15,18,19,48,52,58&&menorPrognostico==1&&maiorPrognostico==60&&";
    public static final String ENCRYPTED_PARAM_STR = "5sqb7m8tljzD73R/uBrZga5ctylBea53WbH0s1wqvEfWrViv5VeWYDkJmaXT6luwK8xaMMO+hUWq6MlrFLEaN+GRJOtPSaW6YlApmEDhnkSTaUw+9tSy4Tm9FkvPVOsapG5+irt9RXyHhkWMaCW6jw==";
    public static final Map<String, String> PARAMS = createDefaultParams();

    SilceCrypto silceCrypto = SilceCrypto.create();

    @Test
    public void encryptParamsTest() {
        String actual = silceCrypto.encryptParams(PARAMS).toBase64();
        String expected = ENCRYPTED_PARAM_STR;

        assertThat(silceCrypto.encryptParams(PARAMS).toBase64(),
            is(ENCRYPTED_PARAM_STR));
    }

    @Test
    public void decryptParamsTest() {
        assertThat(silceCrypto.encryptParams(silceCrypto.decryptParams(Bin.fromBase64(ENCRYPTED_PARAM_STR))).toBase64(),
                is(ENCRYPTED_PARAM_STR));
        assertThat(silceCrypto.decryptParams(silceCrypto.encryptParams(PARAMS)),
                is(PARAMS));
    }

    public static Map<String, String> createDefaultParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("quantidadeTotalNumeros", "6");
        params.put("numerosMarcados", "15,18,19,48,52,58");
        params.put("menorPrognostico", "1");
        params.put("maiorPrognostico", "60");

        return params;
    }
}
