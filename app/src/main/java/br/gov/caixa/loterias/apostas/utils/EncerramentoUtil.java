package br.gov.caixa.loterias.apostas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class EncerramentoUtil {

    private static final SimpleDateFormat DATE_TIME_FORMATE = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
            Date dataHoraErramentoFinal = DATE_TIME_FORMATE.parse(modalidadeDisponivelCota.getDataEncerramentoRevenda() + " " +
                                                                    modalidadeDisponivelCota.getHoraEncerramentoRevenda());

            Date dataHoraEncerramentoInicial = DATE_TIME_FORMATE.parse(parametroJogo.getConcurso().getDataFechamento());

            long tempoAtualMillis = System.currentTimeMillis();

            if (dataHoraEncerramentoInicial.getTime() <= tempoAtualMillis &&
                dataHoraErramentoFinal.getTime() >= tempoAtualMillis) {
                return dataHoraErramentoFinal.getTime() - tempoAtualMillis; //Tempo Restente
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void verificaSeCriaCardTemporario() {
        List<ModalidadeDisponivelCota> listModalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        for (int i = 0; i < listModalidadeDC.size(); i++) {
            ModalidadeDisponivelCota modalidadeDC = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas().get(i);

            //Se já encerrada
            try {
                Date dataHoraErramentoFinal = DATE_TIME_FORMATE.parse(
                        modalidadeDC.getDataEncerramentoRevenda() + " " +
                                modalidadeDC.getHoraEncerramentoRevenda());

                long tempoAtualMillis = System.currentTimeMillis();

                if (tempoAtualMillis > dataHoraErramentoFinal.getTime()) {
                    continue;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }


            ParametroJogoDTO parametroJogo = getParametroJogo(modalidadeDC.getModalidade(), modalidadeDC.getTipoConcurso(), modalidadeDC.getConcurso().intValue());
            if (parametroJogo == null) {
                criarCardTemporario(modalidadeDC);
            }
        }
    }
    private static void criarCardTemporario(ModalidadeDisponivelCota modalidadeDC) {

        ConcursoDTO concursoDTO = new ConcursoDTO();
        concursoDTO.setNumero(modalidadeDC.getConcurso());
        concursoDTO.setModalidade(modalidadeDC.getModalidade());
        concursoDTO.setModalidadeDetalhada(modalidadeDC.getModalidadeDetalhada());
        concursoDTO.setTipoConcurso(modalidadeDC.getTipoConcurso());
        concursoDTO.setEstimativa(modalidadeDC.getEstimativa());
        concursoDTO.setDataFechamento(DATE_TIME_FORMATE.format(new Date()));
        concursoDTO.setDataHoraSorteio(modalidadeDC.getDataHoraSorteio());

        ParametroJogoDTO parametroJogoDTO = new ParametroJogoDTO();
        parametroJogoDTO.setConcurso(concursoDTO);

        ParametroSimulacao parametroSimulacao = new ParametroSimulacao();
        parametroSimulacao.setParametroJogo(parametroJogoDTO);

        SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().add(parametroSimulacao);
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
