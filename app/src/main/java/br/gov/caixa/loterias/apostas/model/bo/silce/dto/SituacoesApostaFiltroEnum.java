package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.util.LinkedHashMap;

public class SituacoesApostaFiltroEnum {
	public  static final int
			TODAS = 1,
			CONFERIDAS = 2,
			NAO_CONFERIDAS = 3,
			PAGAS = 4,
			NAO_PREMIADAS = 5,
			PRESCRITAS = 6;

//	public static LinkedHashMap<Integer,String> getSituacoes(){
//		LinkedHashMap<Integer,String> mapSituacoes = new LinkedHashMap<>();
//		mapSituacoes.put(new Integer(TODAS),getDescricao(TODAS));
//		//mapSituacoes.put(new Integer(CONFERIDAS),getDescricao(CONFERIDAS));
//		//mapSituacoes.put(new Integer(NAO_CONFERIDAS),getDescricao(NAO_CONFERIDAS));
//		mapSituacoes.put(new Integer(PAGAS),getDescricao(PAGAS));
//		//mapSituacoes.put(new Integer(NAO_PREMIADAS),getDescricao(NAO_PREMIADAS));
//		mapSituacoes.put(new Integer(PRESCRITAS),getDescricao(PRESCRITAS));
//
//		return mapSituacoes;
//	}

//	public static LinkedHashMap<Integer,String> getSituacoesMicroServico(){
//		LinkedHashMap<Integer,String> mapSituacoes = new LinkedHashMap<>();
//		mapSituacoes.put(new Integer(TODAS),getDescricao(TODAS));
//		mapSituacoes.put(new Integer(PAGAS),getDescricao(PAGAS));
//		mapSituacoes.put(new Integer(PRESCRITAS),getDescricao(PRESCRITAS));
//
//		return mapSituacoes;
//	}


	public static String getDescricao(int situacao){
		String descricao = "";
		switch (situacao) {
			case TODAS:
				descricao = "Todas";
				break;
			case CONFERIDAS:
				descricao = "Conferidas";
				break;
			case NAO_CONFERIDAS:
				descricao = "Não conferidas";
				break;
			case PAGAS:
				descricao = "Pagas";
				break;
			case NAO_PREMIADAS:
				descricao = "Não premiadas";
				break;
			case PRESCRITAS:
				descricao = "Prescritas";
				break;
		}
		return descricao;
	}
}
