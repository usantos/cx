package br.gov.caixa.loterias.apostas.model.dao.tabela;

import android.provider.BaseColumns;

/**
 * Mapea colunas da tabela DBLoteTabelaLotericarica
 */
public class TabelaLoterica implements BaseColumns {
	public static final String NOME_TABELA = "tb_loterica";
	public static final String COLUNA_NOME_FANTASIA = "nome_fantasia";
	public static final String COLUNA_NOME = "nome";
	public static final String COLUNA_LOGRADOURO = "logradouro";
	public static final String COLUNA_BAIRRO = "bairro";
	public static final String COLUNA_NOME_MUNICIPIO = "nome_municipio";
	public static final String COLUNA_NOME_UF = "nome_uf";
	public static final String COLUNA_ID_LOTERICA = "id_loterica";
	public static final String COLUNA_CODIGO = "codigo";
	public static final String COLUNA_ID_UF = "id_uf";
	public static final String COLUNA_ID_MUNICIPIO = "id_municipio";
	public static final String COLUNA_DV_MUNICIPIO = "dv_municipio";
	public static final String COLUNA_SITUACAO_VALOR = "situacao_valor";
	public static final String COLUNA_SITUACAO_DESCRICAO = "situacao_descricao";
	public static final String COLUNA_POLO = "polo";
	public static final String COLUNA_DV = "dv";
	public static final String COLUNA_NUMERO_FORMATADO = "numero_formatado";
}
