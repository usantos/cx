package caixa.gov.br.loterias.bo.silce.internals;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Crypto {
    Base64 base64 = new Base64();
    IvParameterSpec iv;
    SecretKeySpec key;
    KeyGenerator gen;

    protected Crypto(Bin key, Bin initializationVector) {
        try {
            gen = KeyGenerator.getInstance("AES");
            //iv = new IvParameterSpec(Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==").toBytes());
            //key = new SecretKeySpec(Bin.fromBase64("QEm06ZAThg1E314zziahsg==").toBytes(), "AES");
            this.key = new SecretKeySpec(key.toBytes(), "AES");
            this.iv = new IvParameterSpec(initializationVector.toBytes());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Não foi possível inicializar as funcionalidades de criptografia", e);
        }
    }

    public Bin encrypt(Bin data) {
        return doFinal(Cipher.ENCRYPT_MODE, data);
    }

    public Bin decrypt(Bin data) {
        return doFinal(Cipher.DECRYPT_MODE, data);
    }

    private Bin doFinal(int opmode, Bin data) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, key, iv);
            return Bin.fromBytes(cipher.doFinal(data.toBytes()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Falha ao criptografar dados", e);
        }
    }

    public static Crypto createWithKey(Bin key, Bin initializationVector) {
        return new Crypto(key, initializationVector);
    }
}
