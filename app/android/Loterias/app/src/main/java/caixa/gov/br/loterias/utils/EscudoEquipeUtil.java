package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.io.File;

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

    public static void setEscudo(final Context context, final ParametroEquipe parametroEquipe, final ImageView imageView) {
        ApostaBO apostaBO = new ApostaBO(context);
        apostaBO.obterEscudoTime(parametroEquipe.getNumero().toString(), new SilceClient.Listener<EscudoEquipeEsportivaDTOResponse>() {
            @Override
            public void onResponse(EscudoEquipeEsportivaDTOResponse response) {

                if (response != null && response.getPayload() != null) {
                    String  imagemString = ((EscudoEquipeEsportivaDTO) response.getPayload()).getImagem();
                    if (StringUtils.isNotEmpty(imagemString) || imagemString != null) {
                        try{
                            byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            imageView.setImageBitmap(Bitmap.createBitmap(bitmap));
                        }catch (Exception exception){
                            Log.d("error", "Error: ", exception.getCause());
                        }
                    }else{
                        imageView.setImageResource(R.drawable.time);
                    }
                }

            }
        }, new SilceClient.ErrorListener() {
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
