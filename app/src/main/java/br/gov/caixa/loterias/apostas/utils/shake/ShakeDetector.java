package br.gov.caixa.loterias.apostas.utils.shake;

/** Detecta dois picos opostos, com intervalo e bloqueio entre gestos. */
public final class ShakeDetector {
    private static final float LIMIAR_QUADRADO = 2.7f * 2.7f * 9.81f * 9.81f;
    private long primeiroPico = -1;
    private long ultimoShake = -1;
    private float picoX, picoY, picoZ;

    public boolean detectar(float x, float y, float z, long tempoMs) {
        if (ultimoShake >= 0 && tempoMs - ultimoShake < 1500) return false;
        if (x * x + y * y + z * z < LIMIAR_QUADRADO) return false;
        if (primeiroPico >= 0 && tempoMs - primeiroPico >= 80
                && tempoMs - primeiroPico <= 500
                && x * picoX + y * picoY + z * picoZ < 0) {
            ultimoShake = tempoMs;
            primeiroPico = -1;
            return true;
        }
        if (primeiroPico < 0 || tempoMs - primeiroPico > 500) {
            primeiroPico = tempoMs;
            picoX = x; picoY = y; picoZ = z;
        }
        return false;
    }

    public void bloquear(long tempoMs) {
        primeiroPico = -1;
        ultimoShake = tempoMs;
    }

    public void reset() {
        primeiroPico = -1;
        ultimoShake = -1;
    }
}
