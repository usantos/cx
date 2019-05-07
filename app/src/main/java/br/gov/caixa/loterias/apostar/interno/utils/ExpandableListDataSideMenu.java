package br.gov.caixa.loterias.apostar.interno.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cedesbr450 on 24/01/18.
 */

public class ExpandableListDataSideMenu {
    public static HashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

        List<String> minhaArea = new ArrayList<>();
        minhaArea.add("Dados Pessoais");
        minhaArea.add("Alterar dados do login Caixa");
        minhaArea.add("Apostas confirmadas");
        minhaArea.add("Compras");
        minhaArea.add("Favoritas");

        List<String> Resultados = new ArrayList<>();
        List<String> Rapidao = new ArrayList<>();
        List<String> bilhetes = new ArrayList<>();
        List<String> sociais = new ArrayList<>();


        expandableListDetail.put("Minha área", minhaArea);
        expandableListDetail.put("Resultados", Resultados);
        expandableListDetail.put("Rapidão", Rapidao);
        expandableListDetail.put("Ler bilhete", bilhetes);
        expandableListDetail.put("Repasses sociais", sociais);
        return expandableListDetail;
    }
}