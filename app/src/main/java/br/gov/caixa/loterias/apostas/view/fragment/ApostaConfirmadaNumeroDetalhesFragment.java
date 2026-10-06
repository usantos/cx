package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class ApostaConfirmadaNumeroDetalhesFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";

	private View view, divider;
	private TextView concursoLabel, concursoTxt, quantidadeApostadasTxt, dataAposta, dataApostaTxt, resultTxt,
			txtTimeDiaAposta, labelTimeDiaAposta, tv_colunas, tv_sua_aposta,
			tv_colunas_2, tv_resultado_super_sete, labelTimeDiaResultado, txtTimeDiaResultado, tv_val_premio, tv_val_premio_tit,
			tv_premio_pago_tit; //modalidadeTxt

	private RelativeLayout informacaoAdicionalLayout, rl_fg_numeros_1, rl_fg_numeros_2 ,rl_container_result_1, rl_container_result_2,
			rl_time_mes;
	private RelativeLayout rl_divTopNumerosSorteados, rl_divBotNumerosSorteados;
	private View divTopNumerosSorteados, divBotNumerosSorteados;
	private ConstraintLayout cl_premio_pago, clResgatePremio;

	private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

	private EstiloModalidadeMKP estilo;
	private TipoApostaLinhaView tipoApostaLinhaView;

	List<Integer> numerosSorteadosPrimeiro, numerosSorteadosSegundo;
	List<List<Integer>> matrizSorteados;


	public ApostaConfirmadaNumeroDetalhesFragment() {}

	public static ApostaConfirmadaNumeroDetalhesFragment newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
																	 ResultadoConcursoDTO resultado,
																	 ComprovanteApostaDTO comprovante,
																	 BigDecimal valorPremio) {
		ApostaConfirmadaNumeroDetalhesFragment fragment = new ApostaConfirmadaNumeroDetalhesFragment();
		Bundle args = fragment.getBundle(resultado, comprovante, valorPremio);
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
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_aposta_confirmada_numero_detalhes, container, false);

		estilo = new EstiloModalidadeMKP(aposta.getModalidade());

		setaViews();

		clResgatePremio.setOnClickListener(onResgateClickListenr());

		preencheDadosAposta();

		return view;
	}

	private View.OnClickListener onResgateClickListenr() {
		return v -> {
			irParaResgate(aposta.getId());
		};
	}

	private void preencheDadosAposta() {

		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			concursoLabel.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			concursoTxt.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		}
		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));

		preencheData();

		preenchSituacaoPremio();

		preencheTeimosinha();
		preencheTimeCoracaoDiaSorte();

		preencheTipoApostaLinha();
		
		configuraNumeros();
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
			tipoApostaLinhaView.setupView(listTipoApostaLinha, estilo.getCorClara(), estilo.getCorFonteFundoClaro(), estilo.getCorFonteFundoClaro(), TipoApostaLinhaView.TriangleDirection.DOWN);
		}
	}

	private void preencheData() {
		if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
			dataApostaTxt.setText(aposta.getDataEfetivacao());
		} else {
			dataApostaTxt.setText(aposta.getDataEfetivacao() + "\n" + aposta.getHoraEfetivacao());
		}
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			dataAposta.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			dataApostaTxt.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		}
		dataApostaTxt.setVisibility(View.VISIBLE);
	}

	private void configuraNumeros() {
		if (!aposta.getNumerosSelecionados().isEmpty()) {
			if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
				configuraFragmentSuperSete(aposta);
			} else {
				configuraFragmentNumeros(aposta);
			}
		}
	}

	private void configuraFragmentNumeros(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm          = getActivity().getSupportFragmentManager();
		Fragment fragment, fragResultado;
		switch (aposta.getModalidade()){
			case DUPLA_SENA:
				numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
				numerosSorteadosSegundo =  (List<Integer>) (getResultadoConcurso() != null && getResultadoConcurso().getNumerosSorteadosSegundoSorteio() != null  ?
						getResultadoConcurso().getNumerosSorteadosSegundoSorteio() : null);
				fragment = NumerosFragment.newInstance(getContext().getResources().getString(R.string.seus_numeros) + "\n\n" +
								getContext().getResources().getString(R.string.primeiro_sorteiro),
						aposta.getListaNumerosSelecionados(), numerosSorteadosPrimeiro,
						getEstiloNumeros(aposta), null);
				break;
			case MAIS_MILIONARIA:
				numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
				List<Integer> trevoList = getResultadoConcurso() != null ? getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio() : null;
				if (numerosSorteadosPrimeiro != null){
					Collections.sort(numerosSorteadosPrimeiro);
				}
				fragment = NumerosTrevosFragment
						.newInstance(getContext().getResources().getString(R.string.seus_numeros),
								getContext().getResources().getString(R.string.trevosTitulo),
								aposta.getListaNumerosSelecionados(),
								AppUtils.converteListaInteiros((List<Integer>) aposta.getTrevosSelecionados()),
								numerosSorteadosPrimeiro,
								trevoList,
								getEstiloNumeros(aposta));

				break;
			default:
				numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
				fragment = NumerosFragment.newInstance(getContext()
						.getResources().getString(R.string.seus_numeros),
						aposta.getListaNumerosSelecionados(), numerosSorteadosPrimeiro,
						getEstiloNumeros(aposta), null);
				break;
		}

		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commitAllowingStateLoss();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);
		//frag resultado
		if(numerosSorteadosPrimeiro != null){
			if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
				numerosSorteadosSegundo = getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio();
			} else {
				Collections.sort(numerosSorteadosPrimeiro);
				for (int i = 0; i < numerosSorteadosPrimeiro.size(); i++){
					if (numerosSorteadosPrimeiro.get(i) == 0 || numerosSorteadosPrimeiro.get(i).equals(0)){
						Integer num = numerosSorteadosPrimeiro.get(i);
						numerosSorteadosPrimeiro.remove(i);
						numerosSorteadosPrimeiro.add(num);
						break;
					}
				}
			}
			//fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.numeros_sorteados),
			//		numerosSorteadosPrimeiro ,numerosSorteadosPrimeiro,
			//		getEstiloNumeros(aposta), null);
			rl_divTopNumerosSorteados.setVisibility(View.VISIBLE);
			divTopNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
			rl_divBotNumerosSorteados.setVisibility(View.VISIBLE);
			divBotNumerosSorteados.setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
			String textoNumerosSorteados = getContext().getResources().getString(R.string.numeros_sorteados);
			if (aposta.getModalidade() == ModalidadeEnum.DUPLA_SENA) {
				textoNumerosSorteados = getContext().getResources().getString(R.string.primeiro_sorteiro)+"\n"+
										textoNumerosSorteados;
			}
			fragResultado = NumerosHifenFragment.newInstance(textoNumerosSorteados,
				numerosSorteadosPrimeiro, new ConfiguracaoNumeros(estilo.getCorEscura(), estilo.getCorFonteFundoEscuro()));

			fm.beginTransaction().replace(R.id.rl_container_result_1, fragResultado).commitAllowingStateLoss();
			rl_container_result_1.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rl_container_result_1.getLayoutParams();
            params.rightMargin = 24;
            rl_container_result_1.setLayoutParams(params);

			if (aposta.getModalidade().equals(ModalidadeEnum.TIMEMANIA)) {
				int corBranco = ContextCompat.getColor(getContext(), R.color.branco);
				int corTime = ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro());
				String equipe = getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getNome();
				boolean apostaValida = aposta.getTimeDoCoracao() != null && aposta.getTimeDoCoracao().getNome() != null;
				if (apostaValida && equipe.equalsIgnoreCase(aposta.getTimeDoCoracao().getNome())) {
					txtTimeDiaAposta.setTextColor(corTime);
					labelTimeDiaAposta.setTextColor(corTime);
					txtTimeDiaAposta.setBackgroundColor(corBranco);
					labelTimeDiaAposta.setBackgroundColor(corBranco);
				}
				txtTimeDiaResultado.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
				txtTimeDiaResultado.setText(getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getNome() +
						"/" + getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getUf());
				rl_time_mes.setVisibility(View.VISIBLE);
			}

			if (aposta.getModalidade().equals(ModalidadeEnum.DIA_DE_SORTE)) {
				int corBranco = ContextCompat.getColor(getContext(), R.color.branco);
				int corDia = ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro());
				String mesDaSorte = getResultadoConcurso().getPremiacaoMesDeSorte().getMesDeSorte().getNome();
				if (mesDaSorte.equalsIgnoreCase(aposta.getMesDeSorte().getNome())) {
					txtTimeDiaAposta.setTextColor(corDia);
					txtTimeDiaAposta.setBackgroundColor(corBranco);

					labelTimeDiaAposta.setTextColor(corDia);
					labelTimeDiaAposta.setBackgroundColor(corBranco);
				}

				txtTimeDiaResultado.setTextColor(corDia);
				txtTimeDiaResultado.setBackgroundColor(corBranco);
				labelTimeDiaResultado.setTextColor(corDia);
				labelTimeDiaResultado.setBackgroundColor(corBranco);

				labelTimeDiaResultado.setText(R.string.label_mes_sorte);
				txtTimeDiaResultado.setText(mesDaSorte);
				labelTimeDiaAposta.setText(R.string.label_mes_sorte);
				rl_time_mes.setVisibility(View.VISIBLE);
			}
		}

		if (numerosSorteadosSegundo != null) {

			if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
				Collections.sort(numerosSorteadosSegundo);
				fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.trevos_sorteados),
						numerosSorteadosSegundo,
						numerosSorteadosSegundo,
						getEstiloNumeros(aposta),
						getTrevoShape());
				fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado).commitAllowingStateLoss();
				rl_container_result_2.setVisibility(View.VISIBLE);
			} else {
				Collections.sort(numerosSorteadosSegundo);
				fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.segundo_sorteiro), aposta.getListaNumerosSelecionados() ,numerosSorteadosSegundo,getEstiloNumeros(aposta), null);
				fm.beginTransaction().replace(R.id.rl_fg_numeros_2, fragResultado).commitAllowingStateLoss();
				rl_fg_numeros_2.setVisibility(View.VISIBLE);

				Collections.sort(numerosSorteadosSegundo);
				//fragResultado = NumerosFragment.newInstance("", numerosSorteadosSegundo ,numerosSorteadosSegundo,getEstiloNumeros(aposta), null);
				fragResultado = NumerosHifenFragment.newInstance(getContext().getResources().getString(R.string.segundo_sorteiro)+"\n"+
																	   getContext().getResources().getString(R.string.numeros_sorteados),
						numerosSorteadosSegundo, new ConfiguracaoNumeros(estilo.getCorEscura(), estilo.getCorFonteFundoEscuro()));

				fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado).commitAllowingStateLoss();
				rl_container_result_2.setVisibility(View.VISIBLE);
			}
		}
	}

	private ShapeConfig getTrevoShape() {
		return new ShapeConfig(R.drawable.ic_item_trevo,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.milionaria_escuro_mkp);
	}

	private void configuraFragmentSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm = getActivity().getSupportFragmentManager();

		matrizSorteados = getResultadoConcurso() != null ? getResultadoConcurso().getMatrizNumerosSorteadosPrimeiroSorteio() : null;

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment
				.newInstance(aposta.getMatrizNumerosSelecionados(), matrizSorteados, getEstiloSuperSete(aposta));
		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commitAllowingStateLoss();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);
		tv_colunas.setVisibility(View.VISIBLE);
		tv_sua_aposta.setVisibility(View.VISIBLE);

		tv_sua_aposta.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
		tv_colunas.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));

		if (matrizSorteados != null) {
			Utils.ordenaMatrizInteiro(matrizSorteados);
			NumerosSuperSeteFragment frag = NumerosSuperSeteFragment
					.newInstance(matrizSorteados, matrizSorteados, getEstiloSuperSete(aposta));
			fm.beginTransaction().replace(R.id.rl_container_result_1, frag).commitAllowingStateLoss();
			rl_container_result_1.setVisibility(View.VISIBLE);
			tv_resultado_super_sete.setVisibility(View.VISIBLE);
			tv_colunas_2.setVisibility(View.VISIBLE);

			tv_resultado_super_sete.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
			tv_colunas_2.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
		}
	}

	private void preencheTimeCoracaoDiaSorte() {
		if (aposta.getTimeDoCoracao() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			labelTimeDiaAposta.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			txtTimeDiaAposta.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			txtTimeDiaAposta.setText(aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf());
		} else if (aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNumero() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			labelTimeDiaAposta.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
			labelTimeDiaAposta.setText(R.string.label_mes_sorte);
			txtTimeDiaAposta.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
			txtTimeDiaAposta.setText(aposta.getMesDeSorte().getNome());
		}
	}

	private void preencheTeimosinha() {
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			//modalidadeTxt.setVisibility(View.VISIBLE);
			quantidadeApostadasTxt.setVisibility(View.VISIBLE);

			Integer quantidade      = aposta.getQuantidadeTeimosinhas();
			String  textoTeimosinha = "";
			if (quantidade == 1) {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_aposta));
			} else {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_apostas));
			}
			quantidadeApostadasTxt.setText(textoTeimosinha);
			if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
				quantidadeApostadasTxt.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
				//modalidadeTxt.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			}
		}
	}

	private void preenchSituacaoPremio() {

		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			divider.setBackgroundResource(estilo.getCorFonteFundoClaro());
			resultTxt.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			view.findViewById(R.id.v_separador_esq).setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			view.findViewById(R.id.v_separador_dir).setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		}

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);

			if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
				tv_val_premio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
				tv_val_premio_tit.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
				tv_premio_pago_tit.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			}

			if (getValorDoPremio() != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
			} else {
				buscaDetalhePremio(aposta.getId(), onBuscaDetalhePremioListener());
			}
			if (isApostaPremiada(aposta)){
				resultTxt.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
				clResgatePremio.setVisibility(View.VISIBLE);
				cl_premio_pago.setVisibility(View.GONE);
				resultTxt.setVisibility(View.VISIBLE);
			} else {
				cl_premio_pago.setVisibility(View.VISIBLE);
				clResgatePremio.setVisibility(View.GONE);
				resultTxt.setVisibility(View.GONE);
			}
		} else  {
			clResgatePremio.setVisibility(View.GONE);
			resultTxt.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
		}
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

	private void setaViews() {
		concursoLabel = view.findViewById(R.id.concursoLabel);
		concursoTxt = view.findViewById(R.id.concursoTxt);
		quantidadeApostadasTxt = view.findViewById(R.id.quantidadeApostadasTxt);
		dataAposta = view.findViewById(R.id.dataAposta);
		dataApostaTxt = view.findViewById(R.id.dataApostaTxt);
		resultTxt = view.findViewById(R.id.resultTxt);
		divider = view.findViewById(R.id.divider);
		//modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
		//apostaTrocaTxt = view.findViewById(R.id.apostaTrocaTxt);
		txtTimeDiaAposta = view.findViewById(R.id.informacaoAdicionalTxt);
		labelTimeDiaAposta = view.findViewById(R.id.labelInformacaoAdicionalTxt);
		tv_colunas = view.findViewById(R.id.tv_colunas);
		tv_sua_aposta = view.findViewById(R.id.tv_sua_aposta);
		tv_colunas_2 = view.findViewById(R.id.tv_colunas_2);
		tv_resultado_super_sete = view.findViewById(R.id.tv_resultado_super_sete);
		labelTimeDiaResultado = view.findViewById(R.id.tv_tit_time_mes);
		txtTimeDiaResultado = view.findViewById(R.id.tv_time_mes);

		informacaoAdicionalLayout = view.findViewById(R.id.informacaoAdicionalLayout);

		tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);
		rl_fg_numeros_1 = view.findViewById(R.id.rl_fg_numeros_1);
		rl_fg_numeros_2 = view.findViewById(R.id.rl_fg_numeros_2);
		rl_divTopNumerosSorteados = view.findViewById(R.id.rl_div_top_numeros_sorteados);
		divTopNumerosSorteados = view.findViewById(R.id.v_div_top_numeros_sorteados);
		rl_container_result_1 = view.findViewById(R.id.rl_container_result_1);
		rl_container_result_2 = view.findViewById(R.id.rl_container_result_2);
		rl_divBotNumerosSorteados = view.findViewById(R.id.rl_div_bot_numeros_sorteados);
		divBotNumerosSorteados = view.findViewById(R.id.v_div_bot_numeros_sorteados);
		rl_time_mes = view.findViewById(R.id.rl_time_mes);

		tv_val_premio = view.findViewById(R.id.tv_val_premio);
		tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);
		cl_premio_pago = view.findViewById(R.id.cl_premio_pago);
		tv_premio_pago_tit = view.findViewById(R.id.tv_premio_pago_tit);
		clResgatePremio = view.findViewById(R.id.clResgatePremio);
	}

	public void atualizaValorPremio(BigDecimal valor){
		if (valor != null && tv_val_premio != null){
			setValorDoPremio(valor);
			tv_val_premio.setText(ViewUtils.getMoedaFormat(valor));
		}
	}

}