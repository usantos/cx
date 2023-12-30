package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmBolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaBolaoEnum;
import br.gov.caixa.loterias.apostas.model.repository.BolaoRepository;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;

public class BolaoModel extends AppModel{

	private BolaoRepository bolaoRepository;
	private PaginacaoFiltro paginacaoFiltro;

	public BolaoModel(Activity activity) {
		super(activity);
		this.bolaoRepository = new BolaoRepository(activity);
	}

	public void buscaBoloes(OnSilceListener<ListaBolaoDTO> listener) {
		bolaoRepository.buscarBoloesDisponiveis(paginacaoFiltro, listener);
	}

	public List<CotasBolaoDTO> getCarrosselList(List<CotasBolaoDTO> listaCompleta) {
		if (listaCompleta != null && !listaCompleta.isEmpty() && listaCompleta.size() >= 4){
			return listaCompleta.subList(0, 4);
		}

		return listaCompleta;
	}

	public List<CotasBolaoDTO> getBolaoList(List<CotasBolaoDTO> listaCompleta) {

		if (listaCompleta != null && !listaCompleta.isEmpty() && listaCompleta.size() > 4){
			return listaCompleta.subList(4, listaCompleta.size());
		}

		return new ArrayList<>();
	}

	public void buscaFiltradaBoloes(FiltroAplicadoMarketplace filtroMarketplace, OnSilceListener<ListaBolaoDTO> listener) {
		bolaoRepository.buscarFiltrada(paginacaoFiltro, filtroMarketplace, listener);
	}

	public void buscaDadosUsuario(OnSilceListener<ApostadorDTO> listener) {
		DadosUsuarioBO_.getInstance_( getActivity() ).getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
			@Override
			public void onResponse(ApostadorDTOResponse response) {
				AppCenterManager.registraEvento("DADOS_USUARIO");
				checkRedirect(response);
				listener.success(response.getPayload());

			}

			@Override
			public void onErrorResponse(VolleyError error) {
				AppCenterManager.registraEventoErro("BOLOES_DADOS_USUARIO", error);
				RedirectNetwork.checkRedirect( error, getActivity() );
				listener.error(error);
			}
		} );
	}

	public void addBolaoCarrinho(CotasBolaoDTO bolao) {
		AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(getActivity(), ApostaUtils.converteBolaoEmAposta(bolao));
	}

	public void initPaginacaoFiltro(int idMunicipio, int idUF) {
		this.paginacaoFiltro = new PaginacaoFiltro(TipoConsultaBolaoEnum.MUNICIPIO, idMunicipio, idUF);
	}

	public void atualizaPaginacao(ListaBolaoDTO payload) {
		this.paginacaoFiltro.setQtdTotalPaginas(payload.getUltimaPagina());
		if (paginacaoFiltro.getPagina() < paginacaoFiltro.getQtdTotalPaginas()){
			paginacaoFiltro.setTemMaisRegistros(true);
			paginacaoFiltro.proxPagina();
		} else {
			paginacaoFiltro.setTemMaisRegistros(false);
			paginacaoFiltro.proxArea();
		}
	}

	public boolean isAcabouRegistros() {
		return paginacaoFiltro.acabouRegistros();
	}
}
