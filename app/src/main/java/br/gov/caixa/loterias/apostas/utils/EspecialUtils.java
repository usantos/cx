package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class EspecialUtils {
    public static String MEGA_LINHA = "ARG_30_ANOS_LINHA";
    public static String MEGA_DUAS_LINHAS = "ARG_30_ANOS_DUAS_LINHAS";
    public static String MEGA_VALOR = "ARG_30_ANOS_VALOR";
    public static String LOTECA_PAIS_VALOR = "ARG_LOTECA_PAIS_VALOR";
    public static String LOTECA_PAIS_LINHA = "ARG_LOTECA_PAIS_LINHA";
    public static String LOTECA_PAIS_DUAS_LINHAS = "ARG_LOTECA_PAIS_DUAS_LINHAS";


    public static Boolean isMega30(ModalidadeEnum modalidade, Integer numeroConcurso, TipoConcursoEnum tipoConcurso) {
        if (!modalidade.equals(ModalidadeEnum.MEGA_SENA)) {
            return false;
        }
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.equals(TipoConcursoEnum.ESPECIAL);
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isLotecaPais(ModalidadeEnum modalidade, Integer numeroConcurso, TipoConcursoEnum tipoConcurso) {
        if (!modalidade.equals(ModalidadeEnum.LOTECA)) {
            return false;
        }
        int lotecaPais = SharedPreferencesUtils.getValorInt(LOTECA_PAIS_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.equals(TipoConcursoEnum.ESPECIAL);
        return isEspecial && numeroConcurso.equals(lotecaPais);
    }

    public static Boolean isMega30(ModalidadeEnum modalidade, Integer numeroConcurso, Boolean isEspecial) {
        if (!modalidade.equals(ModalidadeEnum.MEGA_SENA)) {
            return false;
        }
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isLotecaPais(ModalidadeEnum modalidade, Integer numeroConcurso, Boolean isEspecial) {
        if (!modalidade.equals(ModalidadeEnum.LOTECA)) {
            return false;
        }
        int lotecaPais = SharedPreferencesUtils.getValorInt(LOTECA_PAIS_VALOR, -1);
        return isEspecial && numeroConcurso.equals(lotecaPais);
    }

    public static Boolean isMega30(ModalidadeEnum modalidade, Integer numeroConcurso, DTOEnumCharacter tipoConcurso){
        if (!modalidade.equals(ModalidadeEnum.MEGA_SENA)) {
            return false;
        }
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.getValor() != null &&
                tipoConcurso.getValor().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor());
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isLotecaPais(ModalidadeEnum modalidade, Integer numeroConcurso, DTOEnumCharacter tipoConcurso){
        if (!modalidade.equals(ModalidadeEnum.LOTECA)) {
            return false;
        }
        int lotecaPais = SharedPreferencesUtils.getValorInt(LOTECA_PAIS_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.getValor() != null &&
                tipoConcurso.getValor().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor());
        return isEspecial && numeroConcurso.equals(lotecaPais);
    }

    public static Boolean isMega30(Integer numeroConcurso, TipoConcursoEnum tipoConcurso){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.equals(TipoConcursoEnum.ESPECIAL);
        return isEspecial && numeroConcurso.equals(mega30);
    }


    public static Boolean isMega30(Integer numeroConcurso, DTOEnumCharacter tipoConcurso){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.getValor() != null &&
                tipoConcurso.getValor().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor());
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isMega30(Integer numeroConcurso, Integer tipoConcurso){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso != null && tipoConcurso.toString().equals(TipoConcursoEnum.ESPECIAL.getValor());
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isMega30(Integer numeroConcurso, boolean isEspecial){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isMega30(String numeroConcurso, Boolean isEspecial){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        return isEspecial && numeroConcurso.equals(Integer.toString(mega30));
    }

    public static Boolean isMega30(ParametroJogoDTO parametroSimulacao) {
        try {
            return parametroSimulacao != null && parametroSimulacao.getConcurso() != null
                    && parametroSimulacao.getConcurso().getNumero() != null
                    && parametroSimulacao.getConcurso().getTipoConcurso() != null
                    && isMega30(parametroSimulacao.getConcurso().getNumero(), parametroSimulacao.getConcurso().getTipoConcurso());
        } catch (Exception e){
            return false;
        }
    }

    public static Boolean isOutubroRosa(){
        return SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.OUTUBRO_ROSA.get(), false);
    }

    public static Boolean isOutubroRosaMegaSena(){
        return SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.OUTUBRO_ROSA_MEGA_SENA.get(), false);
    }

    public static Boolean isParametrosOutubroRosa(){
        return isOutubroRosa() && isOutubroRosaMegaSena();
    }
}
