package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BoloesDisponiveisResponse
 */
@ApiModel(description = "")
public class BoloesDisponiveisResponse {
    @SerializedName("codigo")
    private String codigo = null;

    @SerializedName("mensagem")
    private String mensagem = null;

    @JsonAdapter(TipoEnum.Adapter.class)
    public enum TipoEnum {
        NUMBER_1(1),

        NUMBER_2(2),

        NUMBER_3(3);

        private Integer value;

        TipoEnum(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static TipoEnum fromValue(String text) {
            for (TipoEnum b : TipoEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<TipoEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final TipoEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public TipoEnum read(final JsonReader jsonReader) throws IOException {
                int value = jsonReader.nextInt();
                return TipoEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("tipo")
    private TipoEnum tipo = null;

    @JsonAdapter(RedirectEnum.Adapter.class)
    public enum RedirectEnum {
        HOME("home"),
        MEGA_SENA("mega-sena"),
        SURPRESINHAMEGASENA("surpresinhaMegaSena"),
        DIA_DE_SORTE("dia-de-sorte"),
        SURPRESINHADIADESORTE("surpresinhaDiaDeSorte"),
        QUINA("quina"),
        SURPRESINHAQUINA("surpresinhaQuina"),
        DUPLA_SENA("dupla-sena"),
        SURPRESINHADUPLASENA("surpresinhaDuplaSena"),
        LOTOMANIA("lotomania"),
        SURPRESINHALOTOMANIA("surpresinhaLotomania"),
        LOTOFACIL("lotofacil"),
        SURPRESINHALOTOFACIL("surpresinhaLotofacil"),
        TIMEMANIA("timemania"),
        SURPRESINHATIMEMANIA("surpresinhaTimemania"),
        LOTOGOL("lotogol"),
        LOTECA("loteca"),
        FORM_APOSTAS("form-apostas"),
        CARRINHO("carrinho"),
        CADASTRAR_APOSTADOR("cadastrar-apostador"),
        SELECIONAR_LOTERICA("selecionar-loterica"),
        ACEITAR_TERMO_DE_USO("aceitar-termo-de-uso");

        private String value;

        RedirectEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static RedirectEnum fromValue(String text) {
            for (RedirectEnum b : RedirectEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<RedirectEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final RedirectEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public RedirectEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return RedirectEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("redirect")
    private RedirectEnum redirect = null;

    @SerializedName("errosValidacao")
    private List<ErroValidacao> errosValidacao = null;

    @SerializedName("versao")
    private String versao = null;

    @SerializedName("payload")
    private BoloesDTO payload = null;

    public BoloesDisponiveisResponse codigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    /**
     * Código do erro
     * @return codigo
     **/
    @ApiModelProperty(value = "Código do erro")
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BoloesDisponiveisResponse mensagem(String mensagem) {
        this.mensagem = mensagem;
        return this;
    }

    /**
     * Mensagem de erro de negócio ou de erro de sistema que deve ser exibido para o usuário
     * @return mensagem
     **/
    @ApiModelProperty(value = "Mensagem de erro de negócio ou de erro de sistema que deve ser exibido para o usuário")
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BoloesDisponiveisResponse tipo(TipoEnum tipo) {
        this.tipo = tipo;
        return this;
    }

    /**
     * Tipo da mensagem. 1&#x3D;Mensagem de usuário, 2&#x3D;Mensagem de sistema, 3&#x3D; Erro de validação
     * @return tipo
     **/
    @ApiModelProperty(value = "Tipo da mensagem. 1=Mensagem de usuário, 2=Mensagem de sistema, 3= Erro de validação")
    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    public BoloesDisponiveisResponse redirect(RedirectEnum redirect) {
        this.redirect = redirect;
        return this;
    }

    /**
     * ID da tela que o usuário deve ser redirecionado
     * @return redirect
     **/
    @ApiModelProperty(value = "ID da tela que o usuário deve ser redirecionado")
    public RedirectEnum getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectEnum redirect) {
        this.redirect = redirect;
    }

    public BoloesDisponiveisResponse errosValidacao(List<ErroValidacao> errosValidacao) {
        this.errosValidacao = errosValidacao;
        return this;
    }

    public BoloesDisponiveisResponse addErrosValidacaoItem(ErroValidacao errosValidacaoItem) {
        if (this.errosValidacao == null) {
            this.errosValidacao = new ArrayList<ErroValidacao>();
        }
        this.errosValidacao.add(errosValidacaoItem);
        return this;
    }

    /**
     * Lista com os erros de validação
     * @return errosValidacao
     **/
    @ApiModelProperty(value = "Lista com os erros de validação")
    public List<ErroValidacao> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<ErroValidacao> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    public BoloesDisponiveisResponse versao(String versao) {
        this.versao = versao;
        return this;
    }

    /**
     * Versão do serviço
     * @return versao
     **/
    @ApiModelProperty(value = "Versão do serviço")
    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public BoloesDisponiveisResponse payload(BoloesDTO payload) {
        this.payload = payload;
        return this;
    }

    /**
     * Get payload
     * @return payload
     **/
    @ApiModelProperty(value = "")
    public BoloesDTO getPayload() {
        return payload;
    }

    public void setPayload(BoloesDTO payload) {
        this.payload = payload;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoloesDisponiveisResponse boloesDisponiveisResponse = (BoloesDisponiveisResponse) o;
        return Objects.equals(this.codigo, boloesDisponiveisResponse.codigo) &&
                Objects.equals(this.mensagem, boloesDisponiveisResponse.mensagem) &&
                Objects.equals(this.tipo, boloesDisponiveisResponse.tipo) &&
                Objects.equals(this.redirect, boloesDisponiveisResponse.redirect) &&
                Objects.equals(this.errosValidacao, boloesDisponiveisResponse.errosValidacao) &&
                Objects.equals(this.versao, boloesDisponiveisResponse.versao) &&
                Objects.equals(this.payload, boloesDisponiveisResponse.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, mensagem, tipo, redirect, errosValidacao, versao, payload);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoloesDisponiveisResponse {\n");

        sb.append("    codigo: ").append(toIndentedString(codigo)).append("\n");
        sb.append("    mensagem: ").append(toIndentedString(mensagem)).append("\n");
        sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
        sb.append("    redirect: ").append(toIndentedString(redirect)).append("\n");
        sb.append("    errosValidacao: ").append(toIndentedString(errosValidacao)).append("\n");
        sb.append("    versao: ").append(toIndentedString(versao)).append("\n");
        sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

