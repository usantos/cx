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

	public static ComprasRepository getComprasService() {
		if (ServicoBffUtil.isServicoBff()) {
			return ComprasBffBO.getInstance();
		} else {
			return ComprasNuvemBO.getInstance();
		}
	}

	public static ComprasLegadoRepository getComprasLegadoService() {
		if (ServicoBffUtil.isServicoBff()) {
			return ComprasLegadoBffBO.getInstance();
		} else {
			return ComprasSilceBO.getInstance();
		}
	}

}
