package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SituacaoReservaCotaBolao
 */
@ApiModel(description = "")
public class SituacaoReservaCotaBolao {
    @SerializedName("id")
    private Long id = null;

    @SerializedName("nome")
    private String nome = null;

    @SerializedName("descricao")
    private String descricao = null;

    /**
     * Gets or Sets _enum
     */
    @JsonAdapter(EnumEnum.Adapter.class)
    public enum EnumEnum {
        INICIADA("INICIADA"),

        RESERVADA("RESERVADA"),

        CANCELADA("CANCELADA"),

        EM_CONFIRMACAO("EM_CONFIRMACAO"),

        CONFIRMADA("CONFIRMADA");

        private String value;

        EnumEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static EnumEnum fromValue(String text) {
            for (EnumEnum b : EnumEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<EnumEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final EnumEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public EnumEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return EnumEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("enum")
    private EnumEnum _enum = null;

    public SituacaoReservaCotaBolao id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     **/
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoReservaCotaBolao nome(String nome) {
        this.nome = nome;
        return this;
    }

    /**
     * Get nome
     * @return nome
     **/
    @ApiModelProperty(value = "")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public SituacaoReservaCotaBolao descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    /**
     * Get descricao
     * @return descricao
     **/
    @ApiModelProperty(value = "")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoReservaCotaBolao _enum(EnumEnum _enum) {
        this._enum = _enum;
        return this;
    }

    /**
     * Get _enum
     * @return _enum
     **/
    @ApiModelProperty(value = "")
    public EnumEnum getEnum() {
        return _enum;
    }

    public void setEnum(EnumEnum _enum) {
        this._enum = _enum;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SituacaoReservaCotaBolao situacaoReservaCotaBolao = (SituacaoReservaCotaBolao) o;
        return Objects.equals(this.id, situacaoReservaCotaBolao.id) &&
                Objects.equals(this.nome, situacaoReservaCotaBolao.nome) &&
                Objects.equals(this.descricao, situacaoReservaCotaBolao.descricao) &&
                Objects.equals(this._enum, situacaoReservaCotaBolao._enum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, _enum);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SituacaoReservaCotaBolao {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    nome: ").append(toIndentedString(nome)).append("\n");
        sb.append("    descricao: ").append(toIndentedString(descricao)).append("\n");
        sb.append("    _enum: ").append(toIndentedString(_enum)).append("\n");
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

