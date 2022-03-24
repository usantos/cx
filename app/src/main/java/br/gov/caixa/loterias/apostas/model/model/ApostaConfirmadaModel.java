package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesDTO;

public class ApostaConfirmadaModel {

	private Activity activity;

	private List<MesAnoDTO> mesesPadrao;

	public ApostaConfirmadaModel(Activity activity) {
		this.activity = activity;
		iniciaMesesPadrao();
	}

	public int getQtdMesAnoPadrao(){
		return mesesPadrao.size();
	}

	public List<MesAnoDTO> getMesesPadrao(){
		if (mesesPadrao == null){
			iniciaMesesPadrao();
		}

		return mesesPadrao;
	}

	private void iniciaMesesPadrao() {
		mesesPadrao = new ArrayList<>();
		mesesPadrao.add(setMesAnoPadroes(90l));
		mesesPadrao.add(setMesAnoPadroes(45l));
		mesesPadrao.add(setMesAnoPadroes(30l));
	}

	private MesAnoDTO setMesAnoPadroes(Long dias){
		MesAnoDTO mesAno = new MesAnoDTO();
		mesAno.setAno(0l);
		mesAno.setMes(setMesPadrao(dias));
		return mesAno;
	}

	private MesDTO setMesPadrao(Long dias){
		MesDTO  mes = new MesDTO();
		mes.setValor(dias);
		mes.setDescricao(activity.getResources().getString(R.string.ultimos_qtd_dias).replace("{quantidade}", dias.toString()));
		return mes;
	}
}
