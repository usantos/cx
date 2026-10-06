package br.gov.caixa.loterias.apostas.bo.silce;


import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Crypto;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CryptoTest {
    public static final Bin TEXT = Bin.fromUtf8("quantidadeTotalNumeros==6&&numerosMarcados==15,18,19,48,52,58&&menorPrognostico==1&&maiorPrognostico==60&&");
    public static final Bin ENCRYPTED = Bin.fromBase64("5sqb7m8tljzD73R/uBrZga5ctylBea53WbH0s1wqvEfWrViv5VeWYDkJmaXT6luwK8xaMMO+hUWq6MlrFLEaN+GRJOtPSaW6YlApmEDhnkSTaUw+9tSy4Tm9FkvPVOsapG5+irt9RXyHhkWMaCW6jw==");
    public static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    public static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    Crypto crypto = Crypto.createWithKey(KEY, IV);

    @Test
    public void encryptTest() throws Exception {
        Bin actual = crypto.encrypt(TEXT);

        System.out.println("Actual = " + actual.toBase64());
        System.out.println("Expected = " + ENCRYPTED.toBase64());

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
