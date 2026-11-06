package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaMercadoPago;

public class MercadoPagoCRUD extends DBLoteriasCRUD {

	public MercadoPagoCRUD(Context context) {
		super(context);
	}

	public Long inserir(Long id,  String publicKey) {

		if (id != null && publicKey != null) {
			this.initDBLoteriasHelper();
			try {

				setDb(getDbLoteriasHelper().getWritableDatabase());
				ContentValues values              = new ContentValues();

				values.put(TabelaMercadoPago.COLUNA_ID, id);
				values.put(TabelaMercadoPago.COLUNA_PUBLIC_KEY, publicKey);

				return getDb().insert(TabelaMercadoPago.NOME_TABELA, null, values);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return null;
			} finally {
				this.closeDBLoteriasHelper();
			}
		} else {
			return null;
		}
	}

	public String getPublicKeyMercadoPago(){
		this.initDBLoteriasHelper();
		Cursor cursor = null;
		String publicKey = "";

		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());

			String[] projection = {
					TabelaMercadoPago.COLUNA_ID,
					TabelaMercadoPago.COLUNA_PUBLIC_KEY
			};

			String selection = TabelaMercadoPago.COLUNA_ID + " = " + new Long(1);

			cursor = getDb().query(
					TabelaMercadoPago.NOME_TABELA,
					projection,
					selection,
					null,
					null,
					null,
					null
								  );

			while (cursor.moveToNext()) {
				publicKey = cursor.getString(cursor.getColumnIndexOrThrow(TabelaMercadoPago.COLUNA_PUBLIC_KEY));
			}

			return publicKey;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return publicKey;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Boolean existe(Long id) {
		Boolean existe = Boolean.FALSE;
		if (id != null) {
			this.initDBLoteriasHelper();
			Cursor cursor = null;

			try {
				setDb(getDbLoteriasHelper().getWritableDatabase());

				String[] projection = {
						TabelaMercadoPago.COLUNA_ID,
						TabelaMercadoPago.COLUNA_PUBLIC_KEY
				};

				String selection = TabelaMercadoPago.COLUNA_ID + " = " + id;

				cursor = getDb().query(
						TabelaMercadoPago.NOME_TABELA,
						projection,
						selection,
						null,
						null,
						null,
						null
								 );

				while (cursor.moveToNext()) {
					existe = Boolean.TRUE;

				}

				return existe;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return existe;
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else {
			return existe;
		}
	}

	public Long atualiza(Long id, String publicKey){
		if (existe(id)){
			deletaTodos();
		}

		return inserir(id, publicKey);
	}

	public void deletaTodos(){
		this.initDBLoteriasHelper();
		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			getDb().delete(TabelaMercadoPago.NOME_TABELA, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}
}
