package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;

public class BolaoModel extends AppModel{

	public BolaoModel(Activity activity) {
		super(activity);
	}

	public List<Bolao> getBoloes(){
		List<Bolao> bolaoList = new ArrayList<>();

		bolaoList.add(new Bolao("mega-sena", "Conc. 2352", "Serra Dourada", "Santos - SP", "R$ 150,00", "R$ 150.000.000,00", 3));
		bolaoList.add(new Bolao("mega-sena", "Conc. 2352", "Serra Dourada", "Santos - SP", "R$ 40,00", "R$ 140.000.000,00", 2));
		bolaoList.add(new Bolao("mega-sena", "Conc. 2352", "Serra Dourada", "Santos - SP", "R$ 50,00", "R$ 130.000.000,00", 4));
		bolaoList.add(new Bolao("mega-sena", "Conc. 2353", "Serra Dourada", "Santos - SP", "R$ 250,00", "R$ 120.000.000,00", 5));
		bolaoList.add(new Bolao("mega-sena", "Conc. 2353", "Serra Dourada", "Santos - SP", "R$ 10,00", "R$ 110.000.000,00", 4));
		bolaoList.add(new Bolao("mega-sena", "Conc. 2353", "Serra Dourada", "Santos - SP", "R$ 100,00", "R$ 100.000.000,00", 3));

		return bolaoList;
	}

}
