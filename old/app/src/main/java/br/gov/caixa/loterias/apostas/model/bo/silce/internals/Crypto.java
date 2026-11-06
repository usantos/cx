package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.os.Build;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin.FLAGS_BASE64;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Crypto {
    private IvParameterSpec iv;
    private SecretKeySpec key;
    private KeyGenerator gen;

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

    static public String cripfyBarcode(String code) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String chave = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmFerQzZx7Z+JPp12mtl7B5cy+pB6L53OAFLd8h1k1gy/my0kqJGugKIZRcklszhPZMyfrNc4fkSh3QPcn4jVVizjzSyGZBc8D97yuDsID0FiNYl5v7so9/6HI95DPdTzrEiGazXm6SuIwW1ylCLKanADzcPb67+pYV3nNuaOQuw7n5e71h97DP+FgJ6xvIuIVQpGYnawmqG3O2MqF00aWXvdJTEEVPogx1azJOjjGYu97DvNj1jpmkOBb0B5I++Z+GYPM+Uu/1z+bcfsUHSkOvLtjkhDmrsbYTnJibyZgb0ohLSfOpMGsvqpqjwzhT11+Sr0Xf55gETvfiiNUnhEqQIDAQAB";
        byte[] keyBytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyBytes = java.util.Base64.getDecoder().decode(chave);
        } else {
            keyBytes = android.util.Base64.decode(chave, FLAGS_BASE64);
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(code.getBytes(UTF_8));

        String codigo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            codigo = java.util.Base64.getEncoder().encodeToString(cipherText);;
        } else {
            codigo = android.util.Base64.encodeToString(cipherText, FLAGS_BASE64);
        }

        return codigo;
    }
}
