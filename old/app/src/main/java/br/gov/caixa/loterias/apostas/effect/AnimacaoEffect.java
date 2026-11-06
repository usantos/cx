package br.gov.caixa.loterias.apostas.effect;

public abstract class AnimacaoEffect {

    public static class Exibir extends AnimacaoEffect {

        private final int animacaoRes;
        private final float velocidade;

        public Exibir(
                int animacaoRes,
                float velocidade
        ) {
            this.animacaoRes = animacaoRes;
            this.velocidade = velocidade;
        }

        public int getAnimacaoRes() {
            return animacaoRes;
        }

        public float getVelocidade() {
            return velocidade;
        }
    }
}