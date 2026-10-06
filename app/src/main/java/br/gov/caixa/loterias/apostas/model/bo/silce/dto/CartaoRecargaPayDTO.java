package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class CartaoRecargaPayDTO {

    @SerializedName("type")
    private String type;
    @SerializedName("brand")
    private String brand;
    @SerializedName("number")
    private String number;
    @SerializedName("cardHolder")
    private CardHolderRecargaPay cardHolder;
    @SerializedName("expirationDate")
    private DataExpiracao dataExpiracao;
    @SerializedName("securityCode")
    private String securityCode;
    @SerializedName("shouldSave")
    private boolean shouldSave = false;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public boolean isShouldSave() {
        return shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartaoRecargaPayDTO that = (CartaoRecargaPayDTO) o;
        return securityCode == that.securityCode && shouldSave == that.shouldSave && Objects.equals(type, that.type) && Objects.equals(brand, that.brand) && Objects.equals(number, that.number) && Objects.equals(cardHolder, that.cardHolder) && Objects.equals(dataExpiracao, that.dataExpiracao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, brand, number, cardHolder, dataExpiracao, securityCode, shouldSave);
    }

    @Override
    public String toString() {
        return "CartaoRecargaPayDTO{" +
                "type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", number='" + number + '\'' +
                ", cardHolder=" + cardHolder +
                ", dataExpiracao=" + dataExpiracao +
                ", securityCode=" + securityCode +
                ", shouldSave=" + shouldSave +
                '}';
    }
}
