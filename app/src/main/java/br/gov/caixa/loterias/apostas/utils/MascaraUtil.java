package br.gov.caixa.loterias.apostas.utils;

public class MascaraUtil {

      public static String removeMascaraCelular(String celular) {
            if (celular == null || celular.isEmpty()){
                  return "";
            }
            celular = celular.replace("(", "");
            celular = celular.replace(")", "");
            celular = celular.replace(" ", "");
            celular = celular.replace("-", "");

            return celular;
      }

}
