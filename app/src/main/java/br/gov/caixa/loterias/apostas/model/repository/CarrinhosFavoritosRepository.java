package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;

public class CarrinhosFavoritosRepository extends AppRepository {
    private ApostaBO apostaBO;

    public CarrinhosFavoritosRepository(Activity activity) {
        super(activity);
        apostaBO = ApostaBO_.getInstance_(activity);
    }

    public void buscaCarrinhosFavoritos(OnSilceListener<List<CarrinhoFavoritoDTO>> listener) {
        apostaBO.buscaCarrinhosFavoritos(new RequestListener<ListCarrinhoFavoritoResponse>() {
            @Override
            public void onResponse(ListCarrinhoFavoritoResponse response) {
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

    public void excluiCarrinho(Long id, OnSilceListener<List<CarrinhoFavoritoDTO>> listener) {
        apostaBO.deletaCarrinhoFavorito(id.toString(), new RequestListener<ListCarrinhoFavoritoResponse>() {
            @Override
            public void onResponse(ListCarrinhoFavoritoResponse response) {
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

    public void buscaApostas(Long idCarrinho, OnSilceListener<List<ApostaCarrinhoFavoritoDTO>> listener) {
        apostaBO.buscaApostasCarrinhoFavorito(idCarrinho, new RequestListener<ListApostaCarrinhoFavoritoResponse>() {
            @Override
            public void onResponse(ListApostaCarrinhoFavoritoResponse response) {
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

    public void transformaCarrinho(Long idCarrinho, int opcaoSelecionada, OnSilceListener<CarrinhoDTO> listener) {
        apostaBO.transformarCarrinho(idCarrinho, opcaoSelecionada, new RequestListener<CarrinhoDTOResponse>() {
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

     public void salvarCarrinhoFavoritoLocal(CarrinhoFavoritoDTO carrinho) {
        CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(getActivity());
        crud.inserirCarrinhoFavorito(carrinho);
    }
}