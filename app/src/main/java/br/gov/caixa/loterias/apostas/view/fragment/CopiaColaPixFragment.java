package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.model.model.PixModel;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;

public class CopiaColaPixFragment extends Fragment {

	private static final String ARG_PIX_COPIA_COLA = "ARG_MILLIS_FIM";

	private View view;
	private Button btnCopia, btnMinhasCompras;
	private EditText edtNumeroPix;
	private TextView timerView;
	private TextView tvAtencao, tvAtencaoDescricao;
	private ImageView iconTimer, iconAtualizar;
	private ProgressBar progressBar;

	private String numeroPix;

	private GerarPixDTO pixDTO;
	private OnCopiaColaListener listener;

	private PixModel model;
	private boolean jaCopiou = false;

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
			Log.d("ACTIVITY_NOT_LISTENER", Objects.requireNonNull(e.getLocalizedMessage()));
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

		model = new PixModel(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_copia_cola_pix, container, false);
		btnCopia = view.findViewById(R.id.btn_copiar_colar);
		edtNumeroPix = view.findViewById(R.id.numero_copiar);
		timerView = view.findViewById(R.id.txt_contador);
		iconTimer = view.findViewById(R.id.img_timer);
		iconAtualizar = view.findViewById(R.id.icon_atualizar);
		progressBar = view.findViewById(R.id.pb_loading);
		btnMinhasCompras = view.findViewById(R.id.btn_minhas_compras);
		btnMinhasCompras.setText(ViewUtils.textFuturaAndFuturaBoldTitle(getContext(), getString(R.string.ir_para_minhas_compras)));

		if (pixDTO.isDetalhesCompras()) {
			tvAtencao = view.findViewById(R.id.id_atencao);
			tvAtencaoDescricao = view.findViewById(R.id.atencao_descricao);
			tvAtencao.setVisibility(View.GONE);
			tvAtencaoDescricao.setVisibility(View.GONE);
		}

		numeroPix = pixDTO.getPixCopiaECola();
		if (numeroPix != null) {
			edtNumeroPix.setText(numeroPix.substring(0, 19) + "...");
		}
		edtNumeroPix.setFocusable(false);

		configListeners();
		startTimer();

		return view;
	}

	private void startTimer() {
		ContagemRegressiva contDown = new ContagemRegressiva(timerView, pixDTO.getTimerZoneMilleSeconds(), 1000) {
			@Override
			public void onFinish() {
				try {

					timerView.setTextColor(Aplicacao.application.getResources().getColor(R.color.vermelho_dialog));
					timerView.setText(R.string.tempo_zerado);
					iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.vermelho_dialog));
					edtNumeroPix.setText(R.string.codigo_expirado);
					edtNumeroPix.setTextColor(Aplicacao.application.getResources().getColor(R.color.vermelho_dialog));
					edtNumeroPix.setBackground(VectorUtils.getShape(R.drawable.shape_input_pix_copia_cola, R.color.cinza_borda));
					edtNumeroPix.setTextColor(getActivity().getResources().getColor(R.color.branco));
					numeroPix = "";
					btnCopia.setEnabled(false);
					btnCopia.setVisibility(View.GONE);
					copiaTexto(getString(R.string.codigo_expirado));
					iconAtualizar.setVisibility(View.INVISIBLE);
					iconAtualizar.setEnabled(false);
					progressBar.setVisibility(View.GONE);
					if (!pixDTO.isDetalhesCompras()) {
						btnMinhasCompras.setVisibility(View.VISIBLE);
					}
					if (listener != null){
						listener.onFinish();
					}
				} catch (Exception e){
					Log.d("ERRO_FINALIZA_CONTADOR", Objects.requireNonNull(e.getLocalizedMessage()));
				}
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
				copiaTexto(numeroPix);
				TestVisao.toast("Pix copiado.");
				btnCopia.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_pix_copiado, null));
				btnCopia.setEnabled(false);
				Utils.delay(2, () -> {
					btnCopia.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_button_copia, null));
					btnCopia.setEnabled(true);
				});
				Utils.delay(10,() -> model.verificaStatusPixPeriodicamente(pixDTO,onCheckPixPagoListener()));
				jaCopiou = true;
			}
		});

		btnMinhasCompras.setOnClickListener(v -> {
			Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ListaComprasActivity.class);
			startActivity(intent);
		});

		iconAtualizar.setOnClickListener(v -> {
			iconAtualizar.setEnabled(false);
			iconAtualizar.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			checkaStatusPix(onCheckStatusPix());
		});
	}

	public void checkaStatusPix(OnCompraAsync listener){
		model.verificaStatusPix(pixDTO, listener);
	}

	private OnCompraAsync onCheckStatusPix() {
		return new OnCompraAsync() {
			@Override
			public void successCompra() {
				sussecoCompra();
			}

			@Override
			public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
				RedirectNetwork.checkRedirectSucesso( compraAsyncResponse.getRedirect(), getActivity());
				if (pixDTO.estaNoPrazo()){
					iconAtualizar.setEnabled(true);
					iconAtualizar.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void errorCompra(VolleyError volleyError) {
				RedirectNetwork.checkRedirectCompraAsync(volleyError, getActivity());
				if (pixDTO.estaNoPrazo()){
					iconAtualizar.setEnabled(true);
					iconAtualizar.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			}
		};
	}

	private OnCompraAsync onCheckPixPagoListener() {
		return new OnCompraAsync() {
			@Override
			public void successCompra() {
				sussecoCompra();
			}

			@Override
			public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
				RedirectNetwork.checkRedirectSucesso( compraAsyncResponse.getRedirect(), getActivity());
			}

			@Override
			public void errorCompra(VolleyError volleyError) {
				RedirectNetwork.checkRedirectCompraAsync(volleyError, getActivity());
			}
		};
	}

	private void sussecoCompra() {
		if (listener != null){
			listener.onSuccess();
		}
	}

	private void copiaTexto(String value) {
		ClipData         clip      = ClipData.newPlainText("label", value.trim());
		ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		clipBoard.setPrimaryClip(clip);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (jaCopiou){
			model.verificaStatusPixPeriodicamente(pixDTO, onCheckPixPagoListener());
		}
	}
}