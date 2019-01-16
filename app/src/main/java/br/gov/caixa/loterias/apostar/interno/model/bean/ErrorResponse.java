package br.gov.caixa.loterias.apostar.interno.model.bean;

/**
 * Created by cedesbr450 on 15/05/18.
 */

public class ErrorResponse {
    private String mensagem;
    private String redirect;
    private String codigo;

    public ErrorResponse(){

    }

    public ErrorResponse(String mensagem, String redirect, String codigo){
        this.mensagem = mensagem;
        this.redirect = redirect;
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
