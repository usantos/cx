package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.repository.BolaoRepository;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;

public class DetalhesBolaoModel extends AppModel{

	private BolaoRepository bolaoRepository;

	public DetalhesBolaoModel(Activity activity) {
		super(activity);

		this.bolaoRepository = new BolaoRepository(activity);
	}

	public void buscaDetalhesBolao(String codigoBolao, OnSilceListener<DetalheBolaoDTO> listener){
		bolaoRepository.buscaDetalhesBolao(codigoBolao, listener);
	}

	public void addBolaoCarrinho(String codigoBolao, DetalheBolaoDTO bolao) {
		AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(getActivity(), ApostaUtils.converteDetalheBolaoEmAposta(codigoBolao, bolao));
	}

	public void incluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
		bolaoRepository.incluirLotericaFavorita(idLoterica, listener);
	}

	public void excluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
		bolaoRepository.excluirLotericaFavorita(idLoterica, listener);
	}

}
