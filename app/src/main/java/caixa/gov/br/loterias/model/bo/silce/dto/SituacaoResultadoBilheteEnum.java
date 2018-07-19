package caixa.gov.br.loterias.model.bo.silce.dto;

/**
 * Created by joafilho on 27/03/2018.
 * Enum SituacaoResultadoBilheteEnum
 */

public enum SituacaoResultadoBilheteEnum {

    PAGA_HISTORICO(3L),
    NAO_PREMIADO(9L),
    PRESCRITA(10L),
    PAGA(11L),
    PREMIADA(100L),
    NAO_APURADO(101L);

    private Long valor;

    SituacaoResultadoBilheteEnum(Long valor) {
        this.valor = valor;
    }

    public Long getValor() {
        return valor;
    }
}
