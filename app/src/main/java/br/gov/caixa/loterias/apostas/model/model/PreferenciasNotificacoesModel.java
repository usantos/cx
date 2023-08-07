package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.PreferenciaNotificacao;

public class PreferenciasNotificacoesModel extends AppModel {

	public PreferenciasNotificacoesModel(Activity activity) {
		super(activity);
	}

	public ArrayList<PreferenciaNotificacao> getListNotificacoes() {
		ArrayList<PreferenciaNotificacao> preferencias = new ArrayList<>();
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.todas_as_notificacoes), "", false));
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.pagamentos_resgates), getActivity().getString(R.string.confirmacao_e_pendencias_de_pagamentos), false));
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.lembretes_sorte), getActivity().getString(R.string.avisos_quando_houver_oportunidade_ganhar_mais), false));
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.resultados_disponiveis), getActivity().getString(R.string.avisos_resultados_disponiveis), false));
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.dicas_novidades), getActivity().getString(R.string.novos_produtos_app), false));
		preferencias.add(new PreferenciaNotificacao(getActivity().getString(R.string.alertas_segurancas), getActivity().getString(R.string.avisos_melhorar_experiencia), false));
		return preferencias;
	}

}
