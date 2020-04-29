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

		List<String> modalidades = new ArrayList<>();
		modalidades.add("modalidades_item");
		List<String> situacoes = new ArrayList<>();
		situacoes.add("situacoes_item");

		expandableListDetail.put("Modalidades", modalidades);
		expandableListDetail.put("Situação da aposta", situacoes);
		return expandableListDetail;
	}
}
