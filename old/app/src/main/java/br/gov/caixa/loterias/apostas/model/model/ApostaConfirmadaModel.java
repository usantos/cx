package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.repository.MinhasApostasRepository;

public class ApostaConfirmadaModel{

	private MinhasApostasRepository repository;

	public ApostaConfirmadaModel(Activity activity) {
		this.repository = new MinhasApostasRepository(activity);
	}

	public void requestConfigConsulta(OnSilceListener<ConfigConsultaDTO> listener) {
		repository.buscaConfigConsulta(listener);
	}
}
