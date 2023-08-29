package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 22/01/2018.
 */

@ApiModel(description = "")
public class EscudoEquipeEsportivaDTO {

    @SerializedName("id")
    private Integer id;
    @SerializedName("modalidades")
    //private List<ModalidadeDTO> modalidades;
    private List<String> modalidades;
    @SerializedName("equipe")
    private EquipeEscudoDTO equipe;
    @SerializedName("imagem")
    private String imagem;

    public EscudoEquipeEsportivaDTO() {
    }

    public EscudoEquipeEsportivaDTO(Integer id, List<String> modalidades, EquipeEscudoDTO equipe, String imagem) {
        this.id = id;
        this.modalidades = modalidades;
        this.equipe = equipe;
        this.imagem = imagem;
    }

//    public EscudoEquipeEsportivaDTO(Integer id, List<ModalidadeDTO> modalidades, EquipeEscudoDTO equipe, String imagem) {
//        this.id = id;
//        this.modalidades = modalidades;
//        this.equipe = equipe;
//        this.imagem = imagem;
//    }

    public Integer getId() {
        return id;
    }

//    public List<ModalidadeDTO> getModalidades() {
//        return modalidades;
//    }

    public List<String> getModalidades() {
        return modalidades;
    }

    public EquipeEscudoDTO getEquipe() {
        return equipe;
    }

    public String getImagem() {
        return imagem;
    }
}
