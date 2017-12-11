package caixa.gov.br.loterias.mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import caixa.gov.br.loterias.model.Modalidade;
import caixa.gov.br.loterias.utils.TipoModalidadeEnum;

/**
 * Created by brfernan on 27/10/2017.
 */

public class ModalidadesMock {

    public static  ModalidadesMock get() {
        return new ModalidadesMock();
    }

    private ModalidadesMock() {

    }

    public List<Modalidade> getData() {
        return Arrays.asList(
//                new Modalidade("mega-sena", new BigDecimal("6.50"),"descricao", "descricao especial", TipoModalidadeEnum.MEGASENA),
//                new Modalidade("quina", new BigDecimal("2.50"),"descricao", "descricao especial", TipoModalidadeEnum.QUINA),
//                new Modalidade("dupla-sena", new BigDecimal("4.00"),"descricao", "descricao especial", TipoModalidadeEnum.DUPLASENA),
//                new Modalidade("federal", new BigDecimal("1.50"),"descricao", "descricao especial", TipoModalidadeEnum.FEDERAL),
//                new Modalidade("lotofacil", new BigDecimal("0.50"),"descricao", "descricao especial", TipoModalidadeEnum.LOTOFACIL),
//                new Modalidade("loteca", new BigDecimal("2.00"),"descricao", "descricao especial", TipoModalidadeEnum.LOTECA),
//                new Modalidade("lotogol", new BigDecimal("3.00"),"descricao", "descricao especial", TipoModalidadeEnum.LOTOGOL),
//                new Modalidade("lotomania", new BigDecimal("2.75"),"descricao", "descricao especial", TipoModalidadeEnum.LOTOMANIA),
//                new Modalidade("timemania", new BigDecimal("3.50"),"descricao", "descricao especial", TipoModalidadeEnum.TIMEMANIA)
        );
    }
}
