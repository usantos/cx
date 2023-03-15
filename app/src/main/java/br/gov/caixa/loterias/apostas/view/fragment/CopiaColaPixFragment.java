package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.Utils;

public class CopiaColaPixFragment extends Fragment {

	private View view;
	private Button btnCopia;

	public CopiaColaPixFragment() {}

	public static CopiaColaPixFragment newInstance() {
		CopiaColaPixFragment fragment = new CopiaColaPixFragment();
		Bundle               args     = new Bundle();

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_copia_cola_pix, container, false);
		btnCopia = view.findViewById(R.id.btn_copiar_colar);

		configListeners();

		return view;
	}

	private void configListeners() {
		btnCopia.setOnClickListener(v -> {
			TestVisao.toast("Pix copiado.");
			Utils.delay(5,() -> {
				Bundle bundle = new Bundle();
				bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);

				Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ConfirmacaoPagamentoActivity.class, bundle);
				startActivity(intent);
			});
		});
	}

}