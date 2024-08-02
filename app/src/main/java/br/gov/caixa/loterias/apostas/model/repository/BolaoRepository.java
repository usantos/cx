package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class BolaoRepository extends AppRepository {

    public BolaoRepository(Activity activity) {
        super(activity);
    }

    public void buscarBoloesDisponiveis(PaginacaoFiltro paginacao, FiltroAplicadoMarketplace filtroAplicado, OnSilceListener<ListaBolaoDTO> listener){
        FiltroMarketPlace filtro = getFiltroMarketPlace(paginacao);

        if (filtroAplicado != null){
            filtro.setQtdMinimaCota(filtroAplicado.getQtdMinCotas().intValue());
            filtro.setQtdMaximaCota(filtroAplicado.getQtdMaxCotas().intValue());

            if (filtroAplicado.getValorMinimoAposta() != null && !filtroAplicado.getValorMinimoAposta().isEmpty()){
                filtro.setValorMinimoCota(new BigDecimal(filtroAplicado.getValorMinimoAposta()));
            }
            if (filtroAplicado.getValorMaximoAposta() != null && !filtroAplicado.getValorMaximoAposta().isEmpty()){
                filtro.setValorMaximoCota(new BigDecimal(filtroAplicado.getValorMaximoAposta()));
            }
            if (filtroAplicado.getLotericaDTO() != null && filtroAplicado.getLotericaDTO().getId() > 0) {
                filtro.setNumeroLoterico(filtroAplicado.getLotericaDTO().getId());
            }
            if (filtroAplicado.getModalidade() != null) {
                filtro.setIdModalidade(ModalidadeEnum.fromStringToIdModalidade(filtroAplicado.getModalidade()));
            } else {
                //força todas as modalidades
                filtro.setIdModalidade(null);
            }
            if (filtroAplicado.getTipoConcurso() != null) {
                filtro.setTipoConcurso(TipoConcursoEnum.fromStringToIdTipoConcurso(filtroAplicado.getTipoConcurso()));
            } else {
                //força todos os tipos de concurso
                filtro.setTipoConcurso(null);
            }
        }

        //listener.success(BolaoListMock.getInstance().filtro(filtro));
        buscaBoloes(filtro, listener);
    }

    private FiltroMarketPlace getFiltroMarketPlace(PaginacaoFiltro paginacao) {
        return new FiltroMarketPlace(paginacao.getTipoConsulta(), paginacao.getIdMunicipio(), paginacao.getIdUf(), paginacao.getPagina(), paginacao.getQtdPorPagina(), paginacao.getIdModalidade(), paginacao.getIdTipoConcurso());
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