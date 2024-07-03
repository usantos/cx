package br.gov.caixa.loterias.apostas.view.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

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

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class DetalhesBolaoActivity extends LoteriasAppMarketPlaceActivity {
	public static final String ARG_CODIGO_BOLAO = "ARG_CODIGO_BOLAO";
	public static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	public static final String ARG_MODO_VISUALIZACAO = "ARG_MODO_VISUALIZACAO";
	public static final String ARG_NUMERO_COTA = "ARG_NUMERO_COTA";

	private TextView tvValorPremio, tvDescricaoPremioConcurso;
	private TextView tvMunicipio, tvValorCota, tvValorTarifa, tvValorTotal, tvNumeroCota;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_bolao);
		configToolbar(R.id.toolbar);

		getExtras();
		estilo = new EstiloModalidadeMKP(modalidade);

		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_bolões_"));

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
				bolao = payload;
				fragmentSubBarraModalidade();
				fragmentRodape();
				preencheDados();
				AlertDialogUtils.dismiss();
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
		tvMunicipio.setText(bolao.getMunicipio().getNome() + "\n" + bolao.getMunicipio().getUf().getNome().trim() + "-" + bolao.getMunicipio().getUf().getSigla() );
		tvValorCota.setText("Valor da Cota: " + ViewUtils.getMoedaFormat(bolao.getVrCotaSemTarifa()));
		tvValorTarifa.setText("Tarifa de Serviço: " + ViewUtils.getMoedaFormat(bolao.getValorTarifaServico()));
		tvValorTotal.setText("Total: " + ViewUtils.getMoedaFormat(bolao.getVrCotaComTarifa()));
		btnAdd.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarAoCarrinho)));
		ViewUtils.setMoedaFormatHtml(getValorApostaBolao(), tvValorCotaBtn);

		textQtdDezenas.setText(ViewUtils.textCaixaSTDBold(this,
				getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros())));

		textQtdCotas.setText(ViewUtils.textCaixaSTDBold(this,
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
		} else {
			if (bolao.getApostas() != null && !bolao.getApostas().isEmpty()){
				listaAdapter = new JogosBolaoRecyclerView(bolao.getModalidade(), estilo.getCorLetraLista(), bolao.getApostas(), new ArrayList<>(), getDezenaConfig());
				listaJogos.setAdapter(listaAdapter);
				listaJogos.setLayoutManager(new LinearLayoutManager(DetalhesBolaoActivity.this));
			}
		}
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
		rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(),
				R.id.id_fg_rodape, getValorApostaBolao(), CarrinhoSingleton.getInstance().getCarrinho().getValorTotal());
	}

	private BigDecimal getValorApostaBolao() {
		return bolao.getVrCotaComTarifa();
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

}