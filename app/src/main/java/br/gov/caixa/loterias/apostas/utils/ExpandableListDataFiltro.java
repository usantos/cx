package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListDataFiltro {
	public static HashMap<String, List<String>> getData() {
		LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

		List<String> modalidades = new ArrayList<>();
		modalidades.add("modalidades_item");
		List<String> situacoes = new ArrayList<>();
		situacoes.add("situacoes_item");
		List<String> concurso = new ArrayList<>();
		concurso.add("concurso_item");

		expandableListDetail.put("Modalidades", modalidades);
		expandableListDetail.put("Situação da aposta", situacoes);
		expandableListDetail.put("Concurso", concurso);
		return expandableListDetail;
	}

	public static HashMap<String, List<String>> getDataSemConcurso() {
		LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

		List<String> tipoApostas = new ArrayList<>();
		tipoApostas.add("tipo_apostas");
		List<String> modalidades = new ArrayList<>();
		modalidades.add("modalidades_item");
		List<String> tipoConcursos = new ArrayList<>();
		tipoConcursos.add("tipo_concursos");
		List<String> situacoes = new ArrayList<>();
		situacoes.add("situacoes_item");
		List<String> ordenarPorOpcoes = new ArrayList<>();
		ordenarPorOpcoes.add("ordenar_por_item");

		expandableListDetail.put("Tipo de aposta", tipoApostas);
		expandableListDetail.put("Modalidades", modalidades);
		expandableListDetail.put("Tipo de concurso", tipoConcursos);
		expandableListDetail.put("Situação da aposta", situacoes);
		expandableListDetail.put("Ordenar por", ordenarPorOpcoes);
		return expandableListDetail;
	}

	public static HashMap<String, List<String>> getDataFiltroCompras() {
		LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

		List<String> situacoesCompra = new ArrayList<>();
		situacoesCompra.add("situacoes_item");
		List<String> meiosDePagamento = new ArrayList<>();
		meiosDePagamento.add("meios_de_pagamento_item");

		expandableListDetail.put("Meio de pagamento", meiosDePagamento);
		expandableListDetail.put("Situação da compra", situacoesCompra);
		return expandableListDetail;
	}

}
