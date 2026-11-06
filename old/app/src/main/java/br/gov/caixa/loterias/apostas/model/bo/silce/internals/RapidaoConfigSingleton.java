package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;

public class RapidaoConfigSingleton {

    private RapidaoDTO rapidaoConfig;
    private static RapidaoConfigSingleton rapidaoInstance;

    private RapidaoConfigSingleton(){ }

    public static RapidaoConfigSingleton getInstance(){
        if (rapidaoInstance == null){
            rapidaoInstance = new RapidaoConfigSingleton();
        }
        return rapidaoInstance;
    }

    public  RapidaoDTO getRapidaoConfig(){
        return rapidaoConfig;
    }
    public void setRapidaoConfig(RapidaoDTO rapidaoConfig){
        this.rapidaoConfig = rapidaoConfig;
    }
}
