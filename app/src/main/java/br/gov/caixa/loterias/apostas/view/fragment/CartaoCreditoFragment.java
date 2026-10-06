package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartaoCreditoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartaoCreditoFragment extends Fragment {
	public static final String ARG_CARTAO = "ARG_CARTAO";

	private  View view;
	private AppCompatImageView imgBandeira;
	private TextView tvNumero, tvNome, tvValidade;

	private CartaoCreditoConfig cartao;

	public CartaoCreditoFragment() {}

	public static CartaoCreditoFragment newInstance(CartaoCreditoConfig cartaoCredito) {
		CartaoCreditoFragment fragment = new CartaoCreditoFragment();
		Bundle                args     = new Bundle();
		args.putSerializable(ARG_CARTAO, cartaoCredito);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {

		}catch (Exception e){

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			cartao = (CartaoCreditoConfig) getArguments().getSerializable(ARG_CARTAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_cartao_credito, container, false);
		imgBandeira = view.findViewById(R.id.imgBandeiraCartao);
		tvNumero = view.findViewById(R.id.textoNumero);
		tvNome = view.findViewById(R.id.textoNome);
		tvValidade = view.findViewById(R.id.textoValidMesAno);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (cartao != null){
			imgBandeira.setImageDrawable(CartaoUtil.getBandeira(getContext(), cartao.getNomeMetodo()));
			tvNumero.setText(cartao.getNumero());
			tvNome.setText(cartao.getNomeTitular());
			tvValidade.setText(cartao.getValidade());
		}
	}

	public void atualizaNumero(String numero){
		tvNumero.setText(numero);
	}

	public void atualizaNome(String nome){
		tvNome.setText(nome);
	}

	public void atualizaValidade(String validade){
		tvValidade.setText(validade);
	}

	public void atualizaBandeira(String nomeMetodo) {
		if (nomeMetodo.isEmpty()){
			imgBandeira.setVisibility(View.INVISIBLE);
		} else {
			imgBandeira.setVisibility(View.VISIBLE);
			imgBandeira.setImageDrawable(CartaoUtil.getBandeira(getContext(), nomeMetodo));
		}
	}
}