package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class VolanteUtils {
   private static int LOTOMANIA_CEM = 100;
   private static int PRIMEIRO_PROGNOSTICO = 1;

   public static List<Dezena> getDezenas(ModalidadeEnum tipoJogo, int qtdPrognostico){
      List<Dezena> dezenas = new ArrayList<>();
      for (int i = PRIMEIRO_PROGNOSTICO; i <= qtdPrognostico; i++) {
         if (i == LOTOMANIA_CEM && tipoJogo == ModalidadeEnum.LOTOMANIA){
            dezenas.add(new Dezena(""+ i, Boolean.FALSE, "00"));
         }else {
            dezenas.add(new Dezena("" + i, Boolean.FALSE));
         }
      }

      return dezenas;
   }

   public static Integer getLabelPrognostico(Integer original, ModalidadeEnum tipoJogo){
      if(tipoJogo == ModalidadeEnum.LOTOMANIA && original.equals(new Integer(LOTOMANIA_CEM))){
         return 0;
      }
      return original;
   }

}
