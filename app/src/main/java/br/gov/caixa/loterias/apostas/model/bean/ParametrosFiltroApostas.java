package br.gov.caixa.loterias.apostas.model.bean;

import java.util.ArrayList;

public class ParametrosFiltroApostas {
	private Integer numConcurso;
	private ArrayList<String> listaModalidades;
	private String situacao;

	 public ParametrosFiltroApostas(ArrayList<String> listaModalidades, String situacao,Integer numConcurso){
		this.numConcurso = numConcurso;
		this.listaModalidades = listaModalidades;
		this.situacao = situacao;
	}
	public ParametrosFiltroApostas(ArrayList<String> listaModalidades, String situacao){
		this.listaModalidades = listaModalidades;
		this.situacao = situacao;
	}
}
