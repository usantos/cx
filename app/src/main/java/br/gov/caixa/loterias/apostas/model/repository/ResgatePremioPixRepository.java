package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConsultaDadosChavePixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;

public class ResgatePremioPixRepository extends AppRepository {
    private ApostaSilceBO apostaSilceBO;

    public ResgatePremioPixRepository(Activity activity) {
        super(activity);
        apostaSilceBO = ApostaSilceBO.getInstance();
    }

    public void consultaDadosChave(String id, InformacaoPagamentoDTO informacoes, OnSilceListener<DadosChavePixDTO> listener) {
        apostaSilceBO.consultaDadosChave(id, informacoes, getListener(listener));
    }

    public void cancelarPix(String idAposta, OnSilceListener<DadosChavePixDTO> listener) {
        apostaSilceBO.cancelarPix(idAposta, getListener(listener));
    }

    public void resgatarPremioPix(String idAposta, InformacaoPagamentoDTO informacoes, OnSilceListener listener){
        apostaSilceBO.postResgatarAposta(new Long(idAposta), informacoes, new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                checkRedirect(response);
                listener.success(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect(error, getActivity());
                listener.error(error);
            }
        });
    }

    private RequestListener<ConsultaDadosChavePixResponse> getListener(OnSilceListener<DadosChavePixDTO> listener){
        return new RequestListener<ConsultaDadosChavePixResponse>() {
            @Override
            public void onResponse(ConsultaDadosChavePixResponse response) {
                checkRedirect(response);
                listener.success(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect(error, getActivity());
                listener.error(error);
            }
        };
    }
}