package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;

/**
 * Created by joafilho on 22/01/2018.
 * Class EscudoEquipeUtil
 */

public class EscudoEquipeUtil {

    public static void setEscudo(final Context context, final ParametroEquipe parametroEquipe, final ImageView imageView) {
        //Comentada a chamada do servico para a maga da virada
        //O app mostrará o escudo padrao
        imageView.setImageResource(R.drawable.time_padrao);
        imageView.setPadding(0, 0, 0, 12);

        Bitmap bmImage = EscudoBO.getInstance().buscaEscudo(parametroEquipe.getNumero());
        if (bmImage != null) {
            imageView.setImageBitmap(bmImage);
        }

//        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
//        apostaBO.obterEscudoTime(parametroEquipe.getNumero().toString(), new RequestListener<EscudoEquipeEsportivaDTOResponse>() {
//            @Override
//            public void onResponse(EscudoEquipeEsportivaDTOResponse response) {
//                if (response != null && response.getPayload() != null) {
//                    String imagemString = (response.getPayload()).getImagem();
//                    if (imagemString != null) {
//                        try {
//                            byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);
//                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//                            //Glide.with(context).load(bytes).into(imageView);
//                        } catch (Exception exception) {
//                            Log.d("error", "Error: ", exception.getCause());
//                        }
//                    } else {
//                        imageView.setImageResource(R.drawable.time);
//                    }
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                imageView.setImageResource(R.drawable.time);
//            }
//        });
//    }
    }
}
