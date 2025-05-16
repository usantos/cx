package br.gov.caixa.loterias.apostas.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.DetalhesBolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.SuperSeteRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class DetalhesBolaoActivity extends LoteriasAppMarketPlaceActivity implements OnCompraBolaoListener {
	public static final String ARG_CODIGO_BOLAO = "ARG_CODIGO_BOLAO";
	public static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	public static final String ARG_MODO_VISUALIZACAO = "ARG_MODO_VISUALIZACAO";
	public static final String ARG_NUMERO_COTA = "ARG_NUMERO_COTA";

	private TextView tvValorPremio, tvDescricaoPremioConcurso;
	private TextView tvMunicipio, tvValorCota, tvValorTarifa, tvValorTotal, tvNumeroCota, tvDescLoterica;
	private TextView textQtdDezenas, textQtdCotas;
	private Button btnAdd, btnDiminui, btnAumenta;
	private TextView tvValorCotaBtn, tvQtdCotas;
	private TextView tvLabelQtdCotas;
	private RecyclerView listaJogos;
	private JogosBolaoRecyclerView listaAdapter;
	private PartidasLotecaRecyclerViewAdapter partidasAdapter;
	private SuperSeteRecyclerViewAdapter superSeteAdpter;
	private ConstraintLayout layoutQtdCotas;
	private FrameLayout frameSomador, frameRodape;

	private String codigoBolao;
	private ModalidadeEnum modalidade;
	private ModoVisualizacaoBolaoEnum modoVisualizacao;
	private DetalhesBolaoModel model;
	private DetalheBolaoDTO bolao;
	private EstiloModalidadeMKP estilo;

	private SomadorCarrinhoFragment somadorCarrinhoFragment;
	private RodapeSimulacaoApostaFragment rodapeFragment;
	private String numeroCota;
	private boolean precisaAtualizarLista = false;
	private ActivityResultLauncher<Intent> launcherAddCarrinho;

	private int controleChamadaServico = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_bolao);
		configToolbar(R.id.toolbar);

		launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(DetalhesBolaoActivity.this, onAtualizaLista());

		getExtras();
		estilo = new EstiloModalidadeMKP(modalidade);

		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, "_bolões_"));

		setViews();
		setMetodos();

		fragmentSomadorCarrinho();
		model = new DetalhesBolaoModel(this);
		aplicaEstilo();
		aplicaModoVisualizacao();
	}

	private void aplicaModoVisualizacao() {
		if (modoVisualizacao != null && modoVisualizacao == ModoVisualizacaoBolaoEnum.LEITURA){
			btnAdd.setVisibility(View.GONE);
			layoutQtdCotas.setVisibility(View.GONE);
			frameSomador.setVisibility(View.VISIBLE);
			frameRodape.setVisibility(View.GONE);
			tvNumeroCota.setVisibility(View.VISIBLE);
			tvNumeroCota.setText("Cota: " + numeroCota);
		}
	}

	private void getExtras() {
		codigoBolao = getIntent().getStringExtra(ARG_CODIGO_BOLAO);
		modalidade = new Gson().fromJson(getIntent().getStringExtra(ARG_MODALIDADE), ModalidadeEnum.class);
		modoVisualizacao = new Gson().fromJson(getIntent().getStringExtra(ARG_MODO_VISUALIZACAO), ModoVisualizacaoBolaoEnum.class);
		numeroCota = getIntent().getStringExtra(ARG_NUMERO_COTA);
	}

	private void aplicaEstilo() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
		}
		configBackgroundColorMenu(estilo.getCorEscura());
		configColorTextMenu(estilo.getCorFonteFundoEscuro());

		tvValorPremio.setTextColor(ContextCompat.getColor(this, estilo.getCorLetraLista()));
		tvMunicipio.setTextColor(ContextCompat.getColor(this, estilo.getCorLetraLista()));
		layoutQtdCotas.setBackground(VectorUtils.getShape(R.drawable.background_border_verde, estilo.getCorClara()));
		tvValorCotaBtn.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
		tvLabelQtdCotas.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
		tvQtdCotas.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
		btnAdd.setBackground(VectorUtils.getShape(R.drawable.background_border_verde, estilo.getCorClara()));
		btnAdd.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		AlertDialogUtils.show(this);
		model.buscaDetalhesBolao(codigoBolao, onBuscaDetalheBolaoListener());
	}

	private void setMetodos() {
		btnAdd.setOnClickListener(v -> {
			model.addBolaoCarrinho(codigoBolao, bolao);
		});
		btnDiminui.setOnClickListener(v -> {
			bolao.diminuiQuantidade();
			atualizaQuantidadeTextView();
		});
		btnAumenta.setOnClickListener(v -> {
			bolao.aumentaQuantidade();
			atualizaQuantidadeTextView();
		});
	}

	private void setViews() {
		tvValorPremio = findViewById(R.id.id_valor_premio);
		tvDescricaoPremioConcurso = findViewById(R.id.id_descricao_valor_premio);
		tvMunicipio = findViewById(R.id.id_municipio);
		tvDescLoterica = findViewById(R.id.descricao_loterica);
		tvValorCota = findViewById(R.id.id_valor_cota);
		textQtdDezenas = findViewById(R.id.descricao_qtd_dezenas_dt);
		textQtdCotas = findViewById(R.id.descricao_qtd_cotas_disponiveis_dt);
		tvNumeroCota = findViewById(R.id.id_numero_qtd_cotas);
		tvValorTarifa = findViewById(R.id.id_valor_tarifa_servico);
		tvValorTotal = findViewById(R.id.id_valor_total);
		btnAdd = findViewById(R.id.id_btn_add);
		tvValorCotaBtn = findViewById(R.id.valor_cota);
		tvLabelQtdCotas = findViewById(R.id.label_quantidade_cotas);
		tvQtdCotas = findViewById(R.id.quantidade_cotas);
		btnDiminui = findViewById(R.id.diminui_cota);
		btnAumenta = findViewById(R.id.aumenta_cota);
		listaJogos = findViewById(R.id.id_lista_jogos);
		layoutQtdCotas = findViewById(R.id.id_layout_qtd_cotas);
		frameSomador = findViewById(R.id.id_fg_somador_carrinho);
		frameRodape = findViewById(R.id.id_fg_rodape);
	}

	public void atualizaQuantidadeTextView() {
		tvQtdCotas.setText(String.valueOf(bolao.getQtdCotaDesejada()));
		atualizaBotoesQtdCotas();
		atualizaValorAposta();
	}

	private void atualizaValorAposta() {
		Double qtdApostas = Double.valueOf(bolao.getQtdCotaDesejada() - 1);
		BigDecimal valor = bolao.getVrUltimaCotaComTarifa().add(BigDecimal.valueOf(bolao.getVrCotaComTarifa().doubleValue() * qtdApostas));
		rodapeFragment.atualizaValorBolao(valor);
	}

	public void atualizaBotoesQtdCotas() {
		if (bolao.getQtdCotaDesejada() == 1){
			btnDiminui.setBackgroundTintList(getCor(R.color.cinza_desativado_mkp));
			btnDiminui.setEnabled(false);
			btnAumenta.setBackgroundTintList(getCor(estilo.getCorFonteFundoClaro()));
			btnAumenta.setEnabled(true);
		} else if (bolao.getQtdCotaDesejada() == bolao.getQtdCotaDisponivel()){
			btnAumenta.setBackgroundTintList(getCor(R.color.cinza_desativado_mkp));
			btnAumenta.setEnabled(false);
			btnDiminui.setBackgroundTintList(getCor(estilo.getCorFonteFundoClaro()));
			btnDiminui.setEnabled(true);
		} else {
			btnDiminui.setBackgroundTintList(getCor(estilo.getCorFonteFundoClaro()));
			btnDiminui.setEnabled(true);
			btnAumenta.setBackgroundTintList(getCor(estilo.getCorFonteFundoClaro()));
			btnAumenta.setEnabled(true);
		}
		btnDiminui.setTextColor(ContextCompat.getColor(this, estilo.getCorClara()));
		btnAumenta.setTextColor(ContextCompat.getColor(this, estilo.getCorClara()));
	}
	private ColorStateList getCor(int cor) {
		return ContextCompat.getColorStateList(this, cor);
	}

	private OnSilceListener<DetalheBolaoDTO> onBuscaDetalheBolaoListener() {
		return new OnSilceListener<DetalheBolaoDTO>() {
			@Override
			public void success(DetalheBolaoDTO payload) {
				if (modalidade != null && modalidade == ModalidadeEnum.MAIS_MILIONARIA && payload.getApostas() != null && !payload.getApostas().isEmpty() && payload.getApostas().get(0).getTrevos() == null && controleChamadaServico <= 3){
					controleChamadaServico++;
					model.buscaDetalhesBolao(codigoBolao, onBuscaDetalheBolaoListener());
				} else {
					bolao = payload;
					fragmentSubBarraModalidade();
					fragmentRodape();
					preencheDados();
					AlertDialogUtils.dismiss();
					if (bolao.getQtdCotaDisponivel() == 0 && modoVisualizacao != null && modoVisualizacao == ModoVisualizacaoBolaoEnum.SIMULACAO) {
						ViewUtils.alertTitleButton(DetalhesBolaoActivity.this, R.string.label_atencao, getString(R.string.cota_zerou),
								"Ok", dialogInterface -> {
									dialogInterface.dismiss();
									setResult(Activity.RESULT_OK);
									finish();
								}
						);
					}
				}
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void preencheDados() {
		ViewUtils.setMoedaFormatHtml(bolao.getVrPremioEstimado(), tvValorPremio);
		tvDescricaoPremioConcurso.setText(ViewUtils.textColor(this, getString(R.string.label_premio_estimado_concurso_numero_bold, bolao.getConcurso()), estilo.getCorLetraLista(), false));
		tvMunicipio.setText(getString(R.string.nome_loteria) +" "+ bolao.getNomeFantasia() + "\n" + bolao.getMunicipio().getNome() + " - " + bolao.getMunicipio().getUf().getSigla() );
		//tvDescLoterica.setText(getString(R.string.nome_loteria) +" "+ bolao.getNomeFantasia());
		tvValorCota.setText("Valor da Cota: " + ViewUtils.getMoedaFormat(bolao.getVrCotaSemTarifa()));
		tvValorTarifa.setText("Tarifa de Serviço: " + ViewUtils.getMoedaFormat(bolao.getVrTarifaServicoUltimaCota()));
		tvValorTotal.setText("Total: " + ViewUtils.getMoedaFormat(bolao.getVrUltimaCotaComTarifa()));
		btnAdd.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.adicionarAoCarrinho)));
		ViewUtils.setMoedaFormatHtml(getValorApostaBolao(), tvValorCotaBtn);

		if (bolao.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && bolao.getApostas() != null
				&& !bolao.getApostas().isEmpty() && bolao.getApostas().get(0).getDezenas() != null &&
				!bolao.getApostas().get(0).getDezenas().isEmpty()){
			textQtdDezenas.setText(ViewUtils.textCaixaSTDBoldTitle(this,
							getApostasDeDezenas(bolao.getQtdApostas(), bolao.getApostas().get(0).getDezenas().size())));
		} else {
			textQtdDezenas.setText(ViewUtils.textCaixaSTDBoldTitle(this,
					bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
							getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
							getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros())));
		}

		textQtdCotas.setText(ViewUtils.textCaixaSTDBoldTitle(this,
				getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal())));

		atualizaQuantidadeTextView();
		preencheJogos();
	}

	private void preencheJogos() {
		if (bolao.getModalidade() == ModalidadeEnum.LOTECA && !bolao.getApostas().isEmpty()){
			partidasAdapter = new PartidasLotecaRecyclerViewAdapter(bolao.getApostas().get(0).getPartidasLoteca(), DetalhesBolaoActivity.this, new ConfiguracaoLoteca(R.color.black));
			listaJogos.setAdapter(partidasAdapter);
			listaJogos.setLayoutManager(new LinearLayoutManager(DetalhesBolaoActivity.this));
		} else if (bolao.getModalidade() == ModalidadeEnum.SUPER_7) {
			superSeteAdpter = new SuperSeteRecyclerViewAdapter(estilo.getCorEscura(), bolao.getApostas(),
					DetalhesBolaoActivity.this, getColunaConfig(), getDezenaConfig(), null);
			listaJogos.setAdapter(superSeteAdpter);
			listaJogos.setLayoutManager(new LinearLayoutManager(DetalhesBolaoActivity.this));
		} else if (bolao.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
			if (bolao.getApostas() != null && !bolao.getApostas().isEmpty()){
				listaAdapter = new JogosBolaoRecyclerView(bolao.getModalidade(), estilo.getCorLetraLista(), bolao.getApostas(), new ArrayList<>(), getDezenaConfig(), getTrevoConfig());
				listaJogos.setAdapter(listaAdapter);
				listaJogos.setLayoutManager(new LinearLayoutManager(DetalhesBolaoActivity.this));
			}
		} else {
			if (bolao.getApostas() != null && !bolao.getApostas().isEmpty()){
				listaAdapter = new JogosBolaoRecyclerView(bolao.getModalidade(), estilo.getCorLetraLista(), bolao.getApostas(), new ArrayList<>(), getDezenaConfig());
				listaJogos.setAdapter(listaAdapter);
				listaJogos.setLayoutManager(new LinearLayoutManager(DetalhesBolaoActivity.this));
			}
		}
	}

	private DezenaConfig getTrevoConfig() {
		ShapeConfig shapeConfig = new ShapeConfig(R.drawable.ic_item_trevo_branco,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.milionaria_escuro_mkp);

		return new DezenaConfig(false, R.color.milionaria_escuro_mkp,
				R.layout.item_dezena_detalhe,
				shapeConfig, false);
	}

	private ColunaConfig getColunaConfig() {
		return new ColunaConfig(estilo.getCorFonteFundoEscuro(), estilo.getCorEscura());
	}

	private DezenaConfig getDezenaConfig() {
		ShapeConfig shapeConfig = new ShapeConfig(estilo.getCorLetraLista(), R.color.branco);

		return new DezenaConfig(false, estilo.getCorLetraLista(),
				R.layout.item_dezena_detalhe,
				shapeConfig,false);
	}

	private void fragmentRodape() {
		try {
			BigDecimal valorTotal = CarrinhoSingleton.getInstance().getCarrinho() != null ?
									CarrinhoSingleton.getInstance().getCarrinho().getValorTotal()
									: BigDecimal.ZERO;
			rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(),
					R.id.id_fg_rodape, getValorApostaBolao(), valorTotal);
		} catch (Exception e){
			Log.d("", e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage());
		}
	}

	private BigDecimal getValorApostaBolao() {
		return bolao.getVrUltimaCotaComTarifa();
	}

	private void fragmentSubBarraModalidade() {
		FragmentUtils.startFragmentSubBarraModalidade(getSupportFragmentManager(),R.id.id_fg_sub_barra,
				bolao.getModalidade(), bolao.getConcurso().toString(),
				bolao.getDataSorteio().substring(0,5), isEspecial());
	}

	private boolean isEspecial() {
		return bolao.getTipoConcurso().intValue() == 2;
	}

	private void fragmentSomadorCarrinho() {
		if(somadorCarrinhoFragment == null){
			somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, false);
		}else {
			somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
		}
	}

	@Override
	public void compraSucesso(Intent intent) {
		IntentUtil.startActivityForResult(launcherAddCarrinho, intent);
	}

	private OnActivityForResult onAtualizaLista() {
		return result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				precisaAtualizarLista = true;
			}
		};
	}

	@Override
	public boolean onSupportNavigateUp() {
		if (precisaAtualizarLista){
			setResult(RESULT_OK);
		}
		finish();
		return true;
	}

	@Override
	public void onBackPressed() {
		if (precisaAtualizarLista){
			setResult(RESULT_OK);
		}
		finish();
	}
}