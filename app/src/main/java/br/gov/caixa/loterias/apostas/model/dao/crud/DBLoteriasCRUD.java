package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasHelper;

abstract public class DBLoteriasCRUD {
	public static final String TAG = "TAG_CRUD_DB";

	private Context context;
	private DBLoteriasHelper dbLoteriasHelper;
	private SQLiteDatabase db;

	public DBLoteriasCRUD(Context context) {
		this.context = context;
	}

	protected void initDBLoteriasHelper() {
		if (this.dbLoteriasHelper == null) {
			this.dbLoteriasHelper = new DBLoteriasHelper(context);
		}
	}

	protected void closeDBLoteriasHelper() {
		if (this.dbLoteriasHelper != null){
			this.dbLoteriasHelper.close();
			this.dbLoteriasHelper = null;
		}

		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	//Getters and Setters
	protected DBLoteriasHelper getDbLoteriasHelper() {
		return dbLoteriasHelper;
	}

	protected SQLiteDatabase getDb() {
		return db;
	}

	protected void setDb(SQLiteDatabase db) {
		this.db = db;
	}
}
