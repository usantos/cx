package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.OrientacaoPix;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.OrientacaoPixRecyclerView;

public class OrientacaoPixFragment extends Fragment {

	private View view;
	private RecyclerView listView;
	private AppCompatCheckBox checkBox;
	private Button btnPagarAgora;

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
				TestVisao.toast("Agora, bora fazer a proxima tela");
			} else {
				TestVisao.toast("Sinto muito! Ta desabilitado.");
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