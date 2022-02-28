package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class NumerosFragment extends Fragment {

	private static final String LISTA_NUMEROS = "LISTA_NUMEROS";
	private static final String LISTA_MARCADOS = "LISTA_MARCADOS";
	private static final String CONFIGURACAO = "CONFIGURACAO";

	private String listaTexto, listaTextoMarcados;
	private View view = null;
	private ExpandableHeightGridView gridNumeros;
	private NumerosAdapter numerosAdapter;
	private List<Integer> listaInteiros, listaInteirosMarcados;
	private ConfiguracaoNumeros configuracao;

	public NumerosFragment() { }

	public static NumerosFragment newInstance(List<Integer> numerosSelecionados, List<Integer> numerosMarcados, ConfiguracaoNumeros configuracao) {
		NumerosFragment fragment = new NumerosFragment();
		Bundle                   args     = new Bundle();
		if(numerosSelecionados != null){
			args.putString(LISTA_NUMEROS, String.valueOf(numerosSelecionados));
		} else {
			args.putString(LISTA_NUMEROS, "");
		}
		if(numerosMarcados != null) {
			args.putString(LISTA_MARCADOS, String.valueOf(numerosMarcados));
		} else {
			args.putString(LISTA_MARCADOS, "");
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
			listaTextoMarcados = getArguments().getString(LISTA_MARCADOS);
			Gson gson = new Gson();
			TypeToken<List<Integer>> token          = new TypeToken<List<Integer>>() {};
			if(listaTexto!= null && listaTexto.length() > 0){
				listaInteiros = gson.fromJson(listaTexto, token.getType());
			} else {
				listaInteiros = new ArrayList<>();
			}
			if (listaTextoMarcados != null && listaTextoMarcados.length() > 0){
				listaInteirosMarcados = gson.fromJson(listaTextoMarcados, token.getType());
			} else {
				listaInteirosMarcados = new ArrayList<>();
			}
			configuracao = (ConfiguracaoNumeros) getArguments().getSerializable(CONFIGURACAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_numeros, container, false);
		setaViews(view);
		setaMetodos();
		return view;
	}

	private void setaViews(View view){

		gridNumeros = view.findViewById(R.id.ehgv_numeros);

		int qtdColMax = getQtdColunas();
		float     dip = qtdColMax * 38f + 40f;
		Resources r   = getActivity().getResources();
		int px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dip,
				r.getDisplayMetrics()
												);

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		param.height = px;
		view.setLayoutParams(param);

	}

	private void setaMetodos (){
		numerosAdapter = new NumerosAdapter(configuracao, listaInteiros, listaInteirosMarcados, getActivity());
		gridNumeros.setAdapter(numerosAdapter);
	}

	private int getQtdColunas(){
		return listaInteiros.size() / 6;
	}

}