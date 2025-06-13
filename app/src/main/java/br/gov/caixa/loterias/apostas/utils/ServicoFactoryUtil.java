package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaRepository;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativoRepository;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioRepository;

public class ServicoFactoryUtil {

	public static ApostaRepository getApostaService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return ApostaNuvemBO.getInstance();
		} else {
			return ApostaSilceBO.getInstance();
		}
	}

	public static DadosCorporativoRepository getDadosCorporativoService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return DadosCorporativosNuvemBO.getInstance();
		} else {
			return DadosCorporativosSilceBO.getInstance();
		}
	}

	public static DadosUsuarioRepository getDadosUsuarioService() {
		if (ServicoNuvemUtil.isServicoNuvem()) {
			return DadosUsuarioNuvemBO.getInstance();
		} else {
			return DadosUsuarioBO.getInstance();
		}
	}

}
