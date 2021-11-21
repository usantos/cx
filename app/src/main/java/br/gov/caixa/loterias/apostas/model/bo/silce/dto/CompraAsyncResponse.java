package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class CompraAsyncResponse implements Serializable {

    @SerializedName("codigo")
    private String codigo = null;

    @SerializedName("mensagem")
    private String mensagem = null;

    @SerializedName("tipo")
    private Integer tipo = null;

    @SerializedName("redirect")
    private RedirectEnum redirect = null;

    @SerializedName("errosValidacao")
    private List<ErroValidacao> errosValidacao = null;

    @SerializedName("versao")
    private String versao = null;

    @SerializedName("payload")
    private CompraAsyncDTO payload = null;

    public CompraAsyncDTO getPayload() {
        return payload;
    }

    public void setPayload(CompraAsyncDTO payload) {
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

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
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

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }





}
