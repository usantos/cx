package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConsultaDadosChavePixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;

public class ResgatePremioPixRepository extends AppRepository {
    private ApostaBO apostaBO;

    public ResgatePremioPixRepository(Activity activity) {
        super(activity);
        apostaBO = ApostaBO.getInstance();
    }

    public void consultaDadosChave(String id, InformacaoPagamentoDTO informacoes, OnSilceListener<DadosChavePixDTO> listener) {
        apostaBO.consultaDadosChave(id, informacoes, new RequestListener<ConsultaDadosChavePixResponse>() {
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
        });
    }
}