package br.gov.caixa.loterias.apostas.view.activity;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnSomadorListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.DetalhesBolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.FavoritarLotericaHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.SuperSeteRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.custom.BotaoFavoritar;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;


public class DetalhesBolaoActivity extends LoteriasAppMarketPlaceActivity implements OnCompraBolaoListener, OnSomadorListener {
	private CarrinhoDTO carrinhoRodape;
	public static final String ARG_CODIGO_BOLAO = "ARG_CODIGO_BOLAO";
	public static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	public static final String ARG_MODO_VISUALIZACAO = "ARG_MODO_VISUALIZACAO";
	public static final String ARG_NUMERO_COTA = "ARG_NUMERO_COTA";
	public static final String ARG_IS_LOTERICA_FAVORITA = "ARG_IS_LOTERICA_FAVORITA";
	private TextView tvValorPremio;
	private TextView tvValorPremioPorExtenso;
	private TextView tvNomeLoterica, tvValorCota, tvValorTarifa, tvValorTotal, tvNumeroCota, tvCodigoLoterica, tvCidadeUfLoterica, tvFavoritar;
	private TextView textQtdDezenas, textQtdCotas;
	private Button btnAdd, btnDiminui, btnAumenta;
	private TextView tvQtdCotas;
    private RecyclerView listaJogos;
	private JogosBolaoRecyclerView listaAdapter;
	private PartidasLotecaRecyclerViewAdapter partidasAdapter;
	private SuperSeteRecyclerViewAdapter superSeteAdpter;
	private ConstraintLayout layoutQtdCotas, layoutDetalhes;
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
	private BotaoFavoritar botaoFavoritar;
	private boolean isLotericaFavorita;

