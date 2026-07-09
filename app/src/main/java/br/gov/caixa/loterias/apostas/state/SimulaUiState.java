package br.gov.caixa.loterias.apostas.state;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;

public class SimulaUiState {

    private final BarraTituloDTO barraTituloDTO;

    private final boolean exibirAnimacao;
    private final int animacaoRes;
    private final float velocidadeAnimacao;

    private final ComandoCarrinho comandoCarrinho;
    private final ComandoCompletarAposta comandoCompletarAposta;

    private final ModalidadeEnum tipoJogo;

    private final List<Integer> dezenasSelecionadas;
    private final ParametroEquipe equipeSelecionada;

    private final boolean telaSelecaoTimeAtivado;
    private final boolean escolhaTimeCoracaoSurpresinha;

    private final int qtdDezenasPossiveisSelecionado;
    private final int qtdConcursoSelecionado;

    private final boolean mostrarBotaoLimparAposta;
    private final boolean mostrarSalvarAposta;
    private final boolean mostrarRodape;

    private final boolean limparNumerosCartela;
    private final boolean limparTimeCoracao;
    private final boolean limparSuperSete;
    private final boolean limparLoteca;
    private final boolean limparLotogol;
    private final boolean limparOpcaoOutrosNumeros;

    private final int qtdTotalSelecionadosSuperSete;

    private final int jogosLoteca;
    private final int simplesLoteca;
    private final int duplasLoteca;
    private final int triplasLoteca;

    private final boolean botaoFinalizarVisivel;
    private final int textoBotaoFinalizarResId;

    public SimulaUiState(
            BarraTituloDTO barraTituloDTO,
            boolean exibirAnimacao,
            int animacaoRes,
            float velocidadeAnimacao,
            ComandoCarrinho comandoCarrinho
    ) {
        this(
                new Builder()
                        .setBarraTituloDTO(barraTituloDTO)
                        .setExibirAnimacao(exibirAnimacao)
                        .setAnimacaoRes(animacaoRes)
                        .setVelocidadeAnimacao(velocidadeAnimacao)
                        .setComandoCarrinho(comandoCarrinho)
        );
    }

    private SimulaUiState(
            Builder builder
    ) {
        this.barraTituloDTO = builder.barraTituloDTO != null
                ? builder.barraTituloDTO
                : new BarraTituloDTO();

        this.exibirAnimacao = builder.exibirAnimacao;
        this.animacaoRes = builder.animacaoRes;
        this.velocidadeAnimacao = builder.velocidadeAnimacao;

        this.comandoCarrinho = builder.comandoCarrinho;
        this.comandoCompletarAposta = builder.comandoCompletarAposta;

        this.tipoJogo = builder.tipoJogo;

        this.dezenasSelecionadas = builder.dezenasSelecionadas != null
                ? new ArrayList<>(builder.dezenasSelecionadas)
                : new ArrayList<>();

        this.equipeSelecionada = builder.equipeSelecionada;

        this.telaSelecaoTimeAtivado = builder.telaSelecaoTimeAtivado;
        this.escolhaTimeCoracaoSurpresinha = builder.escolhaTimeCoracaoSurpresinha;

        this.qtdDezenasPossiveisSelecionado = builder.qtdDezenasPossiveisSelecionado;
        this.qtdConcursoSelecionado = builder.qtdConcursoSelecionado;

        this.mostrarBotaoLimparAposta = builder.mostrarBotaoLimparAposta;
        this.mostrarSalvarAposta = builder.mostrarSalvarAposta;
        this.mostrarRodape = builder.mostrarRodape;

        this.limparNumerosCartela = builder.limparNumerosCartela;
        this.limparTimeCoracao = builder.limparTimeCoracao;
        this.limparSuperSete = builder.limparSuperSete;
        this.limparLoteca = builder.limparLoteca;
        this.limparLotogol = builder.limparLotogol;
        this.limparOpcaoOutrosNumeros = builder.limparOpcaoOutrosNumeros;

        this.qtdTotalSelecionadosSuperSete = builder.qtdTotalSelecionadosSuperSete;

        this.jogosLoteca = builder.jogosLoteca;
        this.simplesLoteca = builder.simplesLoteca;
        this.duplasLoteca = builder.duplasLoteca;
        this.triplasLoteca = builder.triplasLoteca;

        this.botaoFinalizarVisivel = builder.botaoFinalizarVisivel;
        this.textoBotaoFinalizarResId = builder.textoBotaoFinalizarResId;
    }

