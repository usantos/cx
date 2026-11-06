package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;

public class ConfirmacaoChavePixResgateFragment extends Fragment {
	private static final String ARG_DADOS_CHAVE_PIX = "ARG_CHAVE";
	private TextView txtNome, txtCPF, txtChave;
	private DadosChavePixDTO dadosChavePix;

	private ConfirmacaoChavePixResgateFragment() {}

	public static ConfirmacaoChavePixResgateFragment newInstance(DadosChavePixDTO dadosChavePix) {
		ConfirmacaoChavePixResgateFragment fragment = new ConfirmacaoChavePixResgateFragment();
		Bundle               args     = new Bundle();
		args.putSerializable(ARG_DADOS_CHAVE_PIX, dadosChavePix);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null){
			dadosChavePix = (DadosChavePixDTO) getArguments().getSerializable(ARG_DADOS_CHAVE_PIX);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view 	= inflater.inflate(R.layout.fragment_confirmacao_chave_pix_resgate, container, false);
		txtNome		= view.findViewById(R.id.txt_nome);
		txtCPF		= view.findViewById(R.id.txt_cpf);
		txtChave	= view.findViewById(R.id.txt_chave);

		txtNome.setText(dadosChavePix.getNome());
		txtCPF.setText(dadosChavePix.getCpfMascarado());
		txtChave.setText("Chave: " + dadosChavePix.getChave());

		return view;
	}

}