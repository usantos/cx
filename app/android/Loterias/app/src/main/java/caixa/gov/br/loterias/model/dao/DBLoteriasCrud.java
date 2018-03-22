package caixa.gov.br.loterias.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumLong;
import caixa.gov.br.loterias.model.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.LotericaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;

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
        if (this.dbLoteriasHelper == null) {
            this.dbLoteriasHelper = new DBLoteriasHelper(context);
        }
    }

    private void closeDBLoteriasHelper() {
        if (this.dbLoteriasHelper != null) {
            this.db.close();
            this.dbLoteriasHelper.close();

            this.db = null;
            this.dbLoteriasHelper = null;
        }
    }

    /**
     * Método que insere uma nova LotericaDTO na base
     *
     * @param lotericaDTO LotericaDTO
     * @return id
     */
    private Long insertLotericaDTO(LotericaDTO lotericaDTO) {

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
    private LotericaDTO readLotericaDTO(Long id) {

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
     * @param aposta IdentificaoDeUmaApostaDas8Modalidades
     * @return id
     */
    public Long insertIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades aposta) {

        if (aposta != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                String numerosSelecionados = "";

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
                values.put(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO, this.insertParametroEquipe(aposta.getTimeDoCoracao()));

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
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA,
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO
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
                dtoEnumCharacter.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO)));
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
                aposta.setTimeDoCoracao(this.readParametroEquipe(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO))));
                aposta.setPartidasLotogol(this.readPartidaLotogolDTO(aposta.getIdDB()));
                aposta.setPartidasLoteca(this.readPartidaLotecaDTO(aposta.getIdDB()));

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

    /**
     * Método responsável por pesquisar IdentificaoDeUmaApostaDas8Modalidades de acordo com dados solicitados
     *
     * @param aposta IdentificaoDeUmaApostaDas8Modalidades
     * @return boolean retorno
     */
    public boolean existIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades aposta) {

        boolean retorno = Boolean.FALSE;

        this.initDBLoteriasHelper();
        Cursor cursor = null;

        try {
            this.db = dbLoteriasHelper.getReadableDatabase();
            String numerosSelecionados = "";

            if (aposta.getNumerosSelecionados() != null) {
                for (Integer numeroSelecionado : aposta.getNumerosSelecionados()) {
                    numerosSelecionados = numerosSelecionados + numeroSelecionado.toString() + "_";
                }
            }

            String[] projection = {
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades._ID
            };

            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder.append(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " = ? AND ");
            selectionBuilder.append(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " = ? AND ");
            selectionBuilder.append(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " = ? AND ");
            selectionBuilder.append(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " = ? ");

            String[] selectionArgs = {
                    ModalidadeEnum.fromString(aposta.getModalidade()),
                    aposta.getConcursoInicial().toString(),
                    aposta.getConcursoAlvo().toString(),
                    numerosSelecionados
            };


            cursor = db.query(
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
                    projection,
                    selectionBuilder.toString(),
                    selectionArgs,
                    null,
                    null,
                    null
            );

            retorno = (cursor.getCount() > 0);

            return retorno;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return retorno;
        } finally {
            this.closeDBLoteriasHelper();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Método responsável por obter quantidade de registros na base local
     *
     * @return Integer qtd
     */
    public Integer qtdIdentificaoDeUmaApostaDas8Modalidades() {

        this.initDBLoteriasHelper();
        Cursor cursor = null;
        Integer qtd = 0;

        try {
            this.db = dbLoteriasHelper.getReadableDatabase();

            String[] projection = {
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades._ID
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

            qtd = cursor.getCount();

            return qtd;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return qtd;
        } finally {
            this.closeDBLoteriasHelper();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Método responsável por retornar valor total do carrinho
     *
     * @return BigDecimal value
     */
    public BigDecimal sumValorIdentificaoDeUmaApostaDas8Modalidades() {

        this.initDBLoteriasHelper();
        Cursor cursor = null;
        BigDecimal value = BigDecimal.ZERO;

        try {
            this.db = dbLoteriasHelper.getReadableDatabase();

            String[] projection = {
                    DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR
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

            while (cursor.moveToNext()) {
                value = value.add((BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR)))));
            }

            return value;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return value;
        } finally {
            this.closeDBLoteriasHelper();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Método responsável por inserir uma EquipeDTO na base
     *
     * @param equipe EquipeDTO
     * @return id
     */
    private Long insertEquipe(EquipeDTO equipe) {

        if (equipe != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(DBTabelasLoterias.DBEquipe.COLUNA_PLACAR, equipe.getPlacar());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_VITORIA, equipe.getVitoria());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_ID_PARAMETRO_EQUIPE, this.insertParametroEquipe(equipe.getParametroEquipe()));
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_NOME, equipe.getNome());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_NUMERO, equipe.getNumero());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_NUMERO_PAIS, equipe.getNumeroPais());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_PAIS, equipe.getPais());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_SIGLA_PAIS, equipe.getSiglaPais());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_UF, equipe.getUf());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_NOME_CLASS, equipe.getNomeClass());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_INDICADOR_SELECAO, equipe.getIndicadorSelecao());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_CURTA, equipe.getDescricaoCurta());
                values.put(DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_LONGA, equipe.getDescricaoLonga());

                return db.insert(DBTabelasLoterias.DBEquipe.NOME_TABELA, null, values);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por ler EquipeDTO da base local por ID
     *
     * @param id Long
     * @return EquipeDTO
     */
    private EquipeDTO readEquipeDTO(Long id) {

        if (id != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        DBTabelasLoterias.DBEquipe._ID,
                        DBTabelasLoterias.DBEquipe.COLUNA_PLACAR,
                        DBTabelasLoterias.DBEquipe.COLUNA_VITORIA,
                        DBTabelasLoterias.DBEquipe.COLUNA_ID_PARAMETRO_EQUIPE,
                        DBTabelasLoterias.DBEquipe.COLUNA_NOME,
                        DBTabelasLoterias.DBEquipe.COLUNA_NUMERO,
                        DBTabelasLoterias.DBEquipe.COLUNA_NUMERO_PAIS,
                        DBTabelasLoterias.DBEquipe.COLUNA_PAIS,
                        DBTabelasLoterias.DBEquipe.COLUNA_SIGLA_PAIS,
                        DBTabelasLoterias.DBEquipe.COLUNA_UF,
                        DBTabelasLoterias.DBEquipe.COLUNA_NOME_CLASS,
                        DBTabelasLoterias.DBEquipe.COLUNA_INDICADOR_SELECAO,
                        DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_CURTA,
                        DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_LONGA
                };

                String selection = DBTabelasLoterias.DBEquipe._ID + " = " + id;

                cursor = db.query(
                        DBTabelasLoterias.DBEquipe.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                EquipeDTO equipeDTO = new EquipeDTO();
                while (cursor.moveToNext()) {
                    equipeDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe._ID)));
                    equipeDTO.setPlacar(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_PLACAR)));
                    equipeDTO.setVitoria(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_VITORIA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    equipeDTO.setParametroEquipe(this.readParametroEquipe(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_ID_PARAMETRO_EQUIPE))));
                    equipeDTO.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_NOME)));
                    equipeDTO.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_NUMERO)));
                    equipeDTO.setNumeroPais(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_NUMERO_PAIS)));
                    equipeDTO.setPais(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_PAIS)));
                    equipeDTO.setSiglaPais(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_SIGLA_PAIS)));
                    equipeDTO.setUf(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_UF)));
                    equipeDTO.setNomeClass(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_NOME_CLASS)));
                    equipeDTO.setIndicadorSelecao(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_INDICADOR_SELECAO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    equipeDTO.setDescricaoCurta(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_CURTA)));
                    equipeDTO.setDescricaoLonga(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBEquipe.COLUNA_DESCRICAO_LONGA)));
                }

                return equipeDTO;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por inserir uma ParametroEquipe na base
     *
     * @param parametroEquipe ParametroEquipe
     * @return id
     */
    private Long insertParametroEquipe(ParametroEquipe parametroEquipe) {

        if (parametroEquipe != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_INDICADOR_SELECAO, parametroEquipe.getIndicadorSelecao());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME, parametroEquipe.getNome());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO, parametroEquipe.getNumero());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO_PAIS, parametroEquipe.getNumeroPais());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_CURTA, parametroEquipe.getDescricaoCurta());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_LONGA, parametroEquipe.getDescricaoLonga());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_PAIS, parametroEquipe.getPais());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_SIGLA_PAIS, parametroEquipe.getSiglaPais());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_UF, parametroEquipe.getUf());
                values.put(DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME_CLASS, parametroEquipe.getNomeClass());

                return db.insert(DBTabelasLoterias.DBParametroEquipe.NOME_TABELA, null, values);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por ler ParametroEquipe da base local  por ID
     *
     * @param id Long
     * @return ParametroEquipe
     */
    private ParametroEquipe readParametroEquipe(Long id) {

        if (id != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        DBTabelasLoterias.DBParametroEquipe._ID,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_INDICADOR_SELECAO,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO_PAIS,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_CURTA,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_LONGA,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_PAIS,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_SIGLA_PAIS,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_UF,
                        DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME_CLASS
                };

                String selection = DBTabelasLoterias.DBParametroEquipe._ID + " = " + id;

                cursor = db.query(
                        DBTabelasLoterias.DBParametroEquipe.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                ParametroEquipe parametroEquipe = new ParametroEquipe();
                while (cursor.moveToNext()) {
                    parametroEquipe.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe._ID)));
                    parametroEquipe.setIndicadorSelecao(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_INDICADOR_SELECAO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    parametroEquipe.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME)));
                    parametroEquipe.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO)));
                    parametroEquipe.setNumeroPais(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_NUMERO_PAIS)));
                    parametroEquipe.setDescricaoCurta(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_CURTA)));
                    parametroEquipe.setDescricaoLonga(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_DESCRICAO_LONGA)));
                    parametroEquipe.setPais(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_PAIS)));
                    parametroEquipe.setSiglaPais(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_SIGLA_PAIS)));
                    parametroEquipe.setUf(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_UF)));
                    parametroEquipe.setNomeClass(cursor.getString(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBParametroEquipe.COLUNA_NOME_CLASS)));
                }

                return parametroEquipe;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por inserir uma PartidaLotogolDTO na base
     *
     * @param partidaLotogolDTO PartidaLotogolDTO
     * @param idDbAposta        Long
     * @return id
     */
    public Long inserirPartidaLotogol(PartidaLotogolDTO partidaLotogolDTO, Long idDbAposta) {

        if (partidaLotogolDTO != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES, idDbAposta);
                values.put(DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_UM, this.insertEquipe(partidaLotogolDTO.getEquipe1()));
                values.put(DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_DOIS, this.insertEquipe(partidaLotogolDTO.getEquipe2()));
                values.put(DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM, partidaLotogolDTO.getIndexPlacarEquipe1());
                values.put(DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS, partidaLotogolDTO.getIndexPlacarEquipe2());

                return db.insert(DBTabelasLoterias.DBPartidaLotogol.NOME_TABELA, null, values);
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
     * Método responsável por ler todas PartidaLotogolDTO da base local  por ID da aposta
     *
     * @param idDbAposta Long
     * @return List<PartidaLotogolDTO>
     */
    private List<PartidaLotogolDTO> readPartidaLotogolDTO(Long idDbAposta) {

        if (idDbAposta != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        DBTabelasLoterias.DBPartidaLotogol._ID,
                        DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_UM,
                        DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_DOIS,
                        DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM,
                        DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS
                };

                String selection = DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " = " + idDbAposta;

                cursor = db.query(
                        DBTabelasLoterias.DBPartidaLotogol.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                List<PartidaLotogolDTO> partidaLotogolDTOList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PartidaLotogolDTO partidaLotogolDTO = new PartidaLotogolDTO();
                    partidaLotogolDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLotogol._ID)));
                    partidaLotogolDTO.setEquipe1(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_UM))));
                    partidaLotogolDTO.setEquipe2(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLotogol.COLUNA_ID_EQUIPE_DOIS))));
                    partidaLotogolDTO.setIndexPlacarEquipe1(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM)));
                    partidaLotogolDTO.setIndexPlacarEquipe2(cursor.getInt(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS)));

                    partidaLotogolDTOList.add(partidaLotogolDTO);
                }

                if (partidaLotogolDTOList.isEmpty()) {
                    return null;
                } else {
                    return partidaLotogolDTOList;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por inserir uma PartidaLotecaDTO na base
     *
     * @param partidaLotecaDTO PartidaLotecaDTO
     * @param idDbAposta       Long
     * @return id
     */
    public Long inserirPartidaLoteca(PartidaLotecaDTO partidaLotecaDTO, Long idDbAposta) {

        if (partidaLotecaDTO != null) {
            this.initDBLoteriasHelper();
            try {

                this.db = dbLoteriasHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES, idDbAposta);
                values.put(DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_UM, this.insertEquipe(partidaLotecaDTO.getEquipe1()));
                values.put(DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_DOIS, this.insertEquipe(partidaLotecaDTO.getEquipe2()));
                values.put(DBTabelasLoterias.DBPartidaLoteca.COLUNA_EMPATE, partidaLotecaDTO.getEmpate());

                return db.insert(DBTabelasLoterias.DBPartidaLoteca.NOME_TABELA, null, values);
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
     * Método responsável por ler todas PartidaLotecaDTO da base local  por ID da aposta
     *
     * @param idDbAposta Long
     * @return List<PartidaLotecaDTO>
     */
    private List<PartidaLotecaDTO> readPartidaLotecaDTO(Long idDbAposta) {

        if (idDbAposta != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        DBTabelasLoterias.DBPartidaLoteca._ID,
                        DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_UM,
                        DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_DOIS,
                        DBTabelasLoterias.DBPartidaLoteca.COLUNA_EMPATE
                };

                String selection = DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " = " + idDbAposta;

                cursor = db.query(
                        DBTabelasLoterias.DBPartidaLoteca.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                List<PartidaLotecaDTO> partidaLotogolDTOList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PartidaLotecaDTO partidaLotecaDTO = new PartidaLotecaDTO();
                    partidaLotecaDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLoteca._ID)));
                    partidaLotecaDTO.setEquipe1(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_UM))));
                    partidaLotecaDTO.setEquipe2(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLoteca.COLUNA_ID_EQUIPE_DOIS))));
                    partidaLotecaDTO.setEmpate(cursor.getLong(cursor.getColumnIndexOrThrow(DBTabelasLoterias.DBPartidaLoteca.COLUNA_EMPATE)) == 1 ? Boolean.TRUE : Boolean.FALSE);

                    partidaLotogolDTOList.add(partidaLotecaDTO);
                }

                if (partidaLotogolDTOList.isEmpty()) {
                    return null;
                } else {
                    return partidaLotogolDTOList;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return null;
        }
    }

    private void deleteLoterica(LotericaDTO lotericaDTO) {
        if (lotericaDTO != null) {
            this.deleteItem(lotericaDTO.getIdDB(), DBTabelasLoterias.DBLoterica.NOME_TABELA);
        }
    }

    public void deleteIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        try {
            if (aposta != null) {
                this.deleteLoterica(aposta.getLoterica());
                this.deleteParametroEquipe(aposta.getTimeDoCoracao());

                if (aposta.getPartidasLotogol() != null) {
                    for (PartidaLotogolDTO partidaLotogolDTO : aposta.getPartidasLotogol()) {
                        this.deletePartidaLotogol(partidaLotogolDTO);
                    }
                }
                if (aposta.getPartidasLoteca() != null) {
                    for (PartidaLotecaDTO partidaLotecaDTO : aposta.getPartidasLoteca()) {
                        this.deletePartidaLoteca(partidaLotecaDTO);
                    }
                }
                this.deleteItem(aposta.getIdDB(), DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.closeDBLoteriasHelper();
        }
    }

    private void deleteEquipe(EquipeDTO equipeDTO) {
        if (equipeDTO != null) {
            this.deleteParametroEquipe(equipeDTO.getParametroEquipe());
            this.deleteItem(equipeDTO.getIdDB(), DBTabelasLoterias.DBEquipe.NOME_TABELA);
        }
    }

    private void deleteParametroEquipe(ParametroEquipe parametroEquipe) {
        if (parametroEquipe != null) {
            this.deleteItem(parametroEquipe.getIdDB(), DBTabelasLoterias.DBParametroEquipe.NOME_TABELA);
        }
    }

    private void deletePartidaLotogol(PartidaLotogolDTO partidaLotogolDTO) {
        if (partidaLotogolDTO != null) {
            this.deleteEquipe(partidaLotogolDTO.getEquipe1());
            this.deleteEquipe(partidaLotogolDTO.getEquipe2());
            this.deleteItem(partidaLotogolDTO.getIdDB(), DBTabelasLoterias.DBPartidaLotogol.NOME_TABELA);
        }
    }

    private void deletePartidaLoteca(PartidaLotecaDTO partidaLotecaDTO) {
        if (partidaLotecaDTO != null) {
            this.deleteEquipe(partidaLotecaDTO.getEquipe1());
            this.deleteEquipe(partidaLotecaDTO.getEquipe2());
            this.deleteItem(partidaLotecaDTO.getIdDB(), DBTabelasLoterias.DBPartidaLoteca.NOME_TABELA);
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
        }
    }

    public void deleteAll() {
        this.initDBLoteriasHelper();
        try {
            this.db = dbLoteriasHelper.getWritableDatabase();
            db.delete(DBTabelasLoterias.DBLoterica.NOME_TABELA, null, null);
            db.delete(DBTabelasLoterias.DBIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA, null, null);
            db.delete(DBTabelasLoterias.DBEquipe.NOME_TABELA, null, null);
            db.delete(DBTabelasLoterias.DBParametroEquipe.NOME_TABELA, null, null);
            db.delete(DBTabelasLoterias.DBPartidaLotogol.NOME_TABELA, null, null);
            db.delete(DBTabelasLoterias.DBPartidaLoteca.NOME_TABELA, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.closeDBLoteriasHelper();
        }
    }
}
