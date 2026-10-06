package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.view.custom.ChavePixTextWatcher;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolhaChavePisResgateListener;

public class EscolhaChavePixResgateFragment extends Fragment {
	private TextView txtChaveCPF;
	private AppCompatEditText editOutraChave;
	private RadioButton btnCpf, btnOutraChave;
	private ConstraintLayout containerOutraChave;
	private AppCompatCheckBox checkBox;
	private OnEscolhaChavePisResgateListener listener;
	private boolean isOutraChaveValid = false;
	private CHAVE tipoChave = CHAVE.CPF;

	private EscolhaChavePixResgateFragment() {}

	public String getChavePIX() {
		if (tipoChave == CHAVE.CPF){
			return txtChaveCPF.getText().toString();
		} else {
			return editOutraChave.getText().toString();
		}
	}

	private enum CHAVE{
		CPF, OUTRA
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			listener = (OnEscolhaChavePisResgateListener) context;
		}catch (Exception e){
			Log.d("Escolha_CHAVE_PIX", "PRECISA implementar OnEscolhaChavePixResgateListener");
		}

	}

	public static EscolhaChavePixResgateFragment newInstance() {
		EscolhaChavePixResgateFragment fragment = new EscolhaChavePixResgateFragment();
		Bundle               args     = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view 		= inflater.inflate(R.layout.fragment_escolha_chave_pix_resgate, container, false);
		txtChaveCPF 	= view.findViewById(R.id.txtValorChave);
		editOutraChave	= view.findViewById(R.id.edtOutraChave);
		btnCpf			= view.findViewById(R.id.cpfChave);
		btnOutraChave	= view.findViewById(R.id.outraChave);
		containerOutraChave	= view.findViewById(R.id.containerOutraChave);
		checkBox	= view.findViewById(R.id.checkbox_confirmacao);

		txtChaveCPF.setText(DadosUsuarioBO.obterCpf());
		editOutraChave.addTextChangedListener(onTextWatcher());

		btnCpf.setOnClickListener(v -> trocaVisaoPara(CHAVE.CPF));
		btnOutraChave.setOnClickListener(v -> trocaVisaoPara(CHAVE.OUTRA));
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			checagemValidade(CHAVE.OUTRA);
		});
		return view;
	}

	private ChavePixTextWatcher onTextWatcher() {
		return new ChavePixTextWatcher(isValid -> {
			this.isOutraChaveValid = isValid;
			checagemValidade(CHAVE.OUTRA);
			if (!isValid){
				editOutraChave.setError("Chave inválida!");
			} else {
				editOutraChave.setError(null);
			}
		});
	}

	private void trocaVisaoPara(CHAVE tipoChave) {
		switch (tipoChave){
			case CPF:
				btnCpf.setEnabled(false);
				btnOutraChave.setEnabled(true);
				txtChaveCPF.setVisibility(View.VISIBLE);
				containerOutraChave.setVisibility(View.GONE);
				break;
			case OUTRA:
				btnCpf.setEnabled(true);
				btnOutraChave.setEnabled(false);
				txtChaveCPF.setVisibility(View.GONE);
				containerOutraChave.setVisibility(View.VISIBLE);
				break;
		}
		this.tipoChave = tipoChave;
		checagemValidade(tipoChave);
	}

	private void checagemValidade(CHAVE tipoChave){
		switch (tipoChave){
			case CPF:
				listener.habilitaContinuar(true);
				break;
			case OUTRA:
				if (checkBox.isChecked() && isOutraChaveValid){
					listener.habilitaContinuar(true);
				} else {
					listener.habilitaContinuar(false);
				}
				break;
		}
	}
}