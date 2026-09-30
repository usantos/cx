package br.gov.caixa.loterias.apostas.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.DataHoraServidorSingleton;

public class EncerramentoUtil {

    private static final String FORMATO_DATA_HORA = "dd/MM/yyyy HH:mm:ss";
    private static final SimpleDateFormat DATE_TIME_FORMATE = new SimpleDateFormat(FORMATO_DATA_HORA);
    public static ModalidadeDisponivelCota getModalidadeDisponivelCota(ModalidadeEnum modalidade,
                                                                       TipoConcursoEnum tipoConcurso,
                                                                       Integer concurso) {
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        ModalidadeDisponivelCota modalidadeDisponivelCota = null;
        if (parametrosSimulacao != null && parametrosSimulacao.getModalidadesDisponiveisCotas() != null) {
            for (ModalidadeDisponivelCota modalidadeDC : parametrosSimulacao.getModalidadesDisponiveisCotas()) {
                if (modalidade == modalidadeDC.getModalidade() &&
                    modalidadeDC.getTipoConcurso() == tipoConcurso &&
                    modalidadeDC.getConcurso().intValue() == concurso.intValue()) {
                        modalidadeDisponivelCota = modalidadeDC;
                        break;
                }
            }
        }
        return modalidadeDisponivelCota;
    }
    public static ParametroJogoDTO getParametroJogo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso,
                                                    Integer concurso) {
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        ParametroJogoDTO parametroJogoDTO = null;
            if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null) {
                for (ParametroSimulacao parametro : parametrosSimulacao.getParametros()){
                    if (modalidade == parametro.getParametroJogo().getConcurso().getModalidade() &&
                        parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso &&
                        parametro.getParametroJogo().getConcurso().getNumero().intValue() == concurso.intValue()) {
                            parametroJogoDTO = parametro.getParametroJogo();
                            break;
                    }
                }
        }
        return parametroJogoDTO;
    }

    //Calcula o tempo entre a data/hora atual e data/hora final
    //Se a data/hora do sistema estiver abaixo ou acima das data/hora incial ou final retorna null
    public static  Long tempoExtraRestanteAposEncerramento(ModalidadeEnum modalidade,
                                                           TipoConcursoEnum tipoConcurso,
                                                           Integer concurso) {

        //Verifica se Bolao Habilitado
        if (checkBolaoHabilitado()) {
            return null;
        }

        ModalidadeDisponivelCota modalidadeDisponivelCota = getModalidadeDisponivelCota(modalidade,
                                                                                        tipoConcurso,
                                                                                        concurso);
        if (modalidadeDisponivelCota == null) {
            return null;
        }

        ParametroJogoDTO parametroJogo = getParametroJogo(modalidade, tipoConcurso, concurso);
        if (parametroJogo == null) {
            return null;
        }

        try {
            DateTime dataHoraErramentoFinal = DateUtils.criaDataHora(modalidadeDisponivelCota.getDataEncerramentoRevenda() + " " +
                                                                    modalidadeDisponivelCota.getHoraEncerramentoRevenda(), FORMATO_DATA_HORA);

            DateTime dataHoraEncerramentoInicial = DateUtils.criaDataHora(parametroJogo.getConcurso().getDataFechamento(), FORMATO_DATA_HORA);

            long tempoAtualMillis = DataHoraServidorSingleton.getInstance().getDataHoraServidorMillis();

            if (dataHoraEncerramentoInicial.getMillis() <= tempoAtualMillis &&
                dataHoraErramentoFinal.getMillis() >= tempoAtualMillis) {
                return dataHoraErramentoFinal.getMillis() - tempoAtualMillis; //Tempo Restente
            }

        } catch (Exception e) {
        }

        return null;
    }

    public static void verificaSeCriaCardTemporario() {

        //Verifica se Bolao Habilitado
        if (checkBolaoHabilitado()) {
            return;
        }

        List<ModalidadeDisponivelCota> listModalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        for (int i = 0; i < listModalidadeDC.size(); i++) {
            ModalidadeDisponivelCota modalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas().get(i);

            //Se já encerrada
            try {
                DateTime dataHoraErramentoFinal = DateUtils.criaDataHora(
                        modalidadeDC.getDataEncerramentoRevenda() + " " +
                                modalidadeDC.getHoraEncerramentoRevenda(), FORMATO_DATA_HORA);

                long tempoAtualMillis = DataHoraServidorSingleton.getInstance().getDataHoraServidorMillis();

                if (tempoAtualMillis > dataHoraErramentoFinal.getMillis()) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }


            ParametroJogoDTO parametroJogo = getParametroJogo(modalidadeDC.getModalidade(), modalidadeDC.getTipoConcurso(), modalidadeDC.getConcurso().intValue());
            if (parametroJogo == null) {
                criarCardTemporario(modalidadeDC);
            }
        }
    }

    private static boolean checkBolaoHabilitado() {
        return !SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.BOLAO.get(), ConfiguracoesDefaultEnum.BOLAO_HABILITADO.asBoolean());
    }

    private static void criarCardTemporario(ModalidadeDisponivelCota modalidadeDC) {

        ConcursoDTO concursoDTO = new ConcursoDTO();
        concursoDTO.setNumero(modalidadeDC.getConcurso());
        concursoDTO.setModalidade(modalidadeDC.getModalidade());
        concursoDTO.setModalidadeDetalhada(modalidadeDC.getModalidadeDetalhada());
        concursoDTO.setTipoConcurso(modalidadeDC.getTipoConcurso());
        concursoDTO.setEstimativa(modalidadeDC.getEstimativa());
        concursoDTO.setDataFechamento("01/01/2025 12:00:00");
        concursoDTO.setDataHoraSorteio(modalidadeDC.getDataHoraSorteio());

        ParametroJogoDTO parametroJogoDTO = new ParametroJogoDTO();
        parametroJogoDTO.setConcurso(concursoDTO);

        ParametroSimulacao parametroSimulacao = new ParametroSimulacao();
        parametroSimulacao.setParametroJogo(parametroJogoDTO);

        upInsParametroJogo(modalidadeDC.getModalidade(), modalidadeDC.getTipoConcurso(), parametroSimulacao);
    }

    private static void upInsParametroJogo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso,
                                           ParametroSimulacao parametroSimulacaoUpIns) {
        ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null){
            for (int i = 0; i < parametrosSimulacao.getParametros().size(); i++){
                ParametroSimulacao parametro = parametrosSimulacao.getParametros().get(i);
                if (modalidade == parametro.getParametroJogo().getConcurso().getModalidade() &&
                    parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso){
                    //update
                    parametrosSimulacao.getParametros().get(i).setParametroJogo(parametroSimulacaoUpIns.getParametroJogo());
                    return;
                }
            }
        }
        if (parametroSimulacaoUpIns.getParametroJogo().getConcurso().getTipoConcurso().equals(TipoConcursoEnum.NORMAL)) {
            //insert no final da Lista
            SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().add(parametroSimulacaoUpIns);
        } else {
            //insert no inicio da Lista
            SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().add(0, parametroSimulacaoUpIns);
        }
    }

    public static void mockApagaModalidadeNosParametroJogo(ModalidadeEnum modalidade,
                                           TipoConcursoEnum tipoConcurso, Integer concurso) {
        List<ParametroSimulacao> listParametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
        for (int i = 0; i < listParametros.size(); i++) {
            ParametroSimulacao parametro = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().get(i);
            if (parametro.getParametroJogo().getConcurso().getModalidade() == modalidade &&
                parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso &&
                parametro.getParametroJogo().getConcurso().getNumero().intValue() == concurso.intValue()) {

                SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().remove(i);
                break;
            }
        }
    }

    //MOCK APAGAR - data "19/02/2025", hora "16:00:00"
    public static void mockSetDataHoraEncerramento(String dataEncerramento,
                                                   String horaEncerramentoInicio,
                                                   String horaEncerramentoFim,
                                                   ModalidadeEnum modalidade,
                                                   TipoConcursoEnum tipoConcurso,
                                                   Integer concurso) {

        boolean continua = false;
        List<ParametroSimulacao> listParametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
        for (int i = 0; i < listParametros.size(); i++) {
            ParametroSimulacao parametro = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().get(i);
            if (parametro.getParametroJogo().getConcurso().getModalidade() == modalidade &&
                    parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso &&
                    parametro.getParametroJogo().getConcurso().getNumero().intValue() == concurso.intValue()) {
                SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().get(i)
                        .getParametroJogo().getConcurso().setDataFechamento(dataEncerramento + " " + horaEncerramentoInicio);
                continua = true;
                break;
            }
        }

        if (!continua) {
            return;
        }

        List<ModalidadeDisponivelCota> listModalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        for (int i = 0; i < listModalidadeDC.size(); i++) {
            ModalidadeDisponivelCota modalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas().get(i);
            if (modalidadeDC.getModalidade() == modalidade &&
                modalidadeDC.getTipoConcurso() == tipoConcurso &&
                modalidadeDC.getConcurso().intValue() == concurso.intValue()) {
                SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas().get(i).
                        setDataEncerramentoRevenda(dataEncerramento);
                SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas().get(i).
                        setHoraEncerramentoRevenda(horaEncerramentoFim);
                break;
            }
        }
    }
}
