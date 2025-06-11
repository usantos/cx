package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaService;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativoService;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosNuvemBO;

public class ServicoFactoryUtil {

	public static ApostaService getApostaService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return ApostaNuvemBO.getInstance();
		} else {
			return ApostaBO.getInstance();
		}
	}

	public static DadosCorporativoService getDadosCorporativoService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return DadosCorporativosNuvemBO.getInstance();
		} else {
			return DadosCorporativosBO.getInstance();
		}
	}

}
