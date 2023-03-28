package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;

public class CopiaColaPixFragment extends Fragment {

	private static final String ARG_MILLIS_FIM = "ARG_MILLIS_FIM";
	public static int SEGUNDOS = 30000;

	private View view;
	private Button btnCopia;
	private EditText edtNumeroPix;
	private TextView timerView, voceTemView, txtEfetuarPagamento;
	private ImageView iconTimer;

	private String numeroPix;

	private boolean saiuParaPagar = false;
	private boolean copiouText = false;
	private long millisUntilFinish;
	private OnCopiaColaListener listener;

	public CopiaColaPixFragment() {}

	public static CopiaColaPixFragment newInstance(long millisUntilFinish) {
		CopiaColaPixFragment fragment = new CopiaColaPixFragment();
		Bundle               args     = new Bundle();
		args.putLong(ARG_MILLIS_FIM, millisUntilFinish);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			listener = (OnCopiaColaListener) context;
		}catch (Exception e){

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			millisUntilFinish = getArguments().getLong(ARG_MILLIS_FIM, 0l);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_copia_cola_pix, container, false);
		btnCopia = view.findViewById(R.id.btn_copiar_colar);
		edtNumeroPix = view.findViewById(R.id.numero_copiar);
		timerView = view.findViewById(R.id.txt_contador);
		voceTemView = view.findViewById(R.id.txt_voce_tem);
		txtEfetuarPagamento = view.findViewById(R.id.txt_para_efetuar_pagamento);
		iconTimer = view.findViewById(R.id.img_timer);

		numeroPix = "91872361973561823675127365128371238767642";
		edtNumeroPix.setText(numeroPix.substring(0,24) + "...");
		edtNumeroPix.setFocusable(false);

		configListeners();
		startTimer();

		return view;
	}

	private void startTimer() {
		ContagemRegressiva contDown = new ContagemRegressiva(timerView, millisUntilFinish, 1000) {
		//ContagemRegressiva contDown = new ContagemRegressiva(timerView, SEGUNDOS, 1000) {
			@Override
			public void onFinish() {
				try {
					edtNumeroPix.setText("");
					edtNumeroPix.setBackground(VectorUtils.getShape(R.drawable.shape_input_pix_copia_cola, R.color.cinza_borda));
					edtNumeroPix.setTextColor(getActivity().getResources().getColor(R.color.branco));
					numeroPix = "";
					btnCopia.setEnabled(false);
					btnCopia.setVisibility(View.GONE);
					voceTemView.setVisibility(View.INVISIBLE);
					txtEfetuarPagamento.setText("Tempo esgotado, o pagamento não foi confirmado");
					txtEfetuarPagamento.setTextColor(getResources().getColor(R.color.vermelho_dialog));
					if (listener != null){
						listener.onFinish();
					}
				} catch (Exception e){}
			}
		};

		contDown.setListener(onContagemListener(contDown));
		contDown.start();
		TimerSingleton.getInstance().setContagemRegressiva(contDown);
	}

	@NonNull
	private ContagemRegressiva.ContagemRegressivaListener onContagemListener(ContagemRegressiva contDown) {
		return millisUntilFinish -> {
			if (contDown.estaFaltandoXMin(millisUntilFinish, 1)){
				timerView.setTextColor(Aplicacao.application.getResources().getColor(R.color.vermelho_dialog));
				iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.vermelho_dialog));
			} else if (contDown.estaFaltandoXMin(millisUntilFinish, 2)){
				timerView.setTextColor(Aplicacao.application.getResources().getColor(R.color.jr_amarelo));
				iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.jr_amarelo));
			}
		};
	}

	private void configListeners() {
		btnCopia.setOnClickListener(v -> {
			if (!numeroPix.isEmpty()) {
				copiaTesto();
				TestVisao.toast("Pix copiado.");
			}
		});
	}

	private void copiaTesto() {
		ClipData         clip      = ClipData.newPlainText("label", numeroPix);
		ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		clipBoard.setPrimaryClip(clip);
	}

}