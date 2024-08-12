package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;

public class CarrinhoRepository extends AppRepository {
    //region Variables
    private ApostaBO apostaBO;
    //endregion

    //region Constructors
    public CarrinhoRepository(Activity activity) {
        super(activity);
        apostaBO = ApostaBO_.getInstance_(activity);
    }
    //endregion

    //region Methodes
    public void buscaCarrinho(OnSilceListener<CarrinhoDTO> listener) {
        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
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