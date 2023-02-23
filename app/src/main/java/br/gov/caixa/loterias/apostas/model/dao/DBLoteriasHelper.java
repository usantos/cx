package br.gov.caixa.loterias.apostas.model.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * Created by joafilho on 06/02/2018.
 * Class Helper DBLoteriasHelper
 */

public class DBLoteriasHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Loterias.db";

    public DBLoteriasHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBLoteriasHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTebelaLoteria());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaIdentificaoDeUmaApostaDas8Modalidades());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaEquipe());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaParametroEquipe());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaLotogol());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaLoteca());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaMesDeSorte());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaCarrinhoFavorito());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaMercadoPago());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaUltimaCompra());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaUsuario());
        criarTabela(sqLiteDatabase, DBTabelasLoterias.criarTabelaBiometria());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onDelete(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void onDelete(SQLiteDatabase sqLiteDatabase) {
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaLoterica.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaEquipe.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaParametroEquipe.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaPartidaLotogol.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaPartidaLoteca.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(TabelaMesDeSorte.NOME_TABELA));
        deletaTabela(sqLiteDatabase,DBTabelasLoterias.deleteTabela(TabelaCarrinhoFavorito.NOME_TABELA));
        deletaTabela(sqLiteDatabase,DBTabelasLoterias.deleteTabela(TabelaMercadoPago.NOME_TABELA));
        deletaTabela(sqLiteDatabase,DBTabelasLoterias.deleteTabela(TabelaUltimaCompra.NOME_TABELA));
        deletaTabela(sqLiteDatabase,DBTabelasLoterias.deleteTabela(TabelaUsuario.NOME_TABELA));
        deletaTabela(sqLiteDatabase,DBTabelasLoterias.deleteTabela(TabelaBiometria.NOME_TABELA));
    }

    private void criarTabela(SQLiteDatabase sqLiteDatabase, String tabela) {
        sqLiteDatabase.execSQL(tabela);
    }

    private void deletaTabela(SQLiteDatabase sqLiteDatabase, String query) {
        sqLiteDatabase.execSQL(query);
    }
}
