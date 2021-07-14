package br.gov.caixa.loterias.apostas.model.dao.tabela;

import android.provider.BaseColumns;

/**
 * Mapea colunas da tabela TabelaPartidaLotogol
 */
public class TabelaPartidaLotogol implements BaseColumns {
	public static final String NOME_TABELA = "tb_partida_lotogol";
	public static final String COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES = "id_identificao_de_uma_aposta_das_8_modalidades";
	public static final String COLUNA_ID_EQUIPE_UM = "id_equipe_um";
	public static final String COLUNA_ID_EQUIPE_DOIS = "id_equipe_dois";
	public static final String COLUNA_INDEX_PLACAR_EQUIPE_UM = "index_placar_equipe_um";
	public static final String COLUNA_INDEX_PLACAR_EQUIPE_DOIS = "index_placar_equipe_dois";
}
