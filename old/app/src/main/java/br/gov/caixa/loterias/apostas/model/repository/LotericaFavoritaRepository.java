package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericasFavoritasResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class LotericaFavoritaRepository extends AppRepository {

    public LotericaFavoritaRepository(Activity activity) {
        super(activity);
    }

    public void getLotericasFavoritas(
            OnSilceListener<List<LotericaFavoritaDTO>> listener
    ){
        buscaLotericasFavoritas(listener);
    }

    public void buscaLotericasFavoritas(
            OnSilceListener<List<LotericaFavoritaDTO>> listener
    ){
        ApostaSilceBO.getInstance().getLotericasFavoritas(
                new RequestListener<LotericasFavoritasResponse>() {
                    @Override
                    public void onResponse(LotericasFavoritasResponse response) {
                        AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_carregou_lotericas_favoritas));
                        checkRedirect(response);
                        listener.success(response.getPayload());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppCenterManager.registraEventoErro(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA, error);
                        RedirectNetwork.checkRedirect(error, getActivity());
                        listener.error(error);
                    }
                }
        );
    }
}