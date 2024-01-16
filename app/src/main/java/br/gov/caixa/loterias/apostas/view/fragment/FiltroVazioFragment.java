package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnEstadoVazioListener;

public class FiltroVazioFragment extends Fragment {
	private  View view;
	private Button btnAlterar, btnContinuar;
	private OnEstadoVazioListener listener;

	public FiltroVazioFragment() {}

	public static FiltroVazioFragment newInstance() {
		FiltroVazioFragment fragment = new FiltroVazioFragment();
		Bundle                  args     = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			listener = (OnEstadoVazioListener) context;
		}catch (Exception e){}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_filtro_vazio, container, false);
		btnAlterar = view.findViewById(R.id.id_btn_alterar);
		btnContinuar = view.findViewById(R.id.id_btn_continuar);

		btnAlterar.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getActivity().getString(R.string.alterar_pesquisa)));

		btnAlterar.setOnClickListener(onAlterarListener());
		btnContinuar.setOnClickListener(onContinuarListener());
		return view;
	}

	private View.OnClickListener onContinuarListener() {
		return v -> {
			listener.continuar();
		};
	}

	private View.OnClickListener onAlterarListener() {
		return v -> {
			listener.alterar();
		};
	}

}