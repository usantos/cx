package br.gov.caixa.loterias.apostar.utils.helper;

import android.content.Context;

import com.android.volley.VolleyError;

import java.util.Objects;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.ResultadoApostaConfirmadaActivity_;

/**
 * Created by joafilho on 10/04/2018.
 * Class Helper ConferirResultadoHelper
 */

public class ConferirResultadoHelper {

    /**
     * Conferir resultado de acordo FA8 CSU18
     *
     * @param apostaId Long
     */
    public static void conferirResultado(final Long apostaId, final Context contextParam) {
        DialogUtils.show(contextParam);
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {

                if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor().toString(), result.getPayload().getValor().toString())) {
                    getApostaDetalhePremio(apostaId, contextParam);
                } else {
                    DialogUtils.dismiss();
                    ViewUtils.alert(contextParam, contextParam.getString(R.string.MA020));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error, contextParam);
            }
        });
    }

    private static void getApostaDetalhePremio(Long apostaId, final Context contextParam) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                DialogUtils.dismiss();
                ResultadoApostaConfirmadaActivity_.intent(contextParam).detalhesPremioDTO(result.getPayload()).start();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error, contextParam);
            }
        });
    }

    private static void onError(VolleyError error, Context contextParam) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, contextParam);
        ViewUtils.alert(contextParam, msgError);
    }
}
