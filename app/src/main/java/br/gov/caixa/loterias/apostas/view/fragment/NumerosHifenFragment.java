package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(ContextCompat.getColor(getActivity(), configuracao.getCorInteriorCirculo()));
		layout.setPadding(16, 16, 16, 16);

		TextView tituloView = new TextView(getContext());
		tituloView.setText(titulo);
		tituloView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
		tituloView.setTextSize(12);
		tituloView.setTypeface(ViewUtils.getFontCaixaStdBold(getContext()));
		tituloView.setPadding(0, 0, 0, 32);
		layout.addView(tituloView);

		// Monta os números com hífen e quebra de linha a cada 6
		int count = 0;
		StringBuilder linha = new StringBuilder();
		for (int i = 0; i < numerosSelecionados.size(); i++) {
			int numero = numerosSelecionados.get(i);
			//String formatado = numero < 10 ? " " + numero : String.valueOf(numero);
			String formatado = String.format("%02d", numero);

			if (count > 0) {
				linha.append("   -   ");
			}
			linha.append(formatado);
			count++;

			// Verifica se é hora de quebrar a linha
			boolean fimDaLinha = count == 6;
			boolean ultimoNumero = i == numerosSelecionados.size() - 1;

			if (fimDaLinha || ultimoNumero) {
				TextView linhaView = new TextView(getContext());
				linhaView.setText(linha.toString());
				linhaView.setTextColor(configuracao.getCorTextoCirculo());
				linhaView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
				linhaView.setBackgroundColor(ContextCompat.getColor(getActivity(), configuracao.getCorInteriorCirculo()));
				linhaView.setTextSize(14);
				linhaView.setTypeface(ViewUtils.getFontCaixaStdBold(getContext()));
				linhaView.setPadding(16, 0, 0, 20);
				layout.addView(linhaView);
				linha.setLength(0); // limpa
				count = 0;
			}
		}

		return layout;
	}
}
