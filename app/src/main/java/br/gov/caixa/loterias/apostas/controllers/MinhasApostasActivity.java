package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.BotoesFiltroApostas;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaFiltroApostasEnum;
import br.gov.caixa.loterias.apostas.model.model.ApostaConfirmadaModel;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.BoundariesInterface;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.NovaApiUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.ApostasConfirmadasFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnSomadorListener;

/**
 * Created by pmotta on 13/03/2018.
 * Class MinhasApostasActivity
 */

public class MinhasApostasActivity extends AppCompatActivity implements BoundariesInterface, OnSomadorListener {

	private static final String CONFIRMADAS_FRAGMENT = "ConfirmadasFragment";
	private static final String HISTORICO_FRAGMENT = "HistoricoFragment";
	public static final String CAIXA_ACTION = "com.loterias.caixa";
	private Dialog dialogFiltrar;
	private ExpandableListView listViewFiltro;
	private FiltroApostasAdapter filtroAdapter;
	private ApostasConfirmadasFragment fragmentApostas;
	private boolean apresentaHistorico;

	private Toolbar toolbar;
	private ImageButton btnDuvidas;
	private ImageButton btnFiltro;
	private TextView tvBadge;
	private RadioButton btnHistoricoApostas;
	private NestedScrollView scrollView;
	private ImageView imgTrevoServico;
	private ConfigConsultaDTO configConsulta;
	private BotoesFiltroApostas botoesFiltroApostas;

	private View somadorCarrinhoView;

