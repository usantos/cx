package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaBolaoEnum;
import br.gov.caixa.loterias.apostas.model.repository.BolaoRepository;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;

public class BolaoModel extends AppModel{

	private BolaoRepository bolaoRepository;
	private PaginacaoFiltro paginacaoFiltro;
	private int idMunicipio, idUf;
	private Integer idModalidade;
	private Integer idTipoConcurso;

	public BolaoModel(Activity activity) {
		super(activity);
		this.bolaoRepository = new BolaoRepository(activity);
	}

	public void buscaBoloes(OnSilceListener<ListaBolaoDTO> listener) {
		preparaPaginacao();
		bolaoRepository.buscarBoloesDisponiveis(paginacaoFiltro, null, listener);
	}

	private void preparaPaginacao() {
		if (paginacaoFiltro == null){
			initPaginacao();
		} else {
			proxConsulta();
		}
	}

	public void buscaFiltradaBoloes(FiltroAplicadoMarketplace filtroMarketplace, OnSilceListener<ListaBolaoDTO> listener) {
		preparaPaginacao();
		bolaoRepository.buscarBoloesDisponiveis(paginacaoFiltro, filtroMarketplace, listener);
	}

	public void zeraPaginacao(){
		paginacaoFiltro = null;
	}

	private void proxConsulta() {
		if (paginacaoFiltro.getPagina() < paginacaoFiltro.getQtdTotalPaginas()){
			paginacaoFiltro.proxPagina();
		} else {
			// configura proximo tipo de consulta
			paginacaoFiltro.proxArea();
		}
	}

	public List<CotasBolaoDTO> getCarrosselList(List<CotasBolaoDTO> listaCompleta) {
		if (listaCompleta != null && !listaCompleta.isEmpty() && listaCompleta.size() >= 4){
			return new ArrayList<>(listaCompleta.subList(0, 4));
		}

		return listaCompleta;
	}

	public List<CotasBolaoDTO> getBolaoList(List<CotasBolaoDTO> listaCompleta) {
		if (listaCompleta != null && !listaCompleta.isEmpty() && listaCompleta.size() > 4){
			return new ArrayList<>(listaCompleta.subList(4, listaCompleta.size()));
		}

		return new ArrayList<>();
	}

	public void buscaDadosUsuario(OnSilceListener<ApostadorDTO> listener) {
		DadosUsuarioBO.getInstance().getDadosUsuario(new RequestListener<ApostadorDTOResponse>() {
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

	public void incluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
		bolaoRepository.incluirLotericaFavorita(idLoterica, listener);
	}

	public void excluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
		bolaoRepository.excluirLotericaFavorita(idLoterica, listener);
	}

	public void addBolaoCarrinho(CotasBolaoDTO bolao) {
		AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(getActivity(), ApostaUtils.converteBolaoEmAposta(bolao));
	}

	public void setIdMunicipioUF(int idMunicipio, int idUF) {
		this.idMunicipio = idMunicipio;
		this.idUf = idUF;
	}

	public void setIdModalidade(Integer idModalidade) {
		this.idModalidade = idModalidade;
	}

	public void setIdTipoConcurso(Integer idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	private void initPaginacao(){
		this.paginacaoFiltro = new PaginacaoFiltro(TipoConsultaBolaoEnum.MUNICIPIO, idMunicipio, idUf, idModalidade, idTipoConcurso);
	}

	public void atualizaPaginacao(ListaBolaoDTO payload) {
		this.paginacaoFiltro.setQtdTotalPaginas(payload.getUltimaPagina());
		if ((paginacaoFiltro.getTipoConsulta() == TipoConsultaBolaoEnum.NACIONAL ||
				paginacaoFiltro.getTipoConsulta() == TipoConsultaBolaoEnum.LOTERICO) &&
				(payload.getPaginaAtual() == payload.getUltimaPagina() ||
						payload.getUltimaPagina() == 0 || payload.getCotas().isEmpty())){
			paginacaoFiltro.setAcabouRegistros(true);
		}
	}

	public boolean isAcabouRegistros() {
		return paginacaoFiltro != null && paginacaoFiltro.acabouRegistros();
	}
}
