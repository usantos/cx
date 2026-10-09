package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import org.junit.Test;
import org.mockito.MockedStatic;
import java.lang.reflect.Field;
import java.util.ArrayList;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import static org.mockito.Mockito.*;

public class SuperSeteShakeTest {
    @Test public void renovacaoUsaGeradorDeColunasEmVezDoAdapterNumerico() throws Exception {
        try (MockedStatic<AlertDialogUtils> dialogs = mockStatic(AlertDialogUtils.class)) {
            SimulaFragment fragment = mock(SimulaFragment.class, CALLS_REAL_METHODS);
            SimulaUiState state = mock(SimulaUiState.class);
            when(state.getQtdDezenasPossiveisSelecionado()).thenReturn(7);
            when(state.getDezenasSelecionadas()).thenReturn(new ArrayList<>());
            doReturn(state).when(fragment).getStateAtual();
            ParametroJogoDTO jogo = mock(ParametroJogoDTO.class, RETURNS_DEEP_STUBS);
            when(jogo.getConcurso().getModalidade()).thenReturn(ModalidadeEnum.SUPER_7);
            when(jogo.getPrognosticoMaximo()).thenReturn(9);
            SimularApostaModel model = mock(SimularApostaModel.class);
            set(fragment, "parametroJogo", jogo);
            set(fragment, "tipoJogo", ModalidadeEnum.SUPER_7);
            set(fragment, "model", model);
            set(fragment, "listaAdaptersSuperSete", new ArrayList<>());
            fragment.preencherApostaShake(true);
            verify(model).preencheNumerosAleatoriosSuperSete(eq(7), argThat(colunas ->
                    colunas.size() == 7 && colunas.stream().allMatch(ArrayList::isEmpty)),
                    eq("SUPER_7"), any());
            verify(model, never()).preencheNumerosAleatorios(anyInt(), any(), anyInt(), any());
        }
    }
    private static void set(Object alvo, String nome, Object valor) throws Exception {
        Field field = SimulaFragment.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(alvo, valor);
    }
}