    public static SimulaUiState initial() {
        return new Builder()
                .setBarraTituloDTO(new BarraTituloDTO())
                .setExibirAnimacao(false)
                .setAnimacaoRes(0)
                .setVelocidadeAnimacao(1f)
                .setComandoCarrinho(null)
                .setComandoCompletarAposta(null)
                .setTipoJogo(null)
                .setDezenasSelecionadas(new ArrayList<Integer>())
                .setEquipeSelecionada(null)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setQtdDezenasPossiveisSelecionado(0)
                .setQtdConcursoSelecionado(0)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setMostrarRodape(false)
                .setLimparNumerosCartela(false)
                .setLimparTimeCoracao(false)
                .setLimparSuperSete(false)
                .setLimparLoteca(false)
                .setLimparLotogol(false)
                .setLimparOpcaoOutrosNumeros(false)
                .setQtdTotalSelecionadosSuperSete(0)
                .setJogosLoteca(0)
                .setSimplesLoteca(0)
                .setDuplasLoteca(0)
                .setTriplasLoteca(0)
                .setBotaoFinalizarVisivel(true)
                .setTextoBotaoFinalizarResId(0)
                .build();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public BarraTituloDTO getBarraTituloDTO() {
        return barraTituloDTO;
    }

    public boolean isExibirAnimacao() {
        return exibirAnimacao;
    }

    public int getAnimacaoRes() {
        return animacaoRes;
    }

    public float getVelocidadeAnimacao() {
        return velocidadeAnimacao;
    }

    public ComandoCarrinho getComandoCarrinho() {
        return comandoCarrinho;
    }
    public ComandoCompletarAposta getComandoCompletarAposta() {
        return comandoCompletarAposta;
    }
    public ModalidadeEnum getTipoJogo() {
        return tipoJogo;
    }

    public List<Integer> getDezenasSelecionadas() {
        return new ArrayList<>(dezenasSelecionadas);
    }

    public ParametroEquipe getEquipeSelecionada() {
        return equipeSelecionada;
    }

    public boolean isTelaSelecaoTimeAtivado() {
        return telaSelecaoTimeAtivado;
    }

    public boolean isEscolhaTimeCoracaoSurpresinha() {
        return escolhaTimeCoracaoSurpresinha;
    }

    public int getQtdDezenasPossiveisSelecionado() {
        return qtdDezenasPossiveisSelecionado;
    }

    public int getQtdConcursoSelecionado() {
        return qtdConcursoSelecionado;
    }

    public boolean isMostrarBotaoLimparAposta() {
        return mostrarBotaoLimparAposta;
    }

    public boolean isMostrarSalvarAposta() {
        return mostrarSalvarAposta;
    }

    public boolean isMostrarRodape() {
        return mostrarRodape;
    }

    public boolean isLimparNumerosCartela() {
        return limparNumerosCartela;
    }

    public boolean isLimparTimeCoracao() {
        return limparTimeCoracao;
    }

    public boolean isLimparSuperSete() {
        return limparSuperSete;
    }

    public boolean isLimparLoteca() {
        return limparLoteca;
    }

    public boolean isLimparLotogol() {
        return limparLotogol;
    }

    public boolean isLimparOpcaoOutrosNumeros() {
        return limparOpcaoOutrosNumeros;
    }

    public int getQtdTotalSelecionadosSuperSete() {
        return qtdTotalSelecionadosSuperSete;
    }

    public int getJogosLoteca() {
        return jogosLoteca;
    }

    public int getSimplesLoteca() {
        return simplesLoteca;
    }

    public int getDuplasLoteca() {
        return duplasLoteca;
    }

    public int getTriplasLoteca() {
        return triplasLoteca;
    }

    public boolean isBotaoFinalizarVisivel() {
        return botaoFinalizarVisivel;
    }

    public int getTextoBotaoFinalizarResId() {
        return textoBotaoFinalizarResId;
    }

    public static class Builder {

        private BarraTituloDTO barraTituloDTO;

        private boolean exibirAnimacao;
        private int animacaoRes;
        private float velocidadeAnimacao;

        private ComandoCarrinho comandoCarrinho;

        private ModalidadeEnum tipoJogo;

        private List<Integer> dezenasSelecionadas;
        private ParametroEquipe equipeSelecionada;

        private boolean telaSelecaoTimeAtivado;
        private boolean escolhaTimeCoracaoSurpresinha;

        private int qtdDezenasPossiveisSelecionado;
        private int qtdConcursoSelecionado;

        private boolean mostrarBotaoLimparAposta;
        private boolean mostrarSalvarAposta;
        private boolean mostrarRodape;

        private boolean limparNumerosCartela;
        private boolean limparTimeCoracao;
        private boolean limparSuperSete;
        private boolean limparLoteca;
        private boolean limparLotogol;
        private boolean limparOpcaoOutrosNumeros;

        private int qtdTotalSelecionadosSuperSete;

        private int jogosLoteca;
        private int simplesLoteca;
        private int duplasLoteca;
        private int triplasLoteca;

        private boolean botaoFinalizarVisivel;
        private int textoBotaoFinalizarResId;
        private ComandoCompletarAposta comandoCompletarAposta;

        public Builder() {
        }

        public Builder(
                SimulaUiState state
        ) {
            this.barraTituloDTO = state.barraTituloDTO;

            this.exibirAnimacao = state.exibirAnimacao;
            this.animacaoRes = state.animacaoRes;
            this.velocidadeAnimacao = state.velocidadeAnimacao;

            this.comandoCarrinho = state.comandoCarrinho;
            this.comandoCompletarAposta = state.comandoCompletarAposta;

            this.tipoJogo = state.tipoJogo;

            this.dezenasSelecionadas = state.dezenasSelecionadas != null
                    ? new ArrayList<>(state.dezenasSelecionadas)
                    : new ArrayList<>();

            this.equipeSelecionada = state.equipeSelecionada;

            this.telaSelecaoTimeAtivado = state.telaSelecaoTimeAtivado;
            this.escolhaTimeCoracaoSurpresinha = state.escolhaTimeCoracaoSurpresinha;

            this.qtdDezenasPossiveisSelecionado = state.qtdDezenasPossiveisSelecionado;
            this.qtdConcursoSelecionado = state.qtdConcursoSelecionado;

            this.mostrarBotaoLimparAposta = state.mostrarBotaoLimparAposta;
            this.mostrarSalvarAposta = state.mostrarSalvarAposta;
            this.mostrarRodape = state.mostrarRodape;

            this.limparNumerosCartela = state.limparNumerosCartela;
            this.limparTimeCoracao = state.limparTimeCoracao;
            this.limparSuperSete = state.limparSuperSete;
            this.limparLoteca = state.limparLoteca;
            this.limparLotogol = state.limparLotogol;
            this.limparOpcaoOutrosNumeros = state.limparOpcaoOutrosNumeros;

            this.qtdTotalSelecionadosSuperSete = state.qtdTotalSelecionadosSuperSete;

            this.jogosLoteca = state.jogosLoteca;
            this.simplesLoteca = state.simplesLoteca;
            this.duplasLoteca = state.duplasLoteca;
            this.triplasLoteca = state.triplasLoteca;

            this.botaoFinalizarVisivel = state.botaoFinalizarVisivel;
            this.textoBotaoFinalizarResId = state.textoBotaoFinalizarResId;
        }

        public Builder setBarraTituloDTO(
                BarraTituloDTO barraTituloDTO
        ) {
            this.barraTituloDTO = barraTituloDTO;
            return this;
        }

        public Builder setExibirAnimacao(
                boolean exibirAnimacao
        ) {
            this.exibirAnimacao = exibirAnimacao;
            return this;
        }

        public Builder setAnimacaoRes(
                int animacaoRes
        ) {
            this.animacaoRes = animacaoRes;
            return this;
        }

        public Builder setVelocidadeAnimacao(
                float velocidadeAnimacao
        ) {
            this.velocidadeAnimacao = velocidadeAnimacao;
            return this;
        }

        public Builder setComandoCarrinho(
                ComandoCarrinho comandoCarrinho
        ) {
            this.comandoCarrinho = comandoCarrinho;
            return this;
        }
        public Builder setComandoCompletarAposta(
                ComandoCompletarAposta comandoCompletarAposta
        ) {
            this.comandoCompletarAposta = comandoCompletarAposta;
            return this;
        }

        public Builder setTipoJogo(
                ModalidadeEnum tipoJogo
        ) {
            this.tipoJogo = tipoJogo;
            return this;
        }

        public Builder setDezenasSelecionadas(
                List<Integer> dezenasSelecionadas
        ) {
            this.dezenasSelecionadas = dezenasSelecionadas != null
                    ? new ArrayList<>(dezenasSelecionadas)
                    : new ArrayList<Integer>();
            return this;
        }

        public Builder setEquipeSelecionada(
                ParametroEquipe equipeSelecionada
        ) {
            this.equipeSelecionada = equipeSelecionada;
            return this;
        }

        public Builder setTelaSelecaoTimeAtivado(
                boolean telaSelecaoTimeAtivado
        ) {
            this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
            return this;
        }

        public Builder setEscolhaTimeCoracaoSurpresinha(
                boolean escolhaTimeCoracaoSurpresinha
        ) {
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            return this;
        }

        public Builder setQtdDezenasPossiveisSelecionado(
                int qtdDezenasPossiveisSelecionado
        ) {
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
            return this;
        }

        public Builder setQtdConcursoSelecionado(
                int qtdConcursoSelecionado
        ) {
            this.qtdConcursoSelecionado = qtdConcursoSelecionado;
            return this;
        }

        public Builder setMostrarBotaoLimparAposta(
                boolean mostrarBotaoLimparAposta
        ) {
            this.mostrarBotaoLimparAposta = mostrarBotaoLimparAposta;
            return this;
        }

        public Builder setMostrarSalvarAposta(
                boolean mostrarSalvarAposta
        ) {
            this.mostrarSalvarAposta = mostrarSalvarAposta;
            return this;
        }

        public Builder setMostrarRodape(
                boolean mostrarRodape
        ) {
            this.mostrarRodape = mostrarRodape;
            return this;
        }

        public Builder setLimparNumerosCartela(
                boolean limparNumerosCartela
        ) {
            this.limparNumerosCartela = limparNumerosCartela;
            return this;
        }

        public Builder setLimparTimeCoracao(
                boolean limparTimeCoracao
        ) {
            this.limparTimeCoracao = limparTimeCoracao;
            return this;
        }

        public Builder setLimparSuperSete(
                boolean limparSuperSete
        ) {
            this.limparSuperSete = limparSuperSete;
            return this;
        }

        public Builder setLimparLoteca(
                boolean limparLoteca
        ) {
            this.limparLoteca = limparLoteca;
            return this;
        }

        public Builder setLimparLotogol(
                boolean limparLotogol
        ) {
            this.limparLotogol = limparLotogol;
            return this;
        }

        public Builder setLimparOpcaoOutrosNumeros(
                boolean limparOpcaoOutrosNumeros
        ) {
            this.limparOpcaoOutrosNumeros = limparOpcaoOutrosNumeros;
            return this;
        }

        public Builder setQtdTotalSelecionadosSuperSete(
                int qtdTotalSelecionadosSuperSete
        ) {
            this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
            return this;
        }

        public Builder setJogosLoteca(
                int jogosLoteca
        ) {
            this.jogosLoteca = jogosLoteca;
            return this;
        }

        public Builder setSimplesLoteca(
                int simplesLoteca
        ) {
            this.simplesLoteca = simplesLoteca;
            return this;
        }

        public Builder setDuplasLoteca(
                int duplasLoteca
        ) {
            this.duplasLoteca = duplasLoteca;
            return this;
        }

        public Builder setTriplasLoteca(
                int triplasLoteca
        ) {
            this.triplasLoteca = triplasLoteca;
            return this;
        }

        public Builder setBotaoFinalizarVisivel(
                boolean botaoFinalizarVisivel
        ) {
            this.botaoFinalizarVisivel = botaoFinalizarVisivel;
            return this;
        }

        public Builder setTextoBotaoFinalizarResId(
                int textoBotaoFinalizarResId
        ) {
            this.textoBotaoFinalizarResId = textoBotaoFinalizarResId;
            return this;
        }

        public SimulaUiState build() {
            return new SimulaUiState(this);
        }
    }
}