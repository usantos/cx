package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaResponse;

public class MinhasApostasRepository extends AppRepository {
    //region Variables
    private ApostaSilceBO apostaSilceBO;
    //endregion

    //region Constructors
    public MinhasApostasRepository(Activity activity) {
        super(activity);
        apostaSilceBO = ApostaSilceBO.getInstance();
    }
    //endregion

    //region Methodes
    public void buscaConfigConsulta( OnSilceListener<ConfigConsultaDTO> listener) {
        apostaSilceBO.buscaConfigConsultaApostasConfirmadas(new RequestListener<ConfigConsultaResponse>() {
            @Override
            public void onResponse(ConfigConsultaResponse response) {
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
    //endregion

}