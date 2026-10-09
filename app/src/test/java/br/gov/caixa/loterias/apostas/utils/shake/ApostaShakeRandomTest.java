package br.gov.caixa.loterias.apostas.utils.shake;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ApostaShakeRandomTest {
    @Test public void renovaModalidadesNumericasRepetidamente() {
        // Mega-Sena, Lotofácil, Quina, Lotomania, Dupla Sena, Timemania,
        // Dia de Sorte, +Milionária e trevos.
        int[][] modalidades = {{6,1,60},{15,1,25},{5,1,80},{50,0,99},
                {6,1,50},{10,1,80},{7,1,31},{6,1,50},{2,1,6}};
        Random random = new Random(42);
        for (int[] modalidade : modalidades) {
            List<Integer> anterior = Collections.emptyList();
            for (int repeticao = 0; repeticao < 100; repeticao++) {
                List<Integer> nova = ApostaShakeRandom.numeros(modalidade[0], anterior,
                        modalidade[1], modalidade[2], true, random);
                assertEquals(modalidade[0], nova.size());
                assertEquals(nova.size(), new HashSet<>(nova).size());
                assertFalse(ApostaShakeRandom.mesmosNumeros(nova, anterior));
                for (int numero : nova) {
                    assertTrue(numero >= modalidade[1] && numero <= modalidade[2]);
                }
                anterior = nova;
            }
        }
    }
    @Test public void completarPreservaNumerosExistentes() {
        List<Integer> atuais = Arrays.asList(2, 7);
        List<Integer> nova = ApostaShakeRandom.numeros(6, atuais, 1, 60, false, new Random(1));
        assertTrue(nova.containsAll(atuais));
        assertEquals(6, nova.size());
        assertEquals(Arrays.asList(2, 7), atuais);
    }
    @Test public void lotecaPreservaSimplesDuplosETriplos() {
        for (int mascara = 1; mascara <= 7; mascara++) {
            int nova = ApostaShakeRandom.outraMascaraLoteca(mascara, new Random(1));
            assertEquals(Integer.bitCount(mascara), Integer.bitCount(nova));
            if (mascara != 7) assertNotEquals(mascara, nova);
        }
    }
    @Test public void selecaoDeTodosOsNumerosNaoPrecisaDeAlternativa() {
        assertEquals(Arrays.asList(1,2,3), ApostaShakeRandom.numeros(3,
                Arrays.asList(1,2,3), 1,3,true,new Random(1)));
    }
}
