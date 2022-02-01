package br.gov.caixa.loterias.apostas.mock;

import android.util.Log;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;

public class MaisMilhonariaMock {

	private static final String STING_JSON_MAIS_MILHONARIA = "{"+
			"\"parametroJogo\": {"+
		"\"concurso\": {"+
			"\"modalidade\": \"MAIS_MILHONARIA\","+
			"\"modalidadeDetalhada\": {"+
				"\"valor\": 2,"+
			    "\"descricao\": \"Mais-Milhonaria\","+
				"\"descricaoEspecial\": \"+ Milhonaria\""+
			"},"+
			"\"tipoConcurso\": \"NORMAL\","+
					"\"numero\": 0001,"+
					"\"dataFechamento\": \"04/02/2022 16:50:00\","+
					"\"dataAbertura\": \"01/02/2022 20:00:00\","+
					"\"dataHoraSorteio\": \"04/02/2022 18:00:00\","+
					"\"dataSorteio\": \"04 fev 2022\","+
					"\"valorApostaMinima\": 4.50,"+
					"\"situacao\": \"ABERTO\","+
					"\"bloqueado\": false,"+
					"\"dataFinalBloqueio\": \"03/04/2018 15:00:00\","+
					"\"estimativa\": 10000000.00,"+
					"\"naoInicializado\": false,"+
					"\"aberto\": true"+
		"},"+
		"\"situacaoConcursoCanal\": \"ABERTO\","+
			"\"probabilidadeDeGanhar\": 2,"+
			"\"aceitaEspelho\": false,"+
			"\"prognosticoMaximo\": 60,"+
			"\"quantidadeMinima\": 6,"+
			"\"quantidadeMaxima\": 15,"+
			"\"teimosinhas\": [0,2,4,8],"+
		"\"valoresAposta\": ["+
			"{"+
				"\"valor\": 4.50,"+
				"\"numeroPrognosticos\": 6"+
			"},"+
			"{"+
				"\"valor\": 31.50,"+
				"\"numeroPrognosticos\": 7"+
			"},"+
			"{"+
				"\"valor\": 126.00,"+
				"\"numeroPrognosticos\": 8"+
			"},"+
			"{"+
				"\"valor\": 378.00,"+
				"\"numeroPrognosticos\": 9"+
			"},"+
			"{"+
				"\"valor\": 945.00,"+
				"\"numeroPrognosticos\": 10"+
			"},"+
			"{"+
				"\"valor\": 2079.00,"+
				"\"numeroPrognosticos\": 11"+
			"},"+
			"{"+
				"\"valor\": 4158.00,"+
				"\"numeroPrognosticos\": 12"+
			"},"+
			"{"+
				"\"valor\": 7722.00,"+
				"\"numeroPrognosticos\": 13"+
			"},"+
			"{"+
				"\"valor\": 13513.50,"+
				"\"numeroPrognosticos\": 14"+
			"},"+
			"{"+
				"\"valor\": 22522.50,"+
				"\"numeroPrognosticos\": 15"+
		"}"+
	"],"+
			"\"concursoDisponivelSimulacao\": true,"+
			"\"concursoCanalAberto\": true"+
	"}"+
"}";


	public static ParametroSimulacao getParametroMaisMilhonaria(){
		ParametroSimulacao parametroSimulacao = null;

		try {
			parametroSimulacao = new Gson().fromJson(STING_JSON_MAIS_MILHONARIA, ParametroSimulacao.class);
		}catch (Exception e){
			Log.d("+MILHONARIA_ERRO", e.getLocalizedMessage());
		}

		return parametroSimulacao;
	}
}
