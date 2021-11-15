package br.gov.caixa.loterias.apostas.utils.helper;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;

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
                long codigoResultado = result.getPayload().getValor().longValue();
                if(!(codigoResultado == SituacaoResultadoBilheteEnum.PREMIADA_AINDA_CONCORRENDO.getValor() || codigoResultado == SituacaoResultadoBilheteEnum.PREMIADA.getValor())){
                    DialogUtils.dismiss();
                }
                if (result.getRedirect() != null){
                    DialogUtils.dismiss();
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), contextParam);
                }

                final DTOEnumLong resultCheckFinal = new DTOEnumLong();
                resultCheckFinal.setValor(codigoResultado);
                resultCheckFinal.setDescricao(result.getPayload().getDescricao());

                conferirResultadoHandler.handle(resultCheckFinal);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                onError(error, contextParam);
            }
        });
    }

    public static void conferirResultado(ApostaConfirmadaHolder holder, final Long apostaId, final Activity contextParam) {
        DialogUtils.show(contextParam);
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), contextParam);
                }

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

    public static void conferirResultadoWithoutLoading(ApostaConfirmadaHolder holder, final Long apostaId, final Activity contextParam) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaConferir(apostaId, new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {
                if (result.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), contextParam);
                }
                final DTOEnumLong resultCheckFinal = new DTOEnumLong();
                resultCheckFinal.setValor(result.getPayload().getValor().longValue());
                resultCheckFinal.setDescricao(result.getPayload().getDescricao());

                if(DialogUtils.isShow()){
                    DialogUtils.dismiss();
                }
                conferirResultadoHandler.handle(holder,resultCheckFinal);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onErrorWithoutAlert(holder, error);
            }
        });
    }

    private static void onError(VolleyError error, Activity activity) {
        DialogUtils.dismiss();
        RedirectNetwork.checkRedirect(error,activity);
    }

    private static void onErrorWithoutAlert(ApostaConfirmadaHolder holder, VolleyError error) {
        conferirResultadoHandler.handleError(holder, error);
    }
}
