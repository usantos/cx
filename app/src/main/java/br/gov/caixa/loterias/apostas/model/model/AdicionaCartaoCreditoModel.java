package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import org.joda.time.DateTime;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;


public class AdicionaCartaoCreditoModel extends AppModel{

	public AdicionaCartaoCreditoModel(Activity activity) {
		super(activity);
	}

	public ArrayList<String> getListaMeses() {
		ArrayList<String> listaMeses = new ArrayList<>();
		listaMeses.add(getActivity().getString(R.string.title_mesDeValidade));
		for(int x = 1; x <= 12; x++){
			listaMeses.add(x < 10 ? "0" + x: String.valueOf(x));
		}
		return listaMeses;
	}

	public ArrayList<String> getListaAnos() {
		ArrayList<String> listaAnos = new ArrayList<>();
		listaAnos.add(getActivity().getString(R.string.title_anoDeValidade));
		DateTime joda = new DateTime();
		listaAnos.add("" + joda.getYear());
		for(int x = 1; x <= 20; x++){
			listaAnos.add("" + joda.plusYears(x).getYear());
		}
		return listaAnos;
	}

	public boolean isMercadoPago(Long value) {
		return MeioPagamentoUtils.isMercadoPago(value);
	}

}
