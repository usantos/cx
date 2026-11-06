package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaBolaoDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostasConfirmadasFragment;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloBolaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloComboFragment;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloFiltroLotericaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloSimulacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CabecalhoSimulacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ConfirmacaoChavePixResgateFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CopiaColaPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.DetalheApostaBolaoLotecaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.LotericaEmptyFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MesesFragment;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.MesesFragment2;
import br.gov.caixa.loterias.apostas.view.fragment.MeusCartoesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.OrientacaoPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SubBarraModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.SurpresinhaFragment2;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.TimesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TrevosFragment;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.TrevosFragment2;

public class FragmentUtils {

	public static Fragment startFragmentAllowingStateLoss(FragmentManager fm, int id, Fragment fragment){
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		return fragment;
	}

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, String TAG) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(TAG);
			FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		}
		return fragment;
	}

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, Boolean irCarrinho) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(irCarrinho);
			FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		}
		return fragment;
	}

	public static MeusCartoesFragment meusCartoesFragment(FragmentManager fm, int id, List<RetornoCartao> cartoes, MeusCartoesConfig config) {
		MeusCartoesFragment fragment = MeusCartoesFragment.newInstance(cartoes, config);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static PaginacaoFragment startPaginacaoFragment(FragmentManager fm, PaginacaoFragment fragment, int id) {
		if(fragment == null){
			fragment = PaginacaoFragment.newInstance();
			FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		}
		return fragment;
	}

	public static MeioPagamentoFragment startMeioPagamento(FragmentManager fm, int id, MeioPagamentoConfig config) {
		MeioPagamentoFragment fragment = MeioPagamentoFragment.newInstance(config);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoFragment startCartaoCredito(FragmentManager fm, int id, CartaoCreditoConfig config) {
		CartaoCreditoFragment fragment = CartaoCreditoFragment.newInstance(config);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static CardPixFragment startCardOrientacaoPix(FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newOrientacaoInstance(CardPixFragment.TELA_ORIENTACAO);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoVersoFragment startCartaoCreditoVerso(FragmentManager fm, int id, String metodo, String cvv) {
		CartaoCreditoVersoFragment fragment = CartaoCreditoVersoFragment.newInstance(metodo,cvv);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id,fragment);
		return fragment;
	}

	public static CardPixFragment startCardCopiaColaPix(GerarPixDTO pixDTO, FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newCopiaColaInstance(pixDTO, CardPixFragment.TELA_COPIA_COLA);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static OrientacaoPixFragment startOrientacaoPix(FragmentManager fm, int id) {
		OrientacaoPixFragment fragment = OrientacaoPixFragment.newInstance();
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static CopiaColaPixFragment startCopiaColaPix(GerarPixDTO pixDTO, String dataHoraServidor, FragmentManager fm, int id) {
		CopiaColaPixFragment fragment = CopiaColaPixFragment.newInstance(pixDTO, dataHoraServidor);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static SubBarraModalidadeFragment startFragmentSubBarraModalidade(FragmentManager fm, int id, ModalidadeEnum modalidade,
																			 String concurso, String sorteio, boolean especial) {
		SubBarraModalidadeFragment frag = SubBarraModalidadeFragment.newInstance(modalidade, concurso, sorteio, especial);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static BarraTituloSimulacaoFragment startFragmentBarraTituloSimulacao(FragmentManager fm, int id, ModalidadeEnum modalidade,
																				 String concurso, String sorteio, boolean especial, int corBackground, boolean showBGEspecial) {
		BarraTituloSimulacaoFragment frag = BarraTituloSimulacaoFragment.newInstance(modalidade, concurso, sorteio, especial, corBackground, showBGEspecial);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static CabecalhoSimulacaoFragment startCabecalhoSimulacao(FragmentManager fm, int id, ModalidadeEnum modalidade,
																	 String concurso, String sorteio, boolean especial, int corBackground, boolean showBGEspecial) {
		CabecalhoSimulacaoFragment frag = CabecalhoSimulacaoFragment.newInstance(modalidade, concurso, sorteio, especial, corBackground, showBGEspecial);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static BarraTituloBolaoFragment startFragmentBarraTituloBolao (FragmentManager fm, int id, ModalidadeEnum modalidade,
																		  String concurso, String sorteio, BigDecimal valorPremio, boolean especial, int corBackground, int corBackgroundPremio) {
		BarraTituloBolaoFragment frag = BarraTituloBolaoFragment.newInstance(modalidade, concurso, sorteio, valorPremio, especial, corBackground, corBackgroundPremio);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static BarraTituloFiltroLotericaFragment startFragmentBarraTituloFiltroLoterica (FragmentManager fm, int id, FiltroAplicadoMarketplace filtro, boolean isLotericaFavorita) {
		BarraTituloFiltroLotericaFragment frag = BarraTituloFiltroLotericaFragment.newInstance(filtro, isLotericaFavorita);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static BarraTituloComboFragment startFragmentBarraTituloCombo(FragmentManager fm, int id, ModalidadeEnum modalidade,
																		 int corBackground) {
		BarraTituloComboFragment frag = BarraTituloComboFragment.newInstance(modalidade, corBackground);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static RodapeSimulacaoApostaFragment startRodapeSimulacaoAPosta(FragmentManager fm, int id, BigDecimal valorBolao, BigDecimal valorTotal) {
		RodapeSimulacaoApostaFragment frag = RodapeSimulacaoApostaFragment.newInstance(valorBolao, valorTotal);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static ApostaConfirmadaNumeroDetalhesFragment startApostaConfirmadaDetalhes(FragmentManager fm, int id,
																					   IdentificaoDeUmaApostaDas8Modalidades aposta,
																					   ResultadoConcursoDTO resultado,
																					   ComprovanteApostaDTO comprovante,
																					   BigDecimal valorPremio) {
		ApostaConfirmadaNumeroDetalhesFragment fragment = ApostaConfirmadaNumeroDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static ApostaConfirmadaBolaoDetalhesFragment startApostaConfirmadaBolaoDetalhes(FragmentManager fm, int id,
																						   IdentificaoDeUmaApostaDas8Modalidades aposta,
																						   ResultadoConcursoDTO resultado,
																						   ComprovanteApostaDTO comprovante,
																						   BigDecimal valorPremio) {
		ApostaConfirmadaBolaoDetalhesFragment fragment = ApostaConfirmadaBolaoDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static ApostaConfirmadaPartidaDetalhesFragment startApostaConfirmadaPartidaDetalhes(FragmentManager fm, int id,
																							   IdentificaoDeUmaApostaDas8Modalidades aposta,
																							   ResultadoConcursoDTO resultado,
																							   ComprovanteApostaDTO comprovante,
																							   BigDecimal valorPremio, boolean isToteca) {
		ApostaConfirmadaPartidaDetalhesFragment fragment = ApostaConfirmadaPartidaDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio, isToteca);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, fragment);
		return fragment;
	}

	public static DetalheApostaBolaoLotecaFragment startApostaConfirmadaPartidaDetalhesBolao(FragmentManager fm, int id,
																							 IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta,
																							 ResultadoConcursoDTO resultado, ComprovanteApostaDTO comprovante,
																							 BigDecimal valorPremio) {
		DetalheApostaBolaoLotecaFragment frag = DetalheApostaBolaoLotecaFragment.newInstance(aposta, resultado, comprovante, valorPremio);
		FragmentUtils.startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static RodapeSimulacaoApostaFragment startRodapeSimulacaoAPosta(FragmentManager fm, int id, BigDecimal valorBolao) {
		RodapeSimulacaoApostaFragment frag = RodapeSimulacaoApostaFragment.newInstance(valorBolao);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static FiltroVazioFragment startFiltroVazio(FragmentManager fm, int idFrag) {
		FiltroVazioFragment frag = FiltroVazioFragment.newInstance();
		startFragmentAllowingStateLoss(fm, idFrag, frag);
		return frag;
	}

	public static LotericaEmptyFragment startLotericaVazia(FragmentManager fm, int idFrag) {
		LotericaEmptyFragment frag = LotericaEmptyFragment.newInstance();
		startFragmentAllowingStateLoss(fm, idFrag, frag);
		return frag;
	}
	public static MesesFragment startMesesFragment(FragmentManager fm, int id, ModalidadeEnum modalidadeEnum,
												   ParametroJogoDTO parametro, ArrayList<Integer> selecionados) {
		MesesFragment fragment = MesesFragment.newInstance(modalidadeEnum, parametro, selecionados);
		startFragmentAllowingStateLoss(fm,id, fragment);
		return fragment;
	}
	public static MesesFragment2 startMesesFragment2(FragmentManager fm, int id,
													 ParametroJogoDTO parametro, ArrayList<Integer> selecionados) {
		MesesFragment2 fragment = MesesFragment2.newInstance(parametro, selecionados);
		startFragmentAllowingStateLoss(fm,id, fragment);
		return fragment;
	}
	public static TrevosFragment startTrevosFragment(FragmentManager fm, int id, IdentificaoDeUmaApostaDas8Modalidades aposta,
													 ParametroJogoDTO parametroSimulacao, ArrayList<Integer> dezenasSelecionadas) {
		TrevosFragment frag = TrevosFragment.newInstance(aposta, parametroSimulacao, dezenasSelecionadas);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}
	public static TrevosFragment2 startTrevosFragment2(FragmentManager fm, int id,
													 ParametroJogoDTO parametroSimulacao, ArrayList<Integer> dezenasSelecionadas) {
		TrevosFragment2 frag = TrevosFragment2.newInstance(parametroSimulacao, dezenasSelecionadas);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}
	public static TimesFragment startTimesFragment(FragmentManager fm, int id,
													 ParametroJogoDTO parametroSimulacao, ArrayList<Integer> dezenasSelecionadas) {
		TimesFragment frag = TimesFragment.newInstance(parametroSimulacao, dezenasSelecionadas);
		startFragmentAllowingStateLoss(fm, id, frag);
		return frag;
	}

	public static SurpresinhaFragment getSurpresinhaFragment() {
		return SurpresinhaFragment.newInstance();
	}
	public static SurpresinhaFragment2 getSurpresinhaFragment2() {
		return SurpresinhaFragment2.newInstance();
	}

	public static ApostasConfirmadasFragment getApostasConfirmadasFragment(boolean isHistorico, ConfigConsultaDTO configConsulta) {
		ApostasConfirmadasFragment fragment = ApostasConfirmadasFragment.newInstance(isHistorico, configConsulta);
		return fragment;
	}

	public static void startConfirmacaoChavePixResgate(FragmentManager fm, int id, DadosChavePixDTO dadosChavePix) {
		startFragmentAllowingStateLoss(fm, id, ConfirmacaoChavePixResgateFragment.newInstance(dadosChavePix));
	}
}
