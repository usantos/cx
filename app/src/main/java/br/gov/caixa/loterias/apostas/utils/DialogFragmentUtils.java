package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.FragmentManager;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.view.custom.AdicionarCarrinhoFavoritasView;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheModalidadeDialogListener;

public class DialogFragmentUtils {

	public static void startEscolhaModalidade(FragmentManager fm, List<ParametroSimulacao> arrayConcursos,
											  Long idAposta, String string, String txtBolao, boolean mostraTeimosinha, OnEscolheModalidadeDialogListener listener) {
		AdicionarCarrinhoFavoritasView view = AdicionarCarrinhoFavoritasView.newInstance();
		view.setArrayConcursos(arrayConcursos);
		view.setIdApostaFavorita(idAposta);
		view.setListener(listener);
		view.setMostraTeimosinha(mostraTeimosinha);
		if (txtBolao != null){
			view.setTextoBotao(txtBolao);
		}
		view.show(fm, string);
	}

}
