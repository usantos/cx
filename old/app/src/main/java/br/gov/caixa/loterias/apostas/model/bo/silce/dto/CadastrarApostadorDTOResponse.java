package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by cedesbr450 on 10/04/18.
 */



@ApiModel(description = "")
public class CadastrarApostadorDTOResponse {

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
    private CadastrarApostadorDTO payload = null;

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

    /**
     **/
    @ApiModelProperty(value = "")
    public CadastrarApostadorDTO getPayload() {
        return payload;
    }

    public void setPayload(CadastrarApostadorDTO payload) {
        this.payload = payload;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CadastrarApostadorDTOResponse cadastrarApostadorDTOResponse = (CadastrarApostadorDTOResponse) o;
        return (this.codigo == null ? cadastrarApostadorDTOResponse.codigo == null : this.codigo.equals(cadastrarApostadorDTOResponse.codigo)) &&
                (this.mensagem == null ? cadastrarApostadorDTOResponse.mensagem == null : this.mensagem.equals(cadastrarApostadorDTOResponse.mensagem)) &&
                (this.tipo == null ? cadastrarApostadorDTOResponse.tipo == null : this.tipo.equals(cadastrarApostadorDTOResponse.tipo)) &&
                (this.redirect == null ? cadastrarApostadorDTOResponse.redirect == null : this.redirect.equals(cadastrarApostadorDTOResponse.redirect)) &&
                (this.errosValidacao == null ? cadastrarApostadorDTOResponse.errosValidacao == null : this.errosValidacao.equals(cadastrarApostadorDTOResponse.errosValidacao)) &&
                (this.payload == null ? cadastrarApostadorDTOResponse.payload == null : this.payload.equals(cadastrarApostadorDTOResponse.payload));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.codigo == null ? 0 : this.codigo.hashCode());
        result = 31 * result + (this.mensagem == null ? 0 : this.mensagem.hashCode());
        result = 31 * result + (this.tipo == null ? 0 : this.tipo.hashCode());
        result = 31 * result + (this.redirect == null ? 0 : this.redirect.hashCode());
        result = 31 * result + (this.errosValidacao == null ? 0 : this.errosValidacao.hashCode());
        result = 31 * result + (this.payload == null ? 0 : this.payload.hashCode());
        return result;
    }

    @Override
    public String toString() {

        String sb = "class CodigoMensagemResponse {\n" +
                "  codigo: " + codigo + "\n" +
                "  mensagem: " + mensagem + "\n" +
                "  tipo: " + tipo + "\n" +
                "  redirect: " + redirect + "\n" +
                "  errosValidacao: " + errosValidacao + "\n" +
                "  payload: " + payload + "\n" +
                "}\n";
        return sb;
    }
}
