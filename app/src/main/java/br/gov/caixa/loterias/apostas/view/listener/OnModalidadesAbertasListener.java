package br.gov.caixa.loterias.apostas.view.listener;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public interface OnModalidadesAbertasListener {

	void ambasAbertas(List<ParametroSimulacao> arrayConcursos);
	void ambasFechadas();
	void unicaModalidade(List<ParametroSimulacao> arrayConcursos, TipoConcursoEnum tipoConcursoEnum);
}
