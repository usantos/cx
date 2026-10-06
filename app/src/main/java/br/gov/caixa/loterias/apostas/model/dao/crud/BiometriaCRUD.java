package br.gov.caixa.loterias.apostas.model.dao.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioDTO;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaBiometria;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaUsuario;

public class BiometriaCRUD extends DBLoteriasCRUD {

	public BiometriaCRUD(Context context) {
		super(context);
	}

	/**
	 * Método responsável por inserir a biometria
	 *
	 * @param biometriaDTO BiometriaDTO
	 * @return id
	 */
	public Long inserirBiometria(BiometriaDTO biometriaDTO) {

		if (biometriaDTO != null) {
			this.initDBLoteriasHelper();
			try {
				setDb(getDbLoteriasHelper().getWritableDatabase());
				ContentValues values = new ContentValues();

				values.put(TabelaBiometria.COLUNA_REFRESH, biometriaDTO.getRefresh());
				values.put(TabelaBiometria.COLUNA_NOME, biometriaDTO.getNome());
				values.put(TabelaBiometria.COLUNA_CPF, biometriaDTO.getCpf());

				return getDb().insert(TabelaBiometria.NOME_TABELA, null, values);
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
	 * Método responsável por deletar a biometria
	 * @param
	 * @return void
	 */
	public void deletaBiometria(){
		this.initDBLoteriasHelper();
		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			getDb().delete(TabelaBiometria.NOME_TABELA, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			this.closeDBLoteriasHelper();
		}
	}

	/**
	 * Método responsável por ler biometria
	 *
	 * @return BiometriaDTO
	 */
	public BiometriaDTO lerBiometria() {
		BiometriaDTO biometria = null;
		this.initDBLoteriasHelper();
		Cursor cursor = null;

		try {
			setDb(getDbLoteriasHelper().getWritableDatabase());
			String[] projection = {
					TabelaBiometria.COLUNA_ID,
					TabelaBiometria.COLUNA_REFRESH,
					TabelaBiometria.COLUNA_NOME,
					TabelaBiometria.COLUNA_CPF
			};
			cursor = getDb().query(
					TabelaBiometria.NOME_TABELA,
					projection,
					null,
					null,
					null,
					null,
					null
							 );

			while (cursor.moveToNext()) {
				Long id = cursor.getLong(cursor.getColumnIndexOrThrow(TabelaBiometria.COLUNA_ID));
				String refresh = cursor.getString(cursor.getColumnIndexOrThrow(TabelaBiometria.COLUNA_REFRESH));
				String nome = cursor.getString(cursor.getColumnIndexOrThrow(TabelaBiometria.COLUNA_NOME));
				String cpf = cursor.getString(cursor.getColumnIndexOrThrow(TabelaBiometria.COLUNA_CPF));

				biometria = new BiometriaDTO(refresh, nome, cpf);
				biometria.setId(id);
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

		return biometria;
	}

}
