package br.gov.caixa.loterias.apostas.bo.silce;


import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class BinTest {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    @Test
    public void bytes() throws Exception {
        String msg = "Olá Mundo";
        assertThat(new String(Bin.fromBytes(msg.getBytes()).toBytes()),
                is(msg));
    }

    @Test
    public void base64() throws Exception {
        String msg = "Olá Mundo";
        String msgBase64 = "T2zDoSBNdW5kbw==";

        checkArray(Bin.fromBase64("QEm06ZAThg1E314zziahsg").toBytes());
        assertThat(Bin.fromBase64(msgBase64).toBase64(),
                is(msgBase64));
        assertThat(new String(Bin.fromBytes(msg.getBytes(CHARSET_UTF8)).toBase64()),
                is(msgBase64));
        assertThat(new String(Bin.fromBase64(msgBase64).toBytes(), CHARSET_UTF8),
                is(msg));
    }

    void checkArray(byte[] bytes) {
        int[] actual = new int[bytes.length];
        int[] expected = {64, 73, 180, 233, 144, 19, 134, 13, 68, 223, 94, 51, 206, 38, 161, 178};

        for (int i = 0; i < bytes.length; ++i) {
            actual[i] = bytes[i] < 0 ? bytes[i] + 256 : bytes[i];
        }

        assertThat(actual, is(expected));
    }

    @Test
    public void utf8() throws Exception {
        String msg = "Olá Mundo";
        String msgBase64 = "T2zDoSBNdW5kbw==";

        assertThat(Bin.fromUtf8(msg).toUtf8(),
                is(msg));
        assertThat(new String(Bin.fromUtf8(msg).toBase64()),
                is(msgBase64));
        assertThat(new String(Bin.fromBase64(msgBase64).toUtf8()),
                is(msg));
    }
}

