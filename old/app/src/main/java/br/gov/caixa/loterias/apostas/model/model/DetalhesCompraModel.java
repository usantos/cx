package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOIdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;

public class DetalhesCompraModel extends AppModel{

	public DetalhesCompraModel(Activity activity) {
		super(activity);
	}

	public void realizaReaposta(ArrayList<ApostaDTO> listaAposta, OnSilceListener<AgrupadorDTOIdentificaoDeUmaApostaDas8Modalidades> listener) {
		ApostaSilceBO.getInstance().adicionarApostasNoCarrinho(getApostas(listaAposta), new RequestListener<AgrupadorDTOApostaDTOResponse>() {
			@Override
			public void onResponse(AgrupadorDTOApostaDTOResponse result) {
				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				error.toString();
			}
		});
	}

	private List<IdentificaoDeUmaApostaDas8Modalidades> getApostas(ArrayList<ApostaDTO> listaAposta) {
		List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
		for (ApostaDTO apostaDTO: listaAposta){
			apostas.add(ApostaUtils.convertApostaDTOemIdentificaoDeuUmaPostaDas8Modadlidades(apostaDTO));
		}

		return apostas;
	}
}
