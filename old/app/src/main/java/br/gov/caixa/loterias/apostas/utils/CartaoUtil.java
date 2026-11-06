package br.gov.caixa.loterias.apostas.utils;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostas.R;

public class CartaoUtil {

	public static Drawable getBandeira(Context context, String nomeMetodo){
		Drawable drawable = null;
		PorterDuffColorFilter porterDuffColorFilter = VectorUtils.getColorFilter(Color.BLUE);
		switch (nomeMetodo.toLowerCase()){
			case "visa":
				drawable = context.getDrawable(R.drawable.ic_visa);
				drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			case "master":
			case "mastercard":
				//return context.getDrawable(R.drawable.ic_master_card);
				return context.getDrawable(R.drawable.ic_bandeira_master);
			case "hipercard":
				return context.getDrawable(R.drawable.ic_hipercard);
			case "diners":
				return context.getDrawable(R.drawable.ic_diners);
			case "elo":
				//return context.getDrawable(R.drawable.ic_bandeira_elo);
				return context.getDrawable(R.drawable.elo_fundo_claro);
			case "amex":
			case "american express":
			case "americanexpress":
				//drawable = context.getDrawable(R.drawable.ic_american);
				drawable = context.getDrawable(R.drawable.ic_bandeira_amex2);
				//drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			default:
				return context.getDrawable(R.drawable.cartao_cvc);

		}
	}

}
