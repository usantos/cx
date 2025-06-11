package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
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
			if (item.getNomeFantasia().toLowerCase().contains(text.toLowerCase())) {
				filteredList.add(item);
			}
		}
		return filteredList;
	}

	public void buscaLotericas(String text, OnSilceListener<List<LotericaDTO>> listener) {
		DadosCorporativosSilceBO.getInstance().buscarLoteriasNomeCodigo(text, new RequestListener<LotericaDTOResponse>() {
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

	public FiltroAplicadoMarketplace getFiltroAplicado(LotericaDTO selectLotericaDTO, String valorMin, String valorMax,
													   Integer qtdMin, Integer qtdMax, Integer qtdDezenas, String numerosQuero,
													   String numerosNaoQuero, ModalidadeEnum selectModalidade,
													   TipoConcursoEnum selectTipoConcurso) {
		return new FiltroAplicadoMarketplace(
				selectLotericaDTO,
				valorMin.replace(".","").replace(",","."),
				valorMax.replace(".","").replace(",","."),
				qtdMin, qtdMax, qtdDezenas, numerosQuero, numerosNaoQuero, selectModalidade, selectTipoConcurso);
	}
}
