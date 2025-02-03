package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import androidx.annotation.NonNull;

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
		repository.consultaDadosChave(idAposta, getInformacaoPagamentoDTO(chavePix), listener);
	}

	@NonNull
	private static InformacaoPagamentoDTO getInformacaoPagamentoDTO(String chavePix) {
		InformacaoPagamentoDTO informacaoPagamentoDTO = new InformacaoPagamentoDTO();
		informacaoPagamentoDTO.setChavePix(chavePix);
		informacaoPagamentoDTO.setMeioPagamento(MeioPagamentoUtil.PIX);
		return informacaoPagamentoDTO;
	}

	public void cancelarPix(String idAposta, OnSilceListener<DadosChavePixDTO> listener) {
		repository.cancelarPix(idAposta, listener);
	}

	public void resgatarPremio(String id, String chavePix, OnSilceListener listener){
		repository.resgatarPremioPix(id, getInformacaoPagamentoDTO(chavePix), listener);
	}

}
