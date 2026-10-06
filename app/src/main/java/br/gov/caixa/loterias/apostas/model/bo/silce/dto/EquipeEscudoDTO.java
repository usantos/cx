package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 22/01/2018.
 */

@ApiModel(description = "")
public class EquipeEscudoDTO {

    @SerializedName("id")
    private Integer id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("nomeReduzido")
    private String nomeReduzido;
    @SerializedName("uf")
    private String uf;
    @SerializedName("razaoSocial")
    private String razaoSocial;
    @SerializedName("pais")
    private Integer pais;
    @SerializedName("selecao")
    private Boolean selecao;

    public EquipeEscudoDTO() {
    }

    public EquipeEscudoDTO(Integer id, String nome, String nomeReduzido, String uf, String razaoSocial, Integer pais, Boolean selecao) {
        this.id = id;
        this.nome = nome;
        this.nomeReduzido = nomeReduzido;
        this.uf = uf;
        this.razaoSocial = razaoSocial;
        this.pais = pais;
        this.selecao = selecao;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public String getUf() {
        return uf;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public Integer getPais() {
        return pais;
    }

    public Boolean getSelecao() {
        return selecao;
    }
}
