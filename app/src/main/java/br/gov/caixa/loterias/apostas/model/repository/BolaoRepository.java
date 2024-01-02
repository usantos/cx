package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.mock.BolaoListMock;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
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

    private static final BolaoListMock mock = BolaoListMock.getInstance();
    public BolaoRepository(Activity activity) {
        super(activity);
    }

    public void buscarBoloesDisponiveis(PaginacaoFiltro paginacao, OnSilceListener<ListaBolaoDTO> listener){

        listener.success(mock.filtro(paginacao));

        //buscaBoloes(getFiltroMarketPlace(paginacao), listener);
    }

    public void buscarFiltrada(PaginacaoFiltro paginacao, FiltroAplicadoMarketplace filtroAplicado, OnSilceListener<ListaBolaoDTO> listener){
        FiltroMarketPlace filtro = getFiltroMarketPlace(paginacao);

        filtro.setQtdMinimaCota(filtroAplicado.getQtdMinCotas().intValue());
        filtro.setQtdMaximaCota(filtroAplicado.getQtdMaxCotas().intValue());

        if (filtroAplicado.getValorMinimoAposta() != null && !filtroAplicado.getValorMinimoAposta().isEmpty()){
            filtro.setValorMinimoCota(new BigDecimal(filtroAplicado.getValorMinimoAposta()));
        }
        if (filtroAplicado.getValorMaximoAposta() != null && !filtroAplicado.getValorMaximoAposta().isEmpty()){
            filtro.setValorMaximoCota(new BigDecimal(filtroAplicado.getValorMaximoAposta()));
        }

        buscaBoloes(filtro, listener);
    }

    private FiltroMarketPlace getFiltroMarketPlace(PaginacaoFiltro paginacao) {
        return new FiltroMarketPlace(paginacao.getTipoConsulta(), paginacao.getIdMunicipio(), paginacao.getIdUf(), paginacao.getPagina(), paginacao.getQtdPorPagina());
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

    private void buscaBoloes(FiltroMarketPlace filtro, OnSilceListener<ListaBolaoDTO> listener) {
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
}