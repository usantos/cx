package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * RetornoErro
 */
@ApiModel(description = "")
public class RetornoErro {
    @SerializedName("message")
    private String message = null;
    @SerializedName("error")
    private String error = null;
    @SerializedName("status")
    private Integer status = null;
    @SerializedName("causeCode")
    private Integer causeCode = null;
    @SerializedName("causeMessage")
    private String causeMessage = null;
    @SerializedName("code")
    private String code = null;

    /**
     * Get message
     * @return message
     **/
    @ApiModelProperty(value = "")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * Get error
     * @return error
     **/
    @ApiModelProperty(value = "")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Get status
     * @return status
     **/
    @ApiModelProperty(value = "")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Get causeCode
     * @return causeCode
     **/
    @ApiModelProperty(value = "")
    public Integer getCauseCode() {
        return causeCode;
    }

    public void setCauseCode(Integer causeCode) {
        this.causeCode = causeCode;
    }

    /**
     * Get causeMessage
     * @return causeMessage
     **/
    @ApiModelProperty(value = "")
    public String getCauseMessage() {
        return causeMessage;
    }

    public void setCauseMessage(String causeMessage) {
        this.causeMessage = causeMessage;
    }

    /**
     * Get code
     * @return code
     **/
    @ApiModelProperty(value = "")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RetornoErro retornoErro = (RetornoErro) o;
        return Objects.equals(this.message, retornoErro.message) &&
                Objects.equals(this.error, retornoErro.error) &&
                Objects.equals(this.status, retornoErro.status) &&
                Objects.equals(this.causeCode, retornoErro.causeCode) &&
                Objects.equals(this.causeMessage, retornoErro.causeMessage) &&
                Objects.equals(this.code, retornoErro.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, error, status, causeCode, causeMessage, code);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RetornoErro {\n");

        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    error: ").append(toIndentedString(error)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    causeCode: ").append(toIndentedString(causeCode)).append("\n");
        sb.append("    causeMessage: ").append(toIndentedString(causeMessage)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
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
