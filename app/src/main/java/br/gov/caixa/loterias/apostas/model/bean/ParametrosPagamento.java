package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 05/04/18.
 */

public class ParametrosPagamento {

    private String TOKEN;
    private String SAVE_CARD;
    private String SAVED_CARD;
    private String EMAIL;
    private String PAYMENT_METHOD_ID;



    public ParametrosPagamento(String TOKEN, String SAVE_CARD, String PAYMENT_METHOD_ID){
        this.TOKEN = TOKEN;
        this.SAVE_CARD = SAVE_CARD;
        this.PAYMENT_METHOD_ID = PAYMENT_METHOD_ID;
    }

    public ParametrosPagamento(String TOKEN, String SAVED_CARD, String PAYMENT_METHOD_ID, String SAVE_CARD){
        this.TOKEN = TOKEN;
        this.SAVED_CARD = SAVED_CARD;
        this.PAYMENT_METHOD_ID = PAYMENT_METHOD_ID;
        this.SAVE_CARD = SAVE_CARD;
    }

    public String getToken() {
        return TOKEN;
    }

    public void setToken() {
        this.TOKEN = TOKEN;
    }


    public String getSaveCard() {
        return SAVE_CARD;
    }

    public void setSaveCard(String SAVE_CARD) {
        this.SAVE_CARD = SAVE_CARD;
    }


    public String getPAYMENT_METHOD_ID() {
        return PAYMENT_METHOD_ID;
    }

    public void setPAYMENT_METHOD_ID(String PAYMENT_METHOD_ID) {
        this.PAYMENT_METHOD_ID = PAYMENT_METHOD_ID;
    }

    public String getSAVED_CARD() {
        return SAVED_CARD;
    }

    public void setSAVED_CARD(String SAVED_CARD) {
        this.SAVED_CARD = SAVED_CARD;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

}
