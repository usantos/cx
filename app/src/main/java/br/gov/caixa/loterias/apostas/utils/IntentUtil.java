package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.novo.features.dadospessoais.DadosPessoaisActivity;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.MeusCartoesActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.controllers.VisualizarResultadosActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import br.gov.caixa.loterias.apostas.view.activity.TutorialActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;

public class IntentUtil {

	public static Intent getIntentLimpandoPilhaActivities(Activity activity, Class clazz){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		return intent;
	}

	public static Intent getIntentOrigemDestino(Activity activity, Class clazz){
		return new Intent(activity, clazz);
	}

	public static Intent getIntentOrigemDestino(Activity activity, Class clazz, String key, Serializable extra){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.putExtra(key, extra);

		return intent;
	}
	public static Intent getIntentOrigemDestino(Activity activity, Class clazz, Bundle bundle){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.putExtras(bundle);

		return intent;
	}

	public static Intent getIntentWeb(String url){
		return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
	}

	public static Intent getIntentDeepLink(Activity activity, String deepLink) {
		Intent intent = null;

		if (deepLink.equals(activity.getResources().getString(R.string.deeplink_path_combos))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, PrincipalActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.deeplink_path_apostas))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, MinhasApostasActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_compras))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, ListaComprasActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_carrinho))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, CarrinhoActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_bolao_todas))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, BolaoActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_bolao))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, BolaoActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_bolao_loterico))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, BolaoActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_apostador))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, DadosPessoaisActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_cartoes))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, MeusCartoesActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_carrinhos_favoritos))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, CarrinhosFavoritosActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_apostas_favoritas))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, FavoritasActivity.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_mais_milionaria))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.MAIS_MILIONARIA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_mega_sena))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.MEGA_SENA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_lotofacil))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.LOTOFACIL));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_quina))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.QUINA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_lotomania))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.LOTOMANIA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_timemania))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.TIMEMANIA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_dupla_sena))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.DUPLA_SENA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_loteca))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.LOTECA));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_dia_sorte))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.DIA_DE_SORTE));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_resultado_super_sete))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, VisualizarResultadosActivity.class);
			intent.putExtra("MODALIDADE", ModalidadeEnum.fromString(ModalidadeEnum.SUPER_7));
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_mais_milionaria))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.MAIS_MILIONARIA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.MAIS_MILIONARIA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_mega_sena))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.MEGA_SENA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.MEGA_SENA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_lotofacil))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.LOTOFACIL, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.LOTOFACIL);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_quina))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.QUINA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.QUINA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_lotomania))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.LOTOMANIA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.LOTOMANIA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_timemania))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.TIMEMANIA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.TIMEMANIA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_dupla_sena))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.DUPLA_SENA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.DUPLA_SENA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_loteca))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.LOTECA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.LOTECA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_dia_sorte))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.DIA_DE_SORTE, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.DIA_DE_SORTE);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_super_sete))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.SUPER_7, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.SUPER_7);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", false);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_mega_sena_especial))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.MEGA_SENA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.MEGA_SENA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", true);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_dupla_sena_especial))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.DUPLA_SENA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.DUPLA_SENA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", true);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_quina_especial))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.QUINA, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.QUINA);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", true);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_lotofacil_especial))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, SimularApostaActivity.class);
			ParametroJogoDTO parametroJogo = getParametroJogo(ModalidadeEnum.LOTOFACIL, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());
			intent.putExtra("tipoAposta", ModalidadeEnum.LOTOFACIL);
			intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
			intent.putExtra("especial", true);
		}

		return intent;
	}

	private static ParametroJogoDTO getParametroJogo(ModalidadeEnum modalidade, List<ParametroSimulacao> listParametroJogo) {
		for (ParametroSimulacao parametroSimula : listParametroJogo) {
			if (parametroSimula != null || parametroSimula.getParametroJogo().getConcurso().getModalidade() == modalidade) {
				return parametroSimula.getParametroJogo();
			}
		}
		return null;
	}

	public static Intent getIntentFimTutorial(Activity activity, ModalidadeEnum modalidade, Integer IdModalidade, Integer tipoConcurso) {
		if (modalidade == ModalidadeEnum.BOLAO){
			Intent intent = new Intent(activity, BolaoActivity.class);
			intent.putExtra(BolaoActivity.ARG_IDMODALIDADE,IdModalidade);
			intent.putExtra(BolaoActivity.ARG_TIPO_CONCURSO, tipoConcurso);
			return intent;
		} else {
			return new Intent(activity, SimularApostaActivity.class);
		}
	}

	public static Intent getIntentTutorial(Activity activity, ModalidadeEnum modalidade, int idModalidade, TipoConcursoEnum tipoConcurso) {
		Intent intent = new Intent(activity, TutorialActivity.class);
		if (modalidade != null){
			intent.putExtra(activity.getResources().getString(R.string.tipoAposta), modalidade);
		}
		if (tipoConcurso != null){
			intent.putExtra(BolaoActivity.ARG_TIPO_CONCURSO, tipoConcurso.fromStringToIdTipoConcurso());
		}
		intent.putExtra(BolaoActivity.ARG_IDMODALIDADE, idModalidade);
		return intent;
	}

	public static ActivityResultLauncher<Intent> registerLauncherActivityForResult(AppCompatActivity activity, OnActivityForResult listener) {
		return activity
				.registerForActivityResult(new ActivityResultContracts
												   .StartActivityForResult(),
										   result -> {
											   listener.result(result);
										   });
	}

	public static void startActivityForResult(ActivityResultLauncher<Intent> launcher, ComponentActivity activity, Class clazz) {
		launcher.launch(IntentUtil.getIntentOrigemDestino(activity, clazz));
	}

	public static void startActivityForResult(ActivityResultLauncher<Intent> launcher, Intent intent) {
		launcher.launch(intent);
	}
}
