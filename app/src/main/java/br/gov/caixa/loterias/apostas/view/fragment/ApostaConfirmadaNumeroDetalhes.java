package br.gov.caixa.loterias.apostas.view.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by pmotta on 23/03/2018.
 * Class ApostaConfirmadaNumeroDetalhes
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

	List<Integer> numerosSorteadosPrimeiro, numerosSorteadosSegundo;
	List<List<Integer>> matrizSorteados;

	@Bean
	protected ApostaBO apostaBO;

	@ViewById
	protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt,
			apostaTrocaTxt, informacaoAdicionalTxt, labelInformacaoAdicionalTxt, tv_colunas, tv_sua_aposta, tv_resultado,
			tv_colunas_2, tv_resultado_super_sete, tv_tit_time_mes, tv_time_mes;

	@ViewById
	protected RelativeLayout informacaoAdicionalLayout, rl_fg_numeros_1, rl_fg_numeros_2 ,rl_container_result_1, rl_container_result_2,
			rl_time_mes;

	@FragmentArg
	protected IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

	@FragmentArg
	protected ResultadoConcursoDTO resultadoConcurso;

	@ViewById
	protected TextView tv_val_premio, tv_val_premio_tit;

	@ViewById
	protected ConstraintLayout cl_premio_pago, clResgatePremio;

	@FragmentArg
	protected BigDecimal valorPremio;

	@AfterViews
	protected void init() {
		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		if (!StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
			dataApostaTxt.setText(aposta.getDataEfetivacao() + getResources().getString(R.string.barra_n) + aposta.getHoraEfetivacao());
		}

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || apostaPremiada()) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			if (valorPremio != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
			}
			if (apostaPremiada()){
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

		if (aposta.getTroca()) {
			apostaTrocaTxt.setVisibility(View.VISIBLE);
		}
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
		if (aposta.getTimeDoCoracao() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			informacaoAdicionalTxt.setText(aposta.getTimeDoCoracao().getNome());
		} else if (aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNumero() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			labelInformacaoAdicionalTxt.setText(R.string.label_mes_sorte);
			informacaoAdicionalTxt.setText(aposta.getMesDeSorte().getNome());
		}
		configuraNumeros();
	}

	private boolean apostaPremiada() {
		return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA;
	}

	public void atualizaValorPremio(BigDecimal valor){
		if (valor != null){
			valorPremio = valor;
			tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
		}

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
				numerosSorteadosPrimeiro = resultadoConcurso != null ? resultadoConcurso.getListaSorteadosPrimeiroSorteio() : null;
				numerosSorteadosSegundo =  (List<Integer>) (resultadoConcurso != null && resultadoConcurso.getNumerosSorteadosSegundoSorteio() != null  ?
						resultadoConcurso.getNumerosSorteadosSegundoSorteio() : new ArrayList<>());
				fragment = NumerosFragment.newInstance(getContext().getResources().getString(R.string.seus_numeros), aposta.getListaNumerosSelecionados(),numerosSorteadosPrimeiro,configuraEstiloNumeros());
				break;
			case MAIS_MILIONARIA:

				fragment = NumerosTrevosFragment.newInstance(getContext().getResources().getString(R.string.seus_numeros), aposta.getListaNumerosSelecionados(),
															 AppUtils.converteListaInteiros((List<Integer>) aposta.getTrevosSelecionados()),
															 numerosSorteadosPrimeiro,configuraEstiloNumeros());

				break;
			default:
				numerosSorteadosPrimeiro = resultadoConcurso != null ? resultadoConcurso.getListaSorteadosPrimeiroSorteio() : null;
				fragment = NumerosFragment.newInstance(getContext().getResources().getString(R.string.seus_numeros), aposta.getListaNumerosSelecionados(),numerosSorteadosPrimeiro,configuraEstiloNumeros());
				break;
		}

		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commit();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);
		//frag resultado
		if(numerosSorteadosPrimeiro != null){
			if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
				numerosSorteadosPrimeiro.remove(numerosSorteadosPrimeiro.size() - 1);
				numerosSorteadosPrimeiro.remove(numerosSorteadosPrimeiro.size() - 1);
			}
			Collections.sort(numerosSorteadosPrimeiro);
			fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.numeros_sorteados), numerosSorteadosPrimeiro ,numerosSorteadosPrimeiro,configuraEstiloNumeros());
			fm.beginTransaction().replace(R.id.rl_container_result_1, fragResultado).commit();
			rl_container_result_1.setVisibility(View.VISIBLE);
			tv_resultado.setVisibility(View.VISIBLE);

			int corBranco = ContextCompat.getColor(getContext(), R.color.branco);
			int corTime = ContextCompat.getColor(getContext(), R.color.timemaniaclaro);
			int corDia = ContextCompat.getColor(getContext(), R.color.diasorteclaro);


			if (aposta.getModalidade().equals(ModalidadeEnum.TIMEMANIA)) {
				String equipe = resultadoConcurso.getPremiacaoTimeDoCoracao().getEquipe().getNome();
				if (equipe.equalsIgnoreCase(aposta.getTimeDoCoracao().getNome())) {
					informacaoAdicionalTxt.setTextColor(corTime);
					labelInformacaoAdicionalTxt.setTextColor(corTime);
					informacaoAdicionalTxt.setBackgroundColor(corBranco);
					labelInformacaoAdicionalTxt.setBackgroundColor(corBranco);
				}
				tv_time_mes.setText(resultadoConcurso.getPremiacaoTimeDoCoracao().getEquipe().getNome());
				rl_time_mes.setVisibility(View.VISIBLE);
			}

			if (aposta.getModalidade().equals(ModalidadeEnum.DIA_DE_SORTE)) {
				String mesDaSorte = resultadoConcurso.getPremiacaoMesDeSorte().getMesDeSorte().getNome();
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
			Collections.sort(numerosSorteadosSegundo);
			fragResultado = NumerosFragment.newInstance("", aposta.getListaNumerosSelecionados() ,numerosSorteadosSegundo,configuraEstiloNumeros());
			fm.beginTransaction().replace(R.id.rl_fg_numeros_2, fragResultado).commit();
			rl_fg_numeros_2.setVisibility(View.VISIBLE);

			Collections.sort(numerosSorteadosSegundo);
			fragResultado = NumerosFragment.newInstance(getContext().getResources().getString(R.string.numeros_sorteados), numerosSorteadosSegundo ,numerosSorteadosSegundo,configuraEstiloNumeros());
			fm.beginTransaction().replace(R.id.rl_container_result_2, fragResultado).commit();
			rl_container_result_2.setVisibility(View.VISIBLE);
			tv_resultado.setVisibility(View.VISIBLE);
		}
	}

	private void configuraFragmentSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm          = getActivity().getSupportFragmentManager();

		matrizSorteados = resultadoConcurso != null ? resultadoConcurso.getMatrizNumerosSorteadosPrimeiroSorteio() : null;

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados(),matrizSorteados,configuraEstiloSuperSete());
		fm.beginTransaction().replace(R.id.rl_fg_numeros_1, fragment).commit();
		rl_fg_numeros_1.setVisibility(View.VISIBLE);
		tv_colunas.setVisibility(View.VISIBLE);
		tv_sua_aposta.setVisibility(View.VISIBLE);

		if (matrizSorteados != null) {
			Utils.ordenaMatrizInteiro(matrizSorteados);
			NumerosSuperSeteFragment frag = NumerosSuperSeteFragment.newInstance(matrizSorteados,matrizSorteados,configuraEstiloSuperSete());
			fm.beginTransaction().replace(R.id.rl_container_result_1, frag).commit();
			rl_container_result_1.setVisibility(View.VISIBLE);
			tv_resultado_super_sete.setVisibility(View.VISIBLE);
			tv_colunas_2.setVisibility(View.VISIBLE);
		}
	}

	private ConfiguracaoNumeros configuraEstiloNumeros() {
		EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());
		if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
			return new ConfiguracaoNumeros(estilo.getCorClara(), R.color.branco);
		} else {
			return new ConfiguracaoNumeros(estilo.getCorEscura(), R.color.branco);
		}
	}

	private ConfiguracaoNumerosSuperSete configuraEstiloSuperSete() {
		return new ConfiguracaoNumerosSuperSete(R.color.supersetemedio,
												R.color.branco,
												R.color.branco,
												R.color.supersetemedio,
												R.color.branco,
												R.color.supersetemedio,
												R.color.supersetemedio,
												R.color.superseteescuro,
												R.color.supersetemedio);
	}
}