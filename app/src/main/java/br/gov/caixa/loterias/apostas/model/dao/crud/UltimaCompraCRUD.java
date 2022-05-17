package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UltimaCompraDTO;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaUltimaCompra;

public class UltimaCompraCRUD extends DBLoteriasCRUD {

	public UltimaCompraCRUD(Context context) {
		super(context);
	}

	/**
	 * Método responsável por inserir a ultima compra
	 *
	 * @param ultimaCompra UltimaCompraDTO
	 * @return id
	 */
	public Long inserirUltimaCompra(UltimaCompraDTO ultimaCompra) {

		if (ultimaCompra != null) {
			this.initDBLoteriasHelper();
			try {
				setDb(getDbLoteriasHelper().getWritableDatabase());
				ContentValues values              = new ContentValues();

				values.put(TabelaUltimaCompra.COLUNA_VALOR, ultimaCompra.getValor().doubleValue());
				values.put(TabelaUltimaCompra.COLUNA_DATA, ultimaCompra.getDataCompra());

				return getDb().insert(TabelaUltimaCompra.NOME_TABELA, null, values);
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

	/**
	 * Método responsável por deletar a ultima compra
	 * @param
	 * @return void
	 */
	public void deletaUltimaCompra(){
		this.initDBLoteriasHelper();
		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			getDb().delete(TabelaUltimaCompra.NOME_TABELA, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}

	/**
	 * Método responsável por ler a ultima compra
	 *
	 * @return UltimaCompraDTO
	 */
	public UltimaCompraDTO lerUltimaCompra() {
		UltimaCompraDTO ultimaCompra = null;

		this.initDBLoteriasHelper();
		Cursor cursor = null;

		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			String[] projection = {
					TabelaUltimaCompra.COLUNA_ID,
					TabelaUltimaCompra.COLUNA_VALOR,
					TabelaUltimaCompra.COLUNA_DATA
			};
			cursor = getDb().query(
					TabelaUltimaCompra.NOME_TABELA,
					projection,
					null,
					null,
					null,
					null,
					null
							 );

			while (cursor.moveToNext()) {
				String data = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUltimaCompra.COLUNA_DATA));
				BigDecimal valor = BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(TabelaUltimaCompra.COLUNA_VALOR)));

				ultimaCompra = new UltimaCompraDTO(valor, data);
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			this.closeDBLoteriasHelper();
			if (cursor != null) {
				cursor.close();
			}
		}

		return ultimaCompra;
	}
}
