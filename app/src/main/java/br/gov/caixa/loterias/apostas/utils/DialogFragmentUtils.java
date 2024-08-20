package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.math.BigDecimal;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.custom.AdicionarCarrinhoFavoritasView;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaBolaoDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CopiaColaPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.DetalheApostaBolaoLotecaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeusCartoesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.OrientacaoPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SubBarraModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheModalidadeDialogListener;

public class DialogFragmentUtils {

	public static void startEscolhaModalidade(FragmentManager fm, List<ParametroSimulacao> arrayConcursos,
											  Long idAposta, String string,boolean mostraTeimosinha, OnEscolheModalidadeDialogListener listener) {
		AdicionarCarrinhoFavoritasView view = AdicionarCarrinhoFavoritasView.newInstance();
		view.setArrayConcursos(arrayConcursos);
		view.setIdApostaFavorita(idAposta);
		view.setListener(listener);
		view.setMostraTeimosinha(mostraTeimosinha);
		view.show(fm, string);
	}

}