	private SomadorCarrinhoFragment somadorCarrinhoFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apostas_confirmadas);
		init();
	}

	protected void init() {
		setViews();
		requestVerificaApresentaHistorico();
		buscaModalidades();
	}

	private void setViews() {
		this.somadorCarrinhoView = findViewById(R.id.id_fg_somador_carrinho);
		somadorCarrinhoView.setOnClickListener(view -> vaiProCarrinho());
		this.toolbar = findViewById(R.id.toolbar);
		this.btnDuvidas = findViewById(R.id.ib_duvidas);
		this.btnFiltro = findViewById(R.id.ib_filtrar);
		this.tvBadge = findViewById(R.id.tv_badge);
		//this.btnHistoricoApostas = findViewById(R.id.historicoApostasBtn); -> Funcionalidade desabilitada, apenas mantido para referência futura
		this.scrollView = findViewById(R.id.scrollView);
		this.imgTrevoServico = findViewById(R.id.img_trevo_servico);
		//View view_verResultadosBtn = findViewById(R.id.verResultadosBtn);

		//view_verResultadosBtn.setOnClickListener(view -> verResultadosBtn()); Funcionalidade desabilitada, apenas mantido para referência futura

		//btnHistoricoApostas.setOnClickListener(view -> historicoApostasBtn()); Funcionalidade desabilitada, apenas mantido para referência futura
	}

	private void fragmentSomadorCarrinho() {
		if(somadorCarrinhoFragment == null){
			somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment,R.id.id_fg_somador_carrinho,"TELA CONFIRMACAO INCLUSAO");
		}
	}

	protected void vaiProCarrinho() {
		if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
			startActivity(new Intent(MinhasApostasActivity.this, CarrinhoActivity.class));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupScrollView();
		fragmentSomadorCarrinho();
	}


	private void somadorCarrinhoVisibility() {
		if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.getQuantidadeApostas() > 0) {
			somadorCarrinhoView.setVisibility(View.VISIBLE);
		} else {
			somadorCarrinhoView.setVisibility(View.GONE);
		}
	}

	@Override
	public void outDialogEntendiListener() {
		fragmentSomadorCarrinho();
		somadorCarrinhoVisibility();
		somadorCarrinhoFragment.atualizaCarrinho();
	}

	@Override
	public void atualizaDados(CarrinhoDTO carrinho) {
		somadorCarrinhoVisibility();
	}

	public interface OnScrollReachBottomListener {
		void onScrollReachBottom();
	}

	private void setaConfigsIniciais() {
		setaViews();
		setaMetodos();
	}

	private void setaViews() {
		btnFiltro.setVisibility(View.VISIBLE);
		btnDuvidas.setVisibility(View.VISIBLE);

//		if (apresentaHistorico) {
//			btnHistoricoApostas.setVisibility(View.VISIBLE);
//		} else {
//			btnHistoricoApostas.setVisibility(View.GONE);
//		}

		setSupportActionBar(toolbar);
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
			supportActionBar.setElevation(0);
		}
		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getTitle().toString()));
	}

	private void setaMetodos() {
		btnFiltro.setOnClickListener(view -> {
			try {
				if(dialogFiltrar != null){
					dialogFiltrar.show();
				}
			} catch (Exception e){
				Log.d("","");
			}

		});
		btnDuvidas.setOnClickListener(view -> {
			abrirTermosUso();
		});
	}

	private void setaDialogFiltrar(List<ModalidadeDTO> modalidades) {
		dialogFiltrar = new Dialog(this);
		dialogFiltrar.setContentView(R.layout.dialog_filtro_compras);
		TextView titulo = dialogFiltrar.findViewById(R.id.dialog_titulo);
		titulo.setText(R.string.titulo_dialog_filtro_apostas);
		listViewFiltro = dialogFiltrar.findViewById(R.id.elv_filtros);
		Button      btnOk       = dialogFiltrar.findViewById(R.id.btn_ok);
		Button      btnCancelar = dialogFiltrar.findViewById(R.id.btn_cancelar);
		ImageButton btnLimpar   = dialogFiltrar.findViewById(R.id.ib_filtro_limpar);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(Objects.requireNonNull(dialogFiltrar.getWindow()).getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialogFiltrar.getWindow().setAttributes(lp);
		listViewFiltro.setGroupIndicator(null);

		// Modifica a altura do ExpandableListView para 450dp para incluir novos botões de filtro sem precisar rolar o conteúdo
		alterarAltura(listViewFiltro,450);

		if(botoesFiltroApostas == null){
			botoesFiltroApostas = new BotoesFiltroApostas(0,0,0);
		}

		filtroAdapter = new FiltroApostasAdapter(this,
				modalidades,
				MinhasApostasActivity.this,
				true,
				configConsulta,
				botoesFiltroApostas);

		View footerView = getLayoutInflater().inflate(R.layout.botoes_filtro_minhas_apostas, null);
		listViewFiltro.addFooterView(footerView);
		listViewFiltro.setAdapter(filtroAdapter);

		ToggleButton btnIsSurpresinha = listViewFiltro.findViewById(R.id.btn_surpresinha);
		ToggleButton btnIsTeimosinha = listViewFiltro.findViewById(R.id.btn_teimosinha);
		ToggleButton btnIsCombos = listViewFiltro.findViewById(R.id.btn_combos);

		centralizarConteudoBotao(btnIsSurpresinha);
		centralizarConteudoBotao(btnIsTeimosinha);
		centralizarConteudoBotao(btnIsCombos);

		configuraAcessibilidadeBotao(btnIsSurpresinha);
		configuraAcessibilidadeBotao(btnIsTeimosinha);
		configuraAcessibilidadeBotao(btnIsCombos);

		btnIsSurpresinha.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (filtroAdapter != null) {
				if(isChecked){
					botoesFiltroApostas.setSurpresinha(TipoConsultaFiltroApostasEnum.SELECIONADO.getValor());
				} else {
					botoesFiltroApostas.setSurpresinha(TipoConsultaFiltroApostasEnum.TODAS.getValor());
				}
				filtroAdapter.setBotoesFiltroApostas(botoesFiltroApostas);
			}
        });


		btnIsTeimosinha.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if(fragmentApostas != null){
				if(isChecked){
					botoesFiltroApostas.setTeimosinha(TipoConsultaFiltroApostasEnum.SELECIONADO.getValor());
				} else {
					botoesFiltroApostas.setTeimosinha(TipoConsultaFiltroApostasEnum.TODAS.getValor());
				}
				filtroAdapter.setBotoesFiltroApostas(botoesFiltroApostas);
			}
        });

		btnIsCombos.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if(filtroAdapter != null) {
				if(isChecked){
					botoesFiltroApostas.setCombo(TipoConsultaFiltroApostasEnum.SELECIONADO.getValor());
				} else {
					botoesFiltroApostas.setCombo(TipoConsultaFiltroApostasEnum.TODAS.getValor());
				}
				filtroAdapter.setBotoesFiltroApostas(botoesFiltroApostas);
			}
        });

		btnOk.setOnClickListener(v -> {
			if (fragmentApostas != null) {
				if (filtroAdapter.isAddBadge()) {
					tvBadge.setVisibility(View.VISIBLE);
				} else {
					tvBadge.setVisibility(View.GONE);
				}
				fragmentApostas.filtraDados();
				dialogFiltrar.dismiss();
			}
		});
		btnCancelar.setOnClickListener(v -> dialogFiltrar.dismiss());
		btnLimpar.setOnClickListener(v -> {
			btnIsCombos.setChecked(false);
			btnIsTeimosinha.setChecked(false);
			btnIsSurpresinha.setChecked(false);
			filtroAdapter.limpaFiltros();
			tvBadge.setText(String.valueOf(0));
			tvBadge.setVisibility(View.GONE);
			fragmentApostas.filtraDados();
			dialogFiltrar.dismiss();
		});

		//final List<String> expandableListTitle;
		//final HashMap<String, List<String>> expandableListDetail;
		//listViewFiltro.setGroupIndicator(null);
		//expandableListDetail = ExpandableListDataFiltro.getDataSemConcurso();
		//expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

