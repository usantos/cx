package br.gov.caixa.loterias.apostas.mock;

import android.util.Log;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;

public class MaisMilionariaMock {

	private static final String STING_JSON_MAIS_MILIONARIA = "{"+
			"\"parametroJogo\": {" +
		"\"concurso\": {" +
			"\"modalidade\": \"MAIS_MILIONARIA\", " +
			"\"modalidadeDetalhada\": {" +
		"\"valor\": 9," +
				"\"descricao\": \"Mais Milionária\"," +
				"\"descricaoEspecial\": " +
			"},"+
			"\"tipoConcurso\": \"NORMAL\"," +
			"\"numero\": 535, "+
			"\"dataFechamento\": \"18/02/2022 16:00:00\","+
			"\"dataAbertura\": \"16/02/2022 16:15:00\","+
			"\"dataHoraSorteio\": \"18/02/2022 16:15:00\","+
			"\"dataSorteio\": \"18 fev 2022\","+
			"\"valorApostaMinima\": 6.00,"+
			"\"situacao\": \"ABERTO\","+
			"\"bloqueado\": false,"+
			"\"estimativa\": 10000000.00,"+
			"\"naoInicializado\": false,"+
			"\"aberto\": true"+
		"},"+
		"\"situacaoConcursoCanal\": \"ABERTO\","+
			"\"probabilidadeDeGanhar\": 2,"+
			"\"aceitaEspelho\": false,"+
			"\"prognosticoMaximo\": 50,"+
			"\"quantidadeMinima\": 6,"+
			"\"quantidadeMaxima\": 15,"+
			"\"teimosinhas\": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],"+
			"\"quantidadeSurpresinhas\": 10,"+
			"\"valoresAposta\": ["+
		"{"+
			"\"valor\": 6.00,"+
			"\"numeroPrognosticos\": 6,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 18.00,"+
			"\"numeroPrognosticos\": 6,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 36.00,"+
			"\"numeroPrognosticos\": 6,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 42.00,"+
			"\"numeroPrognosticos\": 7,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 60.00,"+
			"\"numeroPrognosticos\": 6,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 90.00,"+
			"\"numeroPrognosticos\": 6,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 126.00,"+
			"\"numeroPrognosticos\": 7,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 168.00,"+
			"\"numeroPrognosticos\": 8,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 252.00,"+
			"\"numeroPrognosticos\": 7,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 420.00,"+
			"\"numeroPrognosticos\": 7,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 504.00,"+
			"\"numeroPrognosticos\": 8,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 504.00,"+
			"\"numeroPrognosticos\": 9,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 630.00,"+
			"\"numeroPrognosticos\": 7,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 1008.00,"+
			"\"numeroPrognosticos\": 8,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 1260.00,"+
			"\"numeroPrognosticos\": 10,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 1512.00,"+
			"\"numeroPrognosticos\": 9,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 1680.00,"+
			"\"numeroPrognosticos\": 8,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 2520.00,"+
			"\"numeroPrognosticos\": 8,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 2772.00,"+
			"\"numeroPrognosticos\": 11,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 3024.00,"+
			"\"numeroPrognosticos\": 9,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 3780.00,"+
			"\"numeroPrognosticos\": 10,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 5040.00,"+
			"\"numeroPrognosticos\": 9,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 5544.00,"+
			"\"numeroPrognosticos\": 12,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 7560.00,"+
			"\"numeroPrognosticos\": 9,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 7560.00,"+
			"\"numeroPrognosticos\": 10,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 8316.00,"+
			"\"numeroPrognosticos\": 11,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 10296.00,"+
			"\"numeroPrognosticos\": 13,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 12600.00,"+
			"\"numeroPrognosticos\": 10,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 16632.00,"+
			"\"numeroPrognosticos\": 11,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 16632.00,"+
			"\"numeroPrognosticos\": 12,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 18018.00,"+
			"\"numeroPrognosticos\": 14,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 18900.00,"+
			"\"numeroPrognosticos\": 10,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 27720.00,"+
			"\"numeroPrognosticos\": 11,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 30030.00,"+
			"\"numeroPrognosticos\": 15,"+
			"\"numeroTrevos\": 2"+
		"},"+
		"{"+
			"\"valor\": 30888.00,"+
			"\"numeroPrognosticos\": 13,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 33264.00,"+
			"\"numeroPrognosticos\": 12,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 41580.00,"+
			"\"numeroPrognosticos\": 11,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 54054.00,"+
			"\"numeroPrognosticos\": 14,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 55440.00,"+
			"\"numeroPrognosticos\": 12,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 61776.00,"+
			"\"numeroPrognosticos\": 13,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 83160.00,"+
			"\"numeroPrognosticos\": 12,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 90090.00,"+
			"\"numeroPrognosticos\": 15,"+
			"\"numeroTrevos\": 3"+
		"},"+
		"{"+
			"\"valor\": 102960.00,"+
			"\"numeroPrognosticos\": 13,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 108108.00,"+
			"\"numeroPrognosticos\": 14,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 154440.00,"+
			"\"numeroPrognosticos\": 13,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 180180.00,"+
			"\"numeroPrognosticos\": 14,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 180180.00,"+
			"\"numeroPrognosticos\": 15,"+
			"\"numeroTrevos\": 4"+
		"},"+
		"{"+
			"\"valor\": 270270.00,"+
			"\"numeroPrognosticos\": 14,"+
			"\"numeroTrevos\": 6"+
		"},"+
		"{"+
			"\"valor\": 300300.00,"+
			"\"numeroPrognosticos\": 15,"+
			"\"numeroTrevos\": 5"+
		"},"+
		"{"+
			"\"valor\": 450450.00,"+
			"\"numeroPrognosticos\": 15,"+
			"\"numeroTrevos\": 6"+
		"}"+
                    "],"+
			"\"trevos\": {"+
		"\"prognosticoMaximoTrevo\": 6,"+
				"\"quantidadeMinimaTrevo\": 2,"+
				"\"quantidadeMaximaTrevo\": 6"+
		"},"+
		"\"concursoCanalAberto\": true,"+
				"\"concursoDisponivelSimulacao\": true"+
	"}}";

	public static ParametroSimulacao getParametroMaisMilionaria(){
		ParametroSimulacao parametroSimulacao = null;

		try {
			parametroSimulacao = new Gson().fromJson(STING_JSON_MAIS_MILIONARIA, ParametroSimulacao.class);
		}catch (Exception e){
			Log.d("+MILIONARIA_ERRO", e.getLocalizedMessage());
		}

		return parametroSimulacao;
	}
}
