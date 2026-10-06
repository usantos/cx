package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.CopiaColaPixActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.OrientacaoPix;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.PixModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.OrientacaoPixRecyclerView;

public class OrientacaoPixFragment extends Fragment {

	private View view;
	private RecyclerView listView;
	private AppCompatCheckBox checkBox;
	private Button btnPagarAgora;
	private PixModel model;

	public OrientacaoPixFragment() {}

	public static OrientacaoPixFragment newInstance() {
		OrientacaoPixFragment fragment = new OrientacaoPixFragment();
		Bundle                args     = new Bundle();

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_orientacao_pix, container, false);
		listView = view.findViewById(R.id.lista_orientacao);
		checkBox = view.findViewById(R.id.checkbox_orientacao);
		btnPagarAgora = view.findViewById(R.id.button_pagar_agora);
		model = new PixModel(getActivity());
		startListaOrientacao();
		configListeners();

		return view;
	}

	private void configListeners() {
		checkBox.setOnClickListener(onCheckBoxListener());
		btnPagarAgora.setOnClickListener(onPagarAgoraListener());
	}

	private View.OnClickListener onPagarAgoraListener() {
		return v -> {
			if (checkBox.isChecked()){
				btnPagarAgora.setEnabled(false);
				AlertDialogUtils.show(getContext());
				model.gerarCodigoPix(new OnSilceListener<GerarPixDTO>() {
					@Override
					public void success(GerarPixDTO payload) {
						AlertDialogUtils.dismiss();
						CarrinhoSingleton.getInstance().zerarCarrinho();

						Bundle bundle = new Bundle();
						bundle.putBoolean(CopiaColaPixActivity.ARG_GEROU_CODIGO, true);
						bundle.putString(CopiaColaPixActivity.ARG_PIX, new Gson().toJson(payload));

						Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), CopiaColaPixActivity.class, bundle);
						startActivity(intent);
						getActivity().finish();
					}

					@Override
					public void error(VolleyError error) {
						AlertDialogUtils.dismiss();
						btnPagarAgora.setEnabled(true);
					}
				});
			}
		};
	}

	private View.OnClickListener onCheckBoxListener() {
		return v -> {
			if (((AppCompatCheckBox) v).isChecked()){
				btnPagarAgora.setEnabled(true);
				btnPagarAgora.setBackgroundColor(getResources().getColor(R.color.verde_pix));
			} else {
				btnPagarAgora.setEnabled(false);
				btnPagarAgora.setBackgroundColor(getResources().getColor(R.color.cinza_pix_disabled));
			}
		};
	}

	private void startListaOrientacao() {
		OrientacaoPixRecyclerView adapter = new OrientacaoPixRecyclerView(getOrientacaoList());
		listView.setLayoutManager(new LinearLayoutManager(getContext()));
		listView.setAdapter(adapter);
	}

	private List<OrientacaoPix> getOrientacaoList() {
		List<OrientacaoPix> lista = new ArrayList();
		String[] stringArray = getResources().getStringArray(R.array.orientacao_pix_list);
		for (int i = 0; i < stringArray.length; i++) {
			lista.add(new OrientacaoPix(String.valueOf(i+1),stringArray[i]));
		}
		return lista;
	}

}