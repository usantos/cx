package caixa.gov.br.loterias.bo.silce;

import org.junit.Test;

import caixa.gov.br.loterias.bo.silce.internals.Bin;
import caixa.gov.br.loterias.bo.silce.internals.Crypto;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CryptoUnitTest {
    public static final Bin TEXT = Bin.fromUtf8("quantidadeTotalNumeros==6&&numerosMarcados==15,18,19,48,52,58&&menorPrognostico==1&&maiorPrognostico==60&&");
    public static final Bin ENCRYPTED = Bin.fromBase64("5sqb7m8tljzD73R/uBrZga5ctylBea53WbH0s1wqvEfWrViv5VeWYDkJmaXT6luwK8xaMMO+hUWq6MlrFLEaN+GRJOtPSaW6YlApmEDhnkSTaUw+9tSy4Tm9FkvPVOsapG5+irt9RXyHhkWMaCW6jw==");
    public static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    public static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    Crypto crypto = new Crypto(KEY, IV);

    @Test
    public void encryptTest() throws Exception {
        Bin actual = crypto.encrypt(TEXT);

        assertThat(actual.toUtf8(),
                is(ENCRYPTED.toUtf8()));
    }

    @Test
    public void decryptTest() throws Exception {
        Bin actual = crypto.decrypt(ENCRYPTED);

        assertThat(actual.toUtf8(),
                is(TEXT.toUtf8()));
    }
}
