package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


public class StatusBilhete {
	public static final int NAO_REGISTRADO = 1,
							PAGA_HISTORICO = 3,
							EFETIVADA = 4,
							PAGAMENTO_EM_PROCESSAMENTO = 7,
							NAO_PREMIADO = 9,
							PRESCRITO = 10,
							PREMIO_PAGO = 11,
							PREMIADO = 100,
							NAO_APURADO = 101,
							NAO_APURADO_AINDA_CONCORRENDO = 102,
							PREMIADO_AINDA_CONCORRENDO = 103;
}