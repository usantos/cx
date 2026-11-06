package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;

/**
 * Created by cedesbr450 on 15/05/18.
 */

public class ErrorResponse implements Serializable {
    private String mensagem;
    private String redirect;
    private String codigo;
    private String tipo;

    public ErrorResponse(){

    }

    public ErrorResponse(String mensagem, String redirect, String codigo, String tipo){
        this.mensagem = mensagem;
        this.redirect = redirect;
        this.codigo = codigo;
        this.tipo = tipo;
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

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "mensagem='" + mensagem + '\'' +
                ", redirect='" + redirect + '\'' +
                ", codigo='" + codigo + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}