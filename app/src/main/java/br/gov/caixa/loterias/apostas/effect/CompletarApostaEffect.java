package br.gov.caixa.loterias.apostas.effect;

public abstract class CompletarApostaEffect {

    private CompletarApostaEffect() {
    }

    public static final class PreencherNumerosAleatorios extends CompletarApostaEffect {
    }
    public static final class SelecionarMesSorteAleatorio
            extends CompletarApostaEffect {
    }

    public static final class SelecionarTrevosAleatorios
            extends CompletarApostaEffect {
    }
    public static final class PreencherSuperSete extends CompletarApostaEffect {
    }

    public static final class AbrirMesDeSorte extends CompletarApostaEffect {
    }

    public static final class AbrirTrevos extends CompletarApostaEffect {
    }

    public static final class MostrarEscolhaTimeCoracao extends CompletarApostaEffect {
    }

    public static final class AdicionarTimeSurpresa extends CompletarApostaEffect {
    }

    public static final class ZerarLoteca extends CompletarApostaEffect {
    }

    public static final class ApostaEffectProntaParaAdicionar extends CompletarApostaEffect {

        private final boolean limparLotecaDepois;

        public ApostaEffectProntaParaAdicionar(
                boolean limparLotecaDepois
        ) {
            this.limparLotecaDepois = limparLotecaDepois;
        }

        public boolean isLimparLotecaDepois() {
            return limparLotecaDepois;
        }
    }
}