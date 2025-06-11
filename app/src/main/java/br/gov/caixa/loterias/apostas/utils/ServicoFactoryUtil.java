package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaService;

public class ServicoFactoryUtil {

	public static ApostaService getApostaService() {
		ApostaService apostaService;
		if (ServicoNuvemUtil.isServicoNuvem()) {
			apostaService = ApostaNuvemBO.getInstance();
		} else {
			apostaService = ApostaBO.getInstance();
		}
		return apostaService;
	}

}
