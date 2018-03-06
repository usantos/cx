package caixa.gov.br.loterias.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by cedesbr450 on 06/03/18.
 */


@ApiModel(description = "")
public class RetornoPadraoDTO {



    @SerializedName("codigo")
    private String codigo = null;
    @SerializedName("mensagem")
    private String mensagem = null;
    @SerializedName("tipo")
    private Integer tipo = null;
    @SerializedName("redirect")
    private String redirect = null;
    @SerializedName("errosValidacao")
    private List<ErroValidacao> errosValidacao = null;
    @SerializedName("versao")
    private String versao = null;
    @SerializedName("payload")
    private Object payload = null;

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
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

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
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
