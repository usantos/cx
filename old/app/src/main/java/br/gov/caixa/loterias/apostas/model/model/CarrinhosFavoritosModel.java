package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.repository.CarrinhosFavoritosRepository;


public class CarrinhosFavoritosModel extends AppModel{

	private CarrinhosFavoritosRepository repository;
	private List<CarrinhoFavoritoDTO> listCarrinhoFavorito;

	public CarrinhosFavoritosModel(Activity activity) {
		super(activity);
		this.repository = new CarrinhosFavoritosRepository(activity);
		this.listCarrinhoFavorito = new ArrayList<>();
	}

	public void buscaCarrinhosFavoritos(OnSilceListener<List<CarrinhoFavoritoDTO>> listener) {
		repository.buscaCarrinhosFavoritos(listener);
	}

	public CarrinhoFavoritoDTO getCarrinhoById(Long id){
		CarrinhoFavoritoDTO carrinhoFavorito = null;
		for (CarrinhoFavoritoDTO carrinho : this.listCarrinhoFavorito) {
			if (carrinho.getId() == id){
				carrinhoFavorito = carrinho;
				break;
			}
		}
		return carrinhoFavorito;
	}

	public void setListCarrinhosFavoritos(List<CarrinhoFavoritoDTO> list) {
		this.listCarrinhoFavorito = list;
	}

	public boolean existeCarrinho(Long id) {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(getActivity());
		return crud.existeCarrinho(id);
	}

	public Long getIdCarrinhoByPosition(int position){
		return listCarrinhoFavorito.get(position).getId();
	}

	public void excluirCarrinho(Long id, OnSilceListener<List<CarrinhoFavoritoDTO>> listener) {
		repository.excluiCarrinho(id, listener);
	}

	public void buscaApostas(Long idCarrinho, OnSilceListener<List<ApostaCarrinhoFavoritoDTO>> listener) {
		repository.buscaApostas(idCarrinho, listener);
	}

	public void transformaCarrinho(Long idCarrinho, int opcaoSelecionada, OnSilceListener<CarrinhoDTO> listener) {
		repository.transformaCarrinho(idCarrinho, opcaoSelecionada, listener);
	}

	public void salvarCarrinhoFavoritoLocal(Long idCarrinho) {
		repository.salvarCarrinhoFavoritoLocal(getCarrinhoById(idCarrinho));
	}
}
