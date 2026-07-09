package br.gov.caixa.loterias.apostas.state;

public abstract class ComandoCompletarAposta {

    private ComandoCompletarAposta() {
    }

    public static final class PreencherNumerosAleatorios extends ComandoCompletarAposta {
    }

    public static final class PreencherSuperSete extends ComandoCompletarAposta {
    }

    public static final class AbrirMesDeSorte extends ComandoCompletarAposta {
    }

    public static final class AbrirTrevos extends ComandoCompletarAposta {
    }

    public static final class MostrarEscolhaTimeCoracao extends ComandoCompletarAposta {
    }

    public static final class SelecionarTimeAleatorio extends ComandoCompletarAposta {
    }

    public static final class AdicionarTimeSurpresa extends ComandoCompletarAposta {
    }

    public static final class ZerarLoteca extends ComandoCompletarAposta {
    }

    public static final class ApostaProntaParaAdicionar extends ComandoCompletarAposta {

        private final boolean limparLotecaDepois;

        public ApostaProntaParaAdicionar(
                boolean limparLotecaDepois
        ) {
            this.limparLotecaDepois = limparLotecaDepois;
        }

        public boolean isLimparLotecaDepois() {
            return limparLotecaDepois;
        }
    }
}