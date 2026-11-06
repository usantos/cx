package br.gov.caixa.loterias.apostas.model.bean;

public class BotoesFiltroApostas {

    /*
    * Para teimosinha - Se passar 1 quer dizer que vc que ver todas as apostas com ou sem teimosinha
    se passar 2 quer dizer que ver apostas sem teimosinha
    se passar 3 quer dizer que ver apostas com teimosinha
    por ser um botão usamos so o 1 ou 3 mas ja deixamos ali o 2 para casos futuros se gestor mudar a forma como deve funcionar ali o filtro.
    * */
    private int teimosinha;

    /*
    * Para surpresinha - Se passar 1 quer dizer que vc que ver todas as apostas com ou sem surpresinha
    se passar 2 quer dizer que ver apostas sem surpresinha
    se passar 3 quer dizer que ver apostas com surpresinha
    por ser um botão usamos so o 1 ou 3 mas ja deixamos ali o 2 para casos futuros se gestor mudar a forma como deve funcionar ali o filtro.
    * */
    private int surpresinha;

    /*
    Para combo de aposta- Se passar 1 quer dizer que vc que ver todas as apostas com ou sem combo de aposta
    se passar 2 quer dizer que ver apostas sem combo de aposta
    se passar 3 quer dizer que ver apostas com combo de aposta
    por ser um botão usamos so o 1 ou 3 mas ja deixamos ali o 2 para casos futuros se gestor mudar a forma como deve funcionar ali o filtro.
     */
    private int combo;

    public BotoesFiltroApostas(int teimosinha, int surpresinha, int combo) {
        this.teimosinha = teimosinha;
        this.surpresinha = surpresinha;
        this.combo = combo;
    }
    public int getTeimosinha() {
        return teimosinha;
    }
    public void setTeimosinha(int teimosinha) {
        this.teimosinha = teimosinha;
    }

    public int getSurpresinha() {
        return surpresinha;
    }
    public void setSurpresinha(int surpresinha) {
        this.surpresinha = surpresinha;
    }

    public int getCombo() {
        return combo;
    }
    public void setCombo(int combo) {
        this.combo = combo;
    }

}
