package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class ApostaConfirmadaDetalheModel extends AppModel {
	private ApostaSilceBO apostaSilceBO;

	public ApostaConfirmadaDetalheModel(Activity activity) {
		super(activity);
		this.apostaSilceBO = ApostaSilceBO.getInstance();
	}

	public  void buscaDetalhePremio(Long id, OnSilceListener<DetalhesPremioDTO> listener){
		apostaSilceBO.getApostaDetalhePremio(id, new RequestListener<DetalhesPremioDTOResponse>() {
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
				R.color.branco, R.color.milionaria_escuro_mkp);
	}

	public ConfiguracaoNumeros configuraEstiloNumeros(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(aposta.getModalidade());
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA) {
			return new ConfiguracaoNumeros(R.color.branco, estilo.getCorFonteFundoClaro());
		} else {
			return new ConfiguracaoNumeros(estilo.getCorEscura(), R.color.branco);
		}
	}

	public ConfiguracaoNumerosSuperSete configuraEstiloSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(aposta.getModalidade());

		return new ConfiguracaoNumerosSuperSete(
				estilo.getCorClara(),			//corInteriorCirculo
				estilo.getCorFonteFundoClaro(),	//corInteriorSelecionado
				estilo.getCorFonteFundoClaro(),	//corTextoCirculo
				R.color.branco,					//corTextoCirculoSelecionado
				estilo.getCorFonteFundoClaro(),	//corBordaCirculo
				R.color.super_sete_claro_mkp,	//corBordaCirculoSelecionado
				estilo.getCorEscura(),			//corInteriorQuadrado
				R.color.branco,					//corTextoQuadrado
				estilo.getCorEscura());	//corBordaQuadrado
	}

}
