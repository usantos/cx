package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

/**
 * Created by joafilho on 23/01/2018.
 */

public class Bolao {

	private static Long ID_GENERATOR = 0l;

	private  Long id;
    private String modalidade;
	private String concurso;
	private String loterica;
	private String cidade;
	private String valor;
	private Boolean aberto;
	private String valorEstimadoPremio;
	private int quantidade;
	private int qtdPermitida;

	public Bolao(String modalidade, String concurso, String loterica, String cidade, String valor, String valorEstimadoPremio, int qtdPermitida) {
		this.valorEstimadoPremio = valorEstimadoPremio;
		this.qtdPermitida = qtdPermitida;
		ID_GENERATOR++;
		this.id = ID_GENERATOR;
		this.modalidade = modalidade;
		this.concurso = concurso;
		this.loterica = loterica;
		this.cidade = cidade;
		this.valor = valor;
		this.quantidade = 1;
		this.aberto = false;
	}

	public void aumentaQuantidade() {
		this.quantidade++;
	}

	public void diminuiQuantidade() {
		if (quantidade > 1){
			this.quantidade--;
		}
	}

	public String getModalidade() {
		return modalidade;
	}

	public String getConcurso() {
		return concurso;
	}

	public String getLoterica() {
		return loterica;
	}

	public String getCidade() {
		return cidade;
	}

	public String getValor() {
		return valor;
	}

	public Long getId() {
		return id;
	}

	public Boolean isAberto() {
		return aberto;
	}

	public void setAberto(Boolean aberto) {
		this.aberto = aberto;
	}

	public String getValorEstimadoPremio() {
		return valorEstimadoPremio;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public int getQtdPermitida() {
		return qtdPermitida;
	}
}
