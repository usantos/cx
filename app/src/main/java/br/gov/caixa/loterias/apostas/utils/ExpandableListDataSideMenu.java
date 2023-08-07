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
    public static final String MENU_DADOS_PESSOAIS = "Dados pessoais";
    public static final String MENU_MINHAS_APOSTAS = "Minhas apostas";
    public static final String MENU_COMPRAS = "Minhas compras";
    public static final String MENU_FAVORITAS = "Apostas favoritas";
    public static final String MENU_CARRINHOS_FAVORITOS = "Carrinhos favoritos";
    public static final String MENU_MEUS_CARTOES = "Meus cartões";
    public static final String MENU_NOTIFICACOES1 = "Mensagens";
    public static final String MENU_NOTIFICACOES2 = "Preferências";

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
        List<String> termo = new ArrayList<>();
        List<String> duvidas = new ArrayList<>();
        List<String> JogoResponsavel = new ArrayList<>();
        List<String> notificacoes = new ArrayList<>();
        notificacoes.add(MENU_NOTIFICACOES1);
        notificacoes.add(MENU_NOTIFICACOES2);


        expandableListDetail.put("Minha área", minhaArea);
        expandableListDetail.put("Resultados", Resultados);
        expandableListDetail.put("Rapidão", Rapidao);
        expandableListDetail.put("Conferir bilhetes", bilhetes);
        expandableListDetail.put("Jogo responsável", JogoResponsavel);
        expandableListDetail.put("Repasses sociais", sociais);
        expandableListDetail.put("Termo de aceite", termo);
        expandableListDetail.put("Dúvidas", duvidas);
        expandableListDetail.put("Notificações", notificacoes);
        return expandableListDetail;
    }
}
