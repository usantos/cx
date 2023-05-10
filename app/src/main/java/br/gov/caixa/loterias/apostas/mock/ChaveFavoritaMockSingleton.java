package br.gov.caixa.loterias.apostas.mock;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.model.bean.ChavePix;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;

public class ChaveFavoritaMockSingleton {

	private ArrayList<ChavePix> chaves;
	private static ChaveFavoritaMockSingleton instance;

	private ChaveFavoritaMockSingleton(){
		this.chaves = new ArrayList<>();
		this.chaves.add(new ChavePix(ChavePixEnum.CELULAR, "(61) 9 9876-5432"));
	}

	public static ChaveFavoritaMockSingleton getInstance(){
		if (instance == null){
			instance = new ChaveFavoritaMockSingleton();
		}
		return instance;
	}

	public ArrayList<ChavePix> getChaves(){
		return chaves;
	}

	public void addChave(ChavePix chave){
		this.chaves.add(chave);
	}

}
