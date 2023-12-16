package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.repository.BolaoRepository;

public class DetalhesBolaoModel extends AppModel{

	private BolaoRepository bolaoRepository;

	public DetalhesBolaoModel(Activity activity) {
		super(activity);

		this.bolaoRepository = new BolaoRepository(activity);
	}

	public void buscaDetalhesBolao(String codigoBolao, OnSilceListener<DetalheBolaoDTO> listener){
		bolaoRepository.buscaDetalhesBolao(codigoBolao, listener);
	}

}
