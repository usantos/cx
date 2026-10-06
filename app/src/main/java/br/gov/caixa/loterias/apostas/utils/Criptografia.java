package br.gov.caixa.loterias.apostas.utils;

public class Criptografia {

    // Declaration of all the required variables
    private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String numberet = "0123456789";
    private static int index;
    private static int updated_index;
    private static int final_index;
    private static int index_p_t_l;
    private static int index_s_t_l;
    private static String plainTxt;
    private static String cipherTxt;
    private static String finalTxt;

    // code for encryption
    public static String encrypt(String plaintext, int encrptionKey) {
        reset();
        plaintext = plaintext.toUpperCase();
        for (index = 0; index < plaintext.length(); index++) {
            if (plaintext.charAt(index) != ' ') {
                index_p_t_l = numberet.indexOf(plaintext.charAt(index));
                updated_index = encrptionKey + numberet.indexOf(plaintext.charAt(index));
                if (updated_index >= numberet.length()) {
                    final_index = updated_index - numberet.length();
                } else
                    final_index = updated_index;
                cipherTxt = numberet.substring(final_index, final_index + 1);
                finalTxt = finalTxt + cipherTxt;
            }
        }
        return finalTxt;
    }

    // code for decryption
    public static String decrypt(String ciphertext, int decryptionKey) {
        reset();
        ciphertext = ciphertext.toUpperCase();
        for (index = 0; index < ciphertext.length(); index++) {
            if (ciphertext.charAt(index) != ' ') {
                index_p_t_l = numberet.indexOf(ciphertext.charAt(index));
                index_s_t_l = index_p_t_l;
                updated_index = numberet.indexOf(ciphertext.charAt(index)) - decryptionKey;
                if (updated_index < 0) {
                    final_index = updated_index + numberet.length();
                } else
                    final_index = updated_index;
                plainTxt = numberet.substring(final_index, final_index + 1);
                finalTxt += plainTxt;
            }
        }
        return finalTxt;
    }

    private static void reset() {
        finalTxt = "";
    }
}
