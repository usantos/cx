package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FiltroBolaoResultadoEnum;
import br.gov.caixa.loterias.apostas.view.listener.OnEstadoVazioListener;

public class LotericaEmptyFragment extends Fragment {
	private  View view;
	private TextView descricao;

	private OnEstadoVazioListener listener;

	private FiltroBolaoResultadoEnum tipoFiltro = FiltroBolaoResultadoEnum.VAZIO;

	public LotericaEmptyFragment() {}

	public static LotericaEmptyFragment newInstance() {
		LotericaEmptyFragment fragment = new LotericaEmptyFragment();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_filtro_vazio_novo, container, false);
		initBindingViews();

		switch (tipoFiltro){
			case VAZIO:
				estadoVazio();
				break;
			case ERRO:
				estadoErro();
				break;
		}
		return view;
	}

	private void initBindingViews() {
		descricao = view.findViewById(R.id.text_estado_vazio);
		descricao.setText(R.string.vazio_Lotericas_favoritas);

	}

	public void estadoVazio() {
		tipoFiltro = FiltroBolaoResultadoEnum.VAZIO;
		if (view != null){
			descricao.setText(R.string.vazio_Lotericas_favoritas);
		}
	}

	public void estadoErro() {
		tipoFiltro = FiltroBolaoResultadoEnum.ERRO;
		if (view != null){
			descricao.setText(R.string.descricao_erro_filtro);
		}
	}
}