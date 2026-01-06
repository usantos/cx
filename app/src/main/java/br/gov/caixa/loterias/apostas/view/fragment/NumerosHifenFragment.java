package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class NumerosHifenFragment extends Fragment {

	private static final String ARG_TITULO = "TITULO";
	private static final String ARG_NUMEROS = "NUMEROS";
	private static final String ARG_CONFIG = "CONFIG";

	private static final String ARG_COR_BG = "COR_BG";
	private static final String ARG_COR_TEXTO = "COR_TEXTO";

	private String titulo;
	private ArrayList<Integer> numerosSelecionados;
	private ConfiguracaoNumeros configuracao;
    private int columnCount = 6;

	public static NumerosHifenFragment newInstance(String titulo, List<Integer> numerosSelecionados, ConfiguracaoNumeros configuracao) {
		NumerosHifenFragment fragment = new NumerosHifenFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TITULO, titulo);
		args.putIntegerArrayList(ARG_NUMEROS, new ArrayList<>(numerosSelecionados));
		args.putSerializable(ARG_CONFIG, configuracao);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			titulo = getArguments().getString(ARG_TITULO);
			numerosSelecionados = getArguments().getIntegerArrayList(ARG_NUMEROS);
			configuracao = (ConfiguracaoNumeros) getArguments().getSerializable(ARG_CONFIG);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Context context = getContext();

		// Layout principal vertical
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(ContextCompat.getColor(getActivity(), configuracao.getCorInteriorCirculo()));
		layout.setPadding(16, 16, 16, 16);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);


		// Título
		TextView tituloView = new TextView(context);
		tituloView.setText(titulo);
		tituloView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
		tituloView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tituloView.setTypeface(ViewUtils.getFontCaixaStdBold(context));
		tituloView.setPadding(0, 0, 0, 32);
		layout.addView(tituloView);

		// GridLayout com 6 colunas
		GridLayout gridLayout = new GridLayout(context);
		gridLayout.setColumnCount(columnCount);
		gridLayout.setOrientation(GridLayout.HORIZONTAL);
		gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		));

		for (int i = 0; i < numerosSelecionados.size(); i++) {
			int numero = numerosSelecionados.get(i);
			//String formatado = numero < 10 ? " " + numero : String.valueOf(numero);
			String formatado = String.format("%02d", numero);

			TextView numeroView = new TextView(context);
			numeroView.setText(formatado);
			numeroView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
			numeroView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);			numeroView.setTypeface(ViewUtils.getFontCaixaStdBold(context));
			numeroView.setGravity(Gravity.CENTER);
			numeroView.setPadding(8, 8, 8, 8);

			// Adiciona hífen à direita, exceto no último da linha
			if ((i + 1) % columnCount != 0 && i != numerosSelecionados.size() - 1) {
				numeroView.setText(formatado + " -");
			}

			gridLayout.addView(numeroView);
		}

		layout.addView(gridLayout);
		return layout;
	}


}
