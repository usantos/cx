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
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.ApostaConfirmadaDetalhesActivity_;
import br.gov.caixa.loterias.apostar.view.ResultadoApostaConfirmadaActivity_;

public class ConferirResultadoHelper  {

    public static ConferirResultadoHandler conferirResultadoHandler;

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
                DialogUtils.dismiss();

                final DTOEnumLong resultCheckFinal = new DTOEnumLong();
                resultCheckFinal.setValor(result.getPayload().getValor().longValue());
                resultCheckFinal.setDescricao(result.getPayload().getDescricao());

                conferirResultadoHandler.handle(resultCheckFinal);
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
