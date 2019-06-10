package br.gov.caixa.loterias.apostar.interno.utils.helper;

import android.app.Activity;
import android.content.Context;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ApostasConfirmadasAdapter;

public class ConferirResultadoHelper  {

    public static ConferirResultadoHandler conferirResultadoHandler;

    /**
     * Conferir resultado de acordo FA8 CSU18
     *
     * @param apostaId Long
     */
    public static void conferirResultado(final Long apostaId, final Activity contextParam) {
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

    public static void conferirResultado(ApostasConfirmadasAdapter.ViewHolder holder, final Long apostaId, final Activity contextParam) {
        DialogUtils.show(contextParam);
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {
                DialogUtils.dismiss();

                final DTOEnumLong resultCheckFinal = new DTOEnumLong();
                resultCheckFinal.setValor(result.getPayload().getValor().longValue());
                resultCheckFinal.setDescricao(result.getPayload().getDescricao());

                conferirResultadoHandler.handle(holder,resultCheckFinal);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error, contextParam);
            }
        });
    }

    public static void conferirResultadoWithoutLoading(ApostasConfirmadasAdapter.ViewHolder holder, final Long apostaId, final Activity contextParam) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {
                final DTOEnumLong resultCheckFinal = new DTOEnumLong();
                resultCheckFinal.setValor(result.getPayload().getValor().longValue());
                resultCheckFinal.setDescricao(result.getPayload().getDescricao());

                conferirResultadoHandler.handle(holder,resultCheckFinal);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onErrorWithoutAlert(holder);
            }
        });
    }

    private static void onError(VolleyError error, Activity contextParam) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, contextParam);

        ViewUtils.alert(contextParam, msgError);
    }

    private static void onErrorWithoutAlert(ApostasConfirmadasAdapter.ViewHolder holder) {
        conferirResultadoHandler.handleError(holder);
    }
}
