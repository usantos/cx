package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class ComboUtil {

    public static void atualizaCardCombo() {
        criarCardCombo();
    }

    private static void criarCardCombo() {

        ModalidadeDTO modalidadeDTO = new ModalidadeDTO();
        modalidadeDTO.setValor(0);
        modalidadeDTO.setDescricao(ModalidadeEnum.getDescricao(ModalidadeEnum.COMBO));

        ConcursoDTO concursoDTO = new ConcursoDTO();
        concursoDTO.setNumero(0);
        concursoDTO.setModalidade(ModalidadeEnum.COMBO);
        concursoDTO.setTipoConcurso(TipoConcursoEnum.NORMAL);
        concursoDTO.setModalidadeDetalhada(modalidadeDTO);

        ParametroJogoDTO parametroJogoDTO = new ParametroJogoDTO();
        parametroJogoDTO.setConcurso(concursoDTO);

        ParametroSimulacao parametroSimulacao = new ParametroSimulacao();
        parametroSimulacao.setParametroJogo(parametroJogoDTO);

        upInsParametroJogo(parametroSimulacao);
    }

    private static void upInsParametroJogo(ParametroSimulacao parametroSimulacaoUpIns) {
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null){
            for (int i = 0; i < parametrosSimulacao.getParametros().size(); i++){
                ParametroSimulacao parametro = parametrosSimulacao.getParametros().get(i);
                if (ModalidadeEnum.COMBO == parametro.getParametroJogo().getConcurso().getModalidade()) {
                    //update
                    parametrosSimulacao.getParametros().get(i).setParametroJogo(parametroSimulacaoUpIns.getParametroJogo());
                    return;
                }
            }
        }

        //insert
        SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().add(parametroSimulacaoUpIns);
    }

    public static void excluiCardCombo() {
        //Verifica se existe Card Combo - Se existir exclui
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null) {
            for (int i = 0; i < parametrosSimulacao.getParametros().size(); i++) {
                ParametroSimulacao parametro = parametrosSimulacao.getParametros().get(i);
                if (ModalidadeEnum.COMBO == parametro.getParametroJogo().getConcurso().getModalidade()) {
                    //delete
                    parametrosSimulacao.getParametros().remove(i);
                    return;
                }
            }
        }
    }

    public static boolean temComboParametroSimulacao() {
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null){
            for (int i = 0; i < parametrosSimulacao.getParametros().size(); i++){
                ParametroSimulacao parametro = parametrosSimulacao.getParametros().get(i);
                if (ModalidadeEnum.COMBO == parametro.getParametroJogo().getConcurso().getModalidade()) {
                    return true;
                }
            }
        }
        return false;
    }
}
