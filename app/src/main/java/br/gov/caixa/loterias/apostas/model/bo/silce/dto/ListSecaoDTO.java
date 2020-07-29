package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 16/02/2018.
 * Class ListSecaoDTO playload
 */
@ApiModel(description = "")
public class ListSecaoDTO implements java.io.Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("duvidas")
    private List<DuvidaDTO> duvidas;

    public ListSecaoDTO() {
    }

    public ListSecaoDTO(Integer id, String nome, List<DuvidaDTO> duvidas) {
        this.id = id;
        this.nome = nome;
        this.duvidas = duvidas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<DuvidaDTO> getDuvidas() {
        return duvidas;
    }

    public void setDuvidas(List<DuvidaDTO> duvidas) {
        this.duvidas = duvidas;
    }
}
