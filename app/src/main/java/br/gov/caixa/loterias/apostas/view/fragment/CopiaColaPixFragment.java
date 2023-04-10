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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Period;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;

public class CopiaColaPixFragment extends Fragment {

	private static final String ARG_PIX_COPIA_COLA = "ARG_MILLIS_FIM";

	private View view;
	private Button btnCopia;
	private EditText edtNumeroPix;
	private TextView timerView, voceTemView, txtEfetuarPagamento, txtAlerta;
	private ImageView iconTimer;

	private String numeroPix;

	private GerarPixDTO pixDTO;
	private OnCopiaColaListener listener;

	public CopiaColaPixFragment() {}

	public static CopiaColaPixFragment newInstance(GerarPixDTO pixDTO) {
		CopiaColaPixFragment fragment = new CopiaColaPixFragment();
		Bundle               args     = new Bundle();
		args.putSerializable(ARG_PIX_COPIA_COLA, new Gson().toJson(pixDTO));
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
			String stringJson = getArguments().getString(ARG_PIX_COPIA_COLA);
			if (stringJson != null && !stringJson.isEmpty()){
				pixDTO = new Gson().fromJson(stringJson, GerarPixDTO.class);
			}
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
		txtAlerta = view.findViewById(R.id.txt_alerta_codigo);

		numeroPix = pixDTO.getPixCopiaECola();
		edtNumeroPix.setText(numeroPix.substring(0,19) + "...");
		edtNumeroPix.setFocusable(false);

		configListeners();
		startTimer();

		return view;
	}

	private void startTimer() {
		ContagemRegressiva contDown = new ContagemRegressiva(timerView, getTimer(), 1000) {
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
					txtAlerta.setText("Código Expirado");
					txtAlerta.setTextColor(getResources().getColor(R.color.vermelho_dialog));
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

	private int getTimer() {

		DateTime  agora  = new DateTime();
		DateTime  expiracao = DateTime.parse(pixDTO.getDataHoraExpiracao());

		int millis = new Period(agora, expiracao).getMillis();

		return millis;
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
				btnCopia.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_pix_copiado, null));
				btnCopia.setEnabled(false);
				Utils.delay(2, () -> {
					btnCopia.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_button_copia, null));
					btnCopia.setEnabled(true);
				});
			}
		});
	}

	private void copiaTesto() {
		ClipData         clip      = ClipData.newPlainText("label", numeroPix);
		ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		clipBoard.setPrimaryClip(clip);
	}

}