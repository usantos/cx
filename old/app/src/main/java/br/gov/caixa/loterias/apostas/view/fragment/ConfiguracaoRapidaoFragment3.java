package br.gov.caixa.loterias.apostas.view.fragment;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.CustomTypefaceSpan;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.MoneyTextWatcher;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ConfiguracaoRapidaoFragment3 extends Fragment implements View.OnClickListener{
	private TextView tvValorMinimo, tvValorMaximo, tvValorMaximoDesc, tvValorMinimoDesc;
	private Double limiteDiario,valorMinimo, valorSelecionadoMin, valorSelecionadoMax;
	private ArrayList<Button>  botoesMinimos = new ArrayList<>(),
							   botoesMaximos = new ArrayList<>();
	private RapidaoConfigSingleton rapidaoConfigSingleton;

	private  final int BTN_1 = 0, BTN_2 = 1, BTN_3 = 2, BTN_4 = 3;
	private static final Boolean MINIMO = true, MAXIMO = false;
	private View view = null;

	private Dialog dialogLimite = null;

	public ConfiguracaoRapidaoFragment3() { }

	public static ConfiguracaoRapidaoFragment3 newInstance() {
		ConfiguracaoRapidaoFragment3 fragment = new ConfiguracaoRapidaoFragment3();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
 		if(view == null){
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment3, container, false);
			setaViews(view);
			setaMetodos();
		}
		return view;
	}

	private void setaViews(View view){
		botoesMinimos.add(view.findViewById(R.id.btn_vl_min_1));
		botoesMinimos.add(view.findViewById(R.id.btn_vl_min_2));
		botoesMinimos.add(view.findViewById(R.id.btn_vl_min_3));
		botoesMinimos.add(view.findViewById(R.id.btn_vl_min_4));

		botoesMaximos.add(view.findViewById(R.id.btn_vl_max_1));
		botoesMaximos.add(view.findViewById(R.id.btn_vl_max_2));
		botoesMaximos.add(view.findViewById(R.id.btn_vl_max_3));
		botoesMaximos.add(view.findViewById(R.id.btn_vl_max_4));


		tvValorMinimo  = view.findViewById(R.id.tv_val_selec_min);
		tvValorMaximo  = view.findViewById(R.id.tv_val_selec_max);
		tvValorMaximoDesc = view.findViewById(R.id.tv_pm_desc_max);
		tvValorMinimoDesc = view.findViewById(R.id.tv_pm_desc_min);

		tvValorMaximoDesc.setText(ViewUtils.textCaixaSTDBold(getActivity(),getResources().getString(R.string.conf_rapd_escolha_max_aposta)));
		tvValorMinimoDesc.setText(ViewUtils.textCaixaSTDBoldTitle(getActivity(),getResources().getString(R.string.conf_rapd_escolha_min_aposta)));
	}

	private void configuraClicks(){
		for (Button btn: botoesMinimos) {
			btn.setOnClickListener(this);
		}
		for (Button btn: botoesMaximos) {
			btn.setOnClickListener(this);
		}
	}

	private void setaMetodos(){
		inicializaValores();
		configuraLogica();
		configuraClicks();
	}

	private void inicializaValores(){
		SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
		rapidaoConfigSingleton = RapidaoConfigSingleton.getInstance();
		limiteDiario = (sessaoUsuario == null || sessaoUsuario.getValorMaximoAposta() == null) ? 0d :
						sessaoUsuario.getValorMaximoAposta().doubleValue();
		valorMinimo = (sessaoUsuario.getParametrosSimulacao() == null ||
						sessaoUsuario.getParametrosSimulacao().getValorMinimoCarrinho() == null) ? 0d :
						sessaoUsuario.getParametrosSimulacao().getValorMinimoCarrinho().doubleValue();
		atualizaValoresSingleton();
	}

	private void configuraLogica(){
		configuraValoresBotoes();
		configuraBotoes(MINIMO);
	}

	private void configuraLogica(Boolean isMin){
		configuraValoresBotoes(isMin);
		configuraBotoes(isMin);
	}

	private void configuraValoresBotoes(Boolean isMin){
		if(valorSelecionadoMin >= valorSelecionadoMax && valorSelecionadoMin * 4 < limiteDiario) {
			configuraDiferencaMinMax(isMin);
			configuraValoresBotoes();
		}
		configuraValoresSelecionados();
	}

	private void configuraDiferencaMinMax(Boolean isMin){
		if(isMin){
			setaValorMaximo(valorSelecionadoMin >= valorSelecionadoMax ? valorSelecionadoMin + 5 : valorSelecionadoMax);
		}else{
			setaValorMinimo(valorSelecionadoMax <= valorSelecionadoMin ? valorSelecionadoMax - 5 : valorSelecionadoMin);
		}

	}

	private void configuraValoresBotoes(){
		configuraTextoBotao(BTN_1, MINIMO, 		 valorSelecionadoMin);
		configuraTextoBotao(BTN_2, MINIMO, valorSelecionadoMin * 2);
		configuraTextoBotao(BTN_3, MINIMO, valorSelecionadoMin * 3);

		configuraTextoBotao(BTN_1, MAXIMO, valorSelecionadoMin * 3);
		configuraTextoBotao(BTN_2, MAXIMO, valorSelecionadoMin * 4);
		configuraTextoBotao(BTN_3, MAXIMO, 		 limiteDiario);
	}

	private void configuraBotoes(Boolean isMin){
		limpaSelecaoBotoes();
		int btnMinSelecionado =  pegaBotaoSelecionado(MINIMO);

		if (valorSelecionadoMin * 4 > limiteDiario) {
			configuraDiferencaMinMax(isMin);
			configuraVisibilidadeBotao(BTN_2,MAXIMO,false);
			configuraVisibilidadeBotao(BTN_1,MAXIMO,false);

			if(btnMinSelecionado != -1){
				switch (btnMinSelecionado) {
					case BTN_1:
						configuraVisibilidadeBotao(BTN_3,MINIMO,false);
						configuraVisibilidadeBotao(BTN_2,MINIMO,false);
						break;
					case BTN_2:
						configuraVisibilidadeBotao(BTN_3,MINIMO,false);
						configuraVisibilidadeBotao(BTN_1,MINIMO,false);
						break;
					case BTN_3:
						configuraVisibilidadeBotao(BTN_1,MINIMO,false);
						configuraVisibilidadeBotao(BTN_2,MINIMO,false);
						break;
				}
			}
		}

		//selecionado minimo
		if (valorSelecionadoMin.equals(pegaValorBotao(BTN_1,MINIMO))){
			configuraBotaoSelecionado(BTN_1,MINIMO,true);

		} else if(valorSelecionadoMin.equals(pegaValorBotao(BTN_2,MINIMO))){
			configuraBotaoSelecionado(BTN_2,MINIMO,true);

		} else if(valorSelecionadoMin.equals(pegaValorBotao(BTN_3,MINIMO))){
			configuraBotaoSelecionado(BTN_3,MINIMO,true);
		}

		//selecionado maximo
		if (valorSelecionadoMax.equals(pegaValorBotao(BTN_1,MAXIMO))){
			configuraBotaoSelecionado(BTN_1,MAXIMO,true);

		} else if(valorSelecionadoMax.equals(pegaValorBotao(BTN_2,MAXIMO))){
			configuraBotaoSelecionado(BTN_2,MAXIMO,true);

		} else if(valorSelecionadoMax.equals(pegaValorBotao(BTN_3,MAXIMO))){
			configuraBotaoSelecionado(BTN_3,MAXIMO ,true);
		}
		configuraValoresSelecionados();
 	}

 	private void configuraValoresSelecionados(){
		tvValorMaximo.setText("R$ " + StringUtils.formatDouble(valorSelecionadoMax));
		tvValorMinimo.setText("R$ " + StringUtils.formatDouble(valorSelecionadoMin));
	}


	private void configuraPopup(Boolean isMin) {
		dialogLimite = new Dialog(getActivity());
		dialogLimite.setContentView(R.layout.custom_dialog_valor_aposta);

		TextView tituloDialog = dialogLimite.findViewById(R.id.tv_titulo);
		TextView valorLimite  = dialogLimite.findViewById(R.id.tv_valor_limite);

		EditText etLimite = dialogLimite.findViewById(R.id.et_valor_aposta);
		etLimite.addTextChangedListener(new MoneyTextWatcher(etLimite));
		Button btnOk       = dialogLimite.findViewById(R.id.btn_ok);
		Button btnCancelar = dialogLimite.findViewById(R.id.btn_cancelar);

		String descricao = getString(R.string.conf_rapd_popup_limite_max).replace("{limite_max}", StringUtils.formatDouble(limiteDiario));


		if (isMin) {
			tituloDialog.setText(getResources().getString(R.string.conf_rapd_popup_limite_titulo_min));
			etLimite.setHint(R.string.conf_rapd_popup_limite_placeholder_min);
			valorLimite.setText(pegaMinMaxTexto(valorMinimo, limiteDiario));
		} else {
			tituloDialog.setText(getResources().getString(R.string.conf_rapd_popup_limite_titulo_max));
			etLimite.setHint(R.string.conf_rapd_popup_limite_placeholder_max);
			valorLimite.setText(ViewUtils.textColor(getActivity(), descricao, R.color.verde_card));

		}

		btnOk.setOnClickListener(v -> {
			if (etLimite.getText().length() > 0) {
				Double valor = Double.valueOf(MoneyTextWatcher.formatPriceSave(etLimite.getText().toString()));
				if (valor >= valorMinimo && valor <= limiteDiario) {

					if (isMin) {
						setaValorMinimo(valor);
						configuraValoresBotoes();
						configuraLogica(MINIMO);
					} else {
						if (valor > valorMinimo + 5) {
							setaValorMaximo(valor);
							configuraValoresBotoes();
							configuraLogica(MAXIMO);
						} else {
							setaValorMaximo(valorMinimo + 5);
							setaValorMinimo(valorMinimo);
							mostraMsgValorAjustado();
							configuraValoresBotoes();
							configuraLogica(MINIMO);
						}
					}
					dialogLimite.dismiss();
				} else {
					mostraMsgValorIncorreto();
				}
			} else {
				mostraMsgValorIncorreto();
			}
		});

		btnCancelar.setOnClickListener(v -> dialogLimite.dismiss());
		dialogLimite.show();
	}

	private void limpaSelecaoBotoes(){
		for (Button btn: botoesMinimos) {
			btn.setSelected(false);
			btn.setVisibility(View.VISIBLE);
		}
		for (Button btn: botoesMaximos) {
			btn.setSelected(false);
			btn.setVisibility(View.VISIBLE);
		}
	}

	private int pegaBotaoSelecionado(Boolean isMin) {
		return isMin ? pegaBotaoSelecionado(valorSelecionadoMin,MINIMO):pegaBotaoSelecionado(valorSelecionadoMax,MAXIMO);
	}

	private int pegaBotaoSelecionado(Double valorSelecionado, Boolean isMin){
		int cont, indiceFinal = -1, tamLista;
		Button btnAtual;
		tamLista = isMin ? botoesMinimos.size() : botoesMaximos.size();
		try {


			for (cont = 0; cont < tamLista - 1; cont++) {
				btnAtual = pegaBotaoPorIndex(cont, isMin);
				if (valorSelecionado.equals(Double.valueOf(btnAtual.getText().toString()))) {
					indiceFinal = cont;
				}
			}
			return indiceFinal;
		} catch (Exception e){
			return indiceFinal;
		}
	}

	private void configuraTextoBotao(int index, Boolean isMin, String texto){
		pegaBotaoPorIndex(index,isMin).setText(texto);
	}

	private void configuraTextoBotao(int index, Boolean isMin, Double valor){
		pegaBotaoPorIndex(index,isMin).setText(String.valueOf(valor.intValue()));
	}

	private void configuraTextoBotao(int index, Boolean isMin,String texto ,Double valor){
		configuraTextoBotao(index,isMin, texto + valor);
	}

	private void configuraBotaoSelecionado(int index, Boolean isMin,Boolean isSelected){
		pegaBotaoPorIndex(index,isMin).setSelected(isSelected);
	}

	private void configuraVisibilidadeBotao(int index, Boolean isMin,Boolean isVisible){
		pegaBotaoPorIndex(index,isMin).setVisibility(isVisible? View.VISIBLE:View.GONE);
	}

	private Button pegaBotaoPorIndex(int index, Boolean isMin){
		return isMin ? botoesMinimos.get(index): botoesMaximos.get(index);
	}

	private Double pegaValorBotao(int index, Boolean isMin){
		try{
			return Double.valueOf(pegaBotaoPorIndex(index, isMin).getText().toString());
		}catch (Exception e){
			return new Double(0);
		}

	}

	private void mostraMsgValorIncorreto(){
		DialogUtils.dialogEntendi(
				getActivity(),
				"O valor selecionado tem que estar entre R$ " +
						StringUtils.formatDouble(valorMinimo) +
						" e R$ " +
						StringUtils.formatDouble(limiteDiario)
		);
	}

	private void mostraMsgValorAjustado(){
		DialogUtils.dialogEntendi(
				getActivity(),
				"O valor escolhido tem que ser um pouco maior que o valor mínimo do carrinho, para isso já ajustamos o máximo para R$ " +
						StringUtils.formatDouble(valorSelecionadoMax) +
						" e o mínimo para R$ " +
						StringUtils.formatDouble(valorSelecionadoMin)+
						" assim tudo vai funcionar perfeitamente."
		);
	}

	private Spannable pegaMinMaxTexto(Double valMin, Double valMax){
		Typeface font = ViewUtils.getFontCaixaStdBold(getActivity());
		String   normal1 = "Mínimo de ";
		String   cor1 = "R$ " + StringUtils.formatDouble(valMin);
		String   normal2 = " e máximo de ";
		String   cor2   = "R$ "+ StringUtils.formatDouble(valMax);
		String   normal3 = ".";

		String    finalString  = normal1+cor1+normal2+cor2+normal3;
		Spannable sb           = new SpannableString(finalString);

		sb.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.verde_card)),
				   finalString.indexOf(cor1),
				   finalString.indexOf(cor1)+ cor1.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new CustomTypefaceSpan("", font),
				   finalString.indexOf(cor1),
				   finalString.indexOf(cor1)+ cor1.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.verde_card)),
				   finalString.indexOf(cor2),
				   finalString.indexOf(cor2)+ cor2.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new CustomTypefaceSpan("", font),
				   finalString.indexOf(cor2),
				   finalString.indexOf(cor2)+ cor2.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sb;
	}

	@Override
	public void onClick(View view) {
		int botaoMinSelecionado = -1;
		switch (view.getId()){
			case R.id.btn_vl_min_1: setaValorMinimo(pegaValorBotao(BTN_1,MINIMO));
				configuraLogica(MINIMO);
				break;
			case R.id.btn_vl_min_2: setaValorMinimo(pegaValorBotao(BTN_2,MINIMO));
				configuraLogica(MINIMO);
				break;
			case R.id.btn_vl_min_3: setaValorMinimo(pegaValorBotao(BTN_3,MINIMO));
				configuraLogica(MINIMO);
				break;
			case R.id.btn_vl_min_4: configuraPopup(MINIMO);
				break;
			case R.id.btn_vl_max_1: setaValorMaximo(pegaValorBotao(BTN_1,MAXIMO));
				configuraLogica(MAXIMO);
				break;
			case R.id.btn_vl_max_2: setaValorMaximo(pegaValorBotao(BTN_2,MAXIMO));
				configuraLogica(MAXIMO);
				break;
			case R.id.btn_vl_max_3: setaValorMaximo(pegaValorBotao(BTN_3,MAXIMO));
				configuraLogica(MAXIMO);
				break;
			case R.id.btn_vl_max_4: configuraPopup(MAXIMO);
				break;
		}
	}

	private void setaValorMaximo(Double valorMax){
		valorSelecionadoMax = valorMax;
		rapidaoConfigSingleton.getRapidaoConfig().setValorMaximo(new BigDecimal(valorMax));
	}

	private void setaValorMinimo(Double valorMin){
		valorSelecionadoMin = valorMin;
		rapidaoConfigSingleton.getRapidaoConfig().setValorMinimo(new BigDecimal(valorMin));
	}

	@Override
	public void onDestroy() {
		atualizaValoresSingleton();
		super.onDestroy();
	}

	private void atualizaValoresSingleton(){
		if (rapidaoConfigSingleton.getRapidaoConfig() != null && rapidaoConfigSingleton.getRapidaoConfig().getValorMinimo() != null){
			valorSelecionadoMin = rapidaoConfigSingleton.getRapidaoConfig().getValorMinimo().doubleValue();
		} else {
			valorSelecionadoMin = valorMinimo;
		}

		if (rapidaoConfigSingleton.getRapidaoConfig() != null && rapidaoConfigSingleton.getRapidaoConfig().getValorMaximo() != null){
			valorSelecionadoMax = rapidaoConfigSingleton.getRapidaoConfig().getValorMaximo().doubleValue();
		} else {
			valorSelecionadoMax = limiteDiario;
		}
	}
}