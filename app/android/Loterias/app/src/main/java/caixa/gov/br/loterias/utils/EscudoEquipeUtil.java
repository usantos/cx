package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.VolleyError;

import org.apache.commons.lang.StringUtils;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.EscudoEquipeEsportivaDTO;
import caixa.gov.br.loterias.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.utils.interfaces.EscudoEquipeListener;

/**
 * Created by joafilho on 22/01/2018.
 */

public class EscudoEquipeUtil {

    public static void retornaEscudo(final Context context, final ParametroEquipe parametroEquipe, final EscudoEquipeListener listener, final int posicao) {
        ApostaBO apostaBO = new ApostaBO(context);
        apostaBO.obterEscudoTime(parametroEquipe.getNumero().toString(), new SilceClient.Listener<EscudoEquipeEsportivaDTOResponse>() {
            @Override
            public void onResponse(EscudoEquipeEsportivaDTOResponse response) {

                if (response != null && response.getPayload() != null) {
                    String imagemString = ((EscudoEquipeEsportivaDTO) response.getPayload()).getImagem();
                    if (StringUtils.isNotEmpty(imagemString)) {
                        parametroEquipe.setEscudo(stringToBitmap(imagemString));
                    }else{
                        parametroEquipe.setEscudo(drawableToBitmap(context, R.drawable.time));
                    }
                }
                listener.buscarEscudo(posicao + 1);
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.buscarEscudo(posicao + 1);
                MensagensNetwork.setMensagem(error, context);
            }
        });
    }

    private static Bitmap drawableToBitmap(Context context, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return bitmap;
    }

    private static Bitmap stringToBitmap(String imagem) {
        byte[] bytes = Base64.decode(imagem, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
