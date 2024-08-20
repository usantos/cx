package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
public class MensagemUtils {
    public static String getMensagemConverteModalidade(ApostaDTO apostaDTO) {
        String descricaoNormal = ModalidadeEnum.getDescricao(apostaDTO.getModalidade());
        String descricaoEspecial = ModalidadeEnum.getDescricaoEspecial(apostaDTO.getModalidade());

        String msg = "A modalidade XXX não está vigente. A aposta será efetivada na modalidade ZZZ. Deseja prosseguir?";
        if (apostaDTO.getTipoConcurso().getValor() == TipoConcursoEnum.ESPECIAL.getValor()){
            msg = msg.replace("XXX", descricaoEspecial).replace("ZZZ", descricaoNormal);
        } else {
            msg = msg.replace("XXX", descricaoNormal).replace("ZZZ", descricaoEspecial);
        }
        return msg;
    }

}
