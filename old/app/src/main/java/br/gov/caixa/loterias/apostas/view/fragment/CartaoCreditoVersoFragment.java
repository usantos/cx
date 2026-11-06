package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartaoCreditoVersoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartaoCreditoVersoFragment extends Fragment {
	public static final String ARG_BANDEIRA = "ARG_BANDEIRA";
	public static final String ARG_CVV = "ARG_CVV";

	private  View view;
	private AppCompatImageView imgBandeira;
	private TextView tvCVV;

	private String metodo, cvv;

	public CartaoCreditoVersoFragment() {}

	public static CartaoCreditoVersoFragment newInstance(String metodo, String cvv) {
		CartaoCreditoVersoFragment fragment = new CartaoCreditoVersoFragment();
		Bundle                     args     = new Bundle();
		args.putString(ARG_BANDEIRA, metodo);
		args.putString(ARG_CVV, cvv);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			metodo = getArguments().getString(ARG_BANDEIRA);
			cvv = getArguments().getString(ARG_CVV);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_cartao_verso, container, false);
		imgBandeira = view.findViewById(R.id.imgBandeiraVerso);
		tvCVV = view.findViewById(R.id.textoCVV);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (metodo != null && !metodo.isEmpty()){
			imgBandeira.setImageDrawable(CartaoUtil.getBandeira(getContext(), metodo));
		}

		if (cvv != null && !cvv.isEmpty()){
			tvCVV.setText(cvv);
		} else {
			if (metodo.equalsIgnoreCase("amex") ||
					metodo.equalsIgnoreCase("american express")) {

				tvCVV.setText("XXXX");
			}
		}
	}
}