package br.gov.caixa.loterias.apostas.model.dao;

import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaBiometria;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaCarrinhoFavorito;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaEquipe;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaIdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaLoterica;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaMercadoPago;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaMesDeSorte;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaParametroEquipe;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaPartidaLoteca;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaPartidaLotogol;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaUltimaCompra;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaUsuario;

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

        String sb = (" CREATE TABLE " + TabelaLoterica.NOME_TABELA) +
                " ( " +
                TabelaLoterica._ID + " INTEGER PRIMARY KEY, " +
                TabelaLoterica.COLUNA_NOME_FANTASIA + " TEXT, " +
                TabelaLoterica.COLUNA_NOME + " TEXT, " +
                TabelaLoterica.COLUNA_LOGRADOURO + " TEXT, " +
                TabelaLoterica.COLUNA_BAIRRO + " TEXT, " +
                TabelaLoterica.COLUNA_NOME_MUNICIPIO + " TEXT," +
                TabelaLoterica.COLUNA_NOME_UF + " TEXT, " +
                TabelaLoterica.COLUNA_ID_LOTERICA + " NUMERIC, " +
                TabelaLoterica.COLUNA_CODIGO + " TEXT, " +
                TabelaLoterica.COLUNA_ID_UF + " NUMERIC, " +
                TabelaLoterica.COLUNA_ID_MUNICIPIO + " NUMERIC, " +
                TabelaLoterica.COLUNA_DV_MUNICIPIO + " NUMERIC, " +
                TabelaLoterica.COLUNA_SITUACAO_VALOR + " NUMERIC, " +
                TabelaLoterica.COLUNA_SITUACAO_DESCRICAO + " TEXT, " +
                TabelaLoterica.COLUNA_POLO + " NUMERIC, " +
                TabelaLoterica.COLUNA_DV + " NUMERIC, " +
                TabelaLoterica.COLUNA_NUMERO_FORMATADO + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de IdentificaoDeUmaApostaDas8Modalidades (Aposta)
     *
     * @return sb
     */
    public static String criarTabelaIdentificaoDeUmaApostaDas8Modalidades() {

        String sb = (" CREATE TABLE " + TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA) +
                " ( " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR + " NUMERIC, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TREVOS_SELECIONADOS + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR + " REAL, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_GERAR_ESPELHO+ " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR + " NUMERIC, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO + " TEXT, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID + " NUMERIC, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO + " NUMERIC, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TREVOS + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA + " INTEGER, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO + " NUMERIC, " +
                TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_MES_SORTE + " NUMERIC " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de EquipeDTO
     *
     * @return sb
     */
    public static String criarTabelaEquipe() {

        String sb = (" CREATE TABLE " + TabelaEquipe.NOME_TABELA) +
                " ( " +
                TabelaEquipe._ID + " INTEGER PRIMARY KEY, " +
                TabelaEquipe.COLUNA_PLACAR + " TEXT, " +
                TabelaEquipe.COLUNA_VITORIA + " INTEGER, " +
                TabelaEquipe.COLUNA_ID_PARAMETRO_EQUIPE + " NUMERIC, " +
                TabelaEquipe.COLUNA_NOME + " TEXT, " +
                TabelaEquipe.COLUNA_NUMERO + " INTEGER, " +
                TabelaEquipe.COLUNA_NUMERO_PAIS + " INTEGER, " +
                TabelaEquipe.COLUNA_PAIS + " TEXT, " +
                TabelaEquipe.COLUNA_SIGLA_PAIS + " TEXT, " +
                TabelaEquipe.COLUNA_UF + " TEXT, " +
                TabelaEquipe.COLUNA_NOME_CLASS + " TEXT, " +
                TabelaEquipe.COLUNA_INDICADOR_SELECAO + " INTEGER, " +
                TabelaEquipe.COLUNA_DESCRICAO_CURTA + " TEXT, " +
                TabelaEquipe.COLUNA_DESCRICAO_LONGA + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de ParametroEquipe
     *
     * @return sb
     */
    public static String criarTabelaParametroEquipe() {

        String sb = (" CREATE TABLE " + TabelaParametroEquipe.NOME_TABELA) +
                " ( " +
                TabelaParametroEquipe._ID + " INTEGER PRIMARY KEY, " +
                TabelaParametroEquipe.COLUNA_INDICADOR_SELECAO + " INTEGER, " +
                TabelaParametroEquipe.COLUNA_NOME + " TEXT, " +
                TabelaParametroEquipe.COLUNA_NUMERO + " INTEGER, " +
                TabelaParametroEquipe.COLUNA_NUMERO_PAIS + " INTEGER, " +
                TabelaParametroEquipe.COLUNA_DESCRICAO_CURTA + " TEXT, " +
                TabelaParametroEquipe.COLUNA_DESCRICAO_LONGA + " TEXT, " +
                TabelaParametroEquipe.COLUNA_PAIS + " TEXT, " +
                TabelaParametroEquipe.COLUNA_SIGLA_PAIS + " TEXT, " +
                TabelaParametroEquipe.COLUNA_UF + " TEXT, " +
                TabelaParametroEquipe.COLUNA_NOME_CLASS + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de PartidaLotogolDTO
     *
     * @return sb
     */
    public static String criarTabelaLotogol() {

        String sb = (" CREATE TABLE " + TabelaPartidaLotogol.NOME_TABELA) +
                " ( " +
                TabelaPartidaLotogol._ID + " INTEGER PRIMARY KEY, " +
                TabelaPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " NUMERIC, " +
                TabelaPartidaLotogol.COLUNA_ID_EQUIPE_UM + " NUMERIC, " +
                TabelaPartidaLotogol.COLUNA_ID_EQUIPE_DOIS + " NUMERIC, " +
                TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM + " INTEGER, " +
                TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS + " INTEGER " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de PartidaLotecaDTO
     *
     * @return sb
     */
    public static String criarTabelaLoteca(){

        String sb = (" CREATE TABLE " + TabelaPartidaLoteca.NOME_TABELA) +
                " ( " +
                TabelaPartidaLoteca._ID + " INTEGER PRIMARY KEY, " +
                TabelaPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " NUMERIC, " +
                TabelaPartidaLoteca.COLUNA_ID_EQUIPE_UM + " NUMERIC, " +
                TabelaPartidaLoteca.COLUNA_ID_EQUIPE_DOIS + " NUMERIC, " +
                TabelaPartidaLoteca.COLUNA_EMPATE + " INTEGER " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de ParametroMesDeSorte
     *
     * @return sb
     */
    public static String criarTabelaMesDeSorte(){

        String sb = (" CREATE TABLE " + TabelaMesDeSorte.NOME_TABELA) +
                " ( " +
                TabelaMesDeSorte._ID + " INTEGER PRIMARY KEY, " +
                TabelaMesDeSorte.COLUNA_NUMERO + " NUMERIC, " +
                TabelaMesDeSorte.COLUNA_NOME + " TEXT, " +
                TabelaMesDeSorte.COLUNA_ABREVIACAO + " TEXT " +
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
     * Método para criar tabela de carrinho favorito
     *
     * @return sb
     */
    public static String criarTabelaCarrinhoFavorito() {

        String sb = (" CREATE TABLE " + TabelaCarrinhoFavorito.NOME_TABELA) +
                " ( " +
                TabelaCarrinhoFavorito.COLUNA_ID_CARRINHO + " INTEGER PRIMARY KEY NOT NULL , " +
                TabelaCarrinhoFavorito.COLUNA_NOME_CARRINHO + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela do mercado pago
     *
     * @return sb
     */
    public static String criarTabelaMercadoPago() {

        String sb = (" CREATE TABLE " + TabelaMercadoPago.NOME_TABELA) +
                " ( " +
                TabelaMercadoPago.COLUNA_ID + " INTEGER PRIMARY KEY NOT NULL , " +
                TabelaMercadoPago.COLUNA_PUBLIC_KEY + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de ultima compra
     *
     * @return sb
     */
    public static String criarTabelaUltimaCompra() {

        String sb = (" CREATE TABLE " + TabelaUltimaCompra.NOME_TABELA) +
                " ( " +
                TabelaUltimaCompra.COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                TabelaUltimaCompra.COLUNA_VALOR + " REAL, " +
                TabelaUltimaCompra.COLUNA_DATA + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de usuario
     *
     * @return sb
     */
    public static String criarTabelaUsuario() {

        String sb = (" CREATE TABLE " + TabelaUsuario.NOME_TABELA) +
                " ( " +
                TabelaUsuario.COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                TabelaUsuario.COLUNA_NOME + " TEXT, " +
                TabelaUsuario.COLUNA_CPF + " TEXT " +
                " ) ";
        return sb;
    }

    /**
     * Método para criar tabela de usuario
     *
     * @return sb
     */
    public static String criarTabelaBiometria() {

        String sb = (" CREATE TABLE " + TabelaBiometria.NOME_TABELA) +
                " ( " +
                TabelaBiometria.COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                TabelaBiometria.COLUNA_REFRESH + " TEXT, " +
                TabelaBiometria.COLUNA_NOME + " TEXT, " +
                TabelaBiometria.COLUNA_CPF + " TEXT " +
                " ) ";
        return sb;
    }

}
