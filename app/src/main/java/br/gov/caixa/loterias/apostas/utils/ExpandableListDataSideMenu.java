package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cedesbr450 on 24/01/18.
 */

public class ExpandableListDataSideMenu {

    public static HashMap<DrawerEnum, List<DrawerEnum>> getData() {
        LinkedHashMap<DrawerEnum, List<DrawerEnum>> expandableListDetail = new LinkedHashMap<>();

        List<DrawerEnum> minhaArea = new ArrayList<>();
        minhaArea.add(DrawerEnum.MENU_DADOS_PESSOAIS);
        if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.MOSTRA_MENU_APOSTA.get(), ConfiguracoesDefaultEnum.MOSTRA_MENU_APOSTAS.asBoolean())) {
            minhaArea.add(DrawerEnum.MENU_MINHAS_APOSTAS);
        }
        minhaArea.add(DrawerEnum.MENU_COMPRAS);
        minhaArea.add(DrawerEnum.MENU_FAVORITAS);
        minhaArea.add(DrawerEnum.MENU_CARRINHOS_FAVORITOS);
        minhaArea.add(DrawerEnum.MENU_MEUS_CARTOES);

        List<DrawerEnum> jogoResponsavel = new ArrayList<>();
        jogoResponsavel.add(DrawerEnum.MENU_JOGO_RESPONSAVEL_POLITICA);
        jogoResponsavel.add(DrawerEnum.MENU_JOGO_RESPONSAVEL_AVALIACAO);
        jogoResponsavel.add(DrawerEnum.MENU_JOGO_RESPONSAVEL_SUSPENSAO);

        expandableListDetail.put(DrawerEnum.MENU_MINHA_AREA, minhaArea);
        expandableListDetail.put(DrawerEnum.MENU_RESULTADOS, new ArrayList<>());
        if(SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_RAPIDAO.get(), ConfiguracoesDefaultEnum.IS_RAPIDAO.asBoolean())){
            expandableListDetail.put(DrawerEnum.MENU_RAPIDAO, new ArrayList<>());
        }
        expandableListDetail.put(DrawerEnum.MENU_COFERIR_BILHETE, new ArrayList<>());
        expandableListDetail.put(DrawerEnum.MENU_JOGO_RESPONSAVEL, jogoResponsavel);
        expandableListDetail.put(DrawerEnum.MENU_REPASSES_SOCIAIS, new ArrayList<>());
        expandableListDetail.put(DrawerEnum.MENU_TERMO_USO, new ArrayList<>());
        expandableListDetail.put(DrawerEnum.MENU_DUVIDAS, new ArrayList<>());
        expandableListDetail.put(DrawerEnum.MENU_SOBRE_CAIXA, new ArrayList<>());
        expandableListDetail.put(DrawerEnum.MENU_APOSTA_SHAKE, new ArrayList<>());

        return expandableListDetail;
    }

}
