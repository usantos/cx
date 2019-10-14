package br.gov.caixa.loterias.apostar.interno.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;

public class RateUtils {

	private static final String VERSAO_APP = "VERSAO_APP";
	private static final String CONT_AVALIACAO = "CONT_AVALIACAO";
	private static final String CONT_ABRIU_AVALIACAO = "CONT_ABRIU_AVALIACAO";
	private static final int QTD_MOSTRAR_AVALIACAO = 10;
	private static final int QTD_AVALIACOES_MAXIMAS = 3;
	private static final int QTD_NULA = -1;

	private static Dialog dialogAvaliar;

	public static void avaliarAplicativo(Activity activity, Boolean mostrarPopup){
		dialogAvaliar = getDialog(activity);

		if(mostrarPopup && getContAbriuAvaliacao() < QTD_AVALIACOES_MAXIMAS){
			setContAbriuAvaliacao(getContAbriuAvaliacao() + 1);
			showDialog();
		}
	}

	public static void avaliarAplicativo(Activity activity){
		dialogAvaliar = getDialog(activity);
		if( mostrarAvaliacao() ){
			showDialog();
		}
	}

	private static Dialog getDialog(Activity activity){
		return ViewUtils.alertTitleCustomPositiveListener(activity,
														  activity.getString(R.string.aval_titulo),
														  activity.getString(R.string.aval_negativo),
														  activity.getString(R.string.aval_positivo),
														  activity.getString(R.string.aval_desc),
														  (dialogInterface, i) -> {
															  setContAbriuAvaliacao(QTD_AVALIACOES_MAXIMAS + 1);
															  redirecionaParaLoja(activity);
														  });
	}

	private static void showDialog(){
		new Handler().postDelayed(() -> dialogAvaliar.show(), 2000);
	}
	private static void redirecionaParaLoja(Activity activity){
		final String appPackageName = activity.getPackageName();
		try {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}

	private static void iniciaDadosLocais(){
		setCont(1);
		setContAbriuAvaliacao(0);
		setVersaoApp(BuildConfig.VERSION_NAME);
	}

	private static Boolean mostrarAvaliacao(){
		int cont = getCont();
		int contAbriuAvaliacao = getContAbriuAvaliacao();
		if(cont == QTD_NULA || !getVersaoApp().equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
			iniciaDadosLocais();
			return false;
		} else {
			if(contAbriuAvaliacao >= QTD_AVALIACOES_MAXIMAS) {
				return false;
			}
			cont++;
			setCont(cont);
			if(cont == QTD_MOSTRAR_AVALIACAO) {
				setContAbriuAvaliacao(contAbriuAvaliacao + 1);
				setCont(0);
				return true;
			}
			return false;
		}
	}

	//Quantidade de vezes que o usuario viu o popup
	private static void setContAbriuAvaliacao(int contAbriuAvaliacao) {
		SharedPreferencesUtils.setValor(CONT_ABRIU_AVALIACAO, contAbriuAvaliacao);
	}

	private static int getContAbriuAvaliacao() {
		return SharedPreferencesUtils.getValorInt(CONT_ABRIU_AVALIACAO, 0);
	}

	//Versao do aplicativo
	private static void setVersaoApp(String versaoApp) {
		SharedPreferencesUtils.setValor(VERSAO_APP, versaoApp);
	}

	private static String getVersaoApp() {
		return SharedPreferencesUtils.getValorString(VERSAO_APP, "");
	}

	//Contador
	private static void setCont(int cont) {
		SharedPreferencesUtils.setValor(CONT_AVALIACAO, cont);
	}

	private static int getCont() {
		return SharedPreferencesUtils.getValorInt(CONT_AVALIACAO, QTD_NULA);
	}
}
