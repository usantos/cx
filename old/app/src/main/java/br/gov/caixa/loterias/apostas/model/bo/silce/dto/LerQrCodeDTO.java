package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

public class LerQrCodeDTO {

    @SerializedName("pro")
    protected String pro = null;

    @SerializedName("ci")
    protected String ci = null;

    @SerializedName("cf")
    protected String cf = null;

    @SerializedName("vlt")
    protected String vlt = null;

    @SerializedName("tm")
    protected String tm = null;

    @SerializedName("ul")
    protected String ul = null;

    @SerializedName("nsb")
    protected String nsb = null;

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getVlt() {
        return vlt;
    }

    public void setVlt(String vlt) {
        this.vlt = vlt;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getUl() {
        return ul;
    }

    public void setUl(String ul) {
        this.ul = ul;
    }

    public String getNsb() {
        return nsb;
    }
    public void setNsb(String nsb) {
        this.nsb = nsb;
    }
}
