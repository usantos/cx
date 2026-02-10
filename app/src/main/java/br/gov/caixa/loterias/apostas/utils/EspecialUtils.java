package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class EspecialUtils {
    public static String MEGA_LINHA = "ARG_30_ANOS_LINHA";
    public static String MEGA_DUAS_LINHAS = "ARG_30_ANOS_DUAS_LINHAS";
    public static String MEGA_VALOR = "ARG_30_ANOS_VALOR";

    public static Boolean isMega30(Integer numeroConcurso, TipoConcursoEnum tipoConcurso){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso.equals(TipoConcursoEnum.ESPECIAL);
        return isEspecial && numeroConcurso.equals(mega30);
    }

    public static Boolean isMega30(Integer numeroConcurso, Integer tipoConcurso){
        int mega30 = SharedPreferencesUtils.getValorInt(MEGA_VALOR, -1);
        boolean isEspecial = tipoConcurso.toString().equals(TipoConcursoEnum.ESPECIAL.getValor());
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

}
