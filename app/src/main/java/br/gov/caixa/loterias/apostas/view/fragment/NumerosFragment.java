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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class NumerosFragment extends Fragment {

	private static final String LISTA_NUMEROS = "LISTA_NUMEROS";
	private static final String LISTA_TREVOS = "LISTA_TREVOS";
	private static final String LISTA_MARCADOS = "LISTA_MARCADOS";
	private static final String CONFIGURACAO = "CONFIGURACAO";

	private static final String TITULO_LISTA = "TITULO_LISTA";
	private static final int QUANTIDADE_COLUNA_LISTA = 6;

	private String listaTexto, listaTextoMarcados;
	private View view = null;
	private RecyclerView listViewNumeros;
	private ListaDezenaRecyclerView numerosAdapter;
	private List<Integer> listaInteiros, listaInteirosMarcados;
	private ConfiguracaoNumeros configuracao;

	private TextView titulo;
	private String tituloTexto;

	public NumerosFragment() { }

	public static NumerosFragment newInstance(String titulo, List<Integer> numerosSelecionados, List<Integer> numerosMarcados, ConfiguracaoNumeros configuracao) {
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
		if (titulo != null && !titulo.isEmpty()){
			args.putString(TITULO_LISTA, titulo);
		} else {
			args.putString(TITULO_LISTA, "");
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
			tituloTexto = getArguments().getString(TITULO_LISTA);
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

		titulo = view.findViewById(R.id.lista_titulo);
		if (tituloTexto == null || tituloTexto.isEmpty()){
			titulo.setVisibility(View.GONE);
		} else {
			titulo.setText(tituloTexto);
		}

		listViewNumeros = view.findViewById(R.id.ehgv_numeros);

		float     dip = getQtdColunas() * 38f + 40f;
		Resources r   = getActivity().getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		param.height = px + 55;
		view.setLayoutParams(param);
	}

	private void setaMetodos (){
		numerosAdapter = new ListaDezenaRecyclerView(getDezenas(), listaInteirosMarcados, getDezenaConfig(), null);
		listViewNumeros.setAdapter(numerosAdapter);

		listViewNumeros.setLayoutManager(new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA));
		listViewNumeros.setNestedScrollingEnabled(false);
	}

	private DezenaConfig getDezenaConfig() {
		return new DezenaConfig(false, R.color.branco, configuracao.getCorTextoCirculoSelecionado(), R.layout.item_dezena_detalhe, new ShapeConfig(), false);
	}

	@NotNull
	private List<Dezena> getDezenas() {
		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < listaInteiros.size(); i++) {
			dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE));
		}
		return dezenas;
	}

	private int getQtdColunas(){
		return listaInteiros.size() / QUANTIDADE_COLUNA_LISTA;
	}

}