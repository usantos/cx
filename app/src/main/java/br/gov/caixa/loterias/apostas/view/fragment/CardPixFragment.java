package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CardPixFragment extends Fragment {
	private static final String ARG_TELA = "ARG_TELA";
	private static final String ARG_VALOR = "ARG_VALOR";
	public static final int TELA_RESGATE = 0;
	public static final int TELA_ORIENTACAO = 1;
	public static final int TELA_COPIA_COLA = 2;

	private View view;
	private TextView txtValor;

	private int numeroTela = 0;
	private GerarPixDTO pixDTO;
	private CopiaColaPixFragment copiaColaPixFragment;

	public CardPixFragment() {}

	public static CardPixFragment newOrientacaoInstance(int tela) {
		CardPixFragment fragment = new CardPixFragment();
		Bundle          args     = new Bundle();

		args.putInt(ARG_TELA, tela);

		fragment.setArguments(args);
		return fragment;
	}

	public static CardPixFragment newCopiaColaInstance(GerarPixDTO pix, int tela) {
		CardPixFragment fragment = new CardPixFragment();
		Bundle          args     = new Bundle();

		args.putInt(ARG_TELA, tela);
		args.putString(ARG_VALOR, new Gson().toJson(pix));

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			numeroTela = getArguments().getInt(ARG_TELA);
			String stringPix = getArguments().getString(ARG_VALOR);
			if (stringPix != null && !stringPix.isEmpty()){
				pixDTO = new Gson().fromJson(stringPix,GerarPixDTO.class);
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_card_pix, container, false);

		view.findViewById(R.id.contuedo).setBackgroundColor(getResources().getColor(R.color.verde_pix));
		txtValor = view.findViewById(R.id.valor_pix);

		if (isTela(TELA_COPIA_COLA)){
			ViewUtils.setMoedaFormatHtml(pixDTO.getValorTotal(), txtValor);
		}

		startFragment();

		return view;
	}

	private void startFragment() {
		if (isTela(TELA_ORIENTACAO)){
			view.findViewById(R.id.valor_pix).setVisibility(View.GONE);
			FragmentUtils.startOrientacaoPix(getActivity().getSupportFragmentManager(),R.id.corpo);
		} else if (isTela(TELA_COPIA_COLA)){
			copiaColaPixFragment = FragmentUtils.startCopiaColaPix(pixDTO, null, getActivity().getSupportFragmentManager(), R.id.corpo);
		} else if (isTela(TELA_RESGATE)){
			view.findViewById(R.id.valor_pix).setVisibility(View.GONE);
		}
	}

	private boolean isTela(int tela) {
		return numeroTela == tela;
	}

	public void checkStatusPix(OnCompraAsync listener) {
		if (copiaColaPixFragment != null){
			copiaColaPixFragment.checkaStatusPix(listener);
		}
	}
}