package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.SuperSeteRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.SuperSeteSorteadosAdapter;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;


public class ApostaConfirmadaBolaoDetalhesFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";

    private ConstraintLayout cl_premio_pago, clResgatePremio, clCorpoAposta, clCorpoApostaCont;

	private IdentificaoDeUmaApostaDas8Modalidades aposta;

	private View view, linhaEsqueda, linhaDireita, linhaCentral, divTopNumerosSorteados, divBotNumerosSorteados;
    private TextView tvPremioPago, concursoLabel, concursoTxt, cotaLabel, cotaTxt, dataApostaLabel, dataApostaTxt, statusApostaTxt, tv_val_premio, tv_val_premio_tit;
	private TextView tvNumerosSorteados, tvNumerosSorteados2, labelMesTime, txtMesTime;
	private TextView tvDuplaSena1, tvDuplaSena2;
	private RecyclerView listViewSorteados, listViewSorteados2, listViewSorteadosS7;
	private View rl_container_result_1, rl_container_result_2;
	private RecyclerView listViewJogos, listViewJogos2, listTrevos, listViewJogosS7;
	//private ImageView imgTriangulo;
	private TipoApostaLinhaView tipoApostaLinhaView;

	private EstiloModalidadeMKP estiloMKP;
	private final int QUANTIDADE_COLUNA_LISTA = 6;

	public ApostaConfirmadaBolaoDetalhesFragment() {}

	public static ApostaConfirmadaBolaoDetalhesFragment newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                                                    ResultadoConcursoDTO resultado,
                                                                    ComprovanteApostaDTO comprovante,
                                                                    BigDecimal valorPremio) {
		ApostaConfirmadaBolaoDetalhesFragment fragment = new ApostaConfirmadaBolaoDetalhesFragment();
		Bundle               args     = fragment.getBundle(resultado, comprovante, valorPremio);
		args.putSerializable(ARG_APOSTA, new Gson().toJson(aposta));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String apostaJson = getArguments().getString(ARG_APOSTA);
			if (apostaJson != null && !apostaJson.isEmpty()){
				aposta = new Gson().fromJson(apostaJson, IdentificaoDeUmaApostaDas8Modalidades.class);
				estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_aposta_confirmada_bolao_detalhes, container, false);
		setaViews();

		clResgatePremio.setOnClickListener(onResgateClickListenr());

		preencheDadosAposta();
		return view;
	}

	private void aplicaCorFontes() {
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			concursoLabel.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			concursoTxt.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			cotaLabel.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			cotaTxt.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			dataApostaTxt.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			dataApostaLabel.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			statusApostaTxt.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tv_val_premio.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tv_val_premio_tit.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			divTopNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tvNumerosSorteados.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tvNumerosSorteados2.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			divBotNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			labelMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			txtMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tvDuplaSena1.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tvDuplaSena2.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			tvPremioPago.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			linhaEsqueda.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			linhaDireita.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
			linhaCentral.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
		}
	}

	private void preencheDadosAposta() {
		EstiloModalidadeMKP estiloModalidadeMKP = new EstiloModalidadeMKP(aposta.getModalidade());
		//imgTriangulo.setImageResource(estiloModalidadeMKP.getImagemTrianguloOutline());

		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		cotaTxt.setText(aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" + aposta.getReservaCotaBolao().getQtdCotaTotalBolao());
		dataApostaTxt.setText(getDataBolao(aposta));

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			if (getValorDoPremio() != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
			} else {
				buscaDetalhePremio(aposta.getId(), onBuscaDetalhePremioListener());
			}
			if (isApostaPremiada(aposta)){
				statusApostaTxt.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
				clResgatePremio.setVisibility(View.VISIBLE);
				cl_premio_pago.setVisibility(View.GONE);
				statusApostaTxt.setVisibility(View.VISIBLE);
			} else {
				cl_premio_pago.setVisibility(View.VISIBLE);
				clResgatePremio.setVisibility(View.GONE);
				statusApostaTxt.setVisibility(View.GONE);
			}
		} else {
			statusApostaTxt.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
			clResgatePremio.setVisibility(View.GONE);
		}

		initList();
		apresentaEspecialidadeModalidades();
	}

	private void apresentaEspecialidadeModalidades() {
		aplicaCorFontes();
		switch (aposta.getModalidade()){
			case DIA_DE_SORTE:
				if (getResultadoConcurso() != null && getResultadoConcurso().getPremiacaoMesDeSorte() != null &&
						getResultadoConcurso().getPremiacaoMesDeSorte().getMesDeSorte() != null){
					labelMesTime.setVisibility(View.VISIBLE);
					txtMesTime.setVisibility(View.VISIBLE);
					labelMesTime.setText(R.string.label_mes_sorte_dois_pontos);
					txtMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorEscura()));
					labelMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoEscuro()));
					String mesSorte = getResultadoConcurso().getPremiacaoMesDeSorte().getMesDeSorte().getNome();
					txtMesTime.setText(mesSorte);
					labelMesTime.setContentDescription(R.string.label_mes_sorte_dois_pontos + mesSorte);
				}
				break;
			case TIMEMANIA:
				if (getResultadoConcurso() != null && getResultadoConcurso().getPremiacaoTimeDoCoracao() != null &&
						getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe() != null){
					labelMesTime.setVisibility(View.VISIBLE);
					txtMesTime.setVisibility(View.VISIBLE);
					labelMesTime.setText(R.string.label_time_coracao_dois_pontos);
					labelMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
					String mesTime = getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getNome() + "-" + getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getUf();
					txtMesTime.setText(mesTime);
					labelMesTime.setContentDescription(R.string.label_time_coracao_dois_pontos + mesTime);
				}
				break;
			case MAIS_MILIONARIA:
				if (getResultadoConcurso() != null && getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio() != null &&
				    !getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio().isEmpty()){
					labelMesTime.setVisibility(View.VISIBLE);
					labelMesTime.setText(R.string.trevosTitulo);
					labelMesTime.setTextColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoEscuro()));
					listTrevos.setVisibility(View.VISIBLE);

					ListaDezenaRecyclerView trevosAdapter = new ListaDezenaRecyclerView(getDezenas(getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio()), getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio(),
							getDezenaTrevoConfig(getTrevoShape()), null);
					listTrevos.setAdapter(trevosAdapter);
					listTrevos.setLayoutManager(new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA));
					listTrevos.setNestedScrollingEnabled(false);
				}
				break;
		}
	}

	private ShapeConfig getTrevoShape() {
		return new ShapeConfig(R.drawable.ic_item_trevo,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.milionaria_escuro_mkp);
	}

	private String getDataBolao(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
		String dataString = aposta.getReservaCotaBolao().getDataRegistroBolao();
		String horaString = aposta.getReservaCotaBolao().getHoraRegistroBolao();

        return dataString + "\n" + horaString;
    }

	private OnSilceListener<DetalhesPremioDTO> onBuscaDetalhePremioListener() {
		return new OnSilceListener<DetalhesPremioDTO>() {
			@Override
			public void success(DetalhesPremioDTO payload) {
				atualizaValorPremio(payload.getPremio().getValorLiquido());
			}

			@Override
			public void error(VolleyError error) {}
		};
	}

	private void initList() {
		boolean temResultadoSegundoSorteio = temResultadoSegundoSorteio();
		List<Integer> listSorteados2 = new ArrayList<>();
		List<Integer> listSorteados = new ArrayList<>();

		FragmentManager fm = getActivity().getSupportFragmentManager();
		Fragment fragResultado, fragResultado2;

		if (temResultadoSorteio()) {
			if (temResultadoSegundoSorteio) {
				tvNumerosSorteados.setText(getString(R.string.numeros_sorteados) + "\n\n" +
										   getString(R.string.primeiro_sorteiro));
				tvDuplaSena1.setVisibility(View.VISIBLE);
				tvDuplaSena1.setText(getString(R.string.seus_jogos) + "\n\n" +
						getString(R.string.primeiro_sorteiro));
			}

			if (aposta.getModalidade() == ModalidadeEnum.SUPER_7){
//				ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) clCorpoAposta.getLayoutParams();
//				layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
//				layoutParams.rightMargin = 150;
//				layoutParams.leftMargin = 0;
//				clCorpoAposta.setLayoutParams(layoutParams);
//
//				ConstraintSet constraintSet = new ConstraintSet();
//				constraintSet.clone(clCorpoAposta);
//				constraintSet.clear(R.id.id_corpo_aposta, ConstraintSet.START);
//				//constraintSet.clear(R.id.id_lista_jogos, ConstraintSet.START);
//				constraintSet.applyTo(clCorpoAposta);

				ArrayList<List<List<Integer>>> matrizList = new ArrayList<>();
				matrizList.add(getResultadoConcurso().getMatrizNumerosSorteadosPrimeiroSorteio());
				SuperSeteSorteadosAdapter adapter = new SuperSeteSorteadosAdapter(matrizList, getContext(), getColunaConfig(), getDezenaConfig());
				//listViewSorteados.setAdapter(adapter);
				//listViewSorteados.setLayoutManager(new LinearLayoutManager(getContext()));
				listViewSorteadosS7.setAdapter(adapter);
				listViewSorteadosS7.setVisibility(View.VISIBLE);
				listViewSorteadosS7.setLayoutManager(new LinearLayoutManager(getContext()));
			} else {
				listSorteados = ListaUtils.orderAscDezenas(getResultadoConcurso().getListaSorteadosPrimeiroSorteio());
//				List dezenas = getDezenas(listSorteados);
//				ListaDezenaRecyclerView numerosAdapter = new ListaDezenaRecyclerView(dezenas, listSorteados
//						, getDezenaConfig(), null);
//				listViewSorteados.setAdapter(numerosAdapter);
//				listViewSorteados.setLayoutManager(new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA));

				listViewSorteados.setVisibility(View.GONE);
				divTopNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
				divBotNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estiloMKP.getCorFonteFundoClaro()));
				divTopNumerosSorteados.setVisibility(View.VISIBLE);
				divBotNumerosSorteados.setVisibility(View.VISIBLE);
				rl_container_result_1.setVisibility(View.VISIBLE);
				fragResultado = NumerosHifenFragment.newInstance("",
						listSorteados, new ConfiguracaoNumeros(estiloMKP.getCorEscura(), estiloMKP.getCorFonteFundoEscuro()));
				fm.beginTransaction().replace(R.id.rl_container_result_1, fragResultado).commitAllowingStateLoss();
				rl_container_result_1.setVisibility(View.VISIBLE);
			}


			if (temResultadoSegundoSorteio) {
				tvDuplaSena2.setVisibility(View.VISIBLE);
				tvDuplaSena2.setText(getString(R.string.segundo_sorteiro));

				tvNumerosSorteados2.setVisibility(View.VISIBLE);
//				listViewSorteados2.setVisibility(View.VISIBLE);

				listSorteados2 = ListaUtils.orderAscDezenas(getResultadoConcurso().getListaSorteadosSegundoSorteio());
//				List dezenas2 = getDezenas(listSorteados2);
//				ListaDezenaRecyclerView numerosAdapter2 = new ListaDezenaRecyclerView(dezenas2, listSorteados2
//						, getDezenaConfig(), null);
//				listViewSorteados2.setAdapter(numerosAdapter2);
//				listViewSorteados2.setLayoutManager(new GridLayoutManager(getContext(), 6));
//				listViewSorteados.setVisibility(View.GONE);

				listViewSorteados2.setVisibility(View.GONE);
				fragResultado2 = NumerosHifenFragment.newInstance("",
						listSorteados2, new ConfiguracaoNumeros(estiloMKP.getCorEscura(), estiloMKP.getCorFonteFundoEscuro()));
				fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado2).commitAllowingStateLoss();
				rl_container_result_2.setVisibility(View.VISIBLE);
			}
		} else {
			((TextView)view.findViewById(R.id.id_seus_numeros)).setText("");
		}

		preencheTipoApostaLinha();

		switch (aposta.getModalidade()){
			case SUPER_7:
//				ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) clCorpoApostaCont.getLayoutParams();
//				layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
//				layoutParams.rightMargin = 150;
//				layoutParams.leftMargin = 0;
//				clCorpoApostaCont.setLayoutParams(layoutParams);
//
//				ConstraintSet constraintSet = new ConstraintSet();
//				constraintSet.clone(clCorpoApostaCont);
//				//constraintSet.clear(R.id.id_corpo_aposta, ConstraintSet.START);
//				constraintSet.clear(R.id.id_lista_jogos, ConstraintSet.START);
//				constraintSet.applyTo(clCorpoApostaCont);

				SuperSeteRecyclerViewAdapter adapter = new SuperSeteRecyclerViewAdapter(R.color.branco, aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
						getContext(), getColunaConfig(), getDezenaConfig(), getMatrizResultado());
				//listViewJogos.setAdapter(adapter);
				listViewJogosS7.setAdapter(adapter);
				listViewJogosS7.setVisibility(View.VISIBLE);
				listViewJogosS7.setLayoutManager(new LinearLayoutManager(getContext()));
				break;
			default:
				JogosBolaoRecyclerView adpterJogos = new JogosBolaoRecyclerView(aposta.getModalidade(), R.color.branco,
						aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
						listSorteados, getDezenaConfig(), getResultadoConcurso());
				listViewJogos.setAdapter(adpterJogos);
				listViewJogos.setLayoutManager(new LinearLayoutManager(getContext()));
		}

		if (temResultadoSegundoSorteio) {
			listViewJogos2.setVisibility(View.VISIBLE);
			JogosBolaoRecyclerView adpterJogos2 = new JogosBolaoRecyclerView(aposta.getModalidade(), R.color.branco,
					aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
					listSorteados2, getDezenaConfig());
			listViewJogos2.setAdapter(adpterJogos2);
			listViewJogos2.setLayoutManager(new LinearLayoutManager(getContext()));
		}

		if (isSituacaoConcursoNaoApurado() || isApostaPequena()){
			view.findViewById(R.id.id_view_base).setVisibility(View.VISIBLE);
		}
	}

	private void preencheTipoApostaLinha() {
		List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

		if (aposta.getTroca()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
		}
		if (aposta.getIndicadorCotaBolao()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
		}
		if (aposta.getCombo()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
		}
		if (aposta.getEspelho()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
		}
		if (aposta.getSurpresinha()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
		}
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
		}

		if (listTipoApostaLinha.size() > 0) {
			tipoApostaLinhaView.setVisibility(View.VISIBLE);
			tipoApostaLinhaView.setupView(listTipoApostaLinha, estiloMKP.getCorClara(), estiloMKP.getCorFonteFundoClaro(), estiloMKP.getCorFonteFundoClaro(), TipoApostaLinhaView.TriangleDirection.DOWN);
		}
	}

	private ArrayList<ArrayList<Integer>> getMatrizResultado() {
		if (getResultadoConcurso() != null){
			return getResultadoConcurso().getMatrizNumerosSorteadosPrimeiroSorteio();
		} else {
			return null;
		}
	}

	private ColunaConfig getColunaConfig() {
		return new ColunaConfig(estiloMKP.getCorEscura(), estiloMKP.getCorFonteFundoEscuro());
	}

	private boolean isApostaPequena() {
		List<ApostaBolaoDTO> list = aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao();
		return list.size() == 1 || (list.size() < 4 && list.get(0).getDezenas().size() <= 6);
	}

	private boolean isSituacaoConcursoNaoApurado() {
		return aposta.getSituacao().getValor() == 101;
	}

	private boolean temResultadoSorteio() {
		return getResultadoConcurso() != null && getResultadoConcurso().getListaSorteadosPrimeiroSorteio() != null
				&& !getResultadoConcurso().getListaSorteadosPrimeiroSorteio().isEmpty();
	}

	private boolean temResultadoSegundoSorteio() {
		return getResultadoConcurso() != null && getResultadoConcurso().getListaSorteadosSegundoSorteio() != null
				&& !getResultadoConcurso().getListaSorteadosSegundoSorteio().isEmpty();
	}

	private DezenaConfig getDezenaConfig() {
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			return new DezenaConfig(false, estiloMKP.getCorFonteFundoClaro(), R.color.branco, false);
		}
		ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, estiloMKP.getCorEscura());

		return new DezenaConfig(false, R.color.branco,
				R.layout.item_dezena_detalhe,
				shapeConfig,false);
	}

	private DezenaConfig getDezenaTrevoConfig(ShapeConfig shapeConfig) {
		return new DezenaConfig(false, R.color.branco,
				R.layout.item_dezena_detalhe,
				shapeConfig,false);
	}

	private List<Dezena> getDezenas(List<Integer> listaInteiros) {
		Collections.sort(listaInteiros);
		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < listaInteiros.size(); i++) {
			if (listaInteiros.get(i) == 100 || listaInteiros.get(i) == 0){
				dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "00"));
			} else {
				dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "" + listaInteiros.get(i)));
			}

		}
		return dezenas;
	}

	private View.OnClickListener onResgateClickListenr() {
		return v -> {
			irParaResgate(aposta.getId());
		};
	}

	private void setaViews() {
        //imgTriangulo = view.findViewById(R.id.id_tag_bolao);
		concursoLabel = view.findViewById(R.id.concursoLabel);
        concursoTxt = view.findViewById(R.id.concursoTxt);
		cotaLabel = view.findViewById(R.id.cotaLabel);
		cotaTxt = view.findViewById(R.id.cotaTxt);
        dataApostaTxt = view.findViewById(R.id.dataApostaTxt);
        dataApostaLabel = view.findViewById(R.id.dataAposta);
        statusApostaTxt = view.findViewById(R.id.statusApostaTextView);
        tv_val_premio = view.findViewById(R.id.tv_val_premio);
        tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);
		tvPremioPago = view.findViewById(R.id.tv_premio_pago_tit);

		tvDuplaSena1 = view.findViewById(R.id.id_dupla_sena1);
		tvNumerosSorteados = view.findViewById(R.id.id_seus_numeros);
		tvDuplaSena2 = view.findViewById(R.id.id_dupla_sena2);
		tvNumerosSorteados2 = view.findViewById(R.id.id_seus_numeros2);
		labelMesTime = view.findViewById(R.id.mes_time_label);
		txtMesTime = view.findViewById(R.id.mes_time_valor);

		divTopNumerosSorteados = view.findViewById(R.id.v_div_top_numeros_sorteados);
		listViewSorteadosS7 = view.findViewById(R.id.id_lista_sorteados_s7);
		listViewSorteados = view.findViewById(R.id.id_lista_sorteados);
		rl_container_result_1 = view.findViewById(R.id.rl_container_result_1);
		listViewSorteados2 = view.findViewById(R.id.id_lista_sorteados2);
		rl_container_result_2 = view.findViewById(R.id.rl_container_result_2);
		divBotNumerosSorteados = view.findViewById(R.id.v_div_bot_numeros_sorteados);

		tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);

		listViewJogosS7 = view.findViewById(R.id.id_lista_jogos_s7);
		listViewJogos = view.findViewById(R.id.id_lista_jogos);
		listViewJogos2 = view.findViewById(R.id.id_lista_jogos2);
		listTrevos = view.findViewById(R.id.id_trevos);

		cl_premio_pago = view.findViewById(R.id.cl_premio_pago);
		clResgatePremio = view.findViewById(R.id.clResgatePremio);
		clCorpoAposta = view.findViewById(R.id.id_corpo_aposta);
		clCorpoApostaCont = view.findViewById(R.id.id_corpo_aposta_cont);
		linhaEsqueda = view.findViewById(R.id.v_separador_esq);
		linhaDireita = view.findViewById(R.id.v_separador_dir);
		linhaCentral = view.findViewById(R.id.divider);
	}

	public void atualizaValorPremio(BigDecimal valor){
		if (valor != null){
			setValorDoPremio(valor);
			if (tv_val_premio != null){
				tv_val_premio.setText(ViewUtils.getMoedaFormat(valor));
				tv_val_premio.setVisibility(View.VISIBLE);
			}
			if (tv_val_premio_tit != null){
				tv_val_premio_tit.setVisibility(View.VISIBLE);
			}
		}
	}

}