package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SuperSeteEnum;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.AutomacaoUtils;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;

public class NumerosSuperSeteAdapter extends BaseAdapter {

	//region Variables
	public List<Dezena> dezenas = new ArrayList<>();
	public List<Integer> dezenasSelecionadas, listaNumerosSelecionados;
	public ConfiguracaoNumerosSuperSete configuracao;
	private SuperSeteEnum coluna;

	private int corSuperSete;
	private Context context;
	//endregion

	//region Initialization Methods
	public NumerosSuperSeteAdapter(int typeGameColorDark, Context parentActivity, List<Integer> dezenasSelecionadas) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.corSuperSete = typeGameColorDark;
		this.configuracao = null;
		preencheNumeros();
	}

	public NumerosSuperSeteAdapter(ConfiguracaoNumerosSuperSete configuracao, Context parentActivity, List<Integer> dezenasSelecionadas, List<Integer> listaNumerosSelecionados) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.configuracao = configuracao;
		this.listaNumerosSelecionados = listaNumerosSelecionados;
		preencheNumeros();
	}

	public NumerosSuperSeteAdapter(int typeGameColorDark, Context parentActivity, List<Integer> dezenasSelecionadas, SuperSeteEnum coluna) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.corSuperSete = typeGameColorDark;
		this.configuracao = null;
		this.coluna = coluna;
		preencheNumeros();
	}
	//endregion

	//region Layout Configuration Methods
	@Override
	public int getCount() {
		return dezenas.size();
	}

	@Override
	public Object getItem(int i) {
		return dezenas.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		TextView         textView;
		GradientDrawable shape = new GradientDrawable();
		int              sizeItens;
		sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());

		if (view == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}
		if (configuracao == null) {
			shape.setColor(Color.WHITE);
			shape.setStroke(3, context.getResources().getColor(corSuperSete));
			//textView.setTextColor(ContextCompat.getColor(context, R.color.black));
			textView.setTextColor(ContextCompat.getColor(context, R.color.super_sete_letra_mkp));
		} else {
			if (dezenas.get(i).isSelected()) {
				shape.setColor(ContextCompat.getColor(context,configuracao.getCorInteriorSelecionado()));
				shape.setStroke(3, ContextCompat.getColor(context,configuracao.getCorBordaCirculoSelecionado()));
				textView.setTextColor(ContextCompat.getColor(context,configuracao.getCorTextoCirculoSelecionado()));
			} else {
				shape.setColor(ContextCompat.getColor(context,configuracao.getCorInteriorCirculo()));
				shape.setStroke(3, ContextCompat.getColor(context,configuracao.getCorBordaCirculo()));
				textView.setTextColor(ContextCompat.getColor(context,configuracao.getCorTextoCirculo()));
			}
		}
		shape.setCornerRadius(sizeItens / 2);
		textView.setBackground(shape);
		textView.setText(dezenas.get(i).getValue());
		//textView.setTypeface(ViewUtils.getFontFutura(context));
		textView.setTypeface(FonteUtils.getFonte(FontCaixaEnum.REGULAR));
		textView.setGravity(Gravity.CENTER);

		// logica apenas para o automatizado
		if (BuildVersionUtil.isAutomacao() && coluna != null){
			AutomacaoUtils.aplicaIdViewSuperSete(coluna, i, textView);
		}

		return textView;
	}
	//endregion

	//region Support Methods
	private void preencheNumeros() {
		for (Integer marcado : dezenasSelecionadas) {
			boolean contains = listaNumerosSelecionados != null &&
					listaNumerosSelecionados.contains(marcado);
			dezenas.add(new Dezena("" + marcado.intValue(), contains));
		}
	}

	//endregion

}