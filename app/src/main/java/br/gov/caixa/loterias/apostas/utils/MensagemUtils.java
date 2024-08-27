package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
public class MensagemUtils {
    public static String getMensagemConverteModalidade(ModalidadeEnum modalidade, String tipoConcurso) {
        String descricaoNormal = ModalidadeEnum.getDescricao(modalidade);
        String descricaoEspecial = ModalidadeEnum.getDescricaoEspecial(modalidade);

        String msg = "A modalidade XXX não está vigente. A aposta será efetivada na modalidade ZZZ. Deseja prosseguir?";
        if (tipoConcurso.equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor())){
            msg = msg.replace("XXX", descricaoEspecial).replace("ZZZ", descricaoNormal);
        } else {
            msg = msg.replace("XXX", descricaoNormal).replace("ZZZ", descricaoEspecial);
        }
        return msg;
    }

}