//		LinkedHashMap<Integer,String> mapSituacoes;
//		if (SharedPreferencesUtils.getValorBoolean(SingletonEnum.IS_MICRO_SERVICO.get(), SingletonDefaultEnum.IS_MICRO_SERVICO.asBoolean())) {
//			mapSituacoes = SituacoesApostaFiltroEnum.getSituacoesMicroServico();
//		} else {
//			mapSituacoes = SituacoesApostaFiltroEnum.getSituacoes();
//		}
	}

	private void alterarAltura(View view, int alturaDp) {
		int alturaPx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				alturaDp,
				view.getResources().getDisplayMetrics()
		);
		ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
		params.height = alturaPx;
		view.setLayoutParams(params);
	}

	//Não há uma maneira direta de centralizar o conteúdo de um ToggleButton com texto e drawable à esquerda
	private void centralizarConteudoBotao(ToggleButton botao) {
		botao.post(() -> {
			int btnLargura = botao.getWidth();
			float textLargura = botao.getPaint().measureText(botao.getText().toString());
			Drawable[] drawables = botao.getCompoundDrawables();
			int figuraLargura = 0;
			if (drawables[0] != null) {// drawable[0] é o drawable à esquerda
				figuraLargura = drawables[0].getIntrinsicWidth();
			}
			int paddingEntreElementos = botao.getCompoundDrawablePadding();
			float conteudoLargura = textLargura + figuraLargura + paddingEntreElementos;
			int paddingStart = (int) ((btnLargura - conteudoLargura) / 2);
			botao.setPadding(paddingStart, botao.getPaddingTop(), 0, botao.getPaddingBottom());
		});
	}

	private void buscaConfigConsulta(List<ModalidadeDTO> listModalidade) {
		if (!AlertDialogUtils.isShow()) {
			AlertDialogUtils.show(MinhasApostasActivity.this);
		}

		new ApostaConfirmadaModel(this).requestConfigConsulta(new OnSilceListener<ConfigConsultaDTO>() {
			@Override
			public void success(ConfigConsultaDTO payload) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}
				configConsulta = payload;
				if (listModalidade != null && configConsulta != null) {
					setaDialogFiltrar(listModalidade);
					setaApostasFragment();
				}
			}

			@Override
			public void error(VolleyError error) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}
				RedirectNetwork.checkRedirect(error, MinhasApostasActivity.this);
			}
		});
	}

	private void buscaModalidades() {
		if (!AlertDialogUtils.isShow()) {
			AlertDialogUtils.show(MinhasApostasActivity.this);
		}

		new ModalidadeModel(this).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
			@Override
			public void success(List<ModalidadeDTO> payload) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}
				if (payload != null) {
					buscaConfigConsulta(payload);
					//setaDialogFiltrar(payload);
					//setaApostasFragment();
				}
			}

			@Override
			public void error(VolleyError error) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}
			}
		});
	}

	private void setupScrollView() {
		scrollView.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
			View view = v.getChildAt(v.getChildCount() - 1);
			int  diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
			// if diff is zero, then the bottom has been reached
			if (diff == 0) {
				Fragment confirmadasFragment = getSupportFragmentManager().findFragmentByTag(CONFIRMADAS_FRAGMENT);
				Fragment historicoFragment   = getSupportFragmentManager().findFragmentByTag(HISTORICO_FRAGMENT);
				if (confirmadasFragment != null && confirmadasFragment.isVisible()) {
					if (confirmadasFragment instanceof OnScrollReachBottomListener) {
						((OnScrollReachBottomListener) confirmadasFragment).onScrollReachBottom();
					}
				} else if (historicoFragment != null && historicoFragment.isVisible()) {
					if (historicoFragment instanceof OnScrollReachBottomListener) {
						((OnScrollReachBottomListener) historicoFragment).onScrollReachBottom();
					}
				}
			}
		});
	}

	private void setaApostasFragment() {

		if(isDestroyed() || isFinishing()){
			return;
		}

		try {
			btnFiltro.setVisibility(View.VISIBLE);
			Fragment frag = getSupportFragmentManager().findFragmentByTag(CONFIRMADAS_FRAGMENT);
			if (frag == null) {
				frag = FragmentUtils.getApostasConfirmadasFragment(false, configConsulta);
			}


			getSupportFragmentManager().beginTransaction()
									   .replace(R.id.container, frag, CONFIRMADAS_FRAGMENT)
									   .commitAllowingStateLoss();

			getSupportFragmentManager().executePendingTransactions();
			fragmentApostas = (ApostasConfirmadasFragment) frag;
			fragmentApostas.setFiltroAdapter(filtroAdapter);
		} catch (Exception e) {

			FirebaseCrashlytics crash = FirebaseCrashlytics.getInstance();
			crash.setCustomKey("Erro", "Erro ao abrir fragment de apostas confirmadas");
			crash.recordException(e);
			Log.e("Erro", "Erro! MinhasApostasActivity: Erro ao abrir fragment de apostas confirmadas: " + e);

			ViewUtils.alertTitleButton(MinhasApostasActivity.this,
					R.string.label_atencao,
					getString(R.string.nao_concluiu_operacao_nova),
					getString(R.string.title_entendi), dialogInterface -> {
						Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(MinhasApostasActivity.this, PrincipalActivity.class);
						startActivity(intent);
					}
			);
		}
	}

	private void requestVerificaApresentaHistorico() {
		if (!AlertDialogUtils.isShow()) {
			AlertDialogUtils.show(MinhasApostasActivity.this);
		}

		ApostaSilceBO.getInstance().getApresentaHistorico(new RequestListener<ParametrosConfiguraveisDTOResponse>() {
			@Override
			public void onResponse(ParametrosConfiguraveisDTOResponse result) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}

				handleApresentaHistorico(result);
				if (!(temUrlNovaApi(result) && finalCPFAcessaNovaApi())) {
					if (temMicroServico(result) && temUrl(result)){
						if (result.getPayload().getMapa().getUrlBase().endsWith("/v1")){
							String urlBase = result.getPayload().getMapa().getUrlBase();
							urlBase.replace("/v1", "/v2");
							result.getPayload().getMapa().setUrlBase(urlBase);
							setConfiguracoesMicroServico(result);
						}else if(result.getPayload().getMapa().getUrlBase().endsWith("/v2")) {
							setConfiguracoesMicroServico(result);
						}else {
							if(BuildConfig.DEBUG && !(BuildConfig.FLAVOR.equalsIgnoreCase("prd"))){
								setConfiguracoesMicroServico(result);
							}else {
								setConfiguracoesSilce();
							}
						}
					}else {
						setConfiguracoesSilce();
					}
				}

				if (NovaApiUtils.isPossoBuscarNovaAPI()) {
					imgTrevoServico.setImageDrawable(AppCompatResources.getDrawable(MinhasApostasActivity.this, R.drawable.trevo_nova_api));
				} else if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean())){
					imgTrevoServico.setImageDrawable(AppCompatResources.getDrawable(MinhasApostasActivity.this, R.drawable.trevo_micro_servico));
				}else {
					imgTrevoServico.setImageDrawable(AppCompatResources.getDrawable(MinhasApostasActivity.this, R.drawable.trevo_silce));
				}

				try {
					if (temDadosPayload(result) && result.getPayload().getMapa().getHabilitaMenuApostas() != null){
						SharedPreferencesUtils.setValor(ConfiguracoesEnum.MOSTRA_MENU_APOSTA.get(), result.getPayload().getMapa().getHabilitaMenuApostas());
					}
				}catch (Exception e){
					Log.d("ERRO", e.getLocalizedMessage());
				}

				setaConfigsIniciais();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				if (AlertDialogUtils.isShow()) {
					AlertDialogUtils.dismiss();
				}
				apresentaHistorico = false;
				setConfiguracoesSilce();
				setaConfigsIniciais();
			}
		});
	}

	private void setConfiguracoesMicroServico(ParametrosConfiguraveisDTOResponse result) {
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BUSCA_APOSTAS.get(),result.getPayload().getMapa().getUrlBase());
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_MICRO_SERVICO.get(),result.getPayload().getMapa().getMicroServico());
	}

	private boolean temDadosPayload(ParametrosConfiguraveisDTOResponse result){
		return result != null && result.getPayload() != null && result.getPayload().getMapa() != null;
	}

	private boolean temMicroServico(ParametrosConfiguraveisDTOResponse result) {
		return temDadosPayload(result) && result.getPayload().getMapa().getMicroServico() != null && result.getPayload().getMapa().getMicroServico();
	}
	private boolean temUrlNovaApi(ParametrosConfiguraveisDTOResponse result) {
		if(BuildConfig.FLAVOR.equalsIgnoreCase("prd")) {
			if (temDadosPayload(result) && result.getPayload().getMapa().getUrlBaseNovaApiPRD() != null
					&& result.getPayload().getMapa().getUrlBaseNovaApiPRD().length() > 1) {
				preencheDadosNovaAPI(result, result.getPayload().getMapa().getUrlBaseNovaApiPRD());
				return true;
			}
		} else {
			if (temDadosPayload(result) && result.getPayload().getMapa().getUrlBaseNovaApiPLT() != null
					&& result.getPayload().getMapa().getUrlBaseNovaApiPLT().length() > 1) {
				preencheDadosNovaAPI(result, result.getPayload().getMapa().getUrlBaseNovaApiPLT());
				return true;
			}
		}
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_NOVA_API.get(), false);
		return false;
	}

	private void preencheDadosNovaAPI(ParametrosConfiguraveisDTOResponse result, String url){
		if (url.endsWith("/")){
			url = url + "v1";
		} else {
			url = url + "/v1";
		}
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPF_NOVA_API.get(), result.getPayload().getMapa().getGrupoCpfNovaApi());
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BUSCA_APOSTAS.get(), url);
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_NOVA_API.get(), true);
	}

	private boolean finalCPFAcessaNovaApi() {
		int finalCpf = SharedPreferencesUtils.getValorInt(ConfiguracoesEnum.GRUPO_CPF_NOVA_API.get(), ConfiguracoesDefaultEnum.GRUPO_CPF_NOVA_API.asInt());

		if (finalCpf == 0) {
			return false;
		}

		if (finalCpf == 100) {
			return true;
		}

		String cpf = DadosUsuarioBO.obterCpf();
		if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {
			//Verifica se o final do CPF < finalCpfAcessamNuvem
			if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpf) {
				return true;
			}
		}

		return false;
	}

	private boolean temUrl(ParametrosConfiguraveisDTOResponse result) {
		return temDadosPayload(result) && result.getPayload().getMapa().getUrlBase() != null;
	}

	private void setConfiguracoesSilce() {
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BUSCA_APOSTAS.get(), BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE"));
		SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), false);
	}

	private void handleApresentaHistorico(ParametrosConfiguraveisDTOResponse result){
		if (result != null && result.getPayload() != null && result.getPayload().getMapa() != null && result.getPayload().getMapa().getMostrar6meses() != null) {
			//apresentaHistorico = result.getPayload().getMapa().getMostrar6meses();
			apresentaHistorico = false;
		} else {
			apresentaHistorico = false;
		}
	}

