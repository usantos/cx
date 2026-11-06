package br.gov.caixa.loterias.apostas.model.dao.tabela;

import android.provider.BaseColumns;

/**
 * Mapea colunas da tabela TabelaIdentificaoDeUmaApostaDas8Modalidades
 */
public class TabelaIdentificaoDeUmaApostaDas8Modalidades implements BaseColumns {
	public static final String NOME_TABELA = "tb_identificao_de_uma_aposta_das_8_modalidades";
	public static final String COLUNA_ID_IDENTIFICADOR = "id_identificador";
	public static final String COLUNA_MODALIDADE = "modalidade";
	public static final String COLUNA_TIPO_CONCURSO_VALOR = "tipo_concurso_valor";
	public static final String COLUNA_TIPO_CONCURSO_DESCRICAO = "tipo_concurso_descricao";
	public static final String COLUNA_NUMEROS_SELECIONADOS = "numeros_selecionados";
	public static final String COLUNA_TREVOS_SELECIONADOS = "trevos_selecionados";
	public static final String COLUNA_INDICADOR_SURPRESINHA_VALOR = "indicador_surpresinha_valor";
	public static final String COLUNA_INDICADOR_SURPRESINHA_DESCRICAO = "indicador_surpresinha_descricao";
	public static final String COLUNA_VALOR = "valor";
	public static final String COLUNA_CONCURSO_ALVO = "concurso_alvo";
	public static final String COLUNA_QUANTIDADE_TEIMOSINHAS = "quantidade_teimosinhas";
	public static final String COLUNA_QUANTIDADE_APOSTAS = "quantidade_apostas";
	public static final String COLUNA_ESPELHO = "espelho";
	public static final String COLUNA_GERAR_ESPELHO = "gera_espelho";
	public static final String COLUNA_CONCURSO_INICIAL = "concurso_inicial";
	public static final String COLUNA_SITUACAO_VALOR = "situacao_valor";
	public static final String COLUNA_SITUACAO_DESCRICAO = "situacao_descricao";
	public static final String COLUNA_TROCA = "troca";
	public static final String COLUNA_DATA_EFETIVACAO = "data_efetivacao";
	public static final String COLUNA_HORA_EFETIVACAO = "hora_efetivacao";
	public static final String COLUNA_LOTERICA_ID = "tb_loterica_id";
	public static final String COLUNA_VINCULO_ESPELHO = "vinculo_espelho";
	public static final String COLUNA_QUANTIDADE_NUMEROS = "quantidade_numeros";
	public static final String COLUNA_QUANTIDADE_TREVOS = "quantidade_trevos";
	public static final String COLUNA_SURPRESINHA = "surpresinha";
	public static final String COLUNA_QUANTIDADE_SURPRESINHA = "quantidade_surpresinha";
	public static final String COLUNA_ID_TIME_CORACAO = "id_time_coracao";
	public static final String COLUNA_ID_MES_SORTE = "id_mes_sorte";
}
