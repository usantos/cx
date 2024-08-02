package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class FiltroMarketplaceModel extends AppModel{
	public FiltroMarketplaceModel(Activity activity) {
		super(activity);
	}

	public List<LotericaDTO> getListaFiltrada(String text, List<LotericaDTO> listLotericaDTO) {
		List<LotericaDTO> filteredList = new ArrayList<>();
		for (LotericaDTO item : listLotericaDTO) {
			if (item.getNome().toLowerCase().contains(text.toLowerCase())) {
				filteredList.add(item);
			}
		}
		return filteredList;
	}

	public void buscaLotericas(String text, OnSilceListener<List<LotericaDTO>> listener) {
		DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(getActivity());
		dadosCorporativosBO.buscarLoteriasNomeCodigo(text, new RequestListener<LotericaDTOResponse>() {
			@Override
			public void onResponse(LotericaDTOResponse response) {
				if (response.getPayload() == null){
					listener.success(new ArrayList<>());
				} else {
					listener.success(response.getPayload());
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				listener.error(error);
			}
		});

	}

	public FiltroAplicadoMarketplace getFiltroAplicado(String valorMin, String valorMax,
													   Float qtdMin, Float qtdMax,
													   LotericaDTO selectLotericaDTO,
													   ModalidadeEnum selectModalidade,
													   TipoConcursoEnum selectTipoConcurso) {
		return new FiltroAplicadoMarketplace(
				valorMin.replace(".","").replace(",","."),
				valorMax.replace(".","").replace(",","."),
				qtdMin, qtdMax, selectLotericaDTO, selectModalidade, selectTipoConcurso);
	}
}
