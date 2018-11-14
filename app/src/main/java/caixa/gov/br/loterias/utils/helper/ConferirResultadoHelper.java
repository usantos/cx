package caixa.gov.br.loterias.utils.helper;

import android.content.Context;

import com.android.volley.VolleyError;

import java.util.Objects;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumIntegerResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import caixa.gov.br.loterias.utils.DialogUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.ResultadoApostaConfirmadaActivity_;

/**
 * Created by joafilho on 10/04/2018.
 * Class Helper ConferirResultadoHelper
 */

public class ConferirResultadoHelper {

    public static Context context;

    /**
     * Conferir resultado de acordo FA8 CSU18
     *
     * @param apostaId
     */
    public static void conferirResultado(final Long apostaId, Context contextParam) {
        context = contextParam;
        DialogUtils.show(context);
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {

                if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor().toString(), result.getPayload().getValor().toString())) {
                    getApostaDetalhePremio(apostaId);
                } else {
                    DialogUtils.dismiss();
                    ViewUtils.alert(context, context.getString(R.string.MA020));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    public static void getApostaDetalhePremio(Long apostaId) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                DialogUtils.dismiss();
                ResultadoApostaConfirmadaActivity_.intent(context).detalhesPremioDTO(result.getPayload()).start();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private static void onError(VolleyError error) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, context);
        ViewUtils.alert(context, msgError);
    }
}
