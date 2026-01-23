package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class NumerosTrevosFragment extends Fragment {

	private static final String LISTA_NUMEROS = "LISTA_NUMEROS";
	private static final String LISTA_TREVOS = "LISTA_TREVOS";
	private static final String LISTA_MARCADOS = "LISTA_MARCADOS";
	private static final String LISTA_TREVOS_MARCADOS = "LISTA_TREVOS_MARCADOS";
	private static final String CONFIGURACAO = "CONFIGURACAO";
	private static final String TITULO_LISTA = "TITULO_LISTA";
	private static final String TITULO_TREVOS = "TITULO_TREVOS";
	private static final int QUANTIDADE_COLUNA_LISTA = 6;

	private String listaTexto, numerosTextoSorteados, trevosTextoSorteados, listaTrevosTexto;
	private List<Integer> listaNumerosInteiros, listaInteirosMarcados, listaTrevosInteiros, listaTrevosMarcados;
	private ConfiguracaoNumeros configuracao;
	private String tituloNumerosTexto;
	private String tituloTrevosTexto;

	public NumerosTrevosFragment() { }

	public static NumerosTrevosFragment newInstance(String tituloNumeros, String tituloTrevos,
													List<Integer> numerosSelecionados,
													List<Integer> trevosSelecionados,
													List<Integer> numerosSorteados,
													List<Integer> trevosSorteados,
													ConfiguracaoNumeros configuracao) {
		NumerosTrevosFragment fragment = new NumerosTrevosFragment();
		Bundle                args     = new Bundle();
		if(numerosSelecionados != null){
			args.putString(LISTA_NUMEROS, String.valueOf(numerosSelecionados));
		} else {
			args.putString(LISTA_NUMEROS, "");
		}
		if(trevosSelecionados != null){
			args.putString(LISTA_TREVOS, String.valueOf(trevosSelecionados));
		} else {
			args.putString(LISTA_TREVOS, "");
		}
		if(numerosSorteados != null) {
			args.putString(LISTA_MARCADOS, String.valueOf(numerosSorteados));
		} else {
			args.putString(LISTA_MARCADOS, "");
		}
		if(trevosSorteados != null) {
			args.putString(LISTA_TREVOS_MARCADOS, String.valueOf(trevosSorteados));
		} else {
			args.putString(LISTA_TREVOS_MARCADOS, "");
		}
		if (tituloNumeros != null && !tituloNumeros.isEmpty()){
			args.putString(TITULO_LISTA, tituloNumeros);
		} else {
			args.putString(TITULO_LISTA, "");
		}
		if (tituloTrevos != null && !tituloTrevos.isEmpty()){
			args.putString(TITULO_TREVOS, tituloTrevos);
		} else {
			args.putString(TITULO_TREVOS, "");
		}
		args.putSerializable(CONFIGURACAO,configuracao);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			listaTexto = getArguments().getString(LISTA_NUMEROS);
			numerosTextoSorteados = getArguments().getString(LISTA_MARCADOS);
			trevosTextoSorteados = getArguments().getString(LISTA_TREVOS_MARCADOS);
			listaTrevosTexto = getArguments().getString(LISTA_TREVOS);
			tituloNumerosTexto = getArguments().getString(TITULO_LISTA);
			tituloTrevosTexto = getArguments().getString(TITULO_TREVOS);
			Gson gson = new Gson();
			TypeToken<List<Integer>> token          = new TypeToken<List<Integer>>() {};
			if(listaTexto!= null && listaTexto.length() > 0){
				listaNumerosInteiros = gson.fromJson(listaTexto, token.getType());
			} else {
				listaNumerosInteiros = new ArrayList<>();
			}
			if (listaTrevosTexto != null && !listaTrevosTexto.isEmpty()){
				listaTrevosInteiros = gson.fromJson(listaTrevosTexto,token.getType());
			} else {
				listaTrevosInteiros = new ArrayList<>();
			}
			if (numerosTextoSorteados != null && numerosTextoSorteados.length() > 0){
				listaInteirosMarcados = gson.fromJson(numerosTextoSorteados, token.getType());
			} else {
				listaInteirosMarcados = new ArrayList<>();
			}
			if (trevosTextoSorteados != null && trevosTextoSorteados.length() > 0){
				listaTrevosMarcados = gson.fromJson(trevosTextoSorteados, token.getType());
			} else {
				listaTrevosMarcados = new ArrayList<>();
			}
			configuracao = (ConfiguracaoNumeros) getArguments().getSerializable(CONFIGURACAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_numeros_trevos, container, false);
		setaViews();
		setHeight(view);
		return view;
	}

	private void setHeight(View view) {
		int pxNumeros = getPx(getQtdColunas(listaNumerosInteiros));
		int pxTrevos = getPx(getQtdColunas(listaTrevosInteiros)) + 155;

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		param.height = pxNumeros + pxTrevos + 155;
		view.setLayoutParams(param);
	}

	private int getPx(int qtdColMax) {
		float     dip = qtdColMax * 38f + 40f;
		Resources r   = getActivity().getResources();
		int       px   = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return px;
	}

	private void setaViews(){
		FragmentManager fm = getActivity().getSupportFragmentManager();

		NumerosFragment numerosFragment = NumerosFragment.newInstance(tituloNumerosTexto, listaNumerosInteiros, listaInteirosMarcados, configuracao, null);
		fm.beginTransaction().replace(R.id.fragmentNumeros, numerosFragment).commit();

		NumerosFragment trevosFragment = NumerosFragment.newInstance(tituloTrevosTexto, listaTrevosInteiros, listaTrevosMarcados, configuracao, getTrevoShape());
		fm.beginTransaction().replace(R.id.fragmentTrevos, trevosFragment).commit();

	}

	private ShapeConfig getTrevoShape() {
		return new ShapeConfig(R.drawable.ic_item_trevo,
							   R.drawable.ic_item_trevo_selecionado,
							   R.color.branco, R.color.milionaria_escuro_mkp);
	}

	private int getQtdColunas(List<Integer> list){
		int qtdColunas = list.size() / QUANTIDADE_COLUNA_LISTA;
		int restante          = list.size() - (qtdColunas * QUANTIDADE_COLUNA_LISTA);
		if (restante > 0){
			qtdColunas++;
		}
		return qtdColunas;
	}

}