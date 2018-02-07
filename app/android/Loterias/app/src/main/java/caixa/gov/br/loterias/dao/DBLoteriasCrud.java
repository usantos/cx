package caixa.gov.br.loterias.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumLong;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;

/**
 * Created by joafilho on 06/02/2018.
 * Class DBLoteriasCrud responsável por realizar CRUD das tabelas
 */

public class DBLoteriasCrud {

    private Context context;
    private DBLoteriasHelper dbLoteriasHelper;
    private SQLiteDatabase db;
    private static final String TAG = "TAG_CRUD_DB";

    public DBLoteriasCrud(Context context) {
        this.context = context;
    }

    private void initDBLoteriasHelper() {
        this.dbLoteriasHelper = new DBLoteriasHelper(context);
    }

    private void closeDBLoteriasHelper() {
        if (this.dbLoteriasHelper != null) {
            this.db.close();
            this.dbLoteriasHelper.close();
        }
    }

    /**
     * Método que insere uma nova LotericaDTO na base
     *
     * @param lotericaDTO LotericaDTO
     * @return Long
     */
    public Long insertLotericaDTO(LotericaDTO lotericaDTO) {

        if (lotericaDTO != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(DBTabelasLoterias.DBLoterica.COLUNA_NOME_FANTASIA, lotericaDTO.getNomeFantasia());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_NOME, lotericaDTO.getNome());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_LOGRADOURO, lotericaDTO.getLogradouro());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_BAIRRO, lotericaDTO.getBairro());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_NOME_MUNICIPIO, lotericaDTO.getNomeMunicipio());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_NOME_UF, lotericaDTO.getNomeUF());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_ID_LOTERICA, lotericaDTO.getId());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_CODIGO, lotericaDTO.getCodigo());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_ID_UF, lotericaDTO.getIdUF());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_ID_MUNICIPIO, lotericaDTO.getIdMunicipio());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_DV_MUNICIPIO, lotericaDTO.getDvMunicipio());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_POLO, lotericaDTO.getPolo());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_DV, lotericaDTO.getDv());
                values.put(DBTabelasLoterias.DBLoterica.COLUNA_NUMERO_FORMATADO, lotericaDTO.getNumeroFormatado());

                if (lotericaDTO.getSituacao() != null) {
                    values.put(DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_VALOR, lotericaDTO.getSituacao().getValor());
                    values.put(DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_DESCRICAO, lotericaDTO.getSituacao().getDescricao());
                }

                return db.insert(DBTabelasLoterias.DBLoterica.NOME_TABELA, null, values);
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
     * Método responsável por ler uma lotericaDTO pelo ID
     *
     * @param id Long
     * @return lotericaDTO
     */
    public LotericaDTO readLotericaDTO(Long id) {

        if (id != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        DBTabelasLoterias.DBLoterica._ID,
                        DBTabelasLoterias.DBLoterica.COLUNA_NOME_FANTASIA,
                        DBTabelasLoterias.DBLoterica.COLUNA_NOME,
                        DBTabelasLoterias.DBLoterica.COLUNA_LOGRADOURO,
                        DBTabelasLoterias.DBLoterica.COLUNA_BAIRRO,
                        DBTabelasLoterias.DBLoterica.COLUNA_NOME_MUNICIPIO,
                        DBTabelasLoterias.DBLoterica.COLUNA_NOME_UF,
                        DBTabelasLoterias.DBLoterica.COLUNA_ID_LOTERICA,
                        DBTabelasLoterias.DBLoterica.COLUNA_CODIGO,
                        DBTabelasLoterias.DBLoterica.COLUNA_ID_UF,
                        DBTabelasLoterias.DBLoterica.COLUNA_ID_MUNICIPIO,
                        DBTabelasLoterias.DBLoterica.COLUNA_DV_MUNICIPIO,
                        DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_VALOR,
                        DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_DESCRICAO,
                        DBTabelasLoterias.DBLoterica.COLUNA_POLO,
                        DBTabelasLoterias.DBLoterica.COLUNA_DV,
                        DBTabelasLoterias.DBLoterica.COLUNA_NUMERO_FORMATADO
                };

                String selection = DBTabelasLoterias.DBLoterica._ID + " = " + id;

                String sortOrder =
                        DBTabelasLoterias.DBLoterica.COLUNA_NOME + " DESC";

                cursor = db.query(
                        DBTabelasLoterias.DBLoterica.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );

                LotericaDTO lotericaDTO = new LotericaDTO();
                while (cursor.moveToNext()) {
                    lotericaDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica._ID)));
                    lotericaDTO.setNomeFantasia(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_NOME_FANTASIA)));
                    lotericaDTO.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.NOME_TABELA)));
                    lotericaDTO.setLogradouro(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_LOGRADOURO)));
                    lotericaDTO.setBairro(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_BAIRRO)));
                    lotericaDTO.setNomeMunicipio(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_NOME_MUNICIPIO)));
                    lotericaDTO.setNomeUF(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_NOME_UF)));
                    lotericaDTO.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_ID_LOTERICA)));
                    lotericaDTO.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_CODIGO)));
                    lotericaDTO.setIdUF(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_ID_UF)));
                    lotericaDTO.setIdMunicipio(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_ID_MUNICIPIO)));

                    DTOEnumLong situacao = new DTOEnumLong();
                    situacao.setValor(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_VALOR)));
                    situacao.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_SITUACAO_DESCRICAO)));
                    lotericaDTO.setSituacao(situacao);

                    lotericaDTO.setPolo(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_POLO)));
                    lotericaDTO.setDv(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_DV)));
                    lotericaDTO.setNumeroFormatado(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBLoterica.COLUNA_NUMERO_FORMATADO)));

                }
                return lotericaDTO;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                this.closeDBLoteriasHelper();
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por inserir uma IdentificaoDeUmaApostaDas8Modalidades (Aposta) na base
     *
     * @param aposta
     * @return
     */
    public Long insertIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades aposta) {

        if (aposta != null) {
            this.initDBLoteriasHelper();
            try {

                String numerosSelecionados = "";
                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                if (aposta.getNumerosSelecionados() != null) {
                    for (Integer numeroSelecionado : aposta.getNumerosSelecionados()) {
                        numerosSelecionados = numerosSelecionados + numeroSelecionado.toString() + "_";
                    }
                }

                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR, aposta.getId());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE, aposta.getModalidade().fromString(aposta.getModalidade()));
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS, numerosSelecionados);
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR, aposta.getValor().doubleValue());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO, aposta.getConcursoAlvo());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS, aposta.getQuantidadeTeimosinhas());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS, aposta.getQuantidadeApostas());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO, aposta.getEspelho());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL, aposta.getConcursoInicial());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA, aposta.getTroca());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO, aposta.getDataEfetivacao());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO, aposta.getHoraEfetivacao());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID, this.insertLotericaDTO(aposta.getLoterica()));
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO, aposta.getVinculoEspelho());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS, aposta.getQuantidadeNumeros());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA, aposta.getSurpresinha());
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA, aposta.getQuantidadeSurpresinhas());

                if (aposta.getTipoConcurso() != null) {

                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR, aposta.getTipoConcurso().getValor());
                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO, aposta.getTipoConcurso().getDescricao());
                }
                if (aposta.getIndicadorSurpresinha() != null) {

                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR, aposta.getIndicadorSurpresinha().getValor());
                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO, aposta.getIndicadorSurpresinha().getDescricao());
                }
                if (aposta.getSituacao() != null) {

                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR, aposta.getSituacao().getValor());
                    values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO, aposta.getSituacao().getDescricao());
                }

                return db.insert(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA, null, values);
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
     * Método responsável por ler todas IdentificaoDeUmaApostaDas8Modalidades (apostas) da base local
     *
     * @return List<IdentificaoDeUmaApostaDas8Modalidades>
     */
    public List<IdentificaoDeUmaApostaDas8Modalidades> readAllIdentificaoDeUmaApostaDas8Modalidades() {

        this.initDBLoteriasHelper();
        Cursor cursor = null;

        try {
            this.db = dbLoteriasHelper.getReadableDatabase();

            String[] projection = {
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades._ID,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CARRINHO_ID,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA
            };

            cursor = db.query(
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
            while (cursor.moveToNext()) {

                IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();

                aposta.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades._ID)));
                aposta.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR)));
                aposta.setModalidade(ModalidadeEnum.toString(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE))));

                DTOEnumCharacter dtoEnumCharacter = new DTOEnumCharacter();
                dtoEnumCharacter.setValor(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR)));
                dtoEnumCharacter.setValor(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO)));
                aposta.setTipoConcurso(dtoEnumCharacter);

                List<Integer> numerosSelecionados = new ArrayList<>();
                String numerosSelecionadosString = cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS));

                if (StringUtils.isNotEmpty(numerosSelecionadosString)) {
                    String[] numerosSelecionadosList = numerosSelecionadosString.split("_");

                    for (int i = 0; i < numerosSelecionadosList.length; i++) {
                        numerosSelecionados.add(Integer.parseInt(numerosSelecionadosList[i]));
                    }
                }
                aposta.setNumerosSelecionados(numerosSelecionados);

                DTOEnumInteger dtoEnumInteger = new DTOEnumInteger();
                dtoEnumInteger.setValor(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR)));
                dtoEnumInteger.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO)));
                aposta.setIndicadorSurpresinha(dtoEnumInteger);

                aposta.setValor(BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR))));
                aposta.setConcursoAlvo(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO)));
                aposta.setQuantidadeTeimosinhas(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS)));
                aposta.setQuantidadeApostas(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS)));
                aposta.setEspelho(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setConcursoInicial(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL)));

                DTOEnumLong dtoEnumLong = new DTOEnumLong();
                dtoEnumLong.setValor(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR)));
                dtoEnumLong.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO)));
                aposta.setSituacao(dtoEnumLong);

                aposta.setTroca(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setDataEfetivacao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO)));
                aposta.setHoraEfetivacao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO)));
                aposta.setLoterica(this.readLotericaDTO(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID))));
                aposta.setVinculoEspelho(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO)));
                aposta.setQuantidadeNumeros(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS)));
                aposta.setSurpresinha(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setQuantidadeSurpresinhas(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA)));

                apostas.add(aposta);
            }
            return apostas;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            this.closeDBLoteriasHelper();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void deleteLoterica(LotericaDTO lotericaDTO) {
        if (lotericaDTO != null) {
            deleteItem(lotericaDTO.getIdDB(), DBTabelasLoterias.DBLoterica.NOME_TABELA);
        }
    }

    public void deleteIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        if (aposta != null) {
            deleteLoterica(aposta.getLoterica());
            deleteItem(aposta.getIdDB(), DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
        }
    }

    private void deleteItem(Long id, String table) {
        this.initDBLoteriasHelper();
        try {
            this.db = dbLoteriasHelper.getWritableDatabase();
            String selection = DBTabelasLoterias.DBLoterica._ID + "=" + id;
            db.delete(table, selection, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.closeDBLoteriasHelper();
        }
    }
}
