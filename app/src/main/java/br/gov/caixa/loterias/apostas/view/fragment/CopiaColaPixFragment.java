package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class CopiaColaPixFragment extends Fragment {

	private View view;
	private Button btnCopia;
	private EditText edtNumeroPix;
	private TextView timerView;
	private ImageView iconTimer;

	private String numeroPix;

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
		edtNumeroPix = view.findViewById(R.id.numero_copiar);
		timerView = view.findViewById(R.id.txt_contador);
		iconTimer = view.findViewById(R.id.img_timer);

		numeroPix = "91872361973561823675127365128371238767642";
		edtNumeroPix.setText(numeroPix.substring(0,24) + "...");
		edtNumeroPix.setFocusable(false);

		configListeners();
		startTimer();

		return view;
	}

	private void startTimer() {
		ContagemRegressiva contDown = new ContagemRegressiva(timerView, 125 * 1000, 1000) {
			@Override
			public void onFinish() {
				edtNumeroPix.setText("Tempo esgotado!");
				edtNumeroPix.setBackground(VectorUtils.getShape(R.drawable.shape_input_pix_copia_cola, R.color.cinza_borda));
				edtNumeroPix.setTextColor(getResources().getColor(R.color.branco));
				numeroPix = "";
				btnCopia.setEnabled(false);
			}
		};

		contDown.setListener(onContagemListener(contDown));
		contDown.start();
	}

	@NonNull
	private ContagemRegressiva.ContagemRegressivaListener onContagemListener(ContagemRegressiva contDown) {
		return (millisUntilFinish) -> {
			if (contDown.estaFaltandoXMin(millisUntilFinish, 1)){
				timerView.setTextColor(Aplicacao.application.getResources().getColor(R.color.vermelho_cancelada));
				iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.vermelho_cancelada));
			} else if (contDown.estaFaltandoXMin(millisUntilFinish, 2)){
				timerView.setTextColor(Aplicacao.application.getResources().getColor(R.color.jr_amarelo));
				iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.jr_amarelo));
			}
		};
	}

	private void configListeners() {
		btnCopia.setOnClickListener(v -> {
			if (!numeroPix.isEmpty()) {
				TestVisao.toast("Pix copiado.");
				Utils.delay(5, () -> {
					Bundle bundle = new Bundle();
					bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);

					Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ConfirmacaoPagamentoActivity.class, bundle);
					startActivity(intent);
				});
			}
		});
	}

}