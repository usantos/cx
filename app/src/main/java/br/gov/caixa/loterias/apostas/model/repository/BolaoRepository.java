package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
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
            if (filtroAplicado.getQtdMinCotas() !=null && filtroAplicado.getQtdMinCotas() > 0){
                filtro.setQtdMinimaCota(filtroAplicado.getQtdMinCotas());
            }

            if(filtroAplicado.getQtdMaxCotas() != null && filtroAplicado.getQtdMaxCotas() > 0){
                filtro.setQtdMaximaCota(filtroAplicado.getQtdMaxCotas());
            }

            if(filtroAplicado.getQtdDezenas() != null && filtroAplicado.getQtdDezenas() > 0){
                filtro.setQtdDezenas(filtroAplicado.getQtdDezenas());
            }

            if(filtroAplicado.getQtdApostas() != null && filtroAplicado.getQtdApostas() > 0){
                filtro.setQtdApostas(filtroAplicado.getQtdApostas());
            }

            if(filtroAplicado.getNumerosQuero() !=null && !filtroAplicado.getNumerosQuero().isEmpty()){
                filtro.setNumerosQuero(filtroAplicado.getNumerosQuero());
            }

            if(filtroAplicado.getNumerosNaoQuero() !=null && !filtroAplicado.getNumerosNaoQuero().isEmpty()){
                filtro.setNumerosNaoQuero(filtroAplicado.getNumerosNaoQuero());
            }

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
        ApostaSilceBO.getInstance().getDetalhesBolao(codigoBolao, new RequestListener<DetalheBolaoResponse>() {
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
        ApostaSilceBO.getInstance().getBoloesDisponiveis(filtro,
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

    public void incluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
        ApostaSilceBO.getInstance().incluirLotericaFavorita(idLoterica, new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                listener.success(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.error(error);
            }
        });
    }

    public void excluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
        ApostaSilceBO.getInstance().excluirLotericaFavorita(idLoterica, new RequestListener<RetornoPadraoResponse>() {

            @Override
            public void onResponse(RetornoPadraoResponse response) {
                listener.success(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.error(error);
            }
        });
    }

}
