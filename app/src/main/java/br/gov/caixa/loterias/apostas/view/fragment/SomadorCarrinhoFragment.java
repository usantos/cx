package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SomadorCarrinhoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SomadorCarrinhoFragment extends Fragment {
	public static final String ARG_CARRINHO = "ARG_CARRINHO";
	public static final String ARG_TELA = "ARG_TELA";

	private  View view;
	private TextView tvValorTotal;

	private CarrinhoDTO carrinho;
	private String TAG;
	private boolean carregouDoServico;

	public SomadorCarrinhoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param carrinho Parameter 1.
	 * @return A new instance of fragment SomadorCarrinhoFragment.
	 */
	public static SomadorCarrinhoFragment newInstance(CarrinhoDTO carrinho, String TAG) {
		SomadorCarrinhoFragment fragment = new SomadorCarrinhoFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_TELA, TAG);
		args.putSerializable(ARG_CARRINHO, carrinho);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			TAG = getArguments().getString(ARG_TELA);
			carrinho = (CarrinhoDTO) getArguments().getSerializable(ARG_CARRINHO);
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
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
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
		final ApostaBO apostaBO = ApostaBO_.getInstance_(getContext());
		apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse response) {
				FirebaseManager.registraEvento(getContext(), getContext().getResources().getString(R.string.evento_entrou_carrinho_online));
				carrinho = response.getPayload();
				List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocal = getApostasLocal();
				if(apostasLocal.size() >  0){
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
		ViewUtils.setMoedaFormatHtml(carrinho.getValorTotal(), tvValorTotal);
		Log.d("SOMADOR CARRINHO "+ TAG +" -----", tvValorTotal.getText().toString());
	}
}