package br.gov.caixa.loterias.apostas.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;

public class ApostaIdentityKeyTest {

    @Test
    public void deveRetornarVazioQuandoApostaForNula() {
        assertEquals("", ApostaIdentityKey.gerar(null));
    }

    @Test
    public void deveRetornarVazioQuandoModalidadeForNula() {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        assertEquals("", ApostaIdentityKey.gerar(aposta));
    }

    @Test
    public void deveRetornarVazioQuandoForCotaBolao() {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(ModalidadeEnum.MEGA_SENA);
        aposta.setIndicadorCotaBolao(true);

        assertEquals("", ApostaIdentityKey.gerar(aposta));
    }

    @Test
    public void deveGerarMesmaChaveParaMegaComOrdemDiferente() {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                criarApostaNumerica(
                        ModalidadeEnum.MEGA_SENA,
                        Arrays.asList(1, 2, 3, 4, 5, 6));

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                criarApostaNumerica(
                        ModalidadeEnum.MEGA_SENA,
                        Arrays.asList(6, 5, 4, 3, 2, 1));

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarMesmaChaveParaMaisMilionariaComOrdemDiferente() {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta1.setModalidade(ModalidadeEnum.MAIS_MILIONARIA);
        aposta1.setIndicadorCotaBolao(false);
        aposta1.setNumerosSelecionados(
                Arrays.asList(1, 2, 3, 4, 5, 6));
        aposta1.setTrevosSelecionados(
                Arrays.asList(1, 2));

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta2.setModalidade(ModalidadeEnum.MAIS_MILIONARIA);
        aposta2.setIndicadorCotaBolao(false);
        aposta2.setNumerosSelecionados(
                Arrays.asList(6, 5, 4, 3, 2, 1));
        aposta2.setTrevosSelecionados(
                Arrays.asList(2, 1));

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarChavesDiferentesParaTrevosDiferentes() {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta1.setModalidade(ModalidadeEnum.MAIS_MILIONARIA);
        aposta1.setNumerosSelecionados(
                Arrays.asList(1, 2, 3, 4, 5, 6));
        aposta1.setTrevosSelecionados(
                Arrays.asList(1, 2));

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta2.setModalidade(ModalidadeEnum.MAIS_MILIONARIA);
        aposta2.setNumerosSelecionados(
                Arrays.asList(1, 2, 3, 4, 5, 6));
        aposta2.setTrevosSelecionados(
                Arrays.asList(3, 4));

        assertNotEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarMesmaChaveTimemaniaComNumerosEmOrdemDiferente() {

        ParametroEquipe time = new ParametroEquipe();
        time.setNumero(10);
        time.setNome("Palmeiras");

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                criarApostaNumerica(
                        ModalidadeEnum.TIMEMANIA,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta1.setTimeDoCoracao(time);

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                criarApostaNumerica(
                        ModalidadeEnum.TIMEMANIA,
                        Arrays.asList(7, 6, 5, 4, 3, 2, 1));

        aposta2.setTimeDoCoracao(time);

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarChavesDiferentesParaTimesDiferentes() {

        ParametroEquipe time1 = new ParametroEquipe();
        time1.setNumero(10);
        time1.setNome("Palmeiras");

        ParametroEquipe time2 = new ParametroEquipe();
        time2.setNumero(20);
        time2.setNome("Flamengo");

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                criarApostaNumerica(
                        ModalidadeEnum.TIMEMANIA,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta1.setTimeDoCoracao(time1);

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                criarApostaNumerica(
                        ModalidadeEnum.TIMEMANIA,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta2.setTimeDoCoracao(time2);

        assertNotEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarMesmaChaveDiaDeSorteComNumerosEmOrdemDiferente() {

        ParametroMesDeSorte mes = new ParametroMesDeSorte();
        mes.setNumero(5);
        mes.setNome("MAIO");
        mes.setAbreviacao("MAI");

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                criarApostaNumerica(
                        ModalidadeEnum.DIA_DE_SORTE,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta1.setMesDeSorte(mes);

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                criarApostaNumerica(
                        ModalidadeEnum.DIA_DE_SORTE,
                        Arrays.asList(7, 6, 5, 4, 3, 2, 1));

        aposta2.setMesDeSorte(mes);

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarChavesDiferentesParaMesesDiferentes() {

        ParametroMesDeSorte maio = new ParametroMesDeSorte();
        maio.setNumero(5);

        ParametroMesDeSorte junho = new ParametroMesDeSorte();
        junho.setNumero(6);

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta1 =
                criarApostaNumerica(
                        ModalidadeEnum.DIA_DE_SORTE,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta1.setMesDeSorte(maio);

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta2 =
                criarApostaNumerica(
                        ModalidadeEnum.DIA_DE_SORTE,
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7));

        aposta2.setMesDeSorte(junho);

        assertNotEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void deveGerarChaveLotecaComPrognostico1X() {

        PartidaLotecaDTO partida =
                criarPartida(1, true, true, false);

        IdentificaoDeUmaApostaDas8Modalidades<Object> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(ModalidadeEnum.LOTECA);
        aposta.setPartidasLoteca(
                Collections.singletonList(partida));

        assertEquals(
                "LOTECA|1=1X",
                ApostaIdentityKey.gerar(aposta));
    }

    @Test
    public void deveGerarMesmaChaveLotecaComPartidasForaDeOrdem() {

        PartidaLotecaDTO partida1 =
                criarPartida(1, true, false, false);

        PartidaLotecaDTO partida2 =
                criarPartida(2, false, true, false);

        IdentificaoDeUmaApostaDas8Modalidades<Object> aposta1 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta1.setModalidade(ModalidadeEnum.LOTECA);
        aposta1.setPartidasLoteca(
                Arrays.asList(partida1, partida2));

        IdentificaoDeUmaApostaDas8Modalidades<Object> aposta2 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta2.setModalidade(ModalidadeEnum.LOTECA);
        aposta2.setPartidasLoteca(
                Arrays.asList(partida2, partida1));

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void naoDeveModificarListaOriginalDaLoteca() {

        PartidaLotecaDTO p2 =
                criarPartida(2, true, false, false);

        PartidaLotecaDTO p1 =
                criarPartida(1, false, true, false);

        List<PartidaLotecaDTO> partidas =
                new ArrayList<>(Arrays.asList(p2, p1));

        IdentificaoDeUmaApostaDas8Modalidades<Object> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(ModalidadeEnum.LOTECA);
        aposta.setPartidasLoteca(partidas);

        ApostaIdentityKey.gerar(aposta);

        assertEquals(
                Integer.valueOf(2),
                aposta.getPartidasLoteca().get(0).getNumero());
    }

    @Test
    public void deveGerarChaveLotogol() {

        PartidaLotogolDTO partida = new PartidaLotogolDTO();

        partida.setIndexPlacarEquipe1(2);
        partida.setIndexPlacarEquipe2(1);

        IdentificaoDeUmaApostaDas8Modalidades<Object> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(ModalidadeEnum.LOTOGOL);
        aposta.setPartidasLotogol(
                Collections.singletonList(partida));

        assertEquals(
                "LOTOGOL|2x1",
                ApostaIdentityKey.gerar(aposta));
    }

    @Test
    public void deveGerarMesmaChaveSuperSeteComOrdemInternaDiferente() {

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> aposta1 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta1.setModalidade(ModalidadeEnum.SUPER_7);

        aposta1.setNumerosSelecionados(
                Arrays.asList(
                        Arrays.asList(3, 1),
                        Arrays.asList(8, 7)));

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> aposta2 =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta2.setModalidade(ModalidadeEnum.SUPER_7);

        aposta2.setNumerosSelecionados(
                Arrays.asList(
                        Arrays.asList(1, 3),
                        Arrays.asList(7, 8)));

        assertEquals(
                ApostaIdentityKey.gerar(aposta1),
                ApostaIdentityKey.gerar(aposta2));
    }

    @Test
    public void naoDeveModificarMatrizOriginalDoSuperSete() {

        List<Integer> coluna =
                new ArrayList<>(Arrays.asList(3, 1));

        List<List<Integer>> matriz =
                new ArrayList<>();

        matriz.add(coluna);

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(ModalidadeEnum.SUPER_7);
        aposta.setNumerosSelecionados(matriz);

        ApostaIdentityKey.gerar(aposta);

        assertEquals(
                Arrays.asList(3, 1),
                aposta.getMatrizNumerosSelecionados().get(0));
    }

    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> criarApostaNumerica(
            ModalidadeEnum modalidade,
            List<Integer> numeros) {

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta =
                new IdentificaoDeUmaApostaDas8Modalidades<>();

        aposta.setModalidade(modalidade);
        aposta.setIndicadorCotaBolao(false);
        aposta.setNumerosSelecionados(numeros);

        return aposta;
    }

    private PartidaLotecaDTO criarPartida(
            int numero,
            boolean equipe1Selecionada,
            boolean empate,
            boolean equipe2Selecionada) {

        EquipeDTO equipe1 = new EquipeDTO();
        equipe1.setIndicadorSelecao(equipe1Selecionada);

        EquipeDTO equipe2 = new EquipeDTO();
        equipe2.setIndicadorSelecao(equipe2Selecionada);

        PartidaLotecaDTO partida =
                new PartidaLotecaDTO();

        partida.setNumero(numero);
        partida.setEquipe1(equipe1);
        partida.setEquipe2(equipe2);
        partida.setEmpate(empate);

        return partida;
    }
}