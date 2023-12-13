package br.gov.caixa.loterias.apostas.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.DetalhesBolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class DetalhesBolaoActivity extends LoteriasAppMarketPlaceActivity {
	public static final String ARG_CODIGO_BOLAO = "ARG_CODIGO_BOLAO";
	public static final String ARG_MODALIDADE = "ARG_MODALIDADE";

	private TextView tvValorPremio, tvDescricaoPremioConcurso;
	private TextView tvMunicipio, tvValorCota, tvValorTarifa, tvValorTotal;

	private String codigoBolao;
	private ModalidadeEnum modalidade;
	private DetalhesBolaoModel model;
	private DetalheBolaoDTO bolao;

	private SomadorCarrinhoFragment somadorCarrinhoFragment;
	private RodapeSimulacaoApostaFragment rodapeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_bolao);
		configToolbar(R.id.toolbar);
		configBackground(R.color.mega_verde_escuro_mkp);

		codigoBolao = getIntent().getStringExtra(ARG_CODIGO_BOLAO);
		modalidade = new Gson().fromJson(getIntent().getStringExtra(ARG_MODALIDADE), ModalidadeEnum.class);

		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_apostas bolão_"));

		setViews();

		fragmentSomadorCarrinho();
		model = new DetalhesBolaoModel(this);
		AlertDialogUtils.show(this);
		model.buscaDetalhesBolao(codigoBolao, onBuscaDetalheBolaoListener());
	}

	private void setViews() {
		tvValorPremio = findViewById(R.id.id_valor_premio);
		tvDescricaoPremioConcurso = findViewById(R.id.id_descricao_valor_premio);
		tvMunicipio = findViewById(R.id.id_municipio);
		tvValorCota = findViewById(R.id.id_valor_cota);
		tvValorTarifa = findViewById(R.id.id_valor_tarifa_servico);
		tvValorTotal = findViewById(R.id.id_valor_total);
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
		tvDescricaoPremioConcurso.setText(ViewUtils.textColor(this, getString(R.string.label_premio_estimado_concurso_numero_bold, bolao.getConcurso()), R.color.mega_verde_claro_mkp, false));
		tvMunicipio.setText(bolao.getMunicipio().getNome() + "\n" + bolao.getMunicipio().getUf().getNome().trim() + "-" + bolao.getMunicipio().getUf().getSigla() );
		tvValorCota.setText("Valor da Cota: " + ViewUtils.getMoedaFormat(bolao.getVrCotaSemTarifa()));
		tvValorTarifa.setText("Tarifa de Serviço: " + ViewUtils.getMoedaFormat(bolao.getValorTarifaServico()));
		tvValorTotal.setText("Total: " + ViewUtils.getMoedaFormat(bolao.getVrCotaComTarifa()));
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
				bolao.getDataSorteio().substring(0,5));
	}

	private void fragmentSomadorCarrinho() {
		if(somadorCarrinhoFragment == null){
			somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
		}else {
			somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
		}
	}

	private void adicionarNoCarrinho(CotasBolaoDTO bolao) {
	}

}