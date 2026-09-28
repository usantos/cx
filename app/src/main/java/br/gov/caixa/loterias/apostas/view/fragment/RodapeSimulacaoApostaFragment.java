package br.gov.caixa.loterias.apostas.view.fragment;


import static br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity.LER_CARRINHO_LOCAL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class RodapeSimulacaoApostaFragment extends Fragment {
	private static final String ARG_DETALHES_BOLAO = "DETALHES_BOLAO";
	private String quantidadeApostas = "0";
	private static final String ARG_VALOR_APOSTA = "VALOR_APOSTA";
	private static final String ARG_VALOR_CARRINHO = "VALOR_CARRINHO";

	private View view;
	private TextView tvValorAposta;
	private TextView tvValorCarrinho;
	private ImageView circuloQtd;
	private TextView tvQtd;

	private BigDecimal valorAposta, valorCarrinho;
	private ConstraintLayout carrinhoApostas, containerValor;

	public RodapeSimulacaoApostaFragment() {
		// Required empty public constructor
	}

	public static RodapeSimulacaoApostaFragment newInstance(BigDecimal valorAposta, BigDecimal valorCarrinho) {
		RodapeSimulacaoApostaFragment fragment = new RodapeSimulacaoApostaFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_VALOR_APOSTA, new Gson().toJson(valorAposta));
		args.putString(ARG_VALOR_CARRINHO, new Gson().toJson(valorCarrinho));
		fragment.setArguments(args);
		return fragment;
	}

    public static RodapeSimulacaoApostaFragment newInstanceDetalhesBolao(BigDecimal valorAposta, BigDecimal valorCarrinho) {
        RodapeSimulacaoApostaFragment fragment = newInstance(valorAposta, valorCarrinho);
        fragment.getArguments().putBoolean(ARG_DETALHES_BOLAO, true);
        return fragment;
    }

    private boolean isDetalhesBolao() {
        return getArguments() != null && getArguments().getBoolean(ARG_DETALHES_BOLAO);
    }

	public static RodapeSimulacaoApostaFragment newInstance(BigDecimal valorAposta) {
		RodapeSimulacaoApostaFragment fragment = new RodapeSimulacaoApostaFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_VALOR_APOSTA, new Gson().toJson(valorAposta));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			if (valorAposta == null) valorAposta = new BigDecimal(getArguments().getString(ARG_VALOR_APOSTA));
			if (valorCarrinho == null && getArguments().getString(ARG_VALOR_CARRINHO) != null){
				valorCarrinho = new BigDecimal(getArguments().getString(ARG_VALOR_CARRINHO));
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(isDetalhesBolao() ? R.layout.fragment_rodape_detalhes_bolao : R.layout.fragment_rodape_simulacao_aposta, container, false);

		tvValorAposta = view.findViewById(R.id.valorApostaCartela);
		tvValorCarrinho = view.findViewById(R.id.valorAtualCarrinho);
		circuloQtd = view.findViewById(R.id.circuloQuantidade);
		tvQtd = view.findViewById(R.id.tvQuantidadeApostas);
		carrinhoApostas = view.findViewById(R.id.simularApostaLayoutCarrinhoApostas);
		containerValor = view.findViewById(R.id.container_valor_aposta);
		carrinhoApostas.setOnClickListener(v -> abreCarrinhoApostas());
		if (!isDetalhesBolao()) setAccessibility(containerValor);
		setAccessibility(carrinhoApostas);

		atualizaValorBolao(valorAposta);
		atualizaValorCarrinho(valorCarrinho);
		if (isDetalhesBolao()) apresentaQtdApostas(true, quantidadeApostas);

		return view;
	}

	private void setAccessibility(View view){
		ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() {
			@Override
			public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
				super.onInitializeAccessibilityNodeInfo(host, info);
				info.setClassName(Button.class.getName());
			}
		});
	}
	public void atualizaValorBolao(BigDecimal valorApostaBolao){
		valorAposta = valorApostaBolao;
		if (valorApostaBolao != null && tvValorAposta != null){
			ViewUtils.setMoedaFormatHtml(valorApostaBolao, tvValorAposta);
		}
	}

	public void atualizaValorCarrinho(BigDecimal valorTotal){
		valorCarrinho = valorTotal;
		if (tvValorCarrinho == null) return;
		if (valorTotal != null && tvValorCarrinho != null){
			view.findViewById(R.id.tv_valor_atual_carrinho).setVisibility(View.VISIBLE);
			tvValorCarrinho.setVisibility(View.VISIBLE);
			ViewUtils.setMoedaFormatHtml(valorTotal, tvValorCarrinho);
		} else {
			view.findViewById(R.id.tv_valor_atual_carrinho).setVisibility(View.INVISIBLE);
			tvValorCarrinho.setVisibility(View.INVISIBLE);
		}
	}

	public void apresentaQtdApostas(boolean isShow, String qtdApostas){
		quantidadeApostas = qtdApostas;
		if (tvQtd == null) return;
		if (isDetalhesBolao()) carrinhoApostas.setContentDescription(getString(R.string.mkp_carrinho_quantidade, qtdApostas));
		if (isShow){
			circuloQtd.setVisibility(View.VISIBLE);
			tvQtd.setVisibility(View.VISIBLE);
			tvQtd.setText(qtdApostas);
		} else {
			circuloQtd.setVisibility(View.INVISIBLE);
			tvQtd.setVisibility(View.INVISIBLE);
		}
	}

	public void abreCarrinhoApostas() {
		AlertDialogUtils.show(getActivity());
		ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse response) {
				AlertDialogUtils.dismiss();
				CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
				startActivity(new Intent(getActivity(), CarrinhoActivity.class));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( response.getRedirect(), getActivity());
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				AlertDialogUtils.dismiss();
				if (MensagensNetwork.isUnauthorizedError(error)) {
					startActivity(new Intent(getActivity(), CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
				} else {
					RedirectNetwork.checkRedirect( error, getActivity() );
				}
			}
		});
	}

	public TextView getTvValorAposta(){
		return tvValorAposta;
	}

	public TextView getTvValorCarrinho(){
		return tvValorCarrinho;
	}
}