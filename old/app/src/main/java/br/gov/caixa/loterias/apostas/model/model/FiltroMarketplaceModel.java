package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

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
												   Integer qtdMin, Integer qtdMax, Integer qtdApostas, Integer qtdDezenas, String numerosQuero,
													   String numerosNaoQuero, ModalidadeEnum selectModalidade,
													   TipoConcursoEnum selectTipoConcurso, boolean todasModalidadesAtiva) {
		FiltroAplicadoMarketplace filtro = new FiltroAplicadoMarketplace(
				selectLotericaDTO,
				valorMin.replace(".","").replace(",","."),
				valorMax.replace(".","").replace(",","."),
				qtdMin, qtdMax, qtdApostas, qtdDezenas, numerosQuero, numerosNaoQuero, selectModalidade, selectTipoConcurso);
		filtro.setTodasModalidades(todasModalidadesAtiva);
		return filtro;
	}

	public enum PrognosticosMaximosQueroNaoQuero{
		MEGA_SENA(60),
		LOTOFACIL(25),
		QUINA(80),
		MAIS_MILIONARIA(50),
		DUPLA_SENA(50),
		TIMEMANIA(80),
		DIA_DE_SORTE(31);

		private final int prognosticosMaximo;

		PrognosticosMaximosQueroNaoQuero(int prognosticosMaximo) {
			this.prognosticosMaximo = prognosticosMaximo;
		}

		public int getPrognosticosMaximo() {
			return prognosticosMaximo;
		}
	}

	public int getPrognosticosMaximoQueroNaoQuero(ModalidadeEnum modalidade) {
		switch (modalidade) {
			case MEGA_SENA:
				return PrognosticosMaximosQueroNaoQuero.MEGA_SENA.getPrognosticosMaximo();
			case LOTOFACIL:
				return PrognosticosMaximosQueroNaoQuero.LOTOFACIL.getPrognosticosMaximo();
			case QUINA:
				return PrognosticosMaximosQueroNaoQuero.QUINA.getPrognosticosMaximo();
			case MAIS_MILIONARIA:
				return PrognosticosMaximosQueroNaoQuero.MAIS_MILIONARIA.getPrognosticosMaximo();
			case DUPLA_SENA:
				return PrognosticosMaximosQueroNaoQuero.DUPLA_SENA.getPrognosticosMaximo();
			case TIMEMANIA:
				return PrognosticosMaximosQueroNaoQuero.TIMEMANIA.getPrognosticosMaximo();
			case DIA_DE_SORTE:
				return PrognosticosMaximosQueroNaoQuero.DIA_DE_SORTE.getPrognosticosMaximo();
			default:
				return 80;
		}
	}

	public List<Integer> getListaQtdDezenas(ModalidadeEnum modalidade){

		switch (modalidade) {
			case MEGA_SENA:	return geraIntervalo(6, 20);
			case LOTOFACIL: return geraIntervalo(15, 20);
			case QUINA: return geraIntervalo(5, 15);
			case MAIS_MILIONARIA: return geraIntervalo(6, 12);
			case DUPLA_SENA: return geraIntervalo(6, 15);
			case DIA_DE_SORTE: return geraIntervalo(7, 15);
			case SUPER_7: return geraIntervalo(7, 21);
			case TIMEMANIA: return Collections.singletonList(10);
			case LOTECA: return geraIntervalo(17, 26);
			default: return geraIntervalo(5, 25);
		}
	}

	public List<Integer> getListaTodasModalidades(){
		return geraIntervalo(5, 26);
	}

	public List<Integer> getIntervalo(int inicio, int fim) {
		if (inicio <= 0 || fim < inicio) return new ArrayList<>();
		return geraIntervalo(inicio, fim);
	}

	private List<Integer> geraIntervalo(int start, int end) {
		List<Integer> lista = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			lista.add(i);
		}
		return lista;
	}


}
