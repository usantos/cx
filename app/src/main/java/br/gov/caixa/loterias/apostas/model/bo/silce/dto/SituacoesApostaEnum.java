package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.util.LinkedHashMap;

public class SituacoesApostaEnum {
	public  static final int
			TODAS = 1,
			PREMIADA = 2,
			NAO_PREMIADA = 3,
			NAO_PREMIADA_OU_NAO_APURADA = 4,
			PREMIO_PAGO = 5,
			EFETIVADA = 6,
			EM_PROCESSAMENTO = 7,
			PRESCRITO = 8;

	public static LinkedHashMap<Integer,String> getSituacoes(){
		LinkedHashMap<Integer,String> mapSituacoes = new LinkedHashMap<>();
		mapSituacoes.put(new Integer(TODAS),getDescricao(TODAS));
		mapSituacoes.put(new Integer(PREMIADA),getDescricao(PREMIADA));
		mapSituacoes.put(new Integer(NAO_PREMIADA),getDescricao(NAO_PREMIADA));
		mapSituacoes.put(new Integer(NAO_PREMIADA_OU_NAO_APURADA),getDescricao(NAO_PREMIADA_OU_NAO_APURADA));
		mapSituacoes.put(new Integer(PREMIO_PAGO),getDescricao(PREMIO_PAGO));
		mapSituacoes.put(new Integer(EFETIVADA),getDescricao(EFETIVADA));
		mapSituacoes.put(new Integer(EM_PROCESSAMENTO),getDescricao(EM_PROCESSAMENTO));
		mapSituacoes.put(new Integer(PRESCRITO),getDescricao(PRESCRITO));

		return mapSituacoes;
	}

	public static String getDescricao(int situacao){
		String descricao = "";
		switch (situacao) {
			case TODAS:
				descricao = "Todas";
				break;
			case EM_PROCESSAMENTO:
				descricao = "Em processamento";
				break;
			case PREMIADA:
				descricao = "Premiada";
				break;
			case NAO_PREMIADA:
				descricao = "Não premiada";
				break;
			case PREMIO_PAGO:
				descricao = "Prêmio pago";
				break;
			case NAO_PREMIADA_OU_NAO_APURADA:
				descricao = "Não premiada ou nao apurada";
				break;
			case PRESCRITO:
				descricao = "Prescrito";
				break;
			case EFETIVADA:
				descricao = "Efetivada";
				break;
		}
		return descricao;
	}
}
