package br.gov.caixa.loterias.apostas.controllers;

import static br.gov.caixa.loterias.apostas.controllers.DetalhesCarrinhoFavoritoActivity.ID_CARRINHO;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.model.CarrinhosFavoritosModel;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhosFavoritosAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCarrinhosFavoritosClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;

public class CarrinhosFavoritosActivity extends LoteriasBaseAppActivity {
	private final static int AMBAS = 0;

	//region Constants
	public final static int RESPOSTAS_APOSTAS_CODIGO = 1;
	public final static int RESPOSTA_EXCLUIU_CARRINHO = 600;
	public final static int RESPOSTA_EXCLUIU_APOSTAS = 300;
	public final static String RESPOSTA_APOSTAS = "RESPOSTA_APOSTAS";
	//endregion

	//region Layout Variables
	private RecyclerView rvCarrinhosFavoritos;
	private TextView tvSemCarrinhos;
	//endregion

	//region Variables
	private CarrinhosFavoritosAdapter adapter;
	private CarrinhosFavoritosModel model;
	private ActivityResultLauncher<Intent> launcher;
	private Dialog dialogSegmentedControl;
	//endregion

	//region Life Cicle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carrinhos_favoritos);
		launcher = IntentUtil.registerLauncherActivityForResult(this, onActivityForResult());
		model = new CarrinhosFavoritosModel(this);
		setaViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		AlertDialogUtils.show(CarrinhosFavoritosActivity.this);
		model.buscaCarrinhosFavoritos(onBuscaCarrinhoListener());
	}

	private OnSilceListener<List<CarrinhoFavoritoDTO>> onBuscaCarrinhoListener() {
		return new OnSilceListener<List<CarrinhoFavoritoDTO>>() {
			@Override
			public void success(List<CarrinhoFavoritoDTO> payload) {
				AlertDialogUtils.dismiss();
				configuraRecyclerView(payload);
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}
	//endregion

	//region Initialization Methods
	private void setaViews() {
		tvSemCarrinhos = findViewById(R.id.tv_sem_carrinhos);
		rvCarrinhosFavoritos = findViewById(R.id.rv_carrinhos_favoritos);

		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textCaixaSTDBoldTitle(CarrinhosFavoritosActivity.this, getString(R.string.carrinho_fav_titulo)));
	}
	//endregion

	//region Network Methods
	//endregion

	//region Layout Configuration Methods
	public void configuraRecyclerView(List<CarrinhoFavoritoDTO> result) {
		if (result.size() > 0) {
			rvCarrinhosFavoritos.setVisibility(View.VISIBLE);
			tvSemCarrinhos.setVisibility(View.GONE);
			model.setListCarrinhosFavoritos(result);
			adapter = new CarrinhosFavoritosAdapter(result, CarrinhosFavoritosActivity.this, onRecyclerViewListener());
			rvCarrinhosFavoritos.setAdapter(adapter);
			RecyclerView.LayoutManager layout = new LinearLayoutManager(CarrinhosFavoritosActivity.this, RecyclerView.VERTICAL, false);
			rvCarrinhosFavoritos.setLayoutManager(layout);
		} else {
			rvCarrinhosFavoritos.setVisibility(View.GONE);
			tvSemCarrinhos.setVisibility(View.VISIBLE);
		}
	}

	private OnCarrinhosFavoritosClickListener onRecyclerViewListener() {
		return new OnCarrinhosFavoritosClickListener() {
			@Override
			public void onExcluirCarrinho(int position) {
				excluirCarrinho(position);
			}

			@Override
			public void onDetalhesCarrinho(LinearLayout layoutDetalhes, int position) {
				detalhesCarrinho(layoutDetalhes, model.getIdCarrinhoByPosition(position));
			}

			@Override
			public void onTransformaCarrinho(int position) {
				if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(CarrinhosFavoritosActivity.this)) {
					transformaCarrinho(position);
				}
			}

			@Override
			public void onBuscaApostas(LinearLayout layoutDetalhes, ConstraintLayout clCard, boolean configLayout, int position) {
				buscaApostas(layoutDetalhes, clCard, configLayout, model.getIdCarrinhoByPosition(position));
			}
		};
	}

	private void buscaApostas(LinearLayout layout, ConstraintLayout card, boolean configLayout, Long idCarrinho) {
		AlertDialogUtils.show(CarrinhosFavoritosActivity.this);
		model.buscaApostas(idCarrinho, new OnSilceListener<List<ApostaCarrinhoFavoritoDTO>>() {
			@Override
			public void success(List<ApostaCarrinhoFavoritoDTO> result) {
				AlertDialogUtils.dismiss();
				if (configLayout) {
					adapter.configViewsAposta(result, layout, card);
				} else {
					adapter.setListaApostasCarrinho(result);
					if (adapter.temApostaEspecial() >= 2) {

						String descricao = getString(R.string.carrinho_fav_segmented_desc);
						descricao = descricao.replace("{modalidade}", adapter.getModadlidadeNormal());

						List<String> list = new ArrayList();
						list.add(0, "Ambas");
						list.add(1, adapter.getModadlidadeNormal());
						list.add(2, adapter.getModalidadeEspecial());

						// Armazena a opção selecionada pelo usuário (-1 = nenhuma seleção)
						final int[] opcaoSelecionada = {-1};

						DialogUtils.showDialogListItensNovo(
								CarrinhosFavoritosActivity.this,
								"Atenção",
								descricao,
								"",
								list,
								"Confirmar",
								"Cancelar",
								new OnDialogListener() {
									@Override
									public void itemSelecionado(int position) {
										opcaoSelecionada[0] = position;
									}

									@Override
									public void ok(int position) {
										if (opcaoSelecionada[0] == -1) {
											// Nenhum item foi selecionado, alerta o usuário
											DialogUtils.dialogEntendi(
													CarrinhosFavoritosActivity.this,
													"Nenhuma opção selecionada"
											);
											return;
										}
										chamaTransformarCarrinho(idCarrinho, opcaoSelecionada[0]);
									}

									@Override
									public void cancelar() {
										//Sem ação, apenas fechar o dialog
									}
								}

						);
					} else {
						chamaTransformarCarrinho(idCarrinho, AMBAS);
					}
				}
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		});
	}

	private void chamaTransformarCarrinho(Long idCarrinho, int opcaoSelecionada) {
		AlertDialogUtils.show(this);
		model.transformaCarrinho(idCarrinho, opcaoSelecionada, new OnSilceListener<CarrinhoDTO>() {
			@Override
			public void success(CarrinhoDTO payload) {
				model.salvarCarrinhoFavoritoLocal(idCarrinho);
				if (dialogSegmentedControl != null && dialogSegmentedControl.isShowing()) {
					dialogSegmentedControl.dismiss();
				}

				startActivity(new Intent(CarrinhosFavoritosActivity.this, CarrinhoActivity.class));
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		});
	}

	private void detalhesCarrinho(LinearLayout layoutDetalhes, Long idCarrinhoAtual) {
		adapter.setLayoutAtual(layoutDetalhes);
		Bundle args = new Bundle();
		args.putLong(ID_CARRINHO, idCarrinhoAtual);
		Intent intent = IntentUtil.getIntentOrigemDestino(CarrinhosFavoritosActivity.this, DetalhesCarrinhoFavoritoActivity.class);
		intent.putExtras(args);
		IntentUtil.startActivityForResult(launcher, intent);
	}

	private void excluirCarrinho(int position) {
		DialogUtils.dialogSim(CarrinhosFavoritosActivity.this,
				getResources().getString(R.string.carrinho_fav_deseja_excluir),

				new OnDialogBotaoListener() {
					@Override
					public void onButtonClick(DialogInterface dialog, int which) {
						AlertDialogUtils.show(CarrinhosFavoritosActivity.this);
						model.excluirCarrinho(model.getIdCarrinhoByPosition(position), onExcluirCarrinhoListener());
					}
				}
		);
	}

	private OnSilceListener<List<CarrinhoFavoritoDTO>> onExcluirCarrinhoListener() {
		return new OnSilceListener<List<CarrinhoFavoritoDTO>>() {
			@Override
			public void success(List<CarrinhoFavoritoDTO> payload) {
				AlertDialogUtils.dismiss();
				configuraRecyclerView(payload);
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void transformaCarrinho(int position) {
		if(!model.existeCarrinho(model.getIdCarrinhoByPosition(position))){
			buscaApostas(null, null, false, model.getIdCarrinhoByPosition(position));
		}else {
			DialogUtils.dialogSim(CarrinhosFavoritosActivity.this,
					getString(R.string.carrinho_favorito_ja_adicionado),

					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							buscaApostas(null, null, false, model.getIdCarrinhoByPosition(position));
						}
					}
			);
		}
	}
	//endregion

	//region Support Methods
	private OnActivityForResult onActivityForResult() {
		return result -> {
			if (result.getResultCode() == RESPOSTAS_APOSTAS_CODIGO){
				switch (result.getResultCode()) {
					case RESPOSTA_EXCLUIU_CARRINHO:
						model.buscaCarrinhosFavoritos(onBuscaCarrinhoListener());
						break;
					case RESPOSTA_EXCLUIU_APOSTAS:
						Bundle bundle = result.getData().getExtras();
						if (bundle != null) {
							List<ApostaCarrinhoFavoritoDTO> resposta = (List<ApostaCarrinhoFavoritoDTO>) bundle.getSerializable(RESPOSTA_APOSTAS);
							adapter.recarregaCelula(resposta, adapter.getLayoutAtual());
						}
						break;
				}
			}
		};
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	//endregion
}
