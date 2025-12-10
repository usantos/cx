package br.gov.caixa.loterias.apostas.model.bo.silce.internals;


public class ConfiguracaoAppSingleton {

    public static final Boolean MOSTRA_MENU_APOSTAS_DEFAULT = true;
    public static final Boolean IS_MICRO_SERVICO_DEFAULT = false;

    private String urlBaseBuscaApostas;
    private Boolean isMicroServico;
    private Boolean mostraMenuAposta;
    private Boolean isAtualizaPublicKey;
    private Boolean isBolaoHabilitado;
    private Boolean isComboApostasHabilitado;
    private Integer grupoCpfNovaApi;
    private Boolean isNovaApi;

    private String urlBaseCarrinhoPiloto;
    private String urlBaseCarrinhoProducao;
    private String grupoDeCpfsQueAcessamOServicoDaAzure;

    private String urlBaseApostador;
    private String grupoDeCpfsApostador;

    private static ConfiguracaoAppSingleton configuracaoAppSingleton;

    private ConfiguracaoAppSingleton(){ }

    public static ConfiguracaoAppSingleton getInstance(){
        if (configuracaoAppSingleton == null){
            configuracaoAppSingleton = new ConfiguracaoAppSingleton();
            configuracaoAppSingleton.mostraMenuAposta = MOSTRA_MENU_APOSTAS_DEFAULT;
            configuracaoAppSingleton.isMicroServico = IS_MICRO_SERVICO_DEFAULT;
            configuracaoAppSingleton.isAtualizaPublicKey = Boolean.FALSE;
            configuracaoAppSingleton.isBolaoHabilitado = Boolean.FALSE;
            configuracaoAppSingleton.isComboApostasHabilitado = Boolean.FALSE;

            configuracaoAppSingleton.grupoCpfNovaApi = 0;
            configuracaoAppSingleton.isNovaApi = Boolean.FALSE;

            configuracaoAppSingleton.urlBaseCarrinhoPiloto="-";
            configuracaoAppSingleton.urlBaseCarrinhoProducao="-";
            configuracaoAppSingleton.grupoDeCpfsQueAcessamOServicoDaAzure = "";

            configuracaoAppSingleton.urlBaseApostador = "-";
            configuracaoAppSingleton.grupoDeCpfsApostador = "";
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

    public Boolean isComboApostasHabilitado() {
        return isComboApostasHabilitado;
    }

    public void setComboApostasHabilitado(Boolean comboApostasHabilitado) {
        isComboApostasHabilitado = comboApostasHabilitado;
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

    public String getUrlBaseCarrinhoPiloto() {
        return urlBaseCarrinhoPiloto;
    }

    public void setUrlBaseCarrinhoPiloto(String urlBaseCarrinhoPiloto) {
        this.urlBaseCarrinhoPiloto = urlBaseCarrinhoPiloto;
    }

    public String getUrlBaseCarrinhoProducao() {
        return urlBaseCarrinhoProducao;
    }

    public void setUrlBaseCarrinhoProducao(String urlBaseCarrinhoProducao) {
        this.urlBaseCarrinhoProducao = urlBaseCarrinhoProducao;
    }

    public String getGrupoDeCpfsQueAcessamOServicoDaAzure() {
        return grupoDeCpfsQueAcessamOServicoDaAzure;
    }

    public void setGrupoDeCpfsQueAcessamOServicoDaAzure(String grupoDeCpfsQueAcessamOServicoDaAzure) {
        this.grupoDeCpfsQueAcessamOServicoDaAzure = grupoDeCpfsQueAcessamOServicoDaAzure;
    }

    public String getUrlBaseApostador() {
        return urlBaseApostador;
    }

    public void setUrlBaseApostador(String urlBaseApostador) {
        this.urlBaseApostador = urlBaseApostador;
    }

    public String getGrupoDeCpfsApostador() {
        return grupoDeCpfsApostador;
    }

    public void setGrupoDeCpfsApostador(String grupoDeCpfsApostador) {
        this.grupoDeCpfsApostador = grupoDeCpfsApostador;
    }
}
