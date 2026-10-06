package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Pagina;
import br.gov.caixa.loterias.apostas.model.bean.Paginacao;

public class PaginacaoUtils {

	public static Paginacao getPaginacaoMaisMilionaria(Context context, int size, int cor){
		Pagina pagina1  = getPagina(context, R.string.escolha_de_seis_a_doze_numeros, 1);
		Pagina pagina2 = getPagina(context, R.string.voce_escolheu_x_numeros, 2, size);

		return getPaginacaoDefault(context, pagina1, pagina2, cor);
	}

	public static Paginacao getPaginacaoTimemania(Context context, int cor) {
		Pagina pagina1 = getPagina(context, R.string.dia_sorte_escolha_dia, 1);
		Pagina pagina2 = getPagina(context, R.string.label_escolha_time_seu_coracao, 2);

		return getPaginacaoDefault(context, pagina1, pagina2, cor);
	}

	public static Paginacao getPaginacaoDiaDeSorte(Context context, int cor) {
		Pagina pagina1 = getPagina(context, R.string.dia_sorte_escolha_dia, 1);
		Pagina pagina2 = getPagina(context, R.string.dia_sorte_escolha_mes, 2);

		return getPaginacaoDefault(context, pagina1, pagina2, cor);
	}

	private static Paginacao getPaginacaoDefault(Context context, Pagina pagina1, Pagina pagina2, int cor) {
		List<Pagina> paginas = new ArrayList<>();
		paginas.add(pagina1);
		paginas.add(pagina2);

		return new Paginacao(paginas, context.getString(R.string.divisor_paginacao), cor);
	}

	private static Pagina getPagina(Context context, int idString, int numeroPagina, int size){

		String tituloSemBold = context.getString(idString).replace("x", String.valueOf(size));

		//SpannableStringBuilder titulo = ViewUtils.textFuturaAndFuturaBold(context, tituloSemBold);
		SpannableStringBuilder titulo = ViewUtils.textCaixaSTDBold(context, tituloSemBold);

		return new Pagina(titulo, numeroPagina);
	}

	private static Pagina getPagina(Context context, int idString, int numeroPagina){

		String tituloSemBold = context.getString(idString);
		//SpannableStringBuilder titulo = ViewUtils.textFuturaAndFuturaBold(context, tituloSemBold);
		SpannableStringBuilder titulo = ViewUtils.textCaixaSTDBold(context, tituloSemBold);

		return new Pagina(titulo, numeroPagina);
	}


}
