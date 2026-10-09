package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnSomadorListener;
import br.gov.caixa.loterias.apostas.view.listener.OnVaiParaCarrinhoListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SomadorCarrinhoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SomadorCarrinhoFragment extends Fragment {
	public static final String ARG_TELA = "ARG_TELA";
	public static final String ARG_IR_CARRINHO = "ARG_IR_CARRINHO";

	private  View view;
	private TextView tvValorTotal, tvQtdApostas;

	private CarrinhoDTO carrinho;
	private String TAG;
	private boolean carregouDoServico;

	private Context context;
	private OnSomadorListener listener;
	private OnVaiParaCarrinhoListener vaiParaCarrinhoListener;
	private boolean podeIrCarrinho = false;

	public SomadorCarrinhoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment SomadorCarrinhoFragment.
	 */
	public static SomadorCarrinhoFragment newInstance(String TAG) {
		SomadorCarrinhoFragment fragment = new SomadorCarrinhoFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_TELA, TAG);
		fragment.setArguments(args);
		return fragment;
	}

	public static SomadorCarrinhoFragment newInstance(Boolean irCarrinho) {
		SomadorCarrinhoFragment fragment = new SomadorCarrinhoFragment();
		Bundle                  args     = new Bundle();
		args.putBoolean(ARG_IR_CARRINHO, irCarrinho);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			listener = (OnSomadorListener) context;
		}catch (Exception e){
			Log.d("SOMADORCARRINHOLISTENER", Objects.requireNonNull(e.getLocalizedMessage()));
		}

		try {
			vaiParaCarrinhoListener = (OnVaiParaCarrinhoListener) context;
		}catch (Exception e){
			Log.d("SOMADORCARRINHOLISTENER", Objects.requireNonNull(e.getLocalizedMessage()));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getContext();
		if (getArguments() != null) {
			TAG = getArguments().getString(ARG_TELA);
			podeIrCarrinho = getArguments().getBoolean(ARG_IR_CARRINHO);
			carrinho = CarrinhoSingleton.getInstance().getCarrinho();
			if (carrinho == null) {
				carregouDoServico = false;
			} else {
				carregouDoServico = true;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_somador_carrinho, container, false);
		tvValorTotal = view.findViewById(R.id.tv_valor_total);
		tvQtdApostas = view.findViewById(R.id.tv_quantidade_apostas);
		if (podeIrCarrinho){
			view.setOnClickListener(v -> {
				if (vaiParaCarrinhoListener != null){
					vaiParaCarrinhoListener.vaiParaCarrinho();
				} else {
					startActivity(new Intent(getActivity(), CarrinhoActivity.class));
				}
			});
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		atualizaCarrinho();
	}

	public void atualizaCarrinho() {
		if(LoginSP.isLoginRealizado()){
			if(carregouDoServico){
				atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
			} else {
				carregaCarrinho();
			}
		} else {
			carregaCarrinhoLocal();
			atualizaValorTotal(carrinho);
		}
	}

	private void carregaCarrinhoLocal() {
		carrinho = new CarrinhoDTO();
		carrinho.setApostas(getApostasLocal());
	}

	private void carregaCarrinho() {
		ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse response) {
				AppCenterManager.registraEvento(context.getResources().getString(R.string.evento_entrou_carrinho_online));
				carrinho = response.getPayload();
				List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocal = getApostasLocal();
				if(apostasLocal != null && apostasLocal.size() >  0){
					carrinho.getApostas().addAll(apostasLocal);
				}
				atualizaValorTotal(carrinho);
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				carregaCarrinhoLocal();
				atualizaValorTotal(carrinho);
			}
		});
	}

	private List<IdentificaoDeUmaApostaDas8Modalidades> getApostasLocal() {
		DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
		return crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
	}

	public void atualizaValorTotal(CarrinhoDTO carrinho){
		this.carrinho = carrinho;

		if(carrinho != null && tvValorTotal != null) {
			ViewUtils.setMoedaFormatHtml(carrinho.getValorTotal(), tvValorTotal);
			Log.d("SOMADOR CARRINHO " + TAG + " -----", tvValorTotal.getText().toString());
		}
		if(carrinho != null && tvQtdApostas != null) {
			if(carrinho.getApostas() != null) {
				if (carrinho.getApostas().size() <= 999) {
					tvQtdApostas.setText(String.valueOf(carrinho.getApostas().size()));
				} else {
					tvQtdApostas.setText(R.string.mais_999);
				}
			}else {
				tvQtdApostas.setText(R.string.zero);
			}
		}
		showFragmentSomadorCarrinho(carrinho);
		if (listener != null){
			listener.atualizaDados(carrinho);
		}
	}

	void showFragmentSomadorCarrinho(CarrinhoDTO carrinho){
		if (carrinho != null && carrinho.getValorTotal() != null && carrinho.getValorTotal().compareTo(BigDecimal.ZERO) == 1){
			view.setVisibility(View.VISIBLE);
		}else {
			view.setVisibility(View.GONE);
		}
	}

	public int getQuantidadeApostas(){
		if(carrinho != null && carrinho.getApostas() != null){
			return carrinho.getApostas().size();
		}
		return 0;
	}

	public BigDecimal getValorTotal(){
		if (carrinho != null){
			return carrinho.getValorTotal();
		}
		return BigDecimal.ZERO;
	}
	
}