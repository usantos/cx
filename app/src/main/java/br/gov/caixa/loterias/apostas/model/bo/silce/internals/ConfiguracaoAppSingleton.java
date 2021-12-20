package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

public class ConfiguracaoAppSingleton {

    private String urlBaseBuscaApostas;
    private Boolean isMicroServico;
    private static ConfiguracaoAppSingleton configuracaoAppSingleton;

    private ConfiguracaoAppSingleton(){ }

    public static ConfiguracaoAppSingleton getInstance(){
        if (configuracaoAppSingleton == null){
            configuracaoAppSingleton = new ConfiguracaoAppSingleton();
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
}
