package br.gov.caixa.loterias.apostas.model.bo.silce.data;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogado;

public final class UsuarioLogadoResponse {
    private String codigo;
    private String mensagem;
    private int tipo;
    private String redirect;
    private List<String> errosValidacao;
    private UsuarioLogado payload;

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

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public List<String> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<String> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    public UsuarioLogado getPayload() {
        return payload;
    }

    public void setPayload(UsuarioLogado payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioLogadoResponse loginRsp = (UsuarioLogadoResponse) o;

        if (tipo != loginRsp.tipo) return false;
        if (codigo != null ? !codigo.equals(loginRsp.codigo) : loginRsp.codigo != null)
            return false;
        if (mensagem != null ? !mensagem.equals(loginRsp.mensagem) : loginRsp.mensagem != null)
            return false;
        if (redirect != null ? !redirect.equals(loginRsp.redirect) : loginRsp.redirect != null)
            return false;
        if (errosValidacao != null ? !errosValidacao.equals(loginRsp.errosValidacao) : loginRsp.errosValidacao != null)
            return false;
        return payload != null ? payload.equals(loginRsp.payload) : loginRsp.payload == null;
    }

    @Override
    public int hashCode() {
        int result = codigo != null ? codigo.hashCode() : 0;
        result = 31 * result + (mensagem != null ? mensagem.hashCode() : 0);
        result = 31 * result + tipo;
        result = 31 * result + (redirect != null ? redirect.hashCode() : 0);
        result = 31 * result + (errosValidacao != null ? errosValidacao.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UsuarioLogadoResponse{" +
                "codigo='" + codigo + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", tipo=" + tipo +
                ", redirect='" + redirect + '\'' +
                ", errosValidacao=" + errosValidacao +
                ", payload=" + payload +
                '}';
    }
}
