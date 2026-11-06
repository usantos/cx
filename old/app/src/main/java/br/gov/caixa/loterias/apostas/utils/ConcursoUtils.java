package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class ConcursoUtils {

	public static boolean concursoApurado(ModalidadeEnum modalidade, Integer numeroConcursoAposta) {
		ParametroSimulacao parametroJogo = getParametroPorJogo(modalidade, TipoConcursoEnum.NORMAL);
		if (parametroJogo == null) {
			parametroJogo = getParametroPorJogo(modalidade, TipoConcursoEnum.ESPECIAL);
		}
		boolean precisaBuscar = false;

		if (parametroJogo != null && parametroJogo.getParametroJogo() != null){
			ConcursoDTO concursoAtual         = parametroJogo.getParametroJogo().getConcurso();

			if (concursoAtual != null && numeroConcursoAposta < concursoAtual.getNumero()){
				if(numeroConcursoAposta + 1 == concursoAtual.getNumero()){
					if (concursoAtual.getAberto() != null && concursoAtual.getAberto()){
						precisaBuscar = true;
					} else {
						precisaBuscar = false;
					}
				}else {
					precisaBuscar = true;
				}
			}
		}

		return precisaBuscar;
	}

	private static ParametroSimulacao getParametroPorJogo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso) {
		ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
		ParametroSimulacao parametroSimulacao = null;
		if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null){
			for (ParametroSimulacao parametro : parametrosSimulacao.getParametros()){
				if(modalidade == parametro.getParametroJogo().getConcurso().getModalidade() && parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso){
					parametroSimulacao = parametro;
					break;
				}
			}
		}

		return parametroSimulacao;
	}
}
