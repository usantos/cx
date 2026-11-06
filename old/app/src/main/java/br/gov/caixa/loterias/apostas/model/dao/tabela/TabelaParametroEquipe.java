package br.gov.caixa.loterias.apostas.model.dao.tabela;

import android.provider.BaseColumns;

/**
 * Mapea colunas da tabela TabelaParametroEquipe
 */
public class TabelaParametroEquipe implements BaseColumns {
	public static final String NOME_TABELA = "tb_parametro_equipe";
	public static final String COLUNA_INDICADOR_SELECAO = "indicadorSelecao";
	public static final String COLUNA_NOME = "nome";
	public static final String COLUNA_NUMERO = "numero";
	public static final String COLUNA_NUMERO_PAIS = "numero_pais";
	public static final String COLUNA_DESCRICAO_CURTA = "descricao_curta";
	public static final String COLUNA_DESCRICAO_LONGA = "descricao_longa";
	public static final String COLUNA_PAIS = "pais";
	public static final String COLUNA_SIGLA_PAIS = "sigla_pais";
	public static final String COLUNA_UF = "uf";
	public static final String COLUNA_NOME_CLASS = "nome_class";
}
