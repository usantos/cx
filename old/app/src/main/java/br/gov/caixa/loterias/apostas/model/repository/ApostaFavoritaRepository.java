package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class ApostaFavoritaRepository extends AppRepository {
    //region Variables
    private ApostaSilceBO apostaSilceBO;
    //endregion

    //region Constructors
    public ApostaFavoritaRepository(Activity activity) {
        super(activity);
        apostaSilceBO = ApostaSilceBO.getInstance();
    }
    //endregion

    //region Methodes
    public void buscaApostas(int offsetItem, OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO> listener) {
        ServicoFactoryUtil.getApostaService().getApostasFavoritas(offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
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

    public void buscaApostasModalidade(int modalidade, int offsetItem, OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO> listener) {
        ServicoFactoryUtil.getApostaService().getApostasFavoritasModalidades(modalidade, offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
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

    public void buscaApostasAgrupadas(OnSilceListener<AgrupadorDTOApostaFavoritaDTO> listener) {
        ServicoFactoryUtil.getApostaService().getApostasFavoritasAgrupadas(new RequestListener<AgrupadorDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaFavoritaDTOResponse response) {
                //checkRedirect(response);
                listener.success(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect(error, getActivity());
                listener.error(error);
            }
        });
    }

    public void incluirApostaFavorita(Long idApostaFavorita, int valorTipoConcurso, int qtdTeimosinhas, Boolean espelho, OnSilceListener<CarrinhoDTO> listener) {
        ServicoFactoryUtil.getApostaService().postIncluirApostaFavoritaCarrinho(idApostaFavorita, valorTipoConcurso, qtdTeimosinhas, espelho, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_efetuou_inclusao_favorito_no_carrinho));
                AlertDialogUtils.dismiss();
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

    public void alteraAposta(ApostaFavoritaDTO aposta, OnSilceListener listener) {
        apostaSilceBO.postApostasFavoritas(aposta, new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_efetuou_troca_do_nome_da_aposta_favorita));
                checkRedirect(response);
                listener.success(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect(error, getActivity());
            }
        });
    }

    public void deletaAposta(Long id, OnSilceListener listener) {
        ServicoFactoryUtil.getApostaService().deleteApostasFavoritas(id.toString(), new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_efetuou_exclusao_favorito));
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