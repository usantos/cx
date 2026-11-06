package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 22/01/2018.
 */

@ApiModel(description = "")
public class EscudoEquipeEsportivaDTOResponse {

    @SerializedName("codigo")
    private String codigo = null;
    @SerializedName("mensagem")
    private String mensagem = null;
    @SerializedName("tipo")
    private TipoEnum tipo = null;
    @SerializedName("redirect")
    private RedirectEnum redirect = null;
    @SerializedName("errosValidacao")
    private List<ErroValidacao> errosValidacao = null;
    @SerializedName("versao")
    private String versao = null;
    @SerializedName("payload")
    private List<EscudoEquipeEsportivaDTO> payload = null;

    public EscudoEquipeEsportivaDTOResponse() {
    }

    public EscudoEquipeEsportivaDTOResponse(String codigo, String mensagem, TipoEnum tipo, RedirectEnum redirect, List<ErroValidacao> errosValidacao, String versao, List<EscudoEquipeEsportivaDTO> payload) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.redirect = redirect;
        this.errosValidacao = errosValidacao;
        this.versao = versao;
        this.payload = payload;
    }


//    public EscudoEquipeEsportivaDTOResponse(String codigo, String mensagem, TipoEnum tipo, RedirectEnum redirect, List<ErroValidacao> errosValidacao, EscudoEquipeEsportivaDTO payload) {
//        this.codigo = codigo;
//        this.mensagem = mensagem;
//        this.tipo = tipo;
//        this.redirect = redirect;
//        this.errosValidacao = errosValidacao;
//        this.payload = payload;
//    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public TipoEnum getTipo() {
        return tipo;
    }

    public RedirectEnum getRedirect() {
        return redirect;
    }

    public List<ErroValidacao> getErrosValidacao() {
        return errosValidacao;
    }

    public String getVersao() {
        return versao;
    }

    public List<EscudoEquipeEsportivaDTO> getPayload() {
        return payload;
    }
}
