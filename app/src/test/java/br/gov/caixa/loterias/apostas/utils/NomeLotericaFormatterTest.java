package br.gov.caixa.loterias.apostas.utils;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class NomeLotericaFormatterTest {
    @Test public void nomeCurtoPermaneceInteiro() {
        assertEquals("Lotérica Mina de Ouro", NomeLotericaFormatter.formatar("Lotérica Mina de Ouro"));
    }
    @Test public void exatamente32CaracteresPermaneceEmUmaLinha() {
        String nome = "Lotérica Mina de Ouro A lotérica";
        assertEquals(nome, NomeLotericaFormatter.formatar(nome));
    }
    @Test public void palavraQueCruzaLimiteDesceInteira() {
        assertEquals("Lotérica Mina de Ouro A lotérica\nda galera",
                NomeLotericaFormatter.formatar("Lotérica Mina de Ouro A lotérica da galera"));
    }
    @Test public void palavraIniciadaAntesDoLimiteNaoEhCortada() {
        assertEquals("Lotérica Mina de Ouro A\nExtraordinária",
                NomeLotericaFormatter.formatar("Lotérica Mina de Ouro A Extraordinária"));
    }
    @Test public void palavraLongaNaoEhCortadaNemTruncada() {
        String palavra = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN";
        assertEquals("Lotérica\n" + palavra, NomeLotericaFormatter.formatar("Lotérica " + palavra));
    }
    @Test public void nomeAusenteNaoCausaErro() {
        assertEquals("", NomeLotericaFormatter.formatar(null));
    }
}
