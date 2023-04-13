package br.gov.caixa.loterias.apostas.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;

public class ChavePixFragment extends Fragment {
	private static final String ARG_TELA= "ARG_TELA";
	public static final String TEXTO_CPF = "CPF";
	public static final String TEXTO_EMAIL = "E-mail";
	public static final String TEXTO_CELULAR = "Celular";
	public static final String TEXTO_CHAVE_ALEATORIA = "Chave Aleatória";

	private View view;

	private String texto = "";

	public ChavePixFragment() {}

	private Dialog dialogNaoCPF;
	private Dialog dialogIsCPF;

	public static ChavePixFragment newInstance(String texto) {
		ChavePixFragment fragment = new ChavePixFragment();
		Bundle          args     = new Bundle();

		args.putString(ARG_TELA, texto);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			texto = getArguments().getString(ARG_TELA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chave_pix, container, false);

		TextView tvTexto = view.findViewById(R.id.chavePix);
		tvTexto.setText(texto);

		configClick();


		return view;
	}

	@SuppressLint("ResourceAsColor")
	private void configClick() {
		view.setOnClickListener(view -> {

			if (texto.equals(TEXTO_CPF)) {
				dialogIsCPF = DialogUtils.showDialogCustom(getContext(),
						getString(R.string.pix_chave_invalida),
						getString(R.string.popup_pix_cpf_nao_vinculado),
						getString(R.string.trocar_tipo_chave),
						getString(R.string.trocar_forma_resgate),
						null,
						onDialogListenerIsCpf());
			}
			if (!texto.equals(TEXTO_CPF)) {
				dialogNaoCPF = DialogUtils.showDialogCustom(getContext(),
					getString(R.string.label_atencao),
					getString(R.string.popup_pix_cpf_vinculado),
					getString(R.string.trocar_tipo_chave),
					getString(R.string.trocar_forma_resgate),
					getString(R.string.pix_continuar),
					onDialogListenerNaoCpf());
			}
		});
	}

	private OnDialog3ButtonsListener onDialogListenerNaoCpf() {
		return new OnDialog3ButtonsListener() {
			@Override
			public void leftButtom() {
				dialogNaoCPF.dismiss();
			}

			@Override
			public void centerButtom() {
				dialogNaoCPF.dismiss();
				getActivity().finish();
			}

			@Override
			public void rightButtom() {
				TestVisao.toast("Aguardando a criação das próximas telas...");
				//PrincipalActivity_.intent(DetalhesComprasActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
				//Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, FormaPagamentoActivity.class);
				//startActivity(intent);
			}
		};
	}

	private OnDialog3ButtonsListener onDialogListenerIsCpf() {
		return new OnDialog3ButtonsListener() {
			@Override
			public void leftButtom() {
				dialogIsCPF.dismiss();
			}

			@Override
			public void centerButtom() {
				dialogIsCPF.dismiss();
				getActivity().finish();
			}

			@Override
			public void rightButtom() {
			}
		};
	}

}