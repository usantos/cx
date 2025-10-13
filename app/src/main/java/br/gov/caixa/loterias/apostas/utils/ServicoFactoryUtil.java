package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.*;

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
		if(ServicoApostadorUtil.isServicoApostador()){
			return DadosUsuarioApostadorBO.getInstance();
		} else if (ServicoNuvemUtil.isServicoNuvem()) {
			return DadosUsuarioNuvemBO.getInstance();
		} else {
			return DadosUsuarioBO.getInstance();
		}
	}

}
