package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoDTO;

public class UltimaNotificacaoSingleton {
    private NotificacaoDTO ultimaNoficacao;
    private Boolean pagamentoNaoIdentificado;
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

    public void setPagamentoNaoIdentificado(Boolean pagamentoNaoIdentificado) {
        this.pagamentoNaoIdentificado = pagamentoNaoIdentificado;
    }

    public Boolean isPagamentoNaoIdentificado(){
        return this.pagamentoNaoIdentificado != null ? this.pagamentoNaoIdentificado : false;
    }
}
