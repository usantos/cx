package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.OrientacaoPixActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;

public class ChavePixFragment extends Fragment {
	private static final String ARG_TELA= "ARG_TELA";
	public static final String TEXTO_CPF = "CPF";
	public static final String TEXTO_EMAIL = "E-mail";
	public static final String TEXTO_CELULAR = "Celular";
	public static final String TEXTO_CHAVE_ALEATORIA = "Chave Aleatória";

	private View view;

	private String texto = "";

	public ChavePixFragment() {}

	public static ChavePixFragment newInstance(String texto) {
		ChavePixFragment fragment = new ChavePixFragment();
		Bundle          args     = new Bundle();

		args.putString(ARG_TELA, texto);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			texto = getArguments().getString(ARG_TELA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chave_pix, container, false);

		TextView tvTexto = view.findViewById(R.id.chavePix);
		tvTexto.setText(texto);

		configClick();


		return view;
	}

	private void configClick() {
		view.setOnClickListener(view -> {

			if (!texto.equals(TEXTO_CPF)) {
				DialogUtils.showDialog3Botoes(getContext(), R.drawable.background_dialogtitle_gradient_color,
						getString(R.string.popup_pix_nao_autorizado),
						getString(R.string.popup_pix_nao_autorizado_descricao),
						getString(R.string.gerar_novo_codigo),
						getString(R.string.alterar_forma_pagamento),
						getString(R.string.btn_title_voltarAoInicio), onDialogListener());
			}

		});
	}

	private OnDialog3ButtonsListener onDialogListener() {
		return new OnDialog3ButtonsListener() {
			@Override
			public void leftButtom() {
				TestVisao.toast("Aguardando a criação das próximas telas...");
				//dismiss();
			}

			@Override
			public void centerButtom() {
				TestVisao.toast("Aguardando a criação das próximas telas...");
				//Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, FormaPagamentoActivity.class);
				//startActivity(intent);
			}

			@Override
			public void rightButtom() {
				TestVisao.toast("Aguardando a criação das próximas telas...");
				//PrincipalActivity_.intent(DetalhesComprasActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
			}
		};
	}
}