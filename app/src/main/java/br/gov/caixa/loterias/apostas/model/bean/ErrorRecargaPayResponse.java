package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 15/05/18.
 */

public class ErrorRecargaPayResponse {

    private int status;
    private String code;
    private String message;
    private String localizedMessage;

    public ErrorRecargaPayResponse(){
    }

    public ErrorRecargaPayResponse(int status, String code, String message, String localizedMessage) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.localizedMessage = localizedMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }
}