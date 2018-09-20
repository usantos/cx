package caixa.gov.br.loterias.model.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joafilho on 06/02/2018.
 * Class Helper DBLoteriasHelper
 */

public class DBLoteriasHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onDelete(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void onDelete(SQLiteDatabase sqLiteDatabase) {
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBLoterica.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBEquipe.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBParametroEquipe.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBPartidaLotogol.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBPartidaLoteca.NOME_TABELA));
        deletaTabela(sqLiteDatabase, DBTabelasLoterias.deleteTabela(DBTabelasLoterias.DBMesDeSorte.NOME_TABELA));
    }

    private void criarTabela(SQLiteDatabase sqLiteDatabase, String tabela) {
        sqLiteDatabase.execSQL(tabela);
    }

    private void deletaTabela(SQLiteDatabase sqLiteDatabase, String query) {
        sqLiteDatabase.execSQL(query);
    }
}
