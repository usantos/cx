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

		android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
		scrollView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		));

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(ContextCompat.getColor(getActivity(), configuracao.getCorInteriorCirculo()));
		layout.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 16));
		layout.setGravity(Gravity.CENTER_HORIZONTAL);


		// Título
		TextView tituloView = new TextView(context);
		tituloView.setText(titulo);
		tituloView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
		tituloView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tituloView.setTypeface(ViewUtils.getFontCaixaStdBold(context));
		tituloView.setPadding(0, 0, 0, dp(context, 16));
		layout.addView(tituloView);

		GridLayout gridLayout = new GridLayout(context);
		gridLayout.setOrientation(GridLayout.HORIZONTAL);
		LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);

		gridLayout.setLayoutParams(gridParams);

		layout.addView(gridLayout);

		layout.post(() -> {
			int availableWidth = layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight();
			int columns = (availableWidth <= 0) ? 4 : calculateColumnsByTextMeasure(context, availableWidth);
			populateGrid(context, gridLayout, columns);
		});

		scrollView.addView(layout);


		return scrollView;
	}




	private int calculateColumnsByTextMeasure(Context context, int availableWidthPx) {
		// Configurações equivalentes ao seu numeroView
		float textSizeSp = 16;
		int paddingHPx = dp(context, 2); // padding left e right do item
		int extraPx = dp(context, 6);    // folga (hífen, espaçamento etc.)

		TextView probe = new TextView(context);
		probe.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
		probe.setTypeface(ViewUtils.getFontCaixaStdBold(context));

		// Mede o texto "00 -"
		float textWidth = probe.getPaint().measureText("00 -");

		int itemWidthPx = Math.round(textWidth) + (paddingHPx * 2) + extraPx;

		int minColumns = 4;
		int maxColumns = 6;

		int columns = availableWidthPx / itemWidthPx;
		if (columns < minColumns) columns = minColumns;
		if (columns > maxColumns) columns = maxColumns;

		return columns;
	}

	private void populateGrid(Context context, GridLayout gridLayout, int columns) {
		gridLayout.removeAllViews();
		gridLayout.setColumnCount(columns);

		for (int i = 0; i < numerosSelecionados.size(); i++) {
			int numero = numerosSelecionados.get(i);
			String formatado = String.format("%02d", numero);
			TextView numeroView = new TextView(context);
			numeroView.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));
			numeroView.setText(formatado);
			numeroView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			numeroView.setTypeface(ViewUtils.getFontCaixaStdBold(context));
			numeroView.setGravity(Gravity.CENTER);
			numeroView.setPadding(dp(context, 2), dp(context, 8), dp(context, 2), dp(context, 8));

			// Adiciona hífen à direita, exceto no último da linha
			if ((i + 1) % columns != 0 && i != numerosSelecionados.size() - 1) {
				numeroView.setText(formatado + " -");
			}

			gridLayout.addView(numeroView);
		}
	}

	private int dp(Context context, int dp) {
		return (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()
		);
	}
}