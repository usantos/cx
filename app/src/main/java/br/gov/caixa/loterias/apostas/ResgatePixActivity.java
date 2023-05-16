package br.gov.caixa.loterias.apostas;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.mock.ChaveFavoritaMockSingleton;
import br.gov.caixa.loterias.apostas.model.bean.ChavePix;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.model.ResgatePixModel;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.ConfirmaDadosPixActivity;
import br.gov.caixa.loterias.apostas.view.activity.DigitaChaveActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaChavePixFavoritaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaChavePixRecyclerView;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;
import br.gov.caixa.loterias.apostas.view.listener.OnChaveFavoritaClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ResgatePixActivity extends LoteriasAppActivity {

	private static final int RESGATE_PIX = 123;

	private RecyclerView listaChaves, listaChavesFavoritas;
	private ConstraintLayout containerFav;
	private Dialog dialogNaoCPF;

	private ResgatePixModel model;
	private ListaChavePixFavoritaRecyclerView chavesFavAdapter;
	private ArrayList<ChavePix> listaChaveFavorita;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		model = new ResgatePixModel(this);

		listaChaves = findViewById(R.id.listaChaves);
		listaChavesFavoritas = findViewById(R.id.listaChavesFavoritas);
		containerFav = findViewById(R.id.containerFavoritas);

		configListaChave();
	}

	@Override
	protected void onResume() {
		super.onResume();
		configListaChaveFavoritas();
	}

	private void configListaChaveFavoritas() {
		if (temChaveFavoristas()){
			containerFav.setVisibility(View.VISIBLE);
			listaChaveFavorita = model.getListaChaveFavorita();
			chavesFavAdapter = new ListaChavePixFavoritaRecyclerView(listaChaveFavorita, onListaChaveFavoritaListener());
			listaChavesFavoritas.setAdapter(chavesFavAdapter);
			listaChavesFavoritas.setLayoutManager(new LinearLayoutManager(this));
		} else {
			containerFav.setVisibility(View.GONE);
		}
	}

	private boolean temChaveFavoristas() {
		return model.temChaveFavorita();
	}

	private OnChaveFavoritaClickListener onListaChaveFavoritaListener() {
		return new OnChaveFavoritaClickListener() {
			@Override
			public void itemClick(int position) {
				starConfirmaCPFActivity(false);
			}

			@Override
			public void excluir(int position) {
				ChavePix chavePix = listaChaveFavorita.get(position);
				DialogUtils.showDialogLeftRight(ResgatePixActivity.this, getString(R.string.label_atencao),
												getString(R.string.tem_certeza_excluir_chave_favorita_xxxx).replace("xxxx", chavePix.getValue()),
												getString(R.string.label_sim),
												getString(R.string.desistir),
												onExcluirFavoritaPopupListener(chavePix));
			}
		};
	}

	private OnDialogListenerLeftRight onExcluirFavoritaPopupListener(ChavePix chavePix) {
		return new OnDialogListenerLeftRight() {
			@Override
			public void left() {
				model.excluirChaveFavorita(chavePix);
				chavesFavAdapter.atualizaLista(ChaveFavoritaMockSingleton.getInstance().getChaves());
				if(ChaveFavoritaMockSingleton.getInstance().getChaves().isEmpty()){
					containerFav.setVisibility(View.GONE);
				}
			}

			@Override
			public void right() {

			}
		};
	}

	private void configListaChave() {
		listaChaves.setAdapter(new ListaChavePixRecyclerView(model.getListaChave(), onListaChaveListener()));
		listaChaves.setLayoutManager(new LinearLayoutManager(this));
	}

	private OnItemClickListener onListaChaveListener() {
		return (holder, position) -> {
			if (isCPF(position)){
				starConfirmaCPFActivity(true);
			} else {
				dialogNaoCPF = DialogUtils.showDialogCustom(ResgatePixActivity.this,
															getString(R.string.label_atencao),
															getString(R.string.popup_pix_cpf_vinculado),
															getString(R.string.trocar_tipo_chave),
															getString(R.string.trocar_forma_resgate),
															getString(R.string.pix_continuar),
															onDialogListenerNaoCpf(position));
			}
		};
	}

	private boolean isCPF(int position) {
		return model.getListaChave().get(position).equalsIgnoreCase(ChavePixEnum.CPF.getTexto()) ;
	}

	private void starConfirmaCPFActivity(Boolean showSalvar) {
		Intent intent = new Intent(this, ConfirmaDadosPixActivity.class);
		intent.putExtra(ConfirmaDadosPixActivity.NOME_ARG, DadosUsuarioBO.obterNome());
		intent.putExtra(ConfirmaDadosPixActivity.CPF_ARG, "***.345.678-***");
		intent.putExtra(ConfirmaDadosPixActivity.INSTITUICAO_ARG, "Caixa Econômica Federal");
		intent.putExtra(ConfirmaDadosPixActivity.TIPO_CHAVE_ARG, ChavePixEnum.CPF);
		intent.putExtra(ConfirmaDadosPixActivity.VALOR_CHAVE_ARG, DadosUsuarioBO.obterCpf());
		if (showSalvar){
			intent.putExtra(ConfirmaDadosPixActivity.SALVAR_CHAVE_ARG, true);
		}
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
				if(position == 1){
					return ChavePixEnum.EMAIL;
				} else if (position == 2){
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