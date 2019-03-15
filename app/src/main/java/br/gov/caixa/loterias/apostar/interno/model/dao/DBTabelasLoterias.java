package br.gov.caixa.loterias.apostar.interno.model.dao;

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

        String sb = (" CREATE TABLE " + DBLoterica.NOME_TABELA) +
                " ( " +
                DBLoterica._ID + " INTEGER PRIMARY KEY, " +
                DBLoterica.COLUNA_NOME_FANTASIA + " TEXT, " +
                DBLoterica.COLUNA_NOME + " TEXT, " +
                DBLoterica.COLUNA_LOGRADOURO + " TEXT, " +
                DBLoterica.COLUNA_BAIRRO + " TEXT, " +
                DBLoterica.COLUNA_NOME_MUNICIPIO + " TEXT," +
                DBLoterica.COLUNA_NOME_UF + " TEXT, " +
                DBLoterica.COLUNA_ID_LOTERICA + " NUMERIC, " +
                DBLoterica.COLUNA_CODIGO + " TEXT, " +
                DBLoterica.COLUNA_ID_UF + " NUMERIC, " +
                DBLoterica.COLUNA_ID_MUNICIPIO + " NUMERIC, " +
                DBLoterica.COLUNA_DV_MUNICIPIO + " NUMERIC, " +
                DBLoterica.COLUNA_SITUACAO_VALOR + " NUMERIC, " +
                DBLoterica.COLUNA_SITUACAO_DESCRICAO + " TEXT, " +
                DBLoterica.COLUNA_POLO + " NUMERIC, " +
                DBLoterica.COLUNA_DV + " NUMERIC, " +
                DBLoterica.COLUNA_NUMERO_FORMATADO + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de IdentificaoDeUmaApostaDas8Modalidades (Aposta)
     *
     * @return sb
     */
    public static String criarTabelaIdentificaoDeUmaApostaDas8Modalidades() {

        String sb = (" CREATE TABLE " + DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA) +
                " ( " +
                DBIdentificaoDeUmaApostaDas8Modalidades._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR + " NUMERIC, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR + " REAL, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR + " NUMERIC, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO + " TEXT, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID + " NUMERIC, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO + " NUMERIC, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA + " INTEGER, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO + " NUMERIC, " +
                DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_MES_SORTE + " NUMERIC " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de EquipeDTO
     *
     * @return sb
     */
    public static String criarTabelaEquipe() {

        String sb = (" CREATE TABLE " + DBEquipe.NOME_TABELA) +
                " ( " +
                DBEquipe._ID + " INTEGER PRIMARY KEY, " +
                DBEquipe.COLUNA_PLACAR + " TEXT, " +
                DBEquipe.COLUNA_VITORIA + " INTEGER, " +
                DBEquipe.COLUNA_ID_PARAMETRO_EQUIPE + " NUMERIC, " +
                DBEquipe.COLUNA_NOME + " TEXT, " +
                DBEquipe.COLUNA_NUMERO + " INTEGER, " +
                DBEquipe.COLUNA_NUMERO_PAIS + " INTEGER, " +
                DBEquipe.COLUNA_PAIS + " TEXT, " +
                DBEquipe.COLUNA_SIGLA_PAIS + " TEXT, " +
                DBEquipe.COLUNA_UF + " TEXT, " +
                DBEquipe.COLUNA_NOME_CLASS + " TEXT, " +
                DBEquipe.COLUNA_INDICADOR_SELECAO + " INTEGER, " +
                DBEquipe.COLUNA_DESCRICAO_CURTA + " TEXT, " +
                DBEquipe.COLUNA_DESCRICAO_LONGA + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de ParametroEquipe
     *
     * @return sb
     */
    public static String criarTabelaParametroEquipe() {

        String sb = (" CREATE TABLE " + DBParametroEquipe.NOME_TABELA) +
                " ( " +
                DBParametroEquipe._ID + " INTEGER PRIMARY KEY, " +
                DBParametroEquipe.COLUNA_INDICADOR_SELECAO + " INTEGER, " +
                DBParametroEquipe.COLUNA_NOME + " TEXT, " +
                DBParametroEquipe.COLUNA_NUMERO + " INTEGER, " +
                DBParametroEquipe.COLUNA_NUMERO_PAIS + " INTEGER, " +
                DBParametroEquipe.COLUNA_DESCRICAO_CURTA + " TEXT, " +
                DBParametroEquipe.COLUNA_DESCRICAO_LONGA + " TEXT, " +
                DBParametroEquipe.COLUNA_PAIS + " TEXT, " +
                DBParametroEquipe.COLUNA_SIGLA_PAIS + " TEXT, " +
                DBParametroEquipe.COLUNA_UF + " TEXT, " +
                DBParametroEquipe.COLUNA_NOME_CLASS + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de PartidaLotogolDTO
     *
     * @return sb
     */
    public static String criarTabelaLotogol() {

        String sb = (" CREATE TABLE " + DBPartidaLotogol.NOME_TABELA) +
                " ( " +
                DBPartidaLotogol._ID + " INTEGER PRIMARY KEY, " +
                DBPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " NUMERIC, " +
                DBPartidaLotogol.COLUNA_ID_EQUIPE_UM + " NUMERIC, " +
                DBPartidaLotogol.COLUNA_ID_EQUIPE_DOIS + " NUMERIC, " +
                DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM + " INTEGER, " +
                DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS + " INTEGER " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de PartidaLotecaDTO
     *
     * @return sb
     */
    public static String criarTabelaLoteca(){

        String sb = (" CREATE TABLE " + DBPartidaLoteca.NOME_TABELA) +
                " ( " +
                DBPartidaLoteca._ID + " INTEGER PRIMARY KEY, " +
                DBPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " NUMERIC, " +
                DBPartidaLoteca.COLUNA_ID_EQUIPE_UM + " NUMERIC, " +
                DBPartidaLoteca.COLUNA_ID_EQUIPE_DOIS + " NUMERIC, " +
                DBPartidaLoteca.COLUNA_EMPATE + " INTEGER " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de ParametroMesDeSorte
     *
     * @return sb
     */
    public static String criarTabelaMesDeSorte(){

        String sb = (" CREATE TABLE " + DBMesDeSorte.NOME_TABELA) +
                " ( " +
                DBMesDeSorte._ID + " INTEGER PRIMARY KEY, " +
                DBMesDeSorte.COLUNA_NUMERO + " NUMERIC, " +
                DBMesDeSorte.COLUNA_NOME + " TEXT, " +
                DBMesDeSorte.COLUNA_ABREVIACAO + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para excluir tabela de acordo com nome
     *
     * @param tabela nome
     * @return sb
     */
    public static String deleteTabela(String tabela) {

        return ("DROP TABLE IF EXISTS " + tabela);
    }

    /**
     * Mapea colunas da tabela DBLoterica
     */
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

    /**
     * Mapea colunas da tabela DBIdentificaoDeUmaApostaDas8Modalidades
     */
    static class DBIdentificaoDeUmaApostaDas8Modalidades implements BaseColumns {
        static final String NOME_TABELA = "tb_identificao_de_uma_aposta_das_8_modalidades";
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
        static final String COLUNA_ID_TIME_CORACAO = "id_time_coracao";
        static final String COLUNA_ID_MES_SORTE = "id_mes_sorte";
    }

    /**
     * Mapea colunas da tabela DBEquipe
     */
    static class DBEquipe implements BaseColumns {
        static final String NOME_TABELA = "tb_equipe";
        static final String COLUNA_PLACAR = "placar";
        static final String COLUNA_VITORIA = "vitoria";
        static final String COLUNA_ID_PARAMETRO_EQUIPE = "id_parametro_equipe";
        static final String COLUNA_NOME = "nome";
        static final String COLUNA_NUMERO = "numero";
        static final String COLUNA_NUMERO_PAIS = "numero_pais";
        static final String COLUNA_PAIS = "pais";
        static final String COLUNA_SIGLA_PAIS = "sigla_pais";
        static final String COLUNA_UF = "uf";
        static final String COLUNA_NOME_CLASS = "nome_class";
        static final String COLUNA_INDICADOR_SELECAO = "indicador_selecao";
        static final String COLUNA_DESCRICAO_CURTA = "descricao_curta";
        static final String COLUNA_DESCRICAO_LONGA = "descricao_longa";
    }

    /**
     * Mapea colunas da tabela DBParametroEquipe
     */
    static class DBParametroEquipe implements BaseColumns {
        static final String NOME_TABELA = "tb_parametro_equipe";
        static final String COLUNA_INDICADOR_SELECAO = "indicadorSelecao";
        static final String COLUNA_NOME = "nome";
        static final String COLUNA_NUMERO = "numero";
        static final String COLUNA_NUMERO_PAIS = "numero_pais";
        static final String COLUNA_DESCRICAO_CURTA = "descricao_curta";
        static final String COLUNA_DESCRICAO_LONGA = "descricao_longa";
        static final String COLUNA_PAIS = "pais";
        static final String COLUNA_SIGLA_PAIS = "sigla_pais";
        static final String COLUNA_UF = "uf";
        static final String COLUNA_NOME_CLASS = "nome_class";
    }

    /**
     * Mapea colunas da tabela DBPartidaLotogol
     */
    static class DBPartidaLotogol implements BaseColumns {
        static final String NOME_TABELA = "tb_partida_lotogol";
        static final String COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES = "id_identificao_de_uma_aposta_das_8_modalidades";
        static final String COLUNA_ID_EQUIPE_UM = "id_equipe_um";
        static final String COLUNA_ID_EQUIPE_DOIS = "id_equipe_dois";
        static final String COLUNA_INDEX_PLACAR_EQUIPE_UM = "index_placar_equipe_um";
        static final String COLUNA_INDEX_PLACAR_EQUIPE_DOIS = "index_placar_equipe_dois";
    }

    /**
     * Mapea colunas da tabela DBPartidaLoteca
     */
    static class DBPartidaLoteca implements BaseColumns {
        static final String NOME_TABELA = "tb_partida_loteca";
        static final String COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES = "id_identificao_de_uma_aposta_das_8_modalidades";
        static final String COLUNA_ID_EQUIPE_UM = "id_equipe_um";
        static final String COLUNA_ID_EQUIPE_DOIS = "id_equipe_dois";
        static final String COLUNA_EMPATE = "empate";
    }

    /**
     * Mapea colunas da tabela DBMesDeSorte
     */
    static class DBMesDeSorte implements BaseColumns {
        static final String NOME_TABELA = "tb_mes_sorte";
        static final String COLUNA_NUMERO = "numero";
        static final String COLUNA_NOME = "nome";
        static final String COLUNA_ABREVIACAO = "abreviacao";
    }

}
