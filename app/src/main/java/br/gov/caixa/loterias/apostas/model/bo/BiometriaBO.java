package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;

public class BiometriaBO {

    private static BiometriaBO instance;

    private Boolean biometriaHabilitada = false;


    private BiometriaBO(){
    }

    public static BiometriaBO getInstance(){
        if (instance == null){
            instance = new BiometriaBO();
        }
        return instance;
    }

    public Boolean getBiometriaHabilitada() {
        return biometriaHabilitada;
    }

    public void setBiometriaHabilitada(Boolean biometriaHabilitada) {
        this.biometriaHabilitada = biometriaHabilitada;
    }

}
