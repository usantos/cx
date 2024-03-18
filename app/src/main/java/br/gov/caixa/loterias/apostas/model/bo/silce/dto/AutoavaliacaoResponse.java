package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AutoavaliacaoResponse {

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
    private AutoavaliacaoDTO payload = null;

    @ApiModelProperty(value = "Código do erro")
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @ApiModelProperty(value = "Mensagem de erro de negócio ou de erro de sistema que deve ser exibido para o usuário")
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    //Tipo da mensagem. 1=Mensagem de usuário, 2=Mensagem de sistema, 3= Erro de validação
    @ApiModelProperty(value = "Tipo da mensagem. 1=Mensagem de usuário, 2=Mensagem de sistema, 3= Erro de validação")
    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    @ApiModelProperty(value = "ID da tela que o usuário deve ser redirecionado")
    public RedirectEnum getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectEnum redirect) {
        this.redirect = redirect;
    }

    @ApiModelProperty(value = "Lista com os erros de validação")
    public List<ErroValidacao> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<ErroValidacao> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    @ApiModelProperty(value = "versao")
    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    @ApiModelProperty(value = "")
    public AutoavaliacaoDTO getPayload() {
        return payload;
    }

    public void setPayload(AutoavaliacaoDTO payload) {
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
       AutoavaliacaoResponse response = (AutoavaliacaoResponse) o;
        return (this.codigo == null ? response.codigo == null : this.codigo.equals(response.codigo)) &&
                (this.mensagem == null ? response.mensagem == null : this.mensagem.equals(response.mensagem)) &&
                (this.versao == null ? response.versao == null : this.mensagem.equals(response.versao)) &&
                (this.tipo == null ? response.tipo == null : this.tipo.equals(response.tipo)) &&
                (this.redirect == null ? response.redirect == null : this.redirect.equals(response.redirect)) &&
                (this.errosValidacao == null ? response.errosValidacao == null : this.errosValidacao.equals(response.errosValidacao)) &&
                (this.payload == null ? response.payload == null : this.payload.equals(response.payload));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.codigo == null ? 0 : this.codigo.hashCode());
        result = 31 * result + (this.mensagem == null ? 0 : this.mensagem.hashCode());
        result = 31 * result + (this.versao == null ? 0 : this.versao.hashCode());
        result = 31 * result + (this.tipo == null ? 0 : this.tipo.hashCode());
        result = 31 * result + (this.redirect == null ? 0 : this.redirect.hashCode());
        result = 31 * result + (this.errosValidacao == null ? 0 : this.errosValidacao.hashCode());
        result = 31 * result + (this.payload == null ? 0 : this.payload.hashCode());
        return result;
    }

    @Override
    public String toString() {

        String sb = "class ParametrosConfiguraveisDTOResponse {\n" +
                "  codigo: " + codigo + "\n" +
                "  mensagem: " + mensagem + "\n" +
                "  versao: " + versao + "\n" +
                "  tipo: " + tipo + "\n" +
                "  redirect: " + redirect + "\n" +
                "  errosValidacao: " + errosValidacao + "\n" +
                "  payload: " + payload + "\n" +
                "}\n";
        return sb;
    }
}
