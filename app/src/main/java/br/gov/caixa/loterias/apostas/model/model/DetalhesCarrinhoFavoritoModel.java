package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.repository.CarrinhosFavoritosRepository;

public class DetalhesCarrinhoFavoritoModel extends AppModel{
	private CarrinhosFavoritosRepository repository;
	private List<ApostaCarrinhoFavoritoDTO> listApostas;
	private Long idCarrinho;

	public DetalhesCarrinhoFavoritoModel(Activity activity) {
		super(activity);
		repository = new CarrinhosFavoritosRepository(getActivity());
		listApostas = new ArrayList<>();
	}

	public void buscaApostas(OnSilceListener<List<ApostaCarrinhoFavoritoDTO>> listener) {
		repository.buscaApostas(idCarrinho, listener);
	}

	public List<ApostaCarrinhoFavoritoDTO> getList() {
		return listApostas;
	}

	public void setListApostas(List<ApostaCarrinhoFavoritoDTO> listApostas) {
		this.listApostas = listApostas;
	}

	public Long getIdApostaByPosition(int position) {
		return listApostas.get(position).getId();
	}

	public void deletaApostaCarrinhoFavorito(Long idAposta, OnSilceListener<List<ApostaCarrinhoFavoritoDTO>> listener) {
		repository.deletaApostaCarrinho(idCarrinho.toString(), idAposta.toString(), listener);
	}

	public void excluiCarrinho(Long idCarrinho, OnSilceListener<List<CarrinhoFavoritoDTO>> listener) {
		repository.excluiCarrinho(idCarrinho, listener);
	}

	public Long getIdCarrinho() {
		return idCarrinho;
	}

	public void setIdCarrinho(Long idCarrinho) {
		this.idCarrinho = idCarrinho;
	}
}
