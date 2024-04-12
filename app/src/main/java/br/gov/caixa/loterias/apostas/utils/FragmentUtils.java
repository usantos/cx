package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.math.BigDecimal;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaBolaoDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CopiaColaPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeusCartoesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.OrientacaoPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SubBarraModalidadeFragment;

public class FragmentUtils {

	public static void startFragment(FragmentManager fm, int id, Fragment fragment){
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commit();
	}

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, String TAG) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), TAG);
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, Boolean irCarrinho) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), irCarrinho);
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static MeusCartoesFragment meusCartoesFragment(FragmentManager fm, int id, List<RetornoCartao> cartoes, MeusCartoesConfig config) {
		MeusCartoesFragment fragment = MeusCartoesFragment.newInstance(cartoes, config);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static PaginacaoFragment startPaginacaoFragment(FragmentManager fm, PaginacaoFragment fragment, int id) {
		if(fragment == null){
			fragment = PaginacaoFragment.newInstance();
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static MeioPagamentoFragment startMeioPagamento(FragmentManager fm, int id, MeioPagamentoConfig config) {
		MeioPagamentoFragment fragment = MeioPagamentoFragment.newInstance(config);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CardPixFragment startCardPix(FragmentManager fm, int id, int tela) {
		CardPixFragment fragment = CardPixFragment.newOrientacaoInstance(tela);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoFragment startCartaoCredito(FragmentManager fm, int id, CartaoCreditoConfig config) {
		CartaoCreditoFragment fragment = CartaoCreditoFragment.newInstance(config);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CardPixFragment startCardOrientacaoPix(FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newOrientacaoInstance(CardPixFragment.TELA_ORIENTACAO);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoVersoFragment startCartaoCreditoVerso(FragmentManager fm, int id, String metodo, String cvv) {
		CartaoCreditoVersoFragment fragment = CartaoCreditoVersoFragment.newInstance(metodo,cvv);
		FragmentUtils.startFragment(fm, id,fragment);
		return fragment;
	}

	public static CardPixFragment startCardCopiaColaPix(GerarPixDTO pixDTO, FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newCopiaColaInstance(pixDTO, CardPixFragment.TELA_COPIA_COLA);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static OrientacaoPixFragment startOrientacaoPix(FragmentManager fm, int id) {
		OrientacaoPixFragment fragment = OrientacaoPixFragment.newInstance();
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CopiaColaPixFragment startCopiaColaPix(GerarPixDTO pixDTO, FragmentManager fm, int id) {
		CopiaColaPixFragment fragment = CopiaColaPixFragment.newInstance(pixDTO);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static ChavePixFragment startChavePix(FragmentManager fm, int id, ChavePixEnum chave) {
		ChavePixFragment fragment = ChavePixFragment.newInstance(chave);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static SubBarraModalidadeFragment startFragmentSubBarraModalidade(FragmentManager fm, int id, ModalidadeEnum modalidade,
																			 String concurso, String sorteio, boolean especial) {
		SubBarraModalidadeFragment frag = SubBarraModalidadeFragment.newInstance(modalidade, concurso, sorteio, especial);
		startFragment(fm, id, frag);
		return frag;
	}

	public static RodapeSimulacaoApostaFragment startRodapeSimulacaoAPosta(FragmentManager fm, int id, BigDecimal valorBolao, BigDecimal valorTotal) {
		RodapeSimulacaoApostaFragment frag = RodapeSimulacaoApostaFragment.newInstance(valorBolao, valorTotal);
		startFragment(fm, id, frag);
		return frag;
	}

	public static ApostaConfirmadaNumeroDetalhesFragment startApostaConfirmadaDetalhes(FragmentManager fm, int id,
																					   IdentificaoDeUmaApostaDas8Modalidades aposta,
																					   ResultadoConcursoDTO resultado,
																					   ComprovanteApostaDTO comprovante,
																					   BigDecimal valorPremio) {
		ApostaConfirmadaNumeroDetalhesFragment fragment = ApostaConfirmadaNumeroDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static ApostaConfirmadaBolaoDetalhesFragment startApostaConfirmadaBolaoDetalhes(FragmentManager fm, int id,
																						   IdentificaoDeUmaApostaDas8Modalidades aposta,
																						   ResultadoConcursoDTO resultado,
																						   ComprovanteApostaDTO comprovante,
																						   BigDecimal valorPremio) {
		ApostaConfirmadaBolaoDetalhesFragment fragment = ApostaConfirmadaBolaoDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static ApostaConfirmadaPartidaDetalhesFragment startApostaConfirmadaPartidaDetalhes(FragmentManager fm, int id,
																							  IdentificaoDeUmaApostaDas8Modalidades aposta,
																					   ResultadoConcursoDTO resultado,
																					   ComprovanteApostaDTO comprovante,
																					   BigDecimal valorPremio, boolean isToteca) {
		ApostaConfirmadaPartidaDetalhesFragment fragment = ApostaConfirmadaPartidaDetalhesFragment
				.newInstance(aposta, resultado, comprovante, valorPremio, isToteca);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static RodapeSimulacaoApostaFragment startRodapeSimulacaoAPosta(FragmentManager fm, int id, BigDecimal valorBolao) {
		RodapeSimulacaoApostaFragment frag = RodapeSimulacaoApostaFragment.newInstance(valorBolao);
		startFragment(fm, id, frag);
		return frag;
	}

	public static FiltroVazioFragment startFiltroVazio(FragmentManager fm, int idFrag) {
		FiltroVazioFragment frag = FiltroVazioFragment.newInstance();
		startFragment(fm, idFrag, frag);
		return frag;
	}
}
