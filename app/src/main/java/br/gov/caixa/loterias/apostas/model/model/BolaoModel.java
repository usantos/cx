package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.repository.BolaoRepository;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class BolaoModel extends AppModel{

	private BolaoRepository bolaoRepository;

	public BolaoModel(Activity activity) {
		super(activity);

		this.bolaoRepository = new BolaoRepository(activity);
	}

	public void buscaBoloes(int idMunicipio, int idUf, OnSilceListener<ListaBolaoDTO> listener) {
		bolaoRepository.buscarBoloesDisponiveis(idMunicipio, idUf, listener);
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

	public void buscaFiltradaBoloes(int idMunicipio, int idUf, FiltroAplicadoMarketplace filtroMarketplace, OnSilceListener<ListaBolaoDTO> listener) {
		bolaoRepository.buscarFiltrada(idMunicipio, idUf, filtroMarketplace, listener);
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
}
