package caixa.gov.br.loterias.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cedesbr450 on 24/01/18.
 */

public class ExpandableListDataSideMenu {
    public static HashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        List<String> minhaArea = new ArrayList<String>();
        minhaArea.add("Dados Pessoais");
        minhaArea.add("Apostas confirmadas");
        minhaArea.add("Compras");
        minhaArea.add("Favoritas");
        minhaArea.add("Formas de pagamento");

        List<String> Apostar = new ArrayList<String>();
        List<String> Resultados = new ArrayList<String>();
        List<String> Rapidao = new ArrayList<String>();
        List<String> bilhetes = new ArrayList<String>();
        List<String> sociais = new ArrayList<String>();
        List<String> Sair = new ArrayList<String>();


        expandableListDetail.put("Minha área", minhaArea);
        expandableListDetail.put("Apostar", Apostar);
        expandableListDetail.put("Resultados", Resultados);
        expandableListDetail.put("Rapidão", Rapidao);
        expandableListDetail.put("Ler bilhetes", bilhetes);
        expandableListDetail.put("Repasses sociais", sociais);
        expandableListDetail.put("Sair", Sair);
        return expandableListDetail;
    }
}