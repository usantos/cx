package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaService;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativoService;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosNuvemBO;

public class ServicoFactoryUtil {

	public static ApostaService getApostaService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return ApostaNuvemBO.getInstance();
		} else {
			return ApostaSilceBO.getInstance();
		}
	}

	public static DadosCorporativoService getDadosCorporativoService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return DadosCorporativosNuvemBO.getInstance();
		} else {
			return DadosCorporativosSilceBO.getInstance();
		}
	}

}
