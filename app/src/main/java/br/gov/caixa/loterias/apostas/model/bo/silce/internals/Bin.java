package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.util.Base64;
import java.nio.charset.Charset;

public class Bin {
    private static final Charset CHARSET_UTF8  = Charset.forName("UTF-8");
    private static final Charset CHARSET_UTF16 = Charset.forName("UTF-16");
    private static final Charset CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final int FLAGS_BASE64 = Base64.NO_WRAP;
    byte[] data;

    protected Bin(byte[] data) {
        this.data = data;
    }

    public String toBase64() {
        //return BaseEncoding.base64().encode(data);
        return Base64.encodeToString(data, FLAGS_BASE64);
    }

    public byte[] toBytes() {
        return data.clone();
    }

    public String toUtf8() {
        return new String(data, CHARSET_UTF8);
    }

    public String toIso8859_1() {
        return new String(data, CHARSET_ISO_8859_1);
    }

    public static Bin fromBase64(String data) {
        //return new Bin(BaseEncoding.base64().decode(data));
        return new Bin(Base64.decode(data, FLAGS_BASE64));
    }

    public static Bin fromBytes(byte[] data) {
        return new Bin(data.clone());
    }

    public static Bin fromUtf8(String data) {
        return new Bin(data.getBytes(CHARSET_UTF8));
    }

    public static Bin fromIso8859_1(String data) {
        return new Bin(data.getBytes(CHARSET_ISO_8859_1));
    }
}