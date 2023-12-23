package br.gov.caixa.loterias.apostas.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.google.android.gms.common.util.DataUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DataUtil;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class ApostaConfirmadaNumeroDetalhesFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";

	private View view;
	private TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt,
			apostaTrocaTxt, informacaoAdicionalTxt, labelInformacaoAdicionalTxt, tv_colunas, tv_sua_aposta,
			tv_colunas_2, tv_resultado_super_sete, tv_tit_time_mes, tv_time_mes, tv_val_premio, tv_val_premio_tit;
	private TextView cotaLabel, cotaValue;
	private ImageView tagBolao;
	private RelativeLayout informacaoAdicionalLayout, rl_fg_numeros_1, rl_fg_numeros_2 ,rl_container_result_1, rl_container_result_2,
			rl_time_mes;
	private ConstraintLayout cl_premio_pago, clResgatePremio;

	private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

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
		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		if (aposta.getIndicadorCotaBolao()){
			cotaLabel.setText("Cota");
			cotaValue.setText(aposta.getReservaCotaBolao().getNumeroCotaReservada()+ "/" + aposta.getReservaCotaBolao().getQtdCotaTotalBolao());
			tagBolao.setVisibility(View.VISIBLE);
			cotaLabel.setVisibility(View.VISIBLE);
			cotaValue.setVisibility(View.VISIBLE);
		}
		preencheData();

		preenchSituacaoPremio();

		if (aposta.getTroca()) {
			apostaTrocaTxt.setVisibility(View.VISIBLE);
		}

		preencheTeimosinha();
		preencheTimeCoracaoDiaSorte();

		configuraNumeros();
	}

	private void preencheData() {
		if (aposta.getIndicadorCotaBolao()){
			dataApostaTxt.setText(getDataBolao(aposta));
		} else {
			if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
				dataApostaTxt.setText(aposta.getDataEfetivacao());
			} else {
				dataApostaTxt.setText(aposta.getDataEfetivacao() + "\n" + aposta.getHoraEfetivacao());
			}
		}
		dataApostaTxt.setVisibility(View.VISIBLE);
	}

	private String getDataBolao(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
		String dataString = DataUtil.formatStringData(DataUtil.DD_MM_YYYY, aposta.getReservaCotaBolao().getDataHoraReserva());
		String horaString = DataUtil.formatStringData(DataUtil.HH_MM_SS, aposta.getReservaCotaBolao().getDataHoraReserva());

		return dataString + "\n" + horaString;
	}

	private void configuraNumeros() {
		if (aposta.getIndicadorCotaBolao()){
			configuraFragmentBolao();
		} else {
			if (!aposta.getNumerosSelecionados().isEmpty()) {
				if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
					configuraFragmentSuperSete(aposta);
				} else {
					configuraFragmentNumeros(aposta);
				}
			}
		}
	}

	private void configuraFragmentBolao() {
		numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
		Fragment fragmentResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.numeros_sorteados),
				numerosSorteadosPrimeiro, numerosSorteadosPrimeiro,
				getEstiloNumeros(aposta), null);
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rl_fg_numeros_1, fragmentResultado).commit();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);

		aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao();
	}

	private void configuraFragmentNumeros(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm          = getActivity().getSupportFragmentManager();
		Fragment fragment, fragResultado;
		switch (aposta.getModalidade()){
			case DUPLA_SENA:
				numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
				numerosSorteadosSegundo =  (List<Integer>) (getResultadoConcurso() != null && getResultadoConcurso().getNumerosSorteadosSegundoSorteio() != null  ?
						getResultadoConcurso().getNumerosSorteadosSegundoSorteio() : new ArrayList<>());
				fragment = NumerosFragment.newInstance(getContext().getResources().getString(R.string.seus_numeros),
						aposta.getListaNumerosSelecionados(), numerosSorteadosPrimeiro,
						getEstiloNumeros(aposta), null);
				break;
			case MAIS_MILIONARIA:
				numerosSorteadosPrimeiro = getResultadoConcurso() != null ? getResultadoConcurso().getListaSorteadosPrimeiroSorteio() : null;
				List<Integer> trevoList = getResultadoConcurso() != null ? getResultadoConcurso().getTrevosSorteadosPrimeiroSorteio() : null;
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
						aposta.getListaNumerosSelecionados(),numerosSorteadosPrimeiro,
						getEstiloNumeros(aposta), null);
				break;
		}

		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commit();
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
			fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.numeros_sorteados),
					numerosSorteadosPrimeiro ,numerosSorteadosPrimeiro,
					getEstiloNumeros(aposta), null);
			fm.beginTransaction().replace(R.id.rl_container_result_1, fragResultado).commit();
			rl_container_result_1.setVisibility(View.VISIBLE);


			if (aposta.getModalidade().equals(ModalidadeEnum.TIMEMANIA)) {
				int corBranco = ContextCompat.getColor(getContext(), R.color.branco);
				int corTime = ContextCompat.getColor(getContext(), R.color.timemaniaclaro);
				String equipe = getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getNome();
				boolean apostaValida = aposta.getTimeDoCoracao() != null && aposta.getTimeDoCoracao().getNome() != null;
				if (apostaValida && equipe.equalsIgnoreCase(aposta.getTimeDoCoracao().getNome())) {
					informacaoAdicionalTxt.setTextColor(corTime);
					labelInformacaoAdicionalTxt.setTextColor(corTime);
					informacaoAdicionalTxt.setBackgroundColor(corBranco);
					labelInformacaoAdicionalTxt.setBackgroundColor(corBranco);
				}
				tv_time_mes.setText(getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getNome() +
						"/" + getResultadoConcurso().getPremiacaoTimeDoCoracao().getEquipe().getUf());
				rl_time_mes.setVisibility(View.VISIBLE);
			}

			if (aposta.getModalidade().equals(ModalidadeEnum.DIA_DE_SORTE)) {
				int corBranco = ContextCompat.getColor(getContext(), R.color.branco);
				int corDia = ContextCompat.getColor(getContext(), R.color.diasorteclaro);
				String mesDaSorte = getResultadoConcurso().getPremiacaoMesDeSorte().getMesDeSorte().getNome();
				if (mesDaSorte.equalsIgnoreCase(aposta.getMesDeSorte().getNome())) {
					informacaoAdicionalTxt.setTextColor(corDia);
					labelInformacaoAdicionalTxt.setTextColor(corDia);
					informacaoAdicionalTxt.setBackgroundColor(corBranco);
					labelInformacaoAdicionalTxt.setBackgroundColor(corBranco);
				}
				tv_time_mes.setText(mesDaSorte);
				tv_tit_time_mes.setText(R.string.label_mes_sorte);
				tv_time_mes.setTextColor(corDia);
				tv_tit_time_mes.setTextColor(corDia);

				labelInformacaoAdicionalTxt.setText(R.string.label_mes_sorte);
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
				fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado).commit();
				rl_container_result_2.setVisibility(View.VISIBLE);
			} else {
				Collections.sort(numerosSorteadosSegundo);
				fragResultado = NumerosFragment.newInstance("", aposta.getListaNumerosSelecionados() ,numerosSorteadosSegundo,getEstiloNumeros(aposta), null);
				fm.beginTransaction().replace(R.id.rl_fg_numeros_2, fragResultado).commit();
				rl_fg_numeros_2.setVisibility(View.VISIBLE);

				Collections.sort(numerosSorteadosSegundo);
				fragResultado = NumerosFragment.newInstance("", numerosSorteadosSegundo ,numerosSorteadosSegundo,getEstiloNumeros(aposta), null);
				fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado).commit();
				rl_container_result_2.setVisibility(View.VISIBLE);
			}
		}

	}

	private ShapeConfig getTrevoShape() {
		return new ShapeConfig(R.drawable.ic_item_trevo,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.mais_milionaria_escura);
	}

	private void configuraFragmentSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm          = getActivity().getSupportFragmentManager();

		matrizSorteados = getResultadoConcurso() != null ? getResultadoConcurso().getMatrizNumerosSorteadosPrimeiroSorteio() : null;

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment
				.newInstance(aposta.getMatrizNumerosSelecionados(),matrizSorteados,getEstiloSuperSete());
		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commit();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);
		tv_colunas.setVisibility(View.VISIBLE);
		tv_sua_aposta.setVisibility(View.VISIBLE);

		if (matrizSorteados != null) {
			Utils.ordenaMatrizInteiro(matrizSorteados);
			NumerosSuperSeteFragment frag = NumerosSuperSeteFragment
					.newInstance(matrizSorteados,matrizSorteados,getEstiloSuperSete());
			fm.beginTransaction().replace(R.id.rl_container_result_1, frag).commit();
			rl_container_result_1.setVisibility(View.VISIBLE);
			tv_resultado_super_sete.setVisibility(View.VISIBLE);
			tv_colunas_2.setVisibility(View.VISIBLE);
		}
	}

	private void preencheTimeCoracaoDiaSorte() {
		if (aposta.getTimeDoCoracao() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			informacaoAdicionalTxt.setText(aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf());
		} else if (aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNumero() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			labelInformacaoAdicionalTxt.setText(R.string.label_mes_sorte);
			informacaoAdicionalTxt.setText(aposta.getMesDeSorte().getNome());
		}
	}

	private void preencheTeimosinha() {
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			modalidadeTxt.setVisibility(View.VISIBLE);
			quantidadeApostadasTxt.setVisibility(View.VISIBLE);

			Integer quantidade      = aposta.getQuantidadeTeimosinhas();
			String  textoTeimosinha = "";
			if (quantidade == 1) {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_aposta));
			} else {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_apostas));
			}
			quantidadeApostadasTxt.setText(textoTeimosinha);
		}
	}

	private void preenchSituacaoPremio() {
		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			if (getValorDoPremio() != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
			} else {
				buscaDetalhePremio(aposta.getId(), onBuscaDetalhePremioListener());
			}
			if (isApostaPremiada(aposta)){
				resultTxt.setText(aposta.getSituacao().getDescricao());
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
			resultTxt.setText(aposta.getSituacao().getDescricao());
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
		concursoTxt = view.findViewById(R.id.concursoTxt);
		quantidadeApostadasTxt = view.findViewById(R.id.quantidadeApostadasTxt);
		dataApostaTxt = view.findViewById(R.id.dataApostaTxt);
		resultTxt = view.findViewById(R.id.resultTxt);
		modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
		apostaTrocaTxt = view.findViewById(R.id.apostaTrocaTxt);
		informacaoAdicionalTxt = view.findViewById(R.id.informacaoAdicionalTxt);
		labelInformacaoAdicionalTxt = view.findViewById(R.id.labelInformacaoAdicionalTxt);
		tv_colunas = view.findViewById(R.id.tv_colunas);
		tv_sua_aposta = view.findViewById(R.id.tv_sua_aposta);
		tv_colunas_2 = view.findViewById(R.id.tv_colunas_2);
		tv_resultado_super_sete = view.findViewById(R.id.tv_resultado_super_sete);
		tv_tit_time_mes = view.findViewById(R.id.tv_tit_time_mes);
		tv_time_mes = view.findViewById(R.id.tv_time_mes);

		informacaoAdicionalLayout = view.findViewById(R.id.informacaoAdicionalLayout);
		rl_fg_numeros_1 = view.findViewById(R.id.rl_fg_numeros_1);
		rl_fg_numeros_2 = view.findViewById(R.id.rl_fg_numeros_2);
		rl_container_result_1 = view.findViewById(R.id.rl_container_result_1);
		rl_container_result_2 = view.findViewById(R.id.rl_container_result_2);
		rl_time_mes = view.findViewById(R.id.rl_time_mes);

		tv_val_premio = view.findViewById(R.id.tv_val_premio);
		tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);
		cl_premio_pago = view.findViewById(R.id.cl_premio_pago);
		clResgatePremio = view.findViewById(R.id.clResgatePremio);

		tagBolao = view.findViewById(R.id.id_tag_bolao);
		cotaLabel = view.findViewById(R.id.cotaLabel);
		cotaValue = view.findViewById(R.id.cotaTxt);
	}

	public void atualizaValorPremio(BigDecimal valor){
		if (valor != null && tv_val_premio != null){
			setValorDoPremio(valor);
			tv_val_premio.setText(ViewUtils.getMoedaFormat(valor));
		}
	}

}