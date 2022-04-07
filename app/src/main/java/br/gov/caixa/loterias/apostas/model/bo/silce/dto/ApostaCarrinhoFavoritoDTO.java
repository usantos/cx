package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.utils.AppUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
public class ApostaCarrinhoFavoritoDTO<T> implements Serializable {

	@SerializedName("modalidade")
	private ModalidadeDTO modalidade = null;
	@SerializedName("numerosSelecionados")
	private T numerosSelecionados = null;
	@SerializedName("timeDoCoracao")
	private ParametroEquipe timeDoCoracao = null;
	@SerializedName("id")
	private Long id = null;
	@SerializedName("quantidadeTeimosinhas")
	private Integer quantidadeTeimosinhas = null;
	@SerializedName("mesDeSorte")
	private ParametroMesDeSorte mesDeSorte = null;
	@SerializedName("espelho")
	private Boolean espelho = null;
	@SerializedName("quantidadeNumeros")
	private Integer quantidadeNumeros = null;
	@SerializedName("trevos")
	private ParametroTrevo parametroTrevo;
	@SerializedName("quantidadeTrevos")
	private Integer quantidadeTrevos;

	protected ApostaCarrinhoFavoritoDTO(Parcel in) {
		modalidade = in.readParcelable(ModalidadeDTO.class.getClassLoader());
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		if (in.readByte() == 0) {
			quantidadeTeimosinhas = null;
		} else {
			quantidadeTeimosinhas = in.readInt();
		}
		byte tmpEspelho = in.readByte();
		espelho = tmpEspelho == 0 ? null : tmpEspelho == 1;
		if (in.readByte() == 0) {
			quantidadeNumeros = null;
		} else {
			quantidadeNumeros = in.readInt();
		}
	}

	public enum IndicadorSurpresinhaEnum {
		NAO_SURPRESINHA, SURPRESINHA, SURPRESINHA_NUMERICA,
	}

	;
	@SerializedName("indicadorSurpresinha")
	private IndicadorSurpresinhaEnum indicadorSurpresinha = null;

	/**
	 *
	 **/
	@ApiModelProperty(value = "")
	public ModalidadeDTO getModalidade() {
		return modalidade;
	}

	public void setModalidade(ModalidadeDTO modalidade) {
		this.modalidade = modalidade;
	}

	/**
	 * Lista de Prognósticos que compoem a Aposta Favorita
	 **/
	@ApiModelProperty(value = "Lista de Prognósticos que compoem a Aposta Favorita")
	public T getNumerosSelecionados() {
		return numerosSelecionados;
	}

	public void setNumerosSelecionados(T numerosSelecionados) {
		this.numerosSelecionados = numerosSelecionados;
	}

	public List<Integer> getListaNumerosSelecionados() {
		if (numerosSelecionados != null) {
			return AppUtils.converteListaInteiros((List<Integer>) numerosSelecionados);
		} else {
			return null;
		}
	}

	public ArrayList<ArrayList<Integer>> getMatrizNumerosSelecionados() {
		return AppUtils.converteMatrizInteiros((List<List<Integer>>) numerosSelecionados);
	}

	/**
	 *
	 **/
	@ApiModelProperty(value = "")
	public ParametroEquipe getTimeDoCoracao() {
		return timeDoCoracao;
	}

	public void setTimeDoCoracao(ParametroEquipe timeDoCoracao) {
		this.timeDoCoracao = timeDoCoracao;
	}

	/**
	 * Identificação da Aposta Favorita
	 **/
	@ApiModelProperty(value = "Identificação da Aposta Favorita")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Quantidade de teimosinhas
	 **/
	@ApiModelProperty(value = "Quantidade de teimosinhas")
	public Integer getQuantidadeTeimosinhas() {
		return quantidadeTeimosinhas;
	}

	public void setQuantidadeTeimosinhas(Integer quantidadeTeimosinhas) {
		this.quantidadeTeimosinhas = quantidadeTeimosinhas;
	}

	/**
	 * Mês de sorte da aposta, referente a apostas do dia de sorte
	 **/
	@ApiModelProperty(value = "Mês de sorte da aposta, referente a apostas do dia de sorte")
	public ParametroMesDeSorte getMesDeSorte() {
		return mesDeSorte;
	}

	public void setMesDeSorte(ParametroMesDeSorte mesDeSorte) {
		this.mesDeSorte = mesDeSorte;
	}

	/**
	 * Indicador se esta aposta é espelho
	 **/
	@ApiModelProperty(value = "Indicador se esta aposta é espelho")
	public Boolean getEspelho() {
		return espelho;
	}

	public void setEspelho(Boolean espelho) {
		this.espelho = espelho;
	}

	/**
	 * Quantidade de Números de prognósticos da aposta
	 **/
	@ApiModelProperty(value = "Quantidade de Números de prognósticos da aposta")
	public Integer getQuantidadeNumeros() {
		return quantidadeNumeros;
	}

	public void setQuantidadeNumeros(Integer quantidadeNumeros) {
		this.quantidadeNumeros = quantidadeNumeros;
	}

	/**
	 * Indicador de Surpresinha
	 **/
	@ApiModelProperty(value = "Indicador de Surpresinha")
	public IndicadorSurpresinhaEnum getIndicadorSurpresinha() {
		return indicadorSurpresinha;
	}

	public void setIndicadorSurpresinha(IndicadorSurpresinhaEnum indicadorSurpresinha) {
		this.indicadorSurpresinha = indicadorSurpresinha;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ApostaCarrinhoFavoritoDTO apostaCarrinhoFavoritoDTO = (ApostaCarrinhoFavoritoDTO) o;
		return (this.modalidade == null ? apostaCarrinhoFavoritoDTO.modalidade == null : this.modalidade.equals(apostaCarrinhoFavoritoDTO.modalidade)) &&
				(this.numerosSelecionados == null ? apostaCarrinhoFavoritoDTO.numerosSelecionados == null : this.numerosSelecionados.equals(apostaCarrinhoFavoritoDTO.numerosSelecionados)) &&
				(this.timeDoCoracao == null ? apostaCarrinhoFavoritoDTO.timeDoCoracao == null : this.timeDoCoracao.equals(apostaCarrinhoFavoritoDTO.timeDoCoracao)) &&
				(this.id == null ? apostaCarrinhoFavoritoDTO.id == null : this.id.equals(apostaCarrinhoFavoritoDTO.id)) &&
				(this.quantidadeTeimosinhas == null ? apostaCarrinhoFavoritoDTO.quantidadeTeimosinhas == null : this.quantidadeTeimosinhas.equals(apostaCarrinhoFavoritoDTO.quantidadeTeimosinhas)) &&
				(this.mesDeSorte == null ? apostaCarrinhoFavoritoDTO.mesDeSorte == null : this.mesDeSorte.equals(apostaCarrinhoFavoritoDTO.mesDeSorte)) &&
				(this.espelho == null ? apostaCarrinhoFavoritoDTO.espelho == null : this.espelho.equals(apostaCarrinhoFavoritoDTO.espelho)) &&
				(this.quantidadeNumeros == null ? apostaCarrinhoFavoritoDTO.quantidadeNumeros == null : this.quantidadeNumeros.equals(apostaCarrinhoFavoritoDTO.quantidadeNumeros)) &&
				(this.indicadorSurpresinha == null ? apostaCarrinhoFavoritoDTO.indicadorSurpresinha == null : this.indicadorSurpresinha.equals(apostaCarrinhoFavoritoDTO.indicadorSurpresinha));
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (this.modalidade == null ? 0 : this.modalidade.hashCode());
		result = 31 * result + (this.numerosSelecionados == null ? 0 : this.numerosSelecionados.hashCode());
		result = 31 * result + (this.timeDoCoracao == null ? 0 : this.timeDoCoracao.hashCode());
		result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
		result = 31 * result + (this.quantidadeTeimosinhas == null ? 0 : this.quantidadeTeimosinhas.hashCode());
		result = 31 * result + (this.mesDeSorte == null ? 0 : this.mesDeSorte.hashCode());
		result = 31 * result + (this.espelho == null ? 0 : this.espelho.hashCode());
		result = 31 * result + (this.quantidadeNumeros == null ? 0 : this.quantidadeNumeros.hashCode());
		result = 31 * result + (this.indicadorSurpresinha == null ? 0 : this.indicadorSurpresinha.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ApostaCarrinhoFavoritoDTO {\n");

		sb.append("  modalidade: ").append(modalidade).append("\n");
		sb.append("  numerosSelecionados: ").append(numerosSelecionados).append("\n");
		sb.append("  timeDoCoracao: ").append(timeDoCoracao).append("\n");
		sb.append("  id: ").append(id).append("\n");
		sb.append("  quantidadeTeimosinhas: ").append(quantidadeTeimosinhas).append("\n");
		sb.append("  mesDeSorte: ").append(mesDeSorte).append("\n");
		sb.append("  espelho: ").append(espelho).append("\n");
		sb.append("  quantidadeNumeros: ").append(quantidadeNumeros).append("\n");
		sb.append("  indicadorSurpresinha: ").append(indicadorSurpresinha).append("\n");
		sb.append("}\n");
		return sb.toString();
	}

	public ParametroTrevo getParametroTrevo() {
		return parametroTrevo;
	}

	public void setParametroTrevo(ParametroTrevo parametroTrevo) {
		this.parametroTrevo = parametroTrevo;
	}

	public Integer getQuantidadeTrevos() {
		return quantidadeTrevos;
	}

	public void setQuantidadeTrevos(Integer quantidadeTrevos) {
		this.quantidadeTrevos = quantidadeTrevos;
	}
}
