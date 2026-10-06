
package br.gov.caixa.loterias.apostas.model.bo.silce.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ErroValidacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public final class CompletaJogoRsp {



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
    @SerializedName("payload")
    private List<Integer> payload;

    /**
     * Código do erro
     **/
    @ApiModelProperty(value = "Código do erro")
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Mensagem de erro de negócio ou de erro de sistema que deve ser exibido para o usuário
     **/
    @ApiModelProperty(value = "Mensagem de erro de negócio ou de erro de sistema que deve ser exibido para o usuário")
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    /**
     * Tipo da mensagem. 1=Mensagem de usuário, 2=Mensagem de sistema, 3= Erro de validação
     **/
    @ApiModelProperty(value = "Tipo da mensagem. 1=Mensagem de usuário, 2=Mensagem de sistema, 3= Erro de validação")
    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    /**
     * ID da tela que o usuário deve ser redirecionado
     **/
    @ApiModelProperty(value = "ID da tela que o usuário deve ser redirecionado")
    public RedirectEnum getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectEnum redirect) {
        this.redirect = redirect;
    }

    /**
     * Lista com os erros de validação
     **/
    @ApiModelProperty(value = "Lista com os erros de validação")
    public List<ErroValidacao> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<ErroValidacao> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletaJogoRsp that = (CompletaJogoRsp) o;
        return Objects.equals(codigo, that.codigo) &&
                Objects.equals(mensagem, that.mensagem) &&
                tipo == that.tipo &&
                redirect == that.redirect &&
                Objects.equals(errosValidacao, that.errosValidacao) &&
                Objects.equals(payload, that.payload);
    }

    public List<Integer> getPayload() {
        return payload;
    }

    public void setPayload(List<Integer> payload) {
        this.payload = payload;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, mensagem, tipo, redirect, errosValidacao, payload);
    }
}

