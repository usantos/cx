package br.gov.caixa.loterias.apostas.model.dao.tabela;

import android.provider.BaseColumns;

/**
 * Mapea colunas da tabela TabelaEquipe
 */
public class TabelaEquipe implements BaseColumns {
	public static final String NOME_TABELA = "tb_equipe";
	public static final String COLUNA_PLACAR = "placar";
	public static final String COLUNA_VITORIA = "vitoria";
	public static final String COLUNA_ID_PARAMETRO_EQUIPE = "id_parametro_equipe";
	public static final String COLUNA_NOME = "nome";
	public static final String COLUNA_NUMERO = "numero";
	public static final String COLUNA_NUMERO_PAIS = "numero_pais";
	public static final String COLUNA_PAIS = "pais";
	public static final String COLUNA_SIGLA_PAIS = "sigla_pais";
	public static final String COLUNA_UF = "uf";
	public static final String COLUNA_NOME_CLASS = "nome_class";
	public static final String COLUNA_INDICADOR_SELECAO = "indicador_selecao";
	public static final String COLUNA_DESCRICAO_CURTA = "descricao_curta";
	public static final String COLUNA_DESCRICAO_LONGA = "descricao_longa";
}
