package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LotecaShakeTest {
    @Test public void segundoShakeRenovaSemAlterarQuantidadeDePalpites() throws Exception {
        LotecaFragment fragment = mock(LotecaFragment.class, CALLS_REAL_METHODS);
        SimulaUiState state = mock(SimulaUiState.class);
        when(state.getPalpitesLoteca()).thenReturn(15);
        when(state.getDezenasSelecionadas()).thenReturn(new ArrayList<>());
        doReturn(state).when(fragment).getStateAtual();
        ParametroJogoDTO jogo = mock(ParametroJogoDTO.class, RETURNS_DEEP_STUBS);
        when(jogo.getConcurso().getModalidade()).thenReturn(ModalidadeEnum.LOTECA);
        List<ParametroPartida> partidas = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            ParametroPartida partida = new ParametroPartida();
            partida.setEquipe1(new ParametroEquipe());
            partida.setEquipe2(new ParametroEquipe());
            partidas.add(partida);
        }
        when(jogo.getPartidas()).thenReturn(partidas);
        ParametroValorApostaDTO valor = new ParametroValorApostaDTO();
        valor.setQuantidadeDuplos(1);
        valor.setQuantidadeTriplos(0);
        when(jogo.getValoresAposta()).thenReturn(List.of(valor));
        ExpandableHeightRecyclerView recycler = mock(ExpandableHeightRecyclerView.class);
        when(recycler.getAdapter()).thenReturn(mock(LotecaAdapter.class));
        set(fragment, "parametroJogo", jogo);
        set(fragment, "recyclerViewPartida", recycler);
        fragment.preencherApostaShake(false);
        int[] anteriores = partidas.stream().mapToInt(LotecaShakeTest::mascara).toArray();
        assertEquals(15, partidas.stream().mapToInt(p -> Integer.bitCount(mascara(p))).sum());
        for (int repeticao = 0; repeticao < 10; repeticao++) {
            fragment.preencherApostaShake(true);
            for (int i = 0; i < partidas.size(); i++) {
                int atual = mascara(partidas.get(i));
                assertNotEquals("Renovar deve trocar os resultados", anteriores[i], atual);
                assertEquals(Integer.bitCount(anteriores[i]), Integer.bitCount(atual));
                anteriores[i] = atual;
            }
        }
    }
    private static int mascara(ParametroPartida p) {
        return (p.getEquipe1().isSelecionado() ? 1 : 0) | (p.isEmpate() ? 2 : 0)
                | (p.getEquipe2().isSelecionado() ? 4 : 0);
    }
    private static void set(Object alvo, String nome, Object valor) throws Exception {
        Field field = LotecaFragment.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(alvo, valor);
    }
}
