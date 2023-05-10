package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.mock.ChaveFavoritaMockSingleton;
import br.gov.caixa.loterias.apostas.model.bean.ChavePix;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;

public class ResgatePixModel extends AppModel {

	public ResgatePixModel(Activity activity) {
		super(activity);
	}

	public List<String> getListaChave() {
		List<String> chaves = new ArrayList<>();
		chaves.add(ChavePixEnum.CPF.getTexto());
		chaves.add(ChavePixEnum.EMAIL.getTexto());
		chaves.add(ChavePixEnum.CELULAR.getTexto());
		chaves.add(ChavePixEnum.CHAVE_ALEATORIA.getTexto());
		return chaves;
	}

	public ArrayList<ChavePix> getListaChaveFavorita(){
		return ChaveFavoritaMockSingleton.getInstance().getChaves();
	}

	public boolean temChaveFavorita() {
		return !ChaveFavoritaMockSingleton.getInstance().getChaves().isEmpty();
	}

	public ArrayList<ChavePix> excluirChaveFavorita(ChavePix chavePix) {
		ChaveFavoritaMockSingleton.getInstance().excluirPorId(chavePix.getId());
		return ChaveFavoritaMockSingleton.getInstance().getChaves();
	}
}
