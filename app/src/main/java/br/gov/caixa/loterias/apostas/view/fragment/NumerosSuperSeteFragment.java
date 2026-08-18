package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AutomacaoUtils;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosSuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class NumerosSuperSeteFragment extends Fragment {
	private static final String MATRIZ_NUMEROS = "MATRIZ_NUMEROS";
	private static final String MATRIZ_MARCADA = "MATRIZ_MARCADA";
	private static final String CONFIGURACAO = "CONFIGURACAO";

	private String matrizTexto, matrizTextoMarcados;
	private View view = null;
	private ArrayList<TextView> arrayColunas;
	private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
	private ArrayList<NumerosSuperSeteAdapter> listaAdaptersSuperSete;
	private List<List<Integer>> matrizInteiros, matrizMarcada;
	private ConfiguracaoNumerosSuperSete configuracao;

	public NumerosSuperSeteFragment() {}

	public static NumerosSuperSeteFragment newInstance(List<List<Integer>> matrizSelecionada) {
		NumerosSuperSeteFragment fragment = new NumerosSuperSeteFragment();
		Bundle                   args     = new Bundle();
		args.putString(MATRIZ_NUMEROS, String.valueOf(matrizSelecionada));
		fragment.setArguments(args);
		return fragment;
	}

	public static NumerosSuperSeteFragment newInstance(List<List<Integer>> matrizSelecionada, ConfiguracaoNumerosSuperSete configuracao) {
		NumerosSuperSeteFragment fragment = new NumerosSuperSeteFragment();
		Bundle                   args     = new Bundle();
		args.putString(MATRIZ_NUMEROS, String.valueOf(matrizSelecionada));
		args.putSerializable(CONFIGURACAO,configuracao);
		fragment.setArguments(args);
		return fragment;
	}

	public static NumerosSuperSeteFragment newInstance(List<List<Integer>> matrizSelecionada,List<List<Integer>> matrizMarcada ,ConfiguracaoNumerosSuperSete configuracao) {
		NumerosSuperSeteFragment fragment = new NumerosSuperSeteFragment();
		Bundle                   args     = new Bundle();
		args.putString(MATRIZ_NUMEROS, String.valueOf(matrizSelecionada));
		args.putString(MATRIZ_MARCADA, String.valueOf(matrizMarcada));
		args.putSerializable(CONFIGURACAO,configuracao);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			matrizTexto = getArguments().getString(MATRIZ_NUMEROS);
			matrizTextoMarcados = getArguments().getString(MATRIZ_MARCADA);
			Gson gson = new Gson();
			TypeToken<List<List<Integer>>> token          = new TypeToken<List<List<Integer>>>() {};
			matrizInteiros = gson.fromJson(matrizTexto, token.getType());
			if(matrizTextoMarcados != null){
				matrizMarcada = gson.fromJson(matrizTextoMarcados, token.getType());
			}
			configuracao = (ConfiguracaoNumerosSuperSete) getArguments().getSerializable(CONFIGURACAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_super_sete_resultado, container, false);
		setaViews(view);
		setaMetodos();
		return view;
	}

	private void setaViews(View view){
		if(configuracao != null){
			Context ctx = Aplicacao.application.getApplicationContext();
			arrayColunas = new ArrayList<>();
			arrayColunas.add(view.findViewById(R.id.tv_col_1));
			arrayColunas.add(view.findViewById(R.id.tv_col_2));
			arrayColunas.add(view.findViewById(R.id.tv_col_3));
			arrayColunas.add(view.findViewById(R.id.tv_col_4));
			arrayColunas.add(view.findViewById(R.id.tv_col_5));
			arrayColunas.add(view.findViewById(R.id.tv_col_6));
			arrayColunas.add(view.findViewById(R.id.tv_col_7));

			for (TextView coluna: arrayColunas ) {
				//coluna.setTextColor(configuracao.getTextoQuadrado());
				coluna.setTextColor(ContextCompat.getColorStateList(ctx, configuracao.getTextoQuadrado()));
				coluna.setBackgroundTintList(ContextCompat.getColorStateList(ctx, configuracao.getCorInteriorQuadrado()));
			}
		}

		listaGridsSuperSete = new ArrayList<>();
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_primeiro));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_segundo));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_terceiro));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_quarto));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_quinto));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_sexto));
		listaGridsSuperSete.add(view.findViewById(R.id.ehgv_super_sete_setimo));

		int qtdColMax = getQtdItensMaiorColuna(matrizInteiros);
		float     dip = qtdColMax * 38f + 40f;
		Resources r   = getActivity().getResources();
		int px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dip,
				r.getDisplayMetrics());

		LinearLayout.LayoutParams param =
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);

		param.height = px;

		view.setLayoutParams(param);

	}

	private void setaMetodos (){
		listaAdaptersSuperSete = new ArrayList<>();
		if (configuracao == null) {
			if (BuildVersionUtil.isAutomacao()){
				int index = 0;
				for (List<Integer> lista: matrizInteiros) {
					listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.super_sete_claro_mkp, getActivity(), lista, AutomacaoUtils.getSuperSeteEnumPorColuna(index)));
					index++;
				}
			} else {
				for (List<Integer> lista: matrizInteiros) {
					listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.super_sete_claro_mkp, getContext(), lista));
				}
			}

		} else {
			int cont = 0;
			for (List<Integer> lista: matrizInteiros) {
				List<Integer> listaMarcada = matrizMarcada != null ? matrizMarcada.get(cont): null;
				listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(configuracao, getContext(), lista, listaMarcada));
				cont++;
			}
		}

		for(int i = 0; i < listaAdaptersSuperSete.size(); i++) {
			listaGridsSuperSete.get(i).setAdapter(listaAdaptersSuperSete.get(i));
		}
	}

	private int getQtdItensMaiorColuna(List<List<Integer>> matriz) {
		int maxCol = 1;
		for (List<Integer> linha:matriz) {
			if(linha.size() > maxCol){
				maxCol = linha.size();
			}
		}
		return maxCol;
	}
}