package br.gov.caixa.loterias.apostar.interno.view;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostar.interno.R;

public class ConfiguracaoRapidaoFragment3 extends Fragment implements View.OnClickListener{
	private TextView tvValorMinimo, tvValorMaximo;
	private int limiteDiario = 500, valorSelecionadoMin = 30, valorSelecionadoMax = 90;
	private ArrayList<Button>  botoesMinimos = new ArrayList<>(),
							   botoesMaximos = new ArrayList<>();

	private static final int BTN_1 = 0, BTN_2 = 1, BTN_3 = 2, BTN_4 = 3;
	private static final Boolean MINIMO = true, MAXIMO = false;

	private Dialog dialogLimite;

	public ConfiguracaoRapidaoFragment3() {
	}

	public static ConfiguracaoRapidaoFragment3 newInstance() {
		ConfiguracaoRapidaoFragment3 fragment = new ConfiguracaoRapidaoFragment3();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment3, container, false);
		setaViews(view);
		setaMetodos();
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
		configuraLogica();
		configuraClicks();
	}

	private void configuraLogica(){
		configuraValores();
		configuraBotoes();
	}

	private void configuraValores(){
		configuraTextoBotao(BTN_1,MINIMO, valorSelecionadoMin);
		configuraTextoBotao(BTN_2,MINIMO, valorSelecionadoMin * 2);
		configuraTextoBotao(BTN_3,MINIMO, valorSelecionadoMin * 3);

		configuraTextoBotao(BTN_1,MAXIMO, valorSelecionadoMin * 3);
		configuraTextoBotao(BTN_2,MAXIMO, valorSelecionadoMin * 4);
		configuraTextoBotao(BTN_3,MAXIMO, limiteDiario);

		if(valorSelecionadoMin == valorSelecionadoMax ){ //levar em consideracao o que estiver sido selecionado
			//valorSelecionadoMax = valorSelecionadoMin == valorSelecionadoMax ? valorSelecionadoMin + 5 : valorSelecionadoMax;
		}

		tvValorMaximo.setText("R$" + valorSelecionadoMax);
		tvValorMinimo.setText("R$" + valorSelecionadoMin);
	}

	private void configuraBotoes(){
		limpaSelecaoBotoes();
		//Configura mínimo
			if(valorSelecionadoMin * 3 < limiteDiario){
				configuraVisibilidadeBotao(BTN_1,MINIMO,true);
				configuraVisibilidadeBotao(BTN_2,MINIMO,true);

			}else {
				if(valorSelecionadoMin == pegaValorBotao(BTN_1,MAXIMO)) {
					configuraTextoBotao(BTN_1,MINIMO,valorSelecionadoMin);
					configuraVisibilidadeBotao(BTN_2,MINIMO,false);
					configuraVisibilidadeBotao(BTN_3,MINIMO,false);
				}
			}


		if (valorSelecionadoMin == pegaValorBotao(BTN_1,MINIMO)) {
			configuraBotaoSelecionado(BTN_1,MINIMO,true);

		} else if(valorSelecionadoMin == pegaValorBotao(BTN_2,MINIMO)){
			configuraBotaoSelecionado(BTN_2,MINIMO,true);

		} else if(valorSelecionadoMin == pegaValorBotao(BTN_3,MINIMO)){
			configuraBotaoSelecionado(BTN_3,MINIMO,true);
		}


		//Configura máximo
		if(valorSelecionadoMin == pegaValorBotao(BTN_1,MAXIMO)){
			if(valorSelecionadoMax * 4 < limiteDiario){
				configuraVisibilidadeBotao(BTN_1,MAXIMO,true);
				configuraVisibilidadeBotao(BTN_2,MAXIMO,true);

			}else {

				configuraTextoBotao(BTN_1,MAXIMO,valorSelecionadoMax);
				configuraVisibilidadeBotao(BTN_2,MAXIMO,false);
				configuraVisibilidadeBotao(BTN_3,MAXIMO,false);

			}
		}

		if (valorSelecionadoMax == pegaValorBotao(BTN_1,MAXIMO)) {
			configuraBotaoSelecionado(BTN_1,MAXIMO,true);

		} else if(valorSelecionadoMax == pegaValorBotao(BTN_2,MAXIMO)) {
			configuraBotaoSelecionado(BTN_2,MAXIMO,true);

		} else if(valorSelecionadoMax == pegaValorBotao(BTN_3,MAXIMO)) {
			configuraBotaoSelecionado(BTN_3,MAXIMO ,true);
		}
 	}


	private void configuraPopup(Boolean isMin) {
		dialogLimite = new Dialog(getActivity());
		dialogLimite.setContentView(R.layout.custom_dialog_valor_aposta);

		TextView tituloDialog = dialogLimite.findViewById(R.id.tv_titulo);
		TextView valorLimite = dialogLimite.findViewById(R.id.tv_valor_limite);

		EditText etLimite = dialogLimite.findViewById(R.id.et_valor_aposta);
		Button      btnOk       = dialogLimite.findViewById(R.id.btn_ok);
		Button      btnCancelar = dialogLimite.findViewById(R.id.btn_cancelar);

		String descricao = getActivity().getString(R.string.conf_rapd_popup_limite);
		descricao = descricao.replace("{limite}",String.valueOf(limiteDiario));
		valorLimite.setText(descricao);

		tituloDialog.setText("Carrinho Favorito");

		btnOk.setOnClickListener(v -> {
			if(etLimite.getText().length() >= 2) {
				if(isMin){
					valorSelecionadoMin = Integer.valueOf(etLimite.getText().toString());
				} else{
					valorSelecionadoMax = Integer.valueOf(etLimite.getText().toString());
				}
				dialogLimite.dismiss();
				configuraLogica();
			}else {
				Toast.makeText(getActivity(),"Insira algum dígito",Toast.LENGTH_SHORT);
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

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_vl_min_1: valorSelecionadoMin = pegaValorBotao(BTN_1,MINIMO);
				break;
			case R.id.btn_vl_min_2: valorSelecionadoMin = pegaValorBotao(BTN_2,MINIMO);
				break;
			case R.id.btn_vl_min_3: valorSelecionadoMin = pegaValorBotao(BTN_3,MINIMO);
				break;
			case R.id.btn_vl_min_4: configuraPopup(MINIMO);
				break;
			case R.id.btn_vl_max_1: valorSelecionadoMax = pegaValorBotao(BTN_1,MAXIMO);
				break;
			case R.id.btn_vl_max_2: valorSelecionadoMax = pegaValorBotao(BTN_2,MAXIMO);
				break;
			case R.id.btn_vl_max_3: valorSelecionadoMax = pegaValorBotao(BTN_3,MAXIMO);
				break;
			case R.id.btn_vl_max_4: configuraPopup(MAXIMO);
				break;
		}
		configuraLogica();
	}

	private void configuraTextoBotao(int index, Boolean isMin, String texto){
		pegaBotaoPorIndex(index,isMin).setText(texto);
	}

	private void configuraTextoBotao(int index, Boolean isMin, int valor){
		pegaBotaoPorIndex(index,isMin).setText(String.valueOf(valor));
	}

	private void configuraTextoBotao(int index, Boolean isMin,String texto ,int valor){
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

	private int pegaValorBotao(int index, Boolean isMin){
		return Integer.valueOf(pegaBotaoPorIndex(index, isMin).getText().toString());
	}

}