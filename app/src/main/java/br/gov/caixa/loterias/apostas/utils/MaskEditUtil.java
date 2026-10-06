package br.gov.caixa.loterias.apostas.utils;

/**
 * Created by cedesbr450 on 12/01/18.
 */
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.view.listener.TextWatcherChangedListener;


public class MaskEditUtil {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_FONE = "(##)#####-####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##/##/####";
    public static final String FORMAT_NSBI = "####-#################-##";
    public static final String FORMAT_LOT = "##.######-#";
    public static final String FORMAT_CRED_CARD = "#### #### #### ####";
    public static final String FORMAT_CRED_CARD_AMEX = "#### ###### #####";


    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param ediTxt
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText ediTxt, final String mask, TextWatcherChangedListener listener) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";
            String maskAuxiliar = mask;
            private List<String> binsAmex = Arrays.asList("34", "37");

            @Override
            public void afterTextChanged(final Editable s) {
                if (listener != null){
                    listener.onChanged(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskEditUtil.unmask(s.toString());
                String mascara = "";
                if (mask.equals(FORMAT_CRED_CARD) || mask.equals(FORMAT_CRED_CARD_AMEX)) {
                    if (s.length() >= 2) {
                        if (binsAmex.contains(s.toString().substring(0, 2))) {
                            maskAuxiliar = FORMAT_CRED_CARD_AMEX;
                        } else {
                            maskAuxiliar = FORMAT_CRED_CARD;
                        }
                    }
                }
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : maskAuxiliar.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[)]", "");
    }

    public static TextWatcher textWatcherAfterChangedListener(TextWatcherChangedListener listener) {
        return new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                listener.onChanged(s.toString());
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }
        };
    }

    public static TextWatcher textWatcherOnTextTChangedListener(TextWatcherChangedListener listener) {
        return new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                listener.onChanged(s.toString());
            }
        };
    }

}
