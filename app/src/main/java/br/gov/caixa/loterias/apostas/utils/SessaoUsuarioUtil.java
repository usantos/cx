package br.gov.caixa.loterias.apostas.utils;

import java.util.Date;

import br.gov.caixa.loterias.apostas.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;

public class SessaoUsuarioUtil {

	public static void atualizaParametrosSingleton(ParametrosSimulacaoResponse parametros) {
		SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
		try {
			DadosUsuarioToken dadosUsuarioToken = Utils.decodedToken(sessaoUser.getToken());
			sessaoUser.setEmail(dadosUsuarioToken.getEmail());
			sessaoUser.setNome(dadosUsuarioToken.getName());
			sessaoUser.setDataUltimaRequisicao(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		sessaoUser.setValorMinimimoAposta(parametros.getPayload().getValorMinimoCarrinho());
		sessaoUser.setValorMaximoAposta(parametros.getPayload().getValorLimiteDiario());
		sessaoUser.setParametrosSimulacao(parametros.getPayload());
		SessaoUsuario.getInstance().setPrecisaMontarCarrossel(true);
	}

	public static boolean precisaAtualizar() {
		SessaoUsuario sessaoUser = SessaoUsuario.getInstance();

		if(sessaoUser.getDataUltimaRequisicao() == null){
			return true;
		}

		long seconds = DateUtils.getDiffSeconds(sessaoUser.getDataUltimaRequisicao());
		if(seconds >= SessaoUsuario.SECONDS){
			return true;
		}

		return false;
	}
}
