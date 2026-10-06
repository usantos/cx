package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;

public class TimerSingleton {

    private ContagemRegressiva contagemRegressiva;
    private static TimerSingleton instance;

    private TimerSingleton(){ }

    public static TimerSingleton getInstance(){
        if (instance == null){
            instance = new TimerSingleton();
        }
        return instance;
    }

    public  ContagemRegressiva getContagemRegressiva(){
        return contagemRegressiva;
    }

    public void setContagemRegressiva(ContagemRegressiva contagemRegressiva){
        this.contagemRegressiva = contagemRegressiva;
    }

    public void cancel(){
        if (contagemRegressiva != null){
            contagemRegressiva.cancel();
        }
    }
}
