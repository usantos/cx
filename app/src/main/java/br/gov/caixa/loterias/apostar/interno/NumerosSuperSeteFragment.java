package br.gov.caixa.loterias.apostar.interno;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosSuperSeteAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;

public class NumerosSuperSeteFragment extends Fragment {
	private static final String MATRIZ_NUMEROS = "MATRIZ_NUMEROS";

	private String matrizTexto;
	private  View view = null;
	private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
	private ArrayList<NumerosSuperSeteAdapter> listaAdaptersSuperSete;
	private List<List<Integer>> matrizInteiros;

	public NumerosSuperSeteFragment() {

	}

	public static NumerosSuperSeteFragment newInstance(List<List<Integer>> matrizSelecionada) {
		NumerosSuperSeteFragment fragment = new NumerosSuperSeteFragment();
		Bundle                   args     = new Bundle();
		args.putString(MATRIZ_NUMEROS, String.valueOf(matrizSelecionada));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			matrizTexto = getArguments().getString(MATRIZ_NUMEROS);
			Gson gson = new Gson();
				TypeToken<List<List<Integer>>> token          = new TypeToken<List<List<Integer>>>() {};
				matrizInteiros = gson.fromJson(matrizTexto, token.getType());

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
				r.getDisplayMetrics()
												);

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		param.height = px;
		view.setLayoutParams(param);

	}

	private void setaMetodos (){
		listaAdaptersSuperSete = new ArrayList<>();
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(), matrizInteiros.get(0)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(1)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(2)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(3)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(4)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(5)));
		listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.superseteescuro, getActivity(),matrizInteiros.get(6)));

		listaGridsSuperSete.get(0).setAdapter(listaAdaptersSuperSete.get(0));
		listaGridsSuperSete.get(1).setAdapter(listaAdaptersSuperSete.get(1));
		listaGridsSuperSete.get(2).setAdapter(listaAdaptersSuperSete.get(2));
		listaGridsSuperSete.get(3).setAdapter(listaAdaptersSuperSete.get(3));
		listaGridsSuperSete.get(4).setAdapter(listaAdaptersSuperSete.get(4));
		listaGridsSuperSete.get(5).setAdapter(listaAdaptersSuperSete.get(5));
		listaGridsSuperSete.get(6).setAdapter(listaAdaptersSuperSete.get(6));

	}

	private int getQtdItensMaiorColuna(List<List<Integer>> matriz){
		int maxCol = 1;
		for (List<Integer> linha:matriz) {
			if(linha.size() > maxCol){
				maxCol = linha.size();
			}
		}
		return maxCol;
	}

}
