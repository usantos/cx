package br.gov.caixa.loterias.apostas.model.model;

import android.view.View;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class CartelaModel {
	public CartelaModel() {}

	public ArrayList<ExpandableHeightGridView> getListGridSuperSete(View viewLayout) {
		ArrayList<ExpandableHeightGridView> listaGridsSuperSete = new ArrayList<>();
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_primeiro));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_segundo));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_terceiro));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_quarto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_quinto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_sexto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_setimo));

		return listaGridsSuperSete;
	}

	public ArrayList<SuperSeteAdapter> getListAdaptersSuperSete(int typeGameColorDark,
																SimularApostaActivity parentActivity){
		ArrayList<SuperSeteAdapter> listaAdaptersSuperSete = new ArrayList<>();
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));

		return listaAdaptersSuperSete;
	}

	public ArrayList<SuperSeteAdapter> getListAdaptersSuperSete(int typeGameColorDark,
																SimulaActivity parentActivity){
		ArrayList<SuperSeteAdapter> listaAdaptersSuperSete = new ArrayList<>();
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));

		return listaAdaptersSuperSete;
	}

	public void preencheGridComListaSuperSete(ArrayList<ExpandableHeightGridView> listaGridsSuperSete,
											  ArrayList<SuperSeteAdapter> listaAdaptersSuperSete){
		listaGridsSuperSete.get(0).setAdapter(listaAdaptersSuperSete.get(0));
		listaGridsSuperSete.get(1).setAdapter(listaAdaptersSuperSete.get(1));
		listaGridsSuperSete.get(2).setAdapter(listaAdaptersSuperSete.get(2));
		listaGridsSuperSete.get(3).setAdapter(listaAdaptersSuperSete.get(3));
		listaGridsSuperSete.get(4).setAdapter(listaAdaptersSuperSete.get(4));
		listaGridsSuperSete.get(5).setAdapter(listaAdaptersSuperSete.get(5));
		listaGridsSuperSete.get(6).setAdapter(listaAdaptersSuperSete.get(6));
	}
}
