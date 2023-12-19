package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class ApostaConfirmadaDetalheModel extends AppModel {
	private ApostaBO apostaBO;

	public ApostaConfirmadaDetalheModel(Activity activity) {
		super(activity);
		this.apostaBO = ApostaBO_.getInstance_(getActivity());
	}

	public  void buscaDetalhePremio(Long id, OnSilceListener<DetalhesPremioDTO> listener){
		apostaBO.getApostaDetalhePremio(id, new RequestListener<DetalhesPremioDTOResponse>() {
			@Override
			public void onResponse(DetalhesPremioDTOResponse result) {
				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}

	public ShapeConfig getTrevoShape() {
		return new ShapeConfig(R.drawable.ic_item_trevo,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.mais_milionaria_escura);
	}

	public ConfiguracaoNumeros configuraEstiloNumeros(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());
		if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
			return new ConfiguracaoNumeros(estilo.getCorClara(), R.color.branco);
		} else {
			return new ConfiguracaoNumeros(estilo.getCorEscura(), R.color.branco);
		}
	}

	public ConfiguracaoNumerosSuperSete configuraEstiloSuperSete() {
		return new ConfiguracaoNumerosSuperSete(R.color.supersetemedio,
				R.color.branco,
				R.color.branco,
				R.color.supersetemedio,
				R.color.branco,
				R.color.supersetemedio,
				R.color.supersetemedio,
				R.color.superseteescuro,
				R.color.supersetemedio);
	}


}
