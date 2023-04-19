package br.gov.caixa.loterias.apostas.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.activity.ConfirmaDadosPixActivity;
import br.gov.caixa.loterias.apostas.view.activity.DigitaChaveActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;

public class ChavePixFragment extends Fragment {
	public static final String ARG_TROCAR_RESGATE = "ARG_TROCAR_RESGATE";
	private static final String ARG_TELA= "ARG_TELA";
	private static final int RESGATE_PIX = 123;

	private View view;

	private ChavePixEnum chave;

	public ChavePixFragment() {}

	private Dialog dialogNaoCPF;
	private Dialog dialogIsCPF;

	public static ChavePixFragment newInstance(ChavePixEnum chave) {
		ChavePixFragment fragment = new ChavePixFragment();
		Bundle          args     = new Bundle();

		args.putSerializable(ARG_TELA, chave);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			chave = (ChavePixEnum) getArguments().getSerializable(ARG_TELA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chave_pix, container, false);

		TextView tvTexto = view.findViewById(R.id.chavePix);
		tvTexto.setText(chave.getTexto());

		configClick();


		return view;
	}

	@SuppressLint("ResourceAsColor")
	private void configClick() {
		view.setOnClickListener(view -> {

			if (chave.getTexto().equals(ChavePixEnum.CPF.getTexto())) {
//				dialogIsCPF = DialogUtils.showDialogCustom(getContext(),
//						getString(R.string.pix_chave_invalida),
//						getString(R.string.popup_pix_cpf_nao_vinculado),
//						getString(R.string.trocar_tipo_chave),
//						getString(R.string.trocar_forma_resgate),
//						null,
//						onDialogListenerIsCpf());
				starConfirmaCPFActivity();
			} else {
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

	private void starConfirmaCPFActivity() {
		Intent intent = new Intent(getActivity(), ConfirmaDadosPixActivity.class);
		intent.putExtra(ConfirmaDadosPixActivity.NOME_ARG, DadosUsuarioBO.obterNome());
		intent.putExtra(ConfirmaDadosPixActivity.CPF_ARG, "***.345.678-***");
		intent.putExtra(ConfirmaDadosPixActivity.INSTITUICAO_ARG, "Caixa Econômica Federal");
		startActivityForResult(intent, RESGATE_PIX);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESGATE_PIX) {
			if (resultCode == Activity.RESULT_CANCELED ) {
				if (data != null){
					getActivity().finish();
				}
			}
		}
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
				dialogNaoCPF.dismiss();
				startNextActivity();
			}
		};
	}

	private void startNextActivity() {
		Intent intent = new Intent(getActivity(), DigitaChaveActivity.class);
		intent.putExtra(DigitaChaveActivity.VALOR_ARG,"36");
		intent.putExtra(DigitaChaveActivity.CHAVE_ARG, chave);
		startActivityForResult(intent, RESGATE_PIX);
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