package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class CartaoRecargaPayResponse {

    @SerializedName("customerCardToken")
    private String customerCardToken;
    @SerializedName("type")
    private String type;
    @SerializedName("brand")
    private String brand;
    @SerializedName("last4")
    private String last4;
    @SerializedName("bin")
    private String bin;
    @SerializedName("cardHolder")
    private CardHolderRecargaPay cardHolder;
    @SerializedName("expirationDate")
    private DataExpiracao dataExpiracao;

    public String getCustomerCardToken() {
        return customerCardToken;
    }

    public void setCustomerCardToken(String customerCardToken) {
        this.customerCardToken = customerCardToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public CardHolderRecargaPay getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(CardHolderRecargaPay cardHolder) {
        this.cardHolder = cardHolder;
    }

    public DataExpiracao getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(DataExpiracao dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartaoRecargaPayResponse that = (CartaoRecargaPayResponse) o;
        return Objects.equals(customerCardToken, that.customerCardToken) && Objects.equals(type, that.type) && Objects.equals(brand, that.brand) && Objects.equals(last4, that.last4) && Objects.equals(bin, that.bin) && Objects.equals(cardHolder, that.cardHolder) && Objects.equals(dataExpiracao, that.dataExpiracao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerCardToken, type, brand, last4, bin, cardHolder, dataExpiracao);
    }

    @Override
    public String toString() {
        return "CartaoRecargaPayResponse{" +
                "customerCardToken='" + customerCardToken + '\'' +
                ", type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", last4='" + last4 + '\'' +
                ", bin='" + bin + '\'' +
                ", cardHolder=" + cardHolder +
                ", dataExpiracao=" + dataExpiracao +
                '}';
    }
}