//	protected void verResultadosBtn() {
//		setaApostasFragment();
//	}

// ------ Funcionalidade desabilitada, apenas mantido para referência futura ---------
//	protected void historicoApostasBtn() {
//		btnFiltro.setVisibility(View.GONE);
//		Fragment frag = getSupportFragmentManager().findFragmentByTag(HISTORICO_FRAGMENT);
//		if (frag == null) {
//			frag = FragmentUtils.getApostasConfirmadasFragment(true, configConsulta);}
//		getSupportFragmentManager().beginTransaction()
//								   .replace(R.id.container, frag, HISTORICO_FRAGMENT)
//								   .commit();
//		getSupportFragmentManager().executePendingTransactions();
//		fragmentApostas = (ApostasConfirmadasFragment) frag;
//		fragmentApostas.setFiltroAdapter(filtroAdapter);
//	}

	protected void abrirTermosUso() {
		Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private void configuraAcessibilidadeBotao(ToggleButton btn){

		ViewCompat.setAccessibilityDelegate(btn, new AccessibilityDelegateCompat(){
			@Override
			public void onInitializeAccessibilityNodeInfo(@NotNull View host, @NonNull AccessibilityNodeInfoCompat info) {
				super.onInitializeAccessibilityNodeInfo(host, info);

				boolean isChecked = btn.isChecked();
				String labelNumero = btn.getText().toString();

				//Remove a verbalização do texto visível
				info.setText(null);

				info.setRoleDescription("Botão");
				info.setStateDescription(labelNumero + " " + (isChecked ? "Selecionado" : "Não selecionado"));

				AccessibilityNodeInfoCompat.AccessibilityActionCompat clickAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;
				String clickActionVerbalizacao = isChecked ?  "Desselecionar" : "Selecionar" ;

				AccessibilityNodeInfoCompat.AccessibilityActionCompat clickPadrao = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(clickAction.getId(), clickActionVerbalizacao);
				info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
				info.addAction(clickPadrao);
			}
		});
	}
}
