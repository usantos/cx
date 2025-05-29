package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

public class FeatureToggleSingleton {
    private boolean isCards = true;
    private static FeatureToggleSingleton instance;

    private FeatureToggleSingleton(){}

    public static FeatureToggleSingleton getInstance(){
        if (instance == null){
            instance = new FeatureToggleSingleton();
        }
        return instance;
    }

    public boolean isEncerramentoCardsOn() {
        return isCards;
    }

    public void setCards(boolean cards) {
        isCards = cards;
    }

}
