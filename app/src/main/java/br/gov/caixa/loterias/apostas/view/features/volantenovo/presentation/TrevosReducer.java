package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class TrevosReducer {

    public SimulaUiState toggleTrevo(
            SimulaUiState atual,
            SimulaUiEvent.ToggleTrevo event,
            List<ParametroValorApostaDTO> valoresTrevosDisponiveis
    ) {
        if (atual == null || event == null) {
            return atual;
        }

        List<Integer> trevos =
                criarListaTrevosSegura(
                        atual.getTrevosSelecionados()
                );

        Integer numero =
                event.getNumeroTrevo();

        boolean jaSelecionado =
                trevos.contains(numero);

        if (jaSelecionado) {
            trevos.remove(numero);
        } else {
            trevos.add(numero);
        }

        List<ParametroValorApostaDTO> valores =
                resolverValoresTrevosDisponiveis(
                        atual,
                        valoresTrevosDisponiveis
                );

        int quantidadeSelecionada =
                trevos.size();

        int quantidadeNecessaria =
                calcularQuantidadeNecessariaPorSelecao(
                        quantidadeSelecionada,
                        valores
                );

        ParametroValorApostaDTO valorAtualizado =
                obterValorTrevosPorQuantidade(
                        valores,
                        quantidadeNecessaria
                );

        if (valorAtualizado == null) {
            valorAtualizado =
                    atual.getValorTrevosSelecionado();
        }

        if (valorAtualizado == null
                || valorAtualizado.getNumeroTrevos() == null) {
            return atual.copy()
                    .setTrevosSelecionados(trevos)
                    .setQuantidadeTrevosSelecionada(quantidadeNecessaria)
                    .setBotaoAdicionarHabilitado(false)
                    .setBotaoCompletarHabilitado(true)
                    .setBotaoCompletarVisivel(true)
                    .setBotaoLimparHabilitado(quantidadeSelecionada > 0)
                    .setMostrarBotaoLimparAposta(quantidadeSelecionada > 0)
                    .setMostrarSalvarAposta(false)
                    .setTextoSelecionados(
                            montarTextoTrevosSelecionados(
                                    quantidadeSelecionada,
                                    quantidadeNecessaria
                            )
                    );
        }

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        valorAtualizado.getValor()
                );

        boolean apostaCompleta =
                quantidadeSelecionada == quantidadeNecessaria
                        && quantidadeSelecionada >= 2;

        boolean aumentouQuantidadeTrevos =
                !jaSelecionado
                        && atual.getValorTrevosSelecionado() != null
                        && atual.getValorTrevosSelecionado().getNumeroTrevos() != null
                        && quantidadeNecessaria
                        > atual.getValorTrevosSelecionado().getNumeroTrevos();

        return atual.copy()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setValoresTrevos(valores)
                .setTrevosSelecionados(trevos)
                .setValorTrevosSelecionado(valorAtualizado)
                .setQuantidadeTrevosSelecionada(quantidadeNecessaria)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setBotaoAdicionarHabilitado(apostaCompleta)
                .setBotaoCompletarHabilitado(!apostaCompleta)
                .setBotaoCompletarVisivel(!apostaCompleta)
                .setBotaoLimparHabilitado(quantidadeSelecionada > 0)
                .setMostrarBotaoLimparAposta(quantidadeSelecionada > 0)
                .setMostrarSalvarAposta(apostaCompleta)
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                quantidadeSelecionada,
                                quantidadeNecessaria
                        )
                )
                .setTextoBotaoQuantidadeTrevos(
                        montarTextoBotaoTrevos(
                                quantidadeNecessaria
                        )
                )
                .setPosicaoTrevosSelecionada(
                        obterPosicaoValorTrevos(
                                valores,
                                quantidadeNecessaria
                        )
                )
                .setExibirAvisoTrevosMaximo(
                        aumentouQuantidadeTrevos
                                && !atual.isAvisouTrevosMaximo()
                )
                .setAvisouTrevosMaximo(
                        atual.isAvisouTrevosMaximo()
                                || aumentouQuantidadeTrevos
                );
    }

    public SimulaUiState trevosAlterados(
            SimulaUiState atual,
            SimulaUiEvent.TrevosAlterados event
    ) {

        if (atual == null
                || event == null
                || event.getValorAposta() == null) {
            return atual;
        }

        int quantidadeSelecionada =
                event.getQuantidadeTrevosSelecionados();

        int quantidadeNecessaria =
                event.getQuantidadeTrevosNecessaria();

        boolean possuiTrevosSelecionados =
                quantidadeSelecionada > 0;

        boolean apostaCompleta =
                quantidadeSelecionada == quantidadeNecessaria;

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        event.getValorAposta().getValor()
                );

        List<Integer> trevosSelecionados =
                criarListaTrevosSegura(
                        event.getTrevosSelecionados()
                );

        return atual.copy()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setBotaoAdicionarHabilitado(apostaCompleta)
                .setBotaoCompletarHabilitado(!apostaCompleta)
                .setBotaoCompletarVisivel(!apostaCompleta)
                .setBotaoLimparHabilitado(possuiTrevosSelecionados)
                .setMostrarBotaoLimparAposta(possuiTrevosSelecionados)
                .setMostrarSalvarAposta(apostaCompleta)
                .setTrevosSelecionados(trevosSelecionados)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                quantidadeSelecionada,
                                quantidadeNecessaria
                        )
                );
    }

    public SimulaUiState quantidadeTrevosAlterada(
            SimulaUiState atual,
            SimulaUiEvent.QuantidadeTrevosAlterada event
    ) {

        if (atual == null
                || event == null
                || event.getValorSelecionado() == null) {
            return atual;
        }

        ParametroValorApostaDTO valorSelecionado =
                event.getValorSelecionado();

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        valorSelecionado.getValor()
                );

        int quantidadeTrevos =
                obterQuantidadeTrevos(
                        valorSelecionado
                );

        return atual.copy()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                0,
                                quantidadeTrevos
                        )
                )
                .setQuantidadeTrevosSelecionada(quantidadeTrevos)
                .setTrevosSelecionados(new ArrayList<>())
                .setValorTrevosSelecionado(valorSelecionado)
                .setTextoBotaoQuantidadeTrevos(
                        montarTextoBotaoTrevos(
                                quantidadeTrevos
                        )
                );
    }

    public SimulaUiState clicarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.ClicarQuantidadeTrevos event
    ) {

        if (atual == null
                || event == null
                || event.getParametro() == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                event.getParametro()
                        .getValoresTrevoByNumero(
                                event.getQuantidadeNumeros()
                        );

        if (valores == null || valores.isEmpty()) {
            return atual;
        }

        List<String> labels =
                montarLabelsTrevos(valores);

        String subtitulo =
                "Para aposta com "
                        + event.getQuantidadeNumeros()
                        + " numeros";

        return atual.copy()
                .setValoresTrevos(valores)
                .setLabelsQuantidadeTrevos(labels)
                .setSubtituloTrevos(subtitulo)
                .setExibirDialogQuantidadeTrevos(true);
    }

    public SimulaUiState selecionarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.SelecionarQuantidadeTrevos event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        int position =
                event.getPosition();

        if (valores == null
                || position < 0
                || position >= valores.size()) {
            return atual;
        }

        boolean deveAvisar =
                position > 0
                        && !atual.isAvisouTrevosMaximo();

        return atual.copy()
                .setPosicaoTrevosSelecionada(position)
                .setExibirAvisoTrevosMaximo(deveAvisar)
                .setAvisouTrevosMaximo(
                        atual.isAvisouTrevosMaximo()
                                || deveAvisar
                );
    }

    public SimulaUiState confirmarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.ConfirmarQuantidadeTrevos event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        if (valores == null || valores.isEmpty()) {
            return atual;
        }

        int position =
                event.getPosition();

        if (position < 0 || position >= valores.size()) {
            position =
                    atual.getPosicaoTrevosSelecionada();
        }

        if (position < 0 || position >= valores.size()) {
            return atual;
        }

        ParametroValorApostaDTO selecionado =
                valores.get(position);

        if (selecionado == null) {
            return atual;
        }

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        selecionado.getValor()
                );

        int quantidadeTrevos =
                obterQuantidadeTrevos(
                        selecionado
                );

        return atual.copy()
                .setPosicaoTrevosSelecionada(position)
                .setValorTrevosSelecionado(selecionado)
                .setExibirDialogQuantidadeTrevos(false)
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTrevosSelecionados(new ArrayList<>())
                .setQuantidadeTrevosSelecionada(quantidadeTrevos)
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                0,
                                quantidadeTrevos
                        )
                )
                .setTextoBotaoQuantidadeTrevos(
                        montarTextoBotaoTrevos(
                                quantidadeTrevos
                        )
                )
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.AtualizarQuantidadeTrevos(
                                selecionado
                        )
                );
    }

    public SimulaUiState consumirDialogQuantidadeTrevos(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setExibirDialogQuantidadeTrevos(false);
    }

    public SimulaUiState consumirAvisoTrevosMaximo(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setExibirAvisoTrevosMaximo(false);
    }

    public ParametroValorApostaDTO obterValorTrevosAtual(
            SimulaUiState atual,
            int quantidadeTrevos
    ) {

        if (atual == null) {
            return null;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        if (valores == null) {
            return atual.getValorTrevosSelecionado();
        }

        for (ParametroValorApostaDTO valor : valores) {

            if (valor == null
                    || valor.getNumeroTrevos() == null) {
                continue;
            }

            if (valor.getNumeroTrevos() == quantidadeTrevos) {
                return valor;
            }
        }

        return atual.getValorTrevosSelecionado();
    }

    public List<String> montarLabelsTrevos(
            List<ParametroValorApostaDTO> valores
    ) {

        List<String> labels =
                new ArrayList<>();

        if (valores == null) {
            return labels;
        }

        for (ParametroValorApostaDTO valor : valores) {

            if (valor == null) {
                continue;
            }

            Integer numeroTrevos =
                    valor.getNumeroTrevos();

            BigDecimal valorAposta =
                    valor.getValor() != null
                            ? valor.getValor()
                            : BigDecimal.ZERO;

            labels.add(
                    numeroTrevos
                            + " trevos por "
                            + ViewUtils.getMoedaFormat(valorAposta)
            );
        }

        return labels;
    }

    public String montarTextoTrevosSelecionados(
            int quantidadeSelecionada,
            int quantidadeNecessaria
    ) {

        if (quantidadeSelecionada <= 0) {
            return "";
        }

        return "Trevos selecionados: "
                + quantidadeSelecionada
                + "/"
                + quantidadeNecessaria;
    }

    public String montarTextoBotaoTrevos(
            int quantidadeTrevos
    ) {

        return quantidadeTrevos + " trevos";
    }

    private BigDecimal aplicarQuantidadeConcursos(
            SimulaUiState atual,
            BigDecimal valorBase
    ) {

        BigDecimal valorSeguro =
                valorBase != null
                        ? valorBase
                        : BigDecimal.ZERO;

        if (atual == null) {
            return valorSeguro;
        }

        int qtdConcursos =
                atual.getQtdConcursoSelecionado();

        if (qtdConcursos > 0) {
            return valorSeguro.multiply(
                    BigDecimal.valueOf(qtdConcursos)
            );
        }

        return valorSeguro;
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }

    private int obterQuantidadeTrevos(
            ParametroValorApostaDTO valor
    ) {

        if (valor == null
                || valor.getNumeroTrevos() == null) {
            return 0;
        }

        return valor.getNumeroTrevos();
    }

    private List<Integer> criarListaTrevosSegura(
            List<Integer> origem
    ) {

        if (origem == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(origem);
    }
    private List<ParametroValorApostaDTO> resolverValoresTrevosDisponiveis(
            SimulaUiState atual,
            List<ParametroValorApostaDTO> valoresTrevosDisponiveis
    ) {
        if (valoresTrevosDisponiveis != null
                && !valoresTrevosDisponiveis.isEmpty()) {
            return new ArrayList<>(
                    valoresTrevosDisponiveis
            );
        }

        if (atual != null
                && atual.getValoresTrevos() != null
                && !atual.getValoresTrevos().isEmpty()) {
            return new ArrayList<>(
                    atual.getValoresTrevos()
            );
        }

        List<ParametroValorApostaDTO> fallback =
                new ArrayList<>();

        if (atual != null
                && atual.getValorTrevosSelecionado() != null) {
            fallback.add(
                    atual.getValorTrevosSelecionado()
            );
        }

        return fallback;
    }

    private int calcularQuantidadeNecessariaPorSelecao(
            int quantidadeSelecionada,
            List<ParametroValorApostaDTO> valores
    ) {
        int minimo =
                obterQuantidadeMinimaTrevos(
                        valores
                );

        int quantidadeDesejada =
                Math.max(
                        minimo,
                        quantidadeSelecionada
                );

        ParametroValorApostaDTO valor =
                obterValorTrevosPorQuantidade(
                        valores,
                        quantidadeDesejada
                );

        if (valor != null) {
            return quantidadeDesejada;
        }

        int maiorQuantidadeDisponivel =
                obterMaiorQuantidadeTrevos(
                        valores
                );

        if (maiorQuantidadeDisponivel > 0) {
            return Math.min(
                    quantidadeDesejada,
                    maiorQuantidadeDisponivel
            );
        }

        return quantidadeDesejada;
    }

    private int obterQuantidadeMinimaTrevos(
            List<ParametroValorApostaDTO> valores
    ) {
        int minimo =
                Integer.MAX_VALUE;

        if (valores != null) {
            for (ParametroValorApostaDTO valor : valores) {
                if (valor == null
                        || valor.getNumeroTrevos() == null) {
                    continue;
                }

                minimo =
                        Math.min(
                                minimo,
                                valor.getNumeroTrevos()
                        );
            }
        }

        if (minimo == Integer.MAX_VALUE) {
            return 2;
        }

        return Math.max(
                2,
                minimo
        );
    }

    private int obterMaiorQuantidadeTrevos(
            List<ParametroValorApostaDTO> valores
    ) {
        int maior =
                0;

        if (valores == null) {
            return maior;
        }

        for (ParametroValorApostaDTO valor : valores) {
            if (valor == null
                    || valor.getNumeroTrevos() == null) {
                continue;
            }

            maior =
                    Math.max(
                            maior,
                            valor.getNumeroTrevos()
                    );
        }

        return maior;
    }

    private ParametroValorApostaDTO obterValorTrevosPorQuantidade(
            List<ParametroValorApostaDTO> valores,
            int quantidadeTrevos
    ) {
        if (valores == null) {
            return null;
        }

        for (ParametroValorApostaDTO valor : valores) {
            if (valor == null
                    || valor.getNumeroTrevos() == null) {
                continue;
            }

            if (valor.getNumeroTrevos() == quantidadeTrevos) {
                return valor;
            }
        }

        return null;
    }

    private int obterPosicaoValorTrevos(
            List<ParametroValorApostaDTO> valores,
            int quantidadeTrevos
    ) {
        if (valores == null) {
            return 0;
        }

        for (int i = 0; i < valores.size(); i++) {
            ParametroValorApostaDTO valor =
                    valores.get(i);

            if (valor == null
                    || valor.getNumeroTrevos() == null) {
                continue;
            }

            if (valor.getNumeroTrevos() == quantidadeTrevos) {
                return i;
            }
        }

        return 0;
    }
}
