package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;
import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class BolaoRepository extends AppRepository {

    private static final int PAGINA_INICIAL = 1;
    private static final int QTD_POR_PAGINA = 30;

    public BolaoRepository(Activity activity) {
        super(activity);
    }

    public void buscarBoloesDisponiveis(int idMunicipio, int idUf, OnSilceListener<ListaBolaoDTO> listener){
        FiltroMarketPlace filtro = new FiltroMarketPlace(TipoConsultaBolaoEnum.NACIONAL,
                0, 0, PAGINA_INICIAL, QTD_POR_PAGINA);

        ApostaBO_.getInstance_(getActivity()).getBoloesDisponiveis(filtro,
                new RequestListener<BoloesDisponiveisResponse>() {
                    @Override
                    public void onResponse(BoloesDisponiveisResponse response) {
                        AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_carregou_boloes_disponiveis));
                        checkRedirect(response);

                        listener.success(response.getPayload());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppCenterManager.registraEventoErro(AppCenterManager.ERRO_BOLOES_DISPONIVEIS, error);
                        RedirectNetwork.checkRedirect(error, getActivity());
                        listener.error(error);
                    }
                });
    }

    public void buscarFiltrada(int idMunicipio, int idUf, FiltroAplicadoMarketplace filtroAplicado, OnSilceListener<ListaBolaoDTO> listener){
        FiltroMarketPlace filtro = null;
        if (filtroAplicado.getLotericaDTO() != null){
            filtro = new FiltroMarketPlace(TipoConsultaBolaoEnum.LOTERICO,
                    idMunicipio, idUf, PAGINA_INICIAL, QTD_POR_PAGINA);
            filtro.setNumeroLoterico(filtroAplicado.getLotericaDTO().getId());
        } else {
            filtro = new FiltroMarketPlace(TipoConsultaBolaoEnum.MUNICIPIO,
                    idMunicipio, idUf, PAGINA_INICIAL, QTD_POR_PAGINA);
        }

        filtro.setQtdMinimaCota(filtroAplicado.getQtdMinCotas().intValue());
        filtro.setQtdMaximaCota(filtroAplicado.getQtdMaxCotas().intValue());

        if (filtroAplicado.getValorMinimoAposta() != null && !filtroAplicado.getValorMinimoAposta().isEmpty()){
            filtro.setValorMinimoCota(new BigDecimal(filtroAplicado.getValorMinimoAposta()));
        }
        if (filtroAplicado.getValorMaximoAposta() != null && !filtroAplicado.getValorMaximoAposta().isEmpty()){
            filtro.setValorMaximoCota(new BigDecimal(filtroAplicado.getValorMaximoAposta()));
        }

        ApostaBO_.getInstance_(getActivity()).getBoloesDisponiveis(filtro,
                new RequestListener<BoloesDisponiveisResponse>() {
                    @Override
                    public void onResponse(BoloesDisponiveisResponse response) {
                        AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_carregou_boloes_disponiveis));
                        checkRedirect(response);

                        listener.success(response.getPayload());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppCenterManager.registraEventoErro(AppCenterManager.ERRO_BOLOES_DISPONIVEIS, error);
                        RedirectNetwork.checkRedirect(error, getActivity());
                        listener.error(error);
                    }
                });
    }

    public void buscaDetalhesBolao(String codigoBolao, OnSilceListener<DetalheBolaoDTO> listener){
        ApostaBO_.getInstance_(getActivity()).getDetalhesBolao(codigoBolao, new RequestListener<DetalheBolaoResponse>() {
            @Override
            public void onResponse(DetalheBolaoResponse response) {
                AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_carregou_detalhe_bolao));
                checkRedirect(response);

                listener.success(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_DETALHE_BOLAO, error);
                RedirectNetwork.checkRedirect(error, getActivity());
                listener.error(error);
            }
        });
    }
}
