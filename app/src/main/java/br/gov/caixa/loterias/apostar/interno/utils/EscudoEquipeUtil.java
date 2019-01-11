package br.gov.caixa.loterias.apostar.interno.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.apache.commons.lang.StringUtils;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroEquipe;

/**
 * Created by joafilho on 22/01/2018.
 * Class EscudoEquipeUtil
 */

public class EscudoEquipeUtil {

    public static void setEscudo(final Context context, final ParametroEquipe parametroEquipe, final ImageView imageView) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        apostaBO.obterEscudoTime(parametroEquipe.getNumero().toString(), new RequestListener<EscudoEquipeEsportivaDTOResponse>() {
            @Override
            public void onResponse(EscudoEquipeEsportivaDTOResponse response) {
                if (response != null && response.getPayload() != null) {
                    String imagemString = (response.getPayload()).getImagem();
                    if (StringUtils.isNotEmpty(imagemString) || imagemString != null) {
                        try {
                            byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);
                            Glide.with(context).load(bytes).into(imageView);
                        } catch (Exception exception) {
                            Log.d("error", "Error: ", exception.getCause());
                        }
                    } else {
                        imageView.setImageResource(R.drawable.time);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.time);
            }
        });
    }
}
