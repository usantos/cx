package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FiltroBolaoResultadoEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnEstadoVazioListener;

public class FiltroVazioFragment extends Fragment {
	private  View view;
	private Button btnAlterar, btnContinuar;
	private ImageView icon, lupa;
	private TextView descricao;
	private OnEstadoVazioListener listener;

	private FiltroBolaoResultadoEnum tipoFiltro = FiltroBolaoResultadoEnum.VAZIO;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_filtro_vazio, container, false);
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
		btnAlterar = view.findViewById(R.id.id_btn_alterar);
		btnContinuar = view.findViewById(R.id.id_btn_continuar);
		descricao = view.findViewById(R.id.id_descricao);
		icon = view.findViewById(R.id.id_trevo);
		lupa = view.findViewById(R.id.id_lupa);

		btnAlterar.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getActivity().getString(R.string.alterar_pesquisa)));

		btnAlterar.setOnClickListener(onAlterarListener());
		btnContinuar.setOnClickListener(onContinuarListener());
	}

	private View.OnClickListener onContinuarListener() {
		return v -> listener.continuar(tipoFiltro);
	}

	private View.OnClickListener onAlterarListener() {
		return v -> listener.alterar(tipoFiltro);
	}

	public void estadoVazio() {
		tipoFiltro = FiltroBolaoResultadoEnum.VAZIO;
		if (view != null){
			descricao.setText(R.string.descricao_filtro_vazio);
			btnAlterar.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.alterar_pesquisa)));
			icon.setBackgroundResource(R.drawable.trevo_caixa_filtro);
			lupa.setVisibility(View.VISIBLE);
		}

	}

	public void estadoErro() {
		tipoFiltro = FiltroBolaoResultadoEnum.ERRO;
		if (view != null){
			descricao.setText(R.string.descricao_erro_filtro);
			btnAlterar.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.pesquisar_novamente)));
			lupa.setVisibility(View.INVISIBLE);
			icon.setBackgroundResource(R.drawable.ic_alerta_filtro_erro);
		}
	}
}