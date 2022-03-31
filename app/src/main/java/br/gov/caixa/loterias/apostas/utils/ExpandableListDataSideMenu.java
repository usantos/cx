package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;

/**
 * Created by cedesbr450 on 24/01/18.
 */

public class ExpandableListDataSideMenu {
    public static final String MENU_DADOS_PESSOAIS = "Dados Pessoais";
    public static final String MENU_MINHAS_APOSTAS = "Minhas Apostas";
    public static final String MENU_COMPRAS = "Minhas Compras";
    public static final String MENU_FAVORITAS = "Apostas Favoritas";
    public static final String MENU_CARRINHOS_FAVORITOS = "Carrinhos Favoritos";
    public static final String MENU_MEUS_CARTOES = "Meus Cartões";

    public static HashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

        List<String> minhaArea = new ArrayList<>();
        minhaArea.add(MENU_DADOS_PESSOAIS);
        //minhaArea.add("Alterar dados do login Caixa");
        if (ConfiguracaoAppSingleton.getInstance().isMostraMenuAposta()) {
            minhaArea.add(MENU_MINHAS_APOSTAS);
        }
        minhaArea.add(MENU_COMPRAS);
        minhaArea.add(MENU_FAVORITAS);
        minhaArea.add(MENU_CARRINHOS_FAVORITOS);
        minhaArea.add(MENU_MEUS_CARTOES);

        List<String> Resultados = new ArrayList<>();
        List<String> Rapidao = new ArrayList<>();
        List<String> bilhetes = new ArrayList<>();
        List<String> sociais = new ArrayList<>();
        List<String> JogoResponsavel = new ArrayList<>();


        expandableListDetail.put("Minha área", minhaArea);
        expandableListDetail.put("Resultados", Resultados);
        expandableListDetail.put("Rapidão", Rapidao);
        expandableListDetail.put("Ler bilhete", bilhetes);
        expandableListDetail.put("Jogo Responsável", JogoResponsavel);
        expandableListDetail.put("Repasses sociais", sociais);
        return expandableListDetail;
    }
}