package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioDTO;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaUsuario;

public class UsuarioCRUD extends DBLoteriasCRUD {

	public UsuarioCRUD(Context context) {
		super(context);
	}

	/**
	 * Método responsável por inserir o usuario
	 *
	 * @param usuarioDTO UsuarioDTO
	 * @return id
	 */
	public Long inserirUsuario(UsuarioDTO usuarioDTO) {

		if (usuarioDTO != null) {
			this.initDBLoteriasHelper();
			try {
				setDb(getDbLoteriasHelper().getWritableDatabase());
				ContentValues values              = new ContentValues();

				values.put(TabelaUsuario.COLUNA_NOME, usuarioDTO.getNome());
				values.put(TabelaUsuario.COLUNA_CPF, usuarioDTO.getCpf());

				return getDb().insert(TabelaUsuario.NOME_TABELA, null, values);
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
	 * Método responsável por deletar o usuario
	 * @param
	 * @return void
	 */
	public void deletaUsuario(){
		this.initDBLoteriasHelper();
		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			getDb().delete(TabelaUsuario.NOME_TABELA, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}

	/**
	 * Método responsável por ler usuario
	 *
	 * @return UsuarioDTO
	 */
	public UsuarioDTO lerUsuario() {
		UsuarioDTO usuario = null;
		this.initDBLoteriasHelper();
		Cursor cursor = null;

		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			String[] projection = {
					TabelaUsuario.COLUNA_ID,
					TabelaUsuario.COLUNA_NOME,
					TabelaUsuario.COLUNA_CPF
			};
			cursor = getDb().query(
					TabelaUsuario.NOME_TABELA,
					projection,
					null,
					null,
					null,
					null,
					null
							 );

			while (cursor.moveToNext()) {
				Long id = cursor.getLong(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_ID));
				String nome = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_NOME));
				String cpf = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_CPF));

				usuario = new UsuarioDTO(nome, cpf);
				usuario.setId(id);
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

		return usuario;
	}

}
