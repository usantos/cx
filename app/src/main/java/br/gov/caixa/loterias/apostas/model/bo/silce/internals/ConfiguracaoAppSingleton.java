package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import com.google.gson.annotations.SerializedName;

public class ConfiguracaoAppSingleton {

    public static final Boolean MOSTRA_MENU_APOSTAS_DEFAULT = true;
    public static final Boolean IS_MICRO_SERVICO_DEFAULT = false;

    private String urlBaseBuscaApostas;
    private Boolean isMicroServico;
    private Boolean mostraMenuAposta;
    private Boolean isAtualizaPublicKey;
    private Boolean isBolaoHabilitado;

    private String urlBaseNovaApiPRD;
    private String urlBaseNovaApiPLT;
    private Integer grupoCpfNovaApi;
    private Boolean isNovaApi;

    private static ConfiguracaoAppSingleton configuracaoAppSingleton;

    private ConfiguracaoAppSingleton(){ }

    public static ConfiguracaoAppSingleton getInstance(){
        if (configuracaoAppSingleton == null){
            configuracaoAppSingleton = new ConfiguracaoAppSingleton();
            configuracaoAppSingleton.mostraMenuAposta = MOSTRA_MENU_APOSTAS_DEFAULT;
            configuracaoAppSingleton.isMicroServico = IS_MICRO_SERVICO_DEFAULT;
            configuracaoAppSingleton.isAtualizaPublicKey = Boolean.FALSE;
            configuracaoAppSingleton.isBolaoHabilitado = Boolean.FALSE;
            configuracaoAppSingleton.urlBaseNovaApiPRD = "";
            configuracaoAppSingleton.urlBaseNovaApiPLT = "";
            configuracaoAppSingleton.grupoCpfNovaApi = 0;
            configuracaoAppSingleton.isNovaApi = Boolean.FALSE;
        }
        return configuracaoAppSingleton;
    }

    public void setUrlBaseBuscaApostas(String url){
        this.urlBaseBuscaApostas = url;
    }

    public String getUrlBaseBuscaApostas(){
        return this.urlBaseBuscaApostas;
    }

	public void setIsMicroServico(Boolean isMicroServico) {
        this.isMicroServico = isMicroServico;
	}

	public Boolean isMicroServico(){
        return  this.isMicroServico;
    }

    public Boolean isMostraMenuAposta() {
        return mostraMenuAposta;
    }

    public void setMostraMenuAposta(Boolean mostraMenuAposta) {
        this.mostraMenuAposta = mostraMenuAposta;
    }

    public boolean isAtualizaPublicKey(){
        return isAtualizaPublicKey;
    }

    public void setAtualizaPublicKey(Boolean atualiza){
        isAtualizaPublicKey = atualiza;
    }

    public Boolean isBolaoHabilitado() {
        return isBolaoHabilitado;
    }

    public void setBolaoHabilitado(Boolean bolaoHabilitado) {
        isBolaoHabilitado = bolaoHabilitado;
    }

    public String getUrlBaseNovaApiPRD() {
        return urlBaseNovaApiPRD;
    }

    public void setUrlBaseNovaApiPRD(String urlBaseNovaApiPRD) {
        this.urlBaseNovaApiPRD = urlBaseNovaApiPRD;
    }

    public String getUrlBaseNovaApiPLT() {
        return urlBaseNovaApiPLT;
    }

    public void setUrlBaseNovaApiPLT(String urlBaseNovaApiPLT) {
        this.urlBaseNovaApiPLT = urlBaseNovaApiPLT;
    }

    public Integer getGrupoCpfNovaApi() {
        return grupoCpfNovaApi;
    }

    public void setGrupoCpfNovaApi(Integer grupoCpfNovaApi) {
        this.grupoCpfNovaApi = grupoCpfNovaApi;
    }

    public Boolean isNovaApi() {
        return isNovaApi;
    }

    public void setNovaApi(Boolean novaApi) {
        isNovaApi = novaApi;
    }
}
