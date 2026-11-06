package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaCarrinhoFavorito;

public class CarrinhoFavoritoCRUD extends DBLoteriasCRUD {

	public CarrinhoFavoritoCRUD(Context context) {
		super(context);
	}

	/**
	 * Método responsável por inserir um carrinho repetido na base
	 *
	 * @param carrinhoFavorito CarrinhoFavoritoDTO
	 * @return id
	 */
	public Long inserirCarrinhoFavorito(CarrinhoFavoritoDTO carrinhoFavorito) {

		if (carrinhoFavorito != null) {
			this.initDBLoteriasHelper();
			try {

				setDb(getDbLoteriasHelper().getWritableDatabase());
				ContentValues values              = new ContentValues();

				values.put(TabelaCarrinhoFavorito.COLUNA_ID_CARRINHO, carrinhoFavorito.getId());
				values.put(TabelaCarrinhoFavorito.COLUNA_NOME_CARRINHO, carrinhoFavorito.getNome());

				return getDb().insert(TabelaCarrinhoFavorito.NOME_TABELA, null, values);
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
	 * Método responsável por ler um CarrinhoFavoritoDTO da base local por ID
	 *
	 * @param id Long
	 * @return CarrinhoFavoritoDTO
	 */
	public Boolean existeCarrinho(Long id) {
		Boolean existe = Boolean.FALSE;
		if (id != null) {
			this.initDBLoteriasHelper();
			Cursor cursor = null;

			try {
				setDb(getDbLoteriasHelper().getWritableDatabase());

				String[] projection = {
						TabelaCarrinhoFavorito.COLUNA_ID_CARRINHO,
						TabelaCarrinhoFavorito.COLUNA_NOME_CARRINHO
				};

				String selection = TabelaCarrinhoFavorito.COLUNA_ID_CARRINHO + " = " + id;

				cursor = getDb().query(
						TabelaCarrinhoFavorito.NOME_TABELA,
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

	/**
	 * Método responsável por deletar um carrinho favorito na base
	 *
	 * @param carrinhoFavorito CarrinhoFavoritoDTO
	 * @return void
	 */
	public void deletaCarrinhoFavorito(CarrinhoFavoritoDTO carrinhoFavorito) {
		try {
			if (carrinhoFavorito != null) {

			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}

	/**
	 * Método responsável por deletar todos os carrinhos favoritos na base
	 *
	 * @param
	 * @return void
	 */
	public void deletaTodos(){
		this.initDBLoteriasHelper();
		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			getDb().delete(TabelaCarrinhoFavorito.NOME_TABELA, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}
}
