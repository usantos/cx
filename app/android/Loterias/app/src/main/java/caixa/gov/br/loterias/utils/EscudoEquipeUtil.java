package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.apache.commons.lang.StringUtils;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.EscudoEquipeEsportivaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;

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
                    String  imagemString = ((EscudoEquipeEsportivaDTO) response.getPayload()).getImagem();
                    if (StringUtils.isNotEmpty(imagemString) || imagemString != null) {
                        try{
                            byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);
                            Glide.with(context).load(bytes).into(imageView);
                        }catch (Exception exception){
                            Log.d("error", "Error: ", exception.getCause());
                        }
                    }else{
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

    private static Bitmap drawableToBitmap(Context context, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return bitmap;
    }

    private static Bitmap stringToBitmap(String imagem) {
        byte[] bytes = Base64.decode(imagem, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
