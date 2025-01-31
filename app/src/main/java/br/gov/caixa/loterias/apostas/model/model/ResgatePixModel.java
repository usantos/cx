package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.repository.ResgatePremioPixRepository;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;

public class ResgatePixModel extends AppModel {
	private ResgatePremioPixRepository repository;

	public ResgatePixModel(Activity activity) {
		super(activity);
		repository = new ResgatePremioPixRepository(getActivity());
	}

	public void consultaDadosChave(String idAposta, String chavePix, OnSilceListener<DadosChavePixDTO> listener) {
		InformacaoPagamentoDTO informacaoPagamentoDTO = new InformacaoPagamentoDTO();
		informacaoPagamentoDTO.setChavePix(chavePix);
		informacaoPagamentoDTO.setMeioPagamento(MeioPagamentoUtil.PIX);
		repository.consultaDadosChave(idAposta, informacaoPagamentoDTO, listener);
	}
}
