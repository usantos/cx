package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoDTO;

public class UltimaNotificacaoSingleton {
    private NotificacaoDTO ultimaNoficacao;
    private static UltimaNotificacaoSingleton ultimaNoficacaoInstance;

    private UltimaNotificacaoSingleton(){ }

    public static UltimaNotificacaoSingleton getInstance(){
        if (ultimaNoficacaoInstance == null){
            ultimaNoficacaoInstance = new UltimaNotificacaoSingleton();
        }
        return ultimaNoficacaoInstance;
    }

    public NotificacaoDTO getUltimaNoficacao() {
        return ultimaNoficacao;
    }

    public void setUltimaNoficacao(NotificacaoDTO ultimaNoficacao) {
        this.ultimaNoficacao = ultimaNoficacao;
    }
}
