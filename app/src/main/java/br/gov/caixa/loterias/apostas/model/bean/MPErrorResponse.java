package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 15/05/18.
 */

public class MPErrorResponse {
    private String message;
    private String error;
    private int status;
    private Object cause;

    public MPErrorResponse(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getCause() {
        return cause;
    }

    public void setCause(Object cause) {
        this.cause = cause;
    }

}