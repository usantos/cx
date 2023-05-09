package br.gov.caixa.loterias.apostas;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.ConfirmaDadosPixActivity;
import br.gov.caixa.loterias.apostas.view.activity.DigitaChaveActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaChavePixRecyclerView;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ResgatePixActivity extends LoteriasAppActivity {

	private static final int RESGATE_PIX = 123;

	private RecyclerView listaChaves, listaChavesFavoritas;
	private Dialog dialogNaoCPF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		listaChaves = findViewById(R.id.listaChaves);
		listaChavesFavoritas = findViewById(R.id.listaChavesFavoritas);

		configListaChave();
	}

	private void configListaChave() {
		listaChaves.setAdapter(new ListaChavePixRecyclerView(getListaChave(), onListaChaveListener()));
		listaChaves.setLayoutManager(new LinearLayoutManager(this));
	}

	private OnItemClickListener onListaChaveListener() {
		return (holder, position) -> {
			if (position != 0){
				if (isCPF(position)){
					starConfirmaCPFActivity();
				} else {

					dialogNaoCPF = DialogUtils.showDialogCustom(ResgatePixActivity.this,
																getString(R.string.label_atencao),
																getString(R.string.popup_pix_cpf_vinculado),
																getString(R.string.trocar_tipo_chave),
																getString(R.string.trocar_forma_resgate),
																getString(R.string.pix_continuar),
																onDialogListenerNaoCpf(position));
				}
			}
		};
	}

	private boolean isCPF(int position) {
		return getListaChave().get(position).equalsIgnoreCase(ChavePixEnum.CPF.getTexto()) ;
	}

	private List<String> getListaChave() {
		List<String> chaves = new ArrayList<>();
		chaves.add("Escolha o tipo de chave");
		chaves.add(ChavePixEnum.CPF.getTexto());
		chaves.add(ChavePixEnum.EMAIL.getTexto());
		chaves.add(ChavePixEnum.CELULAR.getTexto());
		chaves.add(ChavePixEnum.CHAVE_ALEATORIA.getTexto());
		return chaves;
	}

	private void starConfirmaCPFActivity() {
		Intent intent = new Intent(this, ConfirmaDadosPixActivity.class);
		intent.putExtra(ConfirmaDadosPixActivity.NOME_ARG, DadosUsuarioBO.obterNome());
		intent.putExtra(ConfirmaDadosPixActivity.CPF_ARG, "***.345.678-***");
		intent.putExtra(ConfirmaDadosPixActivity.INSTITUICAO_ARG, "Caixa Econômica Federal");
		intent.putExtra(ConfirmaDadosPixActivity.TIPO_CHAVE_ARG, ChavePixEnum.CPF);
		intent.putExtra(ConfirmaDadosPixActivity.VALOR_CHAVE_ARG, DadosUsuarioBO.obterCpf());
		startActivityForResult(intent, RESGATE_PIX);
	}

	private void startNextActivity(ChavePixEnum chave) {
		Intent intent = new Intent(this, DigitaChaveActivity.class);
		intent.putExtra(DigitaChaveActivity.VALOR_ARG,"36");
		intent.putExtra(DigitaChaveActivity.CHAVE_ARG, chave);
		startActivityForResult(intent, RESGATE_PIX);
	}

	private OnDialog3ButtonsListener onDialogListenerNaoCpf(int position) {
		return new OnDialog3ButtonsListener() {
			@Override
			public void leftButtom() {
				dialogNaoCPF.dismiss();
			}

			@Override
			public void centerButtom() {
				dialogNaoCPF.dismiss();
				finish();
			}

			@Override
			public void rightButtom() {
				dialogNaoCPF.dismiss();
				startNextActivity(getChaveEnumByPosition(position));
			}

			private ChavePixEnum getChaveEnumByPosition(int position) {
				if(position == 2){
					return ChavePixEnum.EMAIL;
				} else if (position == 3){
					return ChavePixEnum.CELULAR;
				} else {
					return ChavePixEnum.CHAVE_ALEATORIA;
				}
			}
		};
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESGATE_PIX) {
			if (resultCode == Activity.RESULT_CANCELED ) {
				if (data != null){
					finish();
				}
			}
		}
	}
}