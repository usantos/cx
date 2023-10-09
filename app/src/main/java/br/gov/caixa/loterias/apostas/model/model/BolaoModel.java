package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class BolaoModel extends AppModel{
	private List<Bolao> bolaoList;

	public BolaoModel(Activity activity) {
		super(activity);
		initListaBolao();
	}

	private void initListaBolao() {
		bolaoList = new ArrayList<>();

		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2352, "Serra Dourada", "Santos - SP", "R$ 150,00", "R$ 150.000.000,00", 3, false));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2352, "Serra Dourada", "Santos - SP", "R$ 40,00", "R$ 140.000.000,00", 2, true));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2352, "Serra Dourada", "Santos - SP", "R$ 50,00", "R$ 130.000.000,00", 4, true));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 250,00", "R$ 120.000.000,00", 5, false));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 10,00", "R$ 110.000.000,00", 4, true));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 100,00", "R$ 100.000.000,00", 3, false));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 100,00", "R$ 100.000.000,00", 3, false));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 100,00", "R$ 100.000.000,00", 3, false));
		bolaoList.add(new Bolao(ModalidadeEnum.MEGA_SENA, 2353, "Serra Dourada", "Santos - SP", "R$ 100,00", "R$ 100.000.000,00", 3, false));

	}

	public List<Bolao> getListBolao(){
		List<Bolao> boloes = new ArrayList<>();

		for (Bolao bolao: bolaoList){
			if (!bolao.isDestaque()){
				boloes.add(bolao);
			}
		}

		return boloes;
	}

	public List<Bolao> getListCarrossel(){
		List<Bolao> boloes = new ArrayList<>();

		for (Bolao bolao: bolaoList){
			if (bolao.isDestaque()){
				boloes.add(bolao);
			}
		}

		return boloes;
	}

}
