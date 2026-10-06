package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 16/02/2018.
 * Class ListSecaoDTOResponse
 */
@ApiModel(description = "")
public class ListSecaoDTOResponse {
    @SerializedName("codigo")
    private String codigo;
    @SerializedName("mensagem")
    private String mensagem;
    @SerializedName("tipo")
    private TipoEnum tipo;
    @SerializedName("redirect")
    private RedirectEnum redirect;
    @SerializedName("errosValidacao")
    private List<ErroValidacao> errosValidacao;
    @SerializedName("payload")
    private List<ListSecaoDTO> payload;

    public ListSecaoDTOResponse() {
    }

    public ListSecaoDTOResponse(String codigo, String mensagem, TipoEnum tipo, RedirectEnum redirect, List<ErroValidacao> errosValidacao, List<ListSecaoDTO> payload) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.redirect = redirect;
        this.errosValidacao = errosValidacao;
        this.payload = payload;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    public RedirectEnum getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectEnum redirect) {
        this.redirect = redirect;
    }

    public List<ErroValidacao> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<ErroValidacao> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    public List<ListSecaoDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<ListSecaoDTO> payload) {
        this.payload = payload;
    }
}