    private int controleChamadaServico = 1;
	private Boolean isMega30 = false;
	private Boolean isLotecaPais = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_bolao);

		launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(DetalhesBolaoActivity.this, onAtualizaLista());

		getExtras();
		//estilo = new EstiloModalidadeMKP(modalidade,isMega30);
		estilo = new EstiloModalidadeMKP(modalidade);

		setViews();
		aplicaEstilo();
		setMetodos();

		fragmentSomadorCarrinho();
		model = new DetalhesBolaoModel(this);
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
		isLotericaFavorita = getIntent().getBooleanExtra(ARG_IS_LOTERICA_FAVORITA, false);
	}

	private void desabilitaFavoritar(){
        boolean hasLotericaFavorita = getIntent().hasExtra(ARG_IS_LOTERICA_FAVORITA);
		if (!hasLotericaFavorita) {
			LinearLayout favoritar_detalhes = findViewById(R.id.layout_favoritar_detalhes);
			favoritar_detalhes.setVisibility(View.INVISIBLE);
		}
	}

	private void aplicaEstilo() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
		}

		if (EspecialUtils.isParametrosOutubroRosa() && modalidade == ModalidadeEnum.MEGA_SENA){
			tvValorPremio.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
			tvValorPremioPorExtenso.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
			} else {
			tvValorPremio.setTextColor(ContextCompat.getColor(this, estilo.getCorLetraLista()));
			tvValorPremioPorExtenso.setTextColor(ContextCompat.getColor(this, estilo.getCorLetraLista()));
		}

		textQtdDezenas.setTextColor(ContextCompat.getColor(this, R.color.cinza90));
		textQtdCotas.setTextColor(ContextCompat.getColor(this, R.color.cinza90));
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

		botaoFavoritar.setOnStateChangeListener(this::favoritar);
	}

	private void favoritar(boolean isFavoritado){
		if (bolao == null) return;

		Long lotericaId = bolao.getLoterica();
		boolean estadoAtual = isLotericaFavorita;

		FavoritarLotericaHelper.confirmar(
				this,
				lotericaId,
				bolao.getNomeFantasia(),
				estadoAtual,
				new FavoritarLotericaHelper.Acoes() {
					@Override
					public void incluir(Long id, OnSilceListener<RetornoPadraoResponse> listener) {
						model.incluirLotericaFavorita(id, listener);
					}
					@Override
					public void excluir(Long id, OnSilceListener<RetornoPadraoResponse> listener) {
						model.excluirLotericaFavorita(id, listener);
					}
				},
				novoEstado -> {
					isLotericaFavorita = novoEstado;
					botaoFavoritar.setState(novoEstado);
					atualizaLabelFavoritar();
					precisaAtualizarLista = true;
				}
		);
	}

	private void setViews() {
		layoutDetalhes = findViewById(R.id.layout_detalhes_bolao);
		tvValorPremio = findViewById(R.id.id_valor_premio);
		tvValorPremioPorExtenso = findViewById(R.id.id_valor_premio_por_extenso);
		tvNomeLoterica = findViewById(R.id.nome_loterica);
		tvCodigoLoterica = findViewById(R.id.codigo_loterica);
		tvCidadeUfLoterica = findViewById(R.id.cidade_uf_loterica);
		tvValorCota = findViewById(R.id.id_valor_cota);
		textQtdDezenas = findViewById(R.id.descricao_qtd_dezenas_dt);
		textQtdCotas = findViewById(R.id.descricao_qtd_cotas_disponiveis_dt);
		tvNumeroCota = findViewById(R.id.id_numero_qtd_cotas);
		tvValorTarifa = findViewById(R.id.id_valor_tarifa_servico);
		tvValorTotal = findViewById(R.id.id_valor_total);
		btnAdd = findViewById(R.id.id_btn_add);
		tvQtdCotas = findViewById(R.id.quantidade_cotas);
		btnDiminui = findViewById(R.id.diminui_cota);
		btnAumenta = findViewById(R.id.aumenta_cota);
		listaJogos = findViewById(R.id.id_lista_jogos);
		layoutQtdCotas = findViewById(R.id.id_layout_qtd_cotas);
		frameSomador = findViewById(R.id.id_fg_somador_carrinho);
		frameRodape = findViewById(R.id.id_fg_rodape);
		botaoFavoritar = findViewById(R.id.btn_favoritar_detalhes);
		tvFavoritar = findViewById(R.id.txt_favoritar_loterica);
		ViewCompat.setAccessibilityHeading(tvNomeLoterica,true);
	}

	public void atualizaQuantidadeTextView() {
		tvQtdCotas.setText(String.valueOf(bolao.getQtdCotaDesejada()));
		atualizaBotoesQtdCotas();
		atualizaValorAposta();
	}

	private void atualizaValorAposta() {
		BigDecimal qtdApostas = BigDecimal.valueOf(bolao.getQtdCotaDesejada() - 1);
		BigDecimal valor = bolao.getVrUltimaCotaComTarifa().add(bolao.getVrCotaComTarifa().multiply(qtdApostas));
		rodapeFragment.atualizaValorBolao(valor);
	}

	public void atualizaBotoesQtdCotas() {
        btnDiminui.setEnabled(bolao.getQtdCotaDesejada() > 1);
        btnAumenta.setEnabled(bolao.getQtdCotaDesejada() < bolao.getQtdCotaDisponivel());
        btnAdd.setEnabled(bolao.getQtdCotaDisponivel() > 0);
    }

	private OnSilceListener<DetalheBolaoDTO> onBuscaDetalheBolaoListener() {
		return new OnSilceListener<DetalheBolaoDTO>() {
			@Override
			public void success(DetalheBolaoDTO payload) {
				if (modalidade != null &&
						modalidade == ModalidadeEnum.MAIS_MILIONARIA &&
						payload.getApostas() != null && !payload.getApostas().isEmpty() &&
						payload.getApostas().get(0).getTrevos() == null &&
						controleChamadaServico <= 3) {
					controleChamadaServico++;
					model.buscaDetalhesBolao(codigoBolao, onBuscaDetalheBolaoListener());
				} else {
					bolao = payload;
					//TODO: MEGA 30 ANOS//
					isMega30 = EspecialUtils.isMega30(modalidade, bolao.getConcurso(), bolao.getTipoConcurso().equals(TipoConcursoEnum.ESPECIAL.getValor()));
					isLotecaPais = EspecialUtils.isLotecaPais(modalidade, bolao.getConcurso(), bolao.getTipoConcurso().equals(TipoConcursoEnum.ESPECIAL.getValor()));
					//estilo = new EstiloModalidadeMKP(modalidade,isMega30);
					estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(modalidade, bolao.getConcurso(), bolao.getTipoConcurso().equals(TipoConcursoEnum.ESPECIAL.getValor()));
					aplicaEstilo();

					fragmentSubBarraModalidade();
					fragmentRodape();
					preencheDados();
					AlertDialogUtils.dismiss();
					layoutDetalhes.setVisibility(View.VISIBLE);
					if (bolao.getQtdCotaDisponivel() == 0 && modoVisualizacao != null &&
							modoVisualizacao == ModoVisualizacaoBolaoEnum.SIMULACAO) {
						DialogUtils.dialogEntendiListener(DetalhesBolaoActivity.this,
								getString(R.string.cota_zerou),

								new OnDialogBotaoListener() {
									@Override
									public void onButtonClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										setResult(RESULT_OK);
										finish();
									}
								}
						);
					}
				}
			}

			@Override
			public void error (VolleyError error){
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void preencheDados() {
		if (bolao == null) return;
		String codigoLoterica = getString(R.string.codigo_loteria) +" "+ bolao.getLotericaFormatada().getNumeroFormatado();
		String cidadeUfLoterica = StringUtils.capitalizerNovo(bolao.getMunicipio().getNome()) + " - " + bolao.getMunicipio().getUf().getSigla();
		String valorCota =  "Valor da Cota: " + ViewUtils.getMoedaFormat(bolao.getVrUltimaCotaSemTarifa());
		String valorTarifa = "Tarifa de Serviço: " + ViewUtils.getMoedaFormat(bolao.getVrTarifaServicoUltimaCota());
		String valorTotal = "Total: " + ViewUtils.getMoedaFormat(bolao.getVrUltimaCotaComTarifa());

		botaoFavoritar.setState(isLotericaFavorita);
		tvValorPremio.setText(ViewUtils.getMoedaFormatComCentavos(bolao.getVrPremioEstimado(), 2));
		String valorEstimadoPorExtenso = ViewUtils.getMoedaFormatPorExtenso(bolao.getVrPremioEstimado());
		if (!valorEstimadoPorExtenso.isEmpty()){
			tvValorPremioPorExtenso.setText(String.format("(%s)",valorEstimadoPorExtenso));
			tvValorPremioPorExtenso.setVisibility(View.VISIBLE);
		} else {
			tvValorPremioPorExtenso.setVisibility(View.GONE);
		}
		tvNomeLoterica.setText(StringUtils.capitalizerNovo(bolao.getNomeFantasia()));
		tvCodigoLoterica.setText(codigoLoterica);
		tvCidadeUfLoterica.setText(cidadeUfLoterica);
		tvValorCota.setText(valorCota);
		tvValorTarifa.setText(valorTarifa);
		tvValorTotal.setText(valorTotal);
		btnAdd.setText(R.string.mkp_adicionar_carrinho);
		atualizaLabelFavoritar();

		if (bolao.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && bolao.getApostas() != null
				&& !bolao.getApostas().isEmpty() && bolao.getApostas().get(0).getDezenas() != null &&
				!bolao.getApostas().get(0).getDezenas().isEmpty()){
			textQtdDezenas.setText(ViewUtils.infoPrognosticosMkp(this,
							getApostasDeDezenas(bolao.getQtdApostas(), bolao.getApostas().get(0).getDezenas().size()),
					20,18));
		} else {
			textQtdDezenas.setText(ViewUtils.infoPrognosticosMkp(this,
					bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
							getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
							getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros()), 20,18));
		}

		textQtdCotas.setText(ViewUtils.infoPrognosticosMkp(this,
				getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal()), 20,18));


		atualizaQuantidadeTextView();
		preencheJogos();
		desabilitaFavoritar();
	}

	private void preencheJogos() {
		if (bolao.getModalidade() == ModalidadeEnum.LOTECA && !bolao.getApostas().isEmpty()){
			partidasAdapter = new PartidasLotecaRecyclerViewAdapter(bolao.getApostas().get(0).getPartidasLoteca(), DetalhesBolaoActivity.this, new ConfiguracaoLoteca(R.color.black), isEspecial());
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

	private void atualizaLabelFavoritar(){
		if(isLotericaFavorita){
			tvFavoritar.setText(R.string.loterica_favorita);
		} else {
			tvFavoritar.setText(R.string.favoritar_loterica);
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
			rodapeFragment = RodapeSimulacaoApostaFragment.newInstanceDetalhesBolao(getValorApostaBolao(), valorTotal);
			FragmentUtils.startFragmentAllowingStateLoss(getSupportFragmentManager(), R.id.id_fg_rodape, rodapeFragment);
			atualizaDados(carrinhoRodape != null ? carrinhoRodape : CarrinhoSingleton.getInstance().getCarrinho());
		} catch (Exception e){
			Log.d("", e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage());
		}
	}

    @Override
    public void atualizaDados(CarrinhoDTO carrinho) {
        carrinhoRodape = carrinho;
        if (rodapeFragment == null) return;
        BigDecimal total = carrinho != null && carrinho.getValorTotal() != null
                ? carrinho.getValorTotal() : BigDecimal.ZERO;
        int quantidade = carrinho != null && carrinho.getApostas() != null
                ? carrinho.getApostas().size() : 0;
        rodapeFragment.atualizaValorCarrinho(total);
        rodapeFragment.apresentaQtdApostas(true, quantidade > 999
                ? getString(R.string.mais_999) : String.valueOf(quantidade));
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
			if (result.getResultCode() == RESULT_OK || result.getResultCode() == ResultadoNavegacaoAposta.RESULT_RESET) {
				precisaAtualizarLista = true;

				setResult(RESULT_OK);
				finish();
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
        super.onBackPressed();
        if (precisaAtualizarLista){
			setResult(RESULT_OK);
		}
		finish();
	}
}