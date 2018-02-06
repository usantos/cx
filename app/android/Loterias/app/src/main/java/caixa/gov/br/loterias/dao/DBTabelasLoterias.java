package caixa.gov.br.loterias.dao;

import android.provider.BaseColumns;

/**
 * Created by joafilho on 01/02/2018.
 */

public final class DBTabelasLoterias {

    private DBTabelasLoterias() {
    }

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
        sb.append(DBLoterica.COLUNA_SITUACAO + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_POLO + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_DV + " NUMERIC, ");
        sb.append(DBLoterica.COLUNA_NUMERO_FORMATADO + " TEXT ");
        sb.append(" ) ");

        return sb.toString();
    }

    public static String criarTabelaIdentificaoDeUmaApostaDas8Modalidades() {

        StringBuilder sb = new StringBuilder();

        sb.append(" CREATE TABLE " + DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
        sb.append(" ( ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades._ID + " INTEGER PRIMARY KEY, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_ID + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_ID + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR + " REAL, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_ID + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO + " TEXT, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO + " NUMERIC, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA + " INTEGER, ");
        sb.append(DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CARRINHO_ID + " NUMERIC ");
        sb.append(" ) ");

        return sb.toString();
    }

    public static String criarTabelaCarrinho() {

        StringBuilder sb = new StringBuilder();

        sb.append(" CREATE TABLE " + DBCarrinho.NOME_TABELA);
        sb.append(" ( ");
        sb.append(DBCarrinho._ID + " INTEGER PRIMARY KEY, ");
        sb.append(DBCarrinho.COLUNA_VALOR_TOTAL + " REAL ");
        sb.append(" ) ");

        return sb.toString();
    }

    public static String deleteTabela(String tabela) {

        StringBuilder sb = new StringBuilder();

        sb.append("DROP TABLE IF EXISTS " + tabela);

        return sb.toString();
    }

    public static class DBLoterica implements BaseColumns {
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
        public static final String COLUNA_SITUACAO = "situacao";
        public static final String COLUNA_POLO = "polo";
        public static final String COLUNA_DV = "dv";
        public static final String COLUNA_NUMERO_FORMATADO = "numero_formatado";
    }

    public static class DBIdentificaoDeUmaApostaDas8Modalidades implements BaseColumns {
        public static final String NOME_TABELA = "tb_identificao_de_uma_aposta_das_8_modalidades";
        public static final String COLUNA_CARRINHO_ID = "id_carrinho";
        public static final String COLUNA_ID_IDENTIFICADOR = "id_identificador";
        public static final String COLUNA_MODALIDADE = "modalidade";
        public static final String COLUNA_TIPO_CONCURSO_ID = "tipo_concurso_id";
        public static final String COLUNA_NUMEROS_SELECIONADOS = "numeros_selecionados";
        public static final String COLUNA_INDICADOR_SURPRESINHA_ID = "indicador_surpresinha_id";
        public static final String COLUNA_VALOR = "valor";
        public static final String COLUNA_CONCURSO_ALVO = "concurso_alvo";
        public static final String COLUNA_QUANTIDADE_TEIMOSINHAS = "quantidade_teimosinhas";
        public static final String COLUNA_QUANTIDADE_APOSTAS = "quantidade_apostas";
        public static final String COLUNA_ESPELHO = "espelho";
        public static final String COLUNA_CONCURSO_INICIAL = "concurso_inicial";
        public static final String COLUNA_SITUACAO_ID = "situacao_id";
        public static final String COLUNA_TROCA = "troca";
        public static final String COLUNA_DATA_EFETIVACAO = "data_efetivacao";
        public static final String COLUNA_HORA_EFETIVACAO = "hora_efetivacao";
        public static final String COLUNA_LOTERICA_ID = "tb_loterica_id";
        public static final String COLUNA_VINCULO_ESPELHO = "vinculo_espelho";
        public static final String COLUNA_QUANTIDADE_NUMEROS = "quantidade_numeros";
        public static final String COLUNA_SURPRESINHA = "surpresinha";
    }

    public static class DBCarrinho implements BaseColumns {
        public static final String NOME_TABELA = "tb_carrinho";
        public static final String COLUNA_VALOR_TOTAL = "valor_total";
    }

}
