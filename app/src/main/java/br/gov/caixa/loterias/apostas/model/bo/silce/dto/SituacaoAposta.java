package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

public class SituacaoAposta {
	public static final int PREMIADA = 100;
	public static final int PREMIO_PAGO = 11;
	public static final int NAO_PREMIADA = 9;
	public static final int CONCURSO_NAO_APURADO = 101;
	public static final int NAO_PREMIADA_CONCORRENDO_A_CONCURSO_FUTURO = 102;
	public static final int PRESCRITA = 10;
	public static final int NAO_PREMIADA_CONCORRENDO_CONCURSO_FUTURO = 4;
	public static final int EFETIVADA = 4;

	public enum EnumSituacaoAposta {
		PREMIADA(SituacaoAposta.PREMIADA, "Premiada"),
		PREMIO_PAGO(SituacaoAposta.PREMIO_PAGO, "Prêmio pago"),
		NAO_PREMIADA(SituacaoAposta.NAO_PREMIADA, "Aposta não premiada"),
		CONCURSO_NAO_APURADO(SituacaoAposta.CONCURSO_NAO_APURADO, "Concurso não apurado"),
		NAO_PREMIADA_CONCORRENDO_A_CONCURSO_FUTURO(SituacaoAposta.NAO_PREMIADA_CONCORRENDO_A_CONCURSO_FUTURO,"Aposta não premiada concorrendo em concursos futuros"),
		PRESCRITA(SituacaoAposta.PRESCRITA, "Aposta prescrita"),
		EFETIVADA(SituacaoAposta.EFETIVADA, "Aposta efetivada");

		private final int code;
		private final String descricao;

		EnumSituacaoAposta(int code, String descricao) {
			this.code = code;
			this.descricao = descricao;
		}

		public String getDescricao() {
			return descricao;
		}

		public static EnumSituacaoAposta fromCode(int code) {
			for (EnumSituacaoAposta e : values()) {
				if (e.code == code) {
					return e;
				}
			}
			return null;
		}


	}
}