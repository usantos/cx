package br.gov.caixa.loterias.apostas.utils;
import br.gov.caixa.loterias.apostas.model.bo.*;


public class ServicoFactoryUtil {

	public static ApostaRepository getApostaService() {
		return ApostaNuvemBO.getInstance();
	}

	public static DadosCorporativoRepository getDadosCorporativoService() {
		return DadosCorporativosNuvemBO.getInstance();
	}

	public static DadosUsuarioRepository getDadosUsuarioService() {
		if(ServicoApostadorUtil.isServicoApostador()){
			return DadosUsuarioApostadorBO.getInstance();
		} else {
			return DadosUsuarioNuvemBO.getInstance();
		}
	}

}
