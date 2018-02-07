package caixa.gov.br.loterias.dao;

import android.provider.BaseColumns;

/**
 * Created by joafilho on 01/02/2018.
 * Class DBTabelasLoterias resposnsável por mapear as tabelas
 */

public final class DBTabelasLoterias {

    private DBTabelasLoterias() {
    }

    /**
     * Método para criar tabela de LoteriaDTO
     *
     * @return sb
     */
    public static String criarTebelaLoteria() {

        StringBuilder sb = new StringBuilder();

        sb.append(" CREATE TABLE " + DBLoterica.NOME_TABELA);
        sb.append(" ( ");
        sb.append(DBLoterica._ID + " INTEGER PRIMARY KEY, ");
        sb.append(DBLoterica.COLUNA_NOME_FANTASIA + " TEXT, ");
        sb.append(DBLoterica.COLUNA_NOME + " TEXT, ");
        sb.append(DBLoterica.COLUNA_LOGRADOURO + " TEXT, ");
        sb.append(DBLoterica.COLUNA_BAIRRO + " TEXT, ");
        sb.append(DBLoterica.COLUNA_NOME_MUNICIPIO + " TEXT,");
        sb.append(DBLoterica.COLUNA_NOME_UF + " TEXT, ");
        sb.append(DBLoterica.COLUNA_ID_LOTERICA + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_CODIGO + " TEXT, ");
        sb.append(DBLoterica.COLUNA_ID_UF + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_ID_MUNICIPIO + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_DV_MUNICIPIO + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_SITUACAO_VALOR + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_SITUACAO_DESCRICAO + " TEXT, ");
        sb.append(DBLoterica.COLUNA_POLO + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_DV + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_NUMERO_FORMATADO + " TEXT ");
        sb.append(" ) ");

        return sb.toString();
    }

    /**
     * Método para criar tabela de IdentificaoDeUmaApostaDas8Modalidades (Aposta)
     *
     * @return sb
     */
    public static String criarTabelaIdentificaoDeUmaApostaDas8Modalidades() {

        StringBuilder sb = new StringBuilder();

        sb.append(" CREATE TABLE " + DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
        sb.append(" ( ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades._ID + " INTEGER PRIMARY KEY, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR + " REAL, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CARRINHO_ID + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA + " INTEGER ");
        sb.append(" ) ");

        return sb.toString();
    }

    /**
     * Método para criar tabela de CarrinhoDTO
     *
     * @return sb
     */
    public static String criarTabelaCarrinho() {

        StringBuilder sb = new StringBuilder();

        sb.append(" CREATE TABLE " + DBCarrinho.NOME_TABELA);
        sb.append(" ( ");
        sb.append(DBCarrinho._ID + " INTEGER PRIMARY KEY, ");
        sb.append(DBCarrinho.COLUNA_VALOR_TOTAL + " REAL ");
        sb.append(" ) ");

        return sb.toString();
    }

    /**
     * Método para excluir tabela de acordo com nome
     *
     * @param tabela nome
     * @return sb
     */
    public static String deleteTabela(String tabela) {

        StringBuilder sb = new StringBuilder();

        sb.append("DROP TABLE IF EXISTS " + tabela);

        return sb.toString();
    }

    static class DBLoterica implements BaseColumns {
        static final String NOME_TABELA = "tb_loterica";
        static final String COLUNA_NOME_FANTASIA = "nome_fantasia";
        static final String COLUNA_NOME = "nome";
        static final String COLUNA_LOGRADOURO = "logradouro";
        static final String COLUNA_BAIRRO = "bairro";
        static final String COLUNA_NOME_MUNICIPIO = "nome_municipio";
        static final String COLUNA_NOME_UF = "nome_uf";
        static final String COLUNA_ID_LOTERICA = "id_loterica";
        static final String COLUNA_CODIGO = "codigo";
        static final String COLUNA_ID_UF = "id_uf";
        static final String COLUNA_ID_MUNICIPIO = "id_municipio";
        static final String COLUNA_DV_MUNICIPIO = "dv_municipio";
        static final String COLUNA_SITUACAO_VALOR = "situacao_valor";
        static final String COLUNA_SITUACAO_DESCRICAO = "situacao_descricao";
        static final String COLUNA_POLO = "polo";
        static final String COLUNA_DV = "dv";
        static final String COLUNA_NUMERO_FORMATADO = "numero_formatado";
    }

    static class DBIdentificaoDeUmaApostaDas8Modalidades implements BaseColumns {
        static final String NOME_TABELA = "tb_identificao_de_uma_aposta_das_8_modalidades";
        static final String COLUNA_CARRINHO_ID = "id_carrinho";
        static final String COLUNA_ID_IDENTIFICADOR = "id_identificador";
        static final String COLUNA_MODALIDADE = "modalidade";
        static final String COLUNA_TIPO_CONCURSO_VALOR = "tipo_concurso_valor";
        static final String COLUNA_TIPO_CONCURSO_DESCRICAO = "tipo_concurso_descricao";
        static final String COLUNA_NUMEROS_SELECIONADOS = "numeros_selecionados";
        static final String COLUNA_INDICADOR_SURPRESINHA_VALOR = "indicador_surpresinha_valor";
        static final String COLUNA_INDICADOR_SURPRESINHA_DESCRICAO = "indicador_surpresinha_descricao";
        static final String COLUNA_VALOR = "valor";
        static final String COLUNA_CONCURSO_ALVO = "concurso_alvo";
        static final String COLUNA_QUANTIDADE_TEIMOSINHAS = "quantidade_teimosinhas";
        static final String COLUNA_QUANTIDADE_APOSTAS = "quantidade_apostas";
        static final String COLUNA_ESPELHO = "espelho";
        static final String COLUNA_CONCURSO_INICIAL = "concurso_inicial";
        static final String COLUNA_SITUACAO_VALOR = "situacao_valor";
        static final String COLUNA_SITUACAO_DESCRICAO = "situacao_descricao";
        static final String COLUNA_TROCA = "troca";
        static final String COLUNA_DATA_EFETIVACAO = "data_efetivacao";
        static final String COLUNA_HORA_EFETIVACAO = "hora_efetivacao";
        static final String COLUNA_LOTERICA_ID = "tb_loterica_id";
        static final String COLUNA_VINCULO_ESPELHO = "vinculo_espelho";
        static final String COLUNA_QUANTIDADE_NUMEROS = "quantidade_numeros";
        static final String COLUNA_SURPRESINHA = "surpresinha";
        static final String COLUNA_QUANTIDADE_SURPRESINHA = "quantidade_surpresinha";
    }

    static class DBCarrinho implements BaseColumns {
        static final String NOME_TABELA = "tb_carrinho";
        static final String COLUNA_VALOR_TOTAL = "valor_total";
    }

}
