package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class NumerosTrevosFragment extends Fragment {

	private static final String LISTA_NUMEROS = "LISTA_NUMEROS";
	private static final String LISTA_TREVOS = "LISTA_TREVOS";
	private static final String LISTA_MARCADOS = "LISTA_MARCADOS";
	private static final String CONFIGURACAO = "CONFIGURACAO";
	private static final String TITULO_LISTA = "TITULO_LISTA";

	private String listaTexto, numerosTextoSorteados, listaTrevosTexto;
	private View view = null;
	private ExpandableHeightGridView gridNumeros;
	private NumerosAdapter numerosAdapter;
	private List<Integer> listaInteiros, listaInteirosMarcados, trevosInteiros, listaTrevosMarcados;
	private ConfiguracaoNumeros configuracao;
	private TextView titulo;
	private String tituloTexto;

	public NumerosTrevosFragment() { }

	public static NumerosTrevosFragment newInstance(String titulo, List<Integer> numerosSelecionados,
													List<Integer> trevosSelecionados,
													List<Integer> numerosSorteados, ConfiguracaoNumeros configuracao) {
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
		if (titulo != null && !titulo.isEmpty()){
			args.putString(TITULO_LISTA, titulo);
		} else {
			args.putString(TITULO_LISTA, "Seus números");
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
			listaTrevosTexto = getArguments().getString(LISTA_TREVOS);
			tituloTexto = getArguments().getString(TITULO_LISTA);
			Gson gson = new Gson();
			TypeToken<List<Integer>> token          = new TypeToken<List<Integer>>() {};
			if(listaTexto!= null && listaTexto.length() > 0){
				listaInteiros = gson.fromJson(listaTexto, token.getType());
			} else {
				listaInteiros = new ArrayList<>();
			}
			if (listaTrevosTexto != null && !listaTrevosTexto.isEmpty()){
				trevosInteiros = gson.fromJson(listaTrevosTexto,token.getType());
			} else {
				trevosInteiros = new ArrayList<>();
			}
			if (numerosTextoSorteados != null && numerosTextoSorteados.length() > 0){
				listaInteirosMarcados = gson.fromJson(numerosTextoSorteados, token.getType());
				listaTrevosMarcados = new ArrayList<>();
				listaTrevosMarcados.add(listaInteirosMarcados.get(6));
				listaTrevosMarcados.add(listaInteirosMarcados.get(7));

				listaInteirosMarcados.remove(7);
				listaInteirosMarcados.remove(6);
			} else {
				listaInteirosMarcados = new ArrayList<>();
			}
			configuracao = (ConfiguracaoNumeros) getArguments().getSerializable(CONFIGURACAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_numeros_trevos, container, false);
		setaViews(view);
		setaMetodos();
		return view;
	}

	private void setaViews(View view){

		titulo = view.findViewById(R.id.lista_titulo);
		titulo.setText(tituloTexto);
		gridNumeros = view.findViewById(R.id.ehgv_numeros);

		int qtdColMax = getQtdColunas();
		float     dip = qtdColMax * 38f + 40f;
		Resources r   = getActivity().getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());

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