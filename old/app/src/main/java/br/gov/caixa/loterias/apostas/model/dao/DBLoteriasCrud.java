package br.gov.caixa.loterias.apostas.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaCarrinhoFavorito;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaEquipe;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaIdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaLoterica;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaMesDeSorte;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaParametroEquipe;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaPartidaLoteca;
import br.gov.caixa.loterias.apostas.model.dao.tabela.TabelaPartidaLotogol;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppUtils;

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
            if(this.db != null){
                this.db.close();
            }
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

                values.put(TabelaLoterica.COLUNA_NOME_FANTASIA, lotericaDTO.getNomeFantasia());
                values.put(TabelaLoterica.COLUNA_NOME, lotericaDTO.getNome());
                values.put(TabelaLoterica.COLUNA_LOGRADOURO, lotericaDTO.getLogradouro());
                values.put(TabelaLoterica.COLUNA_BAIRRO, lotericaDTO.getBairro());
                values.put(TabelaLoterica.COLUNA_NOME_MUNICIPIO, lotericaDTO.getNomeMunicipio());
                values.put(TabelaLoterica.COLUNA_NOME_UF, lotericaDTO.getNomeUF());
                values.put(TabelaLoterica.COLUNA_ID_LOTERICA, lotericaDTO.getId());
                values.put(TabelaLoterica.COLUNA_CODIGO, lotericaDTO.getCodigo());
                values.put(TabelaLoterica.COLUNA_ID_UF, lotericaDTO.getIdUF());
                values.put(TabelaLoterica.COLUNA_ID_MUNICIPIO, lotericaDTO.getIdMunicipio());
                values.put(TabelaLoterica.COLUNA_DV_MUNICIPIO, lotericaDTO.getDvMunicipio());
                values.put(TabelaLoterica.COLUNA_POLO, lotericaDTO.getPolo());
                values.put(TabelaLoterica.COLUNA_DV, lotericaDTO.getDv());
                values.put(TabelaLoterica.COLUNA_NUMERO_FORMATADO, lotericaDTO.getNumeroFormatado());

                if (lotericaDTO.getSituacao() != null) {
                    values.put(TabelaLoterica.COLUNA_SITUACAO_VALOR, lotericaDTO.getSituacao().getValor());
                    values.put(TabelaLoterica.COLUNA_SITUACAO_DESCRICAO, lotericaDTO.getSituacao().getDescricao());
                }

                return db.insert(TabelaLoterica.NOME_TABELA, null, values);
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
                        TabelaLoterica._ID,
                        TabelaLoterica.COLUNA_NOME_FANTASIA,
                        TabelaLoterica.COLUNA_NOME,
                        TabelaLoterica.COLUNA_LOGRADOURO,
                        TabelaLoterica.COLUNA_BAIRRO,
                        TabelaLoterica.COLUNA_NOME_MUNICIPIO,
                        TabelaLoterica.COLUNA_NOME_UF,
                        TabelaLoterica.COLUNA_ID_LOTERICA,
                        TabelaLoterica.COLUNA_CODIGO,
                        TabelaLoterica.COLUNA_ID_UF,
                        TabelaLoterica.COLUNA_ID_MUNICIPIO,
                        TabelaLoterica.COLUNA_DV_MUNICIPIO,
                        TabelaLoterica.COLUNA_SITUACAO_VALOR,
                        TabelaLoterica.COLUNA_SITUACAO_DESCRICAO,
                        TabelaLoterica.COLUNA_POLO,
                        TabelaLoterica.COLUNA_DV,
                        TabelaLoterica.COLUNA_NUMERO_FORMATADO
                };

                String selection = TabelaLoterica._ID + " = " + id;

                String sortOrder =
                        TabelaLoterica.COLUNA_NOME + " DESC";

                cursor = db.query(
                        TabelaLoterica.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );

                LotericaDTO lotericaDTO = new LotericaDTO();
                while (cursor.moveToNext()) {
                    lotericaDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica._ID)));
                    lotericaDTO.setNomeFantasia(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_NOME_FANTASIA)));
                    lotericaDTO.setNome(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.NOME_TABELA)));
                    lotericaDTO.setLogradouro(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_LOGRADOURO)));
                    lotericaDTO.setBairro(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_BAIRRO)));
                    lotericaDTO.setNomeMunicipio(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_NOME_MUNICIPIO)));
                    lotericaDTO.setNomeUF(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_NOME_UF)));
                    lotericaDTO.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_ID_LOTERICA)));
                    lotericaDTO.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_CODIGO)));
                    lotericaDTO.setIdUF(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_ID_UF)));
                    lotericaDTO.setIdMunicipio(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_ID_MUNICIPIO)));

                    DTOEnumLong situacao = new DTOEnumLong();
                    situacao.setValor(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_SITUACAO_VALOR)));
                    situacao.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_SITUACAO_DESCRICAO)));
                    lotericaDTO.setSituacao(situacao);

                    lotericaDTO.setPolo(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_POLO)));
                    lotericaDTO.setDv(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_DV)));
                    lotericaDTO.setNumeroFormatado(cursor.getString(cursor.getColumnIndexOrThrow(TabelaLoterica.COLUNA_NUMERO_FORMATADO)));

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
                    if(aposta.getModalidade().equals(ModalidadeEnum.SUPER_7)){
                        numerosSelecionados = new Gson().toJson(aposta.getMatrizNumerosSelecionados());
                    } else {
                        numerosSelecionados = new Gson().toJson(aposta.getListaNumerosSelecionados());
                    }
                }

                String trevosSelecionados = "";
                if (aposta.getTrevosSelecionados() != null){
                    trevosSelecionados = new Gson()
                            .toJson(AppUtils.converteListaInteiros((List<Integer>) aposta.getTrevosSelecionados()));
                }

                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR, aposta.getId());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE, aposta.getModalidade().fromString(aposta.getModalidade()));
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS, numerosSelecionados);
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TREVOS_SELECIONADOS, trevosSelecionados);
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR, aposta.getValor().doubleValue());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO, aposta.getConcursoAlvo());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS, aposta.getQuantidadeTeimosinhas());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS, aposta.getQuantidadeApostas());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO, aposta.getEspelho());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_GERAR_ESPELHO,aposta.getGerarApostaEspelho());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL, aposta.getConcursoInicial());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA, aposta.getTroca());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO, aposta.getDataEfetivacao());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO, aposta.getHoraEfetivacao());
                //values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID, this.insertLotericaDTO(aposta.getLoterica()));
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO, aposta.getVinculoEspelho());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS, aposta.getQuantidadeNumeros());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TREVOS, aposta.getQuantidadeTrevos());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA, aposta.getSurpresinha());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA, aposta.getQuantidadeSurpresinhas());
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO, this.insertParametroEquipe(aposta.getTimeDoCoracao()));
                values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_MES_SORTE, this.insertParametroMesDeSorte(aposta.getMesDeSorte()));

                if (aposta.getTipoConcurso() != null) {
                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR, aposta.getTipoConcurso().getValor());
                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO, aposta.getTipoConcurso().getDescricao());
                }
                if (aposta.getIndicadorSurpresinha() != null) {

                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR, aposta.getIndicadorSurpresinha().getValor());
                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO, aposta.getIndicadorSurpresinha().getDescricao());
                }
                if (aposta.getSituacao() != null) {

                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR, aposta.getSituacao().getValor());
                    values.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO, ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
                }

                return db.insert(TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA, null, values);
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
                    TabelaIdentificaoDeUmaApostaDas8Modalidades._ID,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TREVOS_SELECIONADOS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_GERAR_ESPELHO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TREVOS,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO,
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_MES_SORTE
            };
            cursor = db.query(
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
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

                aposta.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades._ID)));
                aposta.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_IDENTIFICADOR)));
                aposta.setModalidade(ModalidadeEnum.toString(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE))));

                DTOEnumCharacter dtoEnumCharacter = new DTOEnumCharacter();
                dtoEnumCharacter.setValor(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_VALOR)));
                dtoEnumCharacter.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TIPO_CONCURSO_DESCRICAO)));
                aposta.setTipoConcurso(dtoEnumCharacter);

                String numerosSelecionadosString = cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS));
                // Numeros Selecionados
                if (StringUtils.isNotEmpty(numerosSelecionadosString)) {
                    Gson gson = new Gson();
                    if (aposta.getModalidade().equals(ModalidadeEnum.SUPER_7)) {
                        TypeToken<List<List<Integer>>> token = new TypeToken<List<List<Integer>>>() {};
                        List<List<Integer>> matrizInteiros = gson.fromJson(numerosSelecionadosString, token.getType());
                        aposta.setNumerosSelecionados(matrizInteiros);
                    } else {
                        TypeToken<List<Integer>> token = new TypeToken<List<Integer>>() {};
                        List<Integer> listaInteiros = gson.fromJson(numerosSelecionadosString, token.getType());
                        aposta.setNumerosSelecionados(listaInteiros);
                    }
                }

                String trevosSelecionadosString = cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TREVOS_SELECIONADOS));
                if (StringUtils.isNotEmpty(trevosSelecionadosString)) {
                    TypeToken<List<Integer>> token = new TypeToken<List<Integer>>() {};
                    List<Integer> listaInteiros = new Gson().fromJson(trevosSelecionadosString, token.getType());
                    aposta.setTrevosSelecionados(listaInteiros);
                }


                DTOEnumInteger dtoEnumInteger = new DTOEnumInteger();
                dtoEnumInteger.setValor(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_VALOR)));
                dtoEnumInteger.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_INDICADOR_SURPRESINHA_DESCRICAO)));
                aposta.setIndicadorSurpresinha(dtoEnumInteger);

                aposta.setValor(BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR))));
                aposta.setConcursoAlvo(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO)));
                aposta.setQuantidadeTeimosinhas(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TEIMOSINHAS)));
                aposta.setQuantidadeApostas(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_APOSTAS)));
                aposta.setEspelho(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setConcursoInicial(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL)));

                DTOEnumLong dtoEnumLong = new DTOEnumLong();
                dtoEnumLong.setValor(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_VALOR)));
                dtoEnumLong.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SITUACAO_DESCRICAO)));
                aposta.setSituacao(dtoEnumLong);

                aposta.setTroca(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_TROCA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setDataEfetivacao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_DATA_EFETIVACAO)));
                aposta.setHoraEfetivacao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_HORA_EFETIVACAO)));
                aposta.setGerarEspelho(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_GERAR_ESPELHO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
               //aposta.setLoterica(this.readLotericaDTO(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_LOTERICA_ID))));
                aposta.setVinculoEspelho(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO)));
                aposta.setQuantidadeNumeros(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_NUMEROS)));
                aposta.setQuantidadeTrevos(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_TREVOS)));
                aposta.setSurpresinha(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_SURPRESINHA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                aposta.setQuantidadeSurpresinhas(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_QUANTIDADE_SURPRESINHA)));
                aposta.setTimeDoCoracao(this.readParametroEquipe(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_TIME_CORACAO))));
                aposta.setMesDeSorte(this.readParametroMesDeSorte(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ID_MES_SORTE))));
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
    public boolean existIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {

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
                    TabelaIdentificaoDeUmaApostaDas8Modalidades._ID
            };

            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder.append(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_MODALIDADE + " = ? AND ");
            selectionBuilder.append(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_INICIAL + " = ? AND ");
            selectionBuilder.append(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_CONCURSO_ALVO + " = ? AND ");
            selectionBuilder.append(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_NUMEROS_SELECIONADOS + " = ? ");

            String[] selectionArgs = {
                    ModalidadeEnum.fromString(aposta.getModalidade()),
                    aposta.getConcursoInicial().toString(),
                    aposta.getConcursoAlvo().toString(),
                    numerosSelecionados
            };


            cursor = db.query(
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
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
                    TabelaIdentificaoDeUmaApostaDas8Modalidades._ID
            };

            cursor = db.query(
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
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
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR
            };

            cursor = db.query(
                    TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                value = value.add((BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VALOR)))));
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

                values.put(TabelaEquipe.COLUNA_PLACAR, equipe.getPlacar());
                values.put(TabelaEquipe.COLUNA_VITORIA, equipe.getVitoria());
                values.put(TabelaEquipe.COLUNA_ID_PARAMETRO_EQUIPE, this.insertParametroEquipe(equipe.getParametroEquipe()));
                values.put(TabelaEquipe.COLUNA_NOME, equipe.getNome());
                values.put(TabelaEquipe.COLUNA_NUMERO, equipe.getNumero());
                values.put(TabelaEquipe.COLUNA_NUMERO_PAIS, equipe.getNumeroPais());
                values.put(TabelaEquipe.COLUNA_PAIS, equipe.getPais());
                values.put(TabelaEquipe.COLUNA_SIGLA_PAIS, equipe.getSiglaPais());
                values.put(TabelaEquipe.COLUNA_UF, equipe.getUf());
                values.put(TabelaEquipe.COLUNA_NOME_CLASS, equipe.getNomeClass());
                values.put(TabelaEquipe.COLUNA_INDICADOR_SELECAO, equipe.getIndicadorSelecao());
                values.put(TabelaEquipe.COLUNA_DESCRICAO_CURTA, equipe.getDescricaoCurta());
                values.put(TabelaEquipe.COLUNA_DESCRICAO_LONGA, equipe.getDescricaoLonga());

                return db.insert(TabelaEquipe.NOME_TABELA, null, values);
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
                        TabelaEquipe._ID,
                        TabelaEquipe.COLUNA_PLACAR,
                        TabelaEquipe.COLUNA_VITORIA,
                        TabelaEquipe.COLUNA_ID_PARAMETRO_EQUIPE,
                        TabelaEquipe.COLUNA_NOME,
                        TabelaEquipe.COLUNA_NUMERO,
                        TabelaEquipe.COLUNA_NUMERO_PAIS,
                        TabelaEquipe.COLUNA_PAIS,
                        TabelaEquipe.COLUNA_SIGLA_PAIS,
                        TabelaEquipe.COLUNA_UF,
                        TabelaEquipe.COLUNA_NOME_CLASS,
                        TabelaEquipe.COLUNA_INDICADOR_SELECAO,
                        TabelaEquipe.COLUNA_DESCRICAO_CURTA,
                        TabelaEquipe.COLUNA_DESCRICAO_LONGA
                };

                String selection = TabelaEquipe._ID + " = " + id;

                cursor = db.query(
                        TabelaEquipe.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                EquipeDTO equipeDTO = new EquipeDTO();
                while (cursor.moveToNext()) {
                    equipeDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaEquipe._ID)));
                    equipeDTO.setPlacar(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_PLACAR)));
                    equipeDTO.setVitoria(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_VITORIA)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    equipeDTO.setParametroEquipe(this.readParametroEquipe(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_ID_PARAMETRO_EQUIPE))));
                    equipeDTO.setNome(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_NOME)));
                    equipeDTO.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_NUMERO)));
                    equipeDTO.setNumeroPais(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_NUMERO_PAIS)));
                    equipeDTO.setPais(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_PAIS)));
                    equipeDTO.setSiglaPais(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_SIGLA_PAIS)));
                    equipeDTO.setUf(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_UF)));
                    equipeDTO.setNomeClass(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_NOME_CLASS)));
                    equipeDTO.setIndicadorSelecao(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_INDICADOR_SELECAO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    equipeDTO.setDescricaoCurta(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_DESCRICAO_CURTA)));
                    equipeDTO.setDescricaoLonga(cursor.getString(cursor.getColumnIndexOrThrow(TabelaEquipe.COLUNA_DESCRICAO_LONGA)));
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

                values.put(TabelaParametroEquipe.COLUNA_INDICADOR_SELECAO, parametroEquipe.getIndicadorSelecao());
                values.put(TabelaParametroEquipe.COLUNA_NOME, parametroEquipe.getNome());
                values.put(TabelaParametroEquipe.COLUNA_NUMERO, parametroEquipe.getNumero());
                values.put(TabelaParametroEquipe.COLUNA_NUMERO_PAIS, parametroEquipe.getNumeroPais());
                values.put(TabelaParametroEquipe.COLUNA_DESCRICAO_CURTA, parametroEquipe.getDescricaoCurta());
                values.put(TabelaParametroEquipe.COLUNA_DESCRICAO_LONGA, parametroEquipe.getDescricaoLonga());
                values.put(TabelaParametroEquipe.COLUNA_PAIS, parametroEquipe.getPais());
                values.put(TabelaParametroEquipe.COLUNA_SIGLA_PAIS, parametroEquipe.getSiglaPais());
                values.put(TabelaParametroEquipe.COLUNA_UF, parametroEquipe.getUf());
                values.put(TabelaParametroEquipe.COLUNA_NOME_CLASS, parametroEquipe.getNomeClass());

                return db.insert(TabelaParametroEquipe.NOME_TABELA, null, values);
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
                        TabelaParametroEquipe._ID,
                        TabelaParametroEquipe.COLUNA_INDICADOR_SELECAO,
                        TabelaParametroEquipe.COLUNA_NOME,
                        TabelaParametroEquipe.COLUNA_NUMERO,
                        TabelaParametroEquipe.COLUNA_NUMERO_PAIS,
                        TabelaParametroEquipe.COLUNA_DESCRICAO_CURTA,
                        TabelaParametroEquipe.COLUNA_DESCRICAO_LONGA,
                        TabelaParametroEquipe.COLUNA_PAIS,
                        TabelaParametroEquipe.COLUNA_SIGLA_PAIS,
                        TabelaParametroEquipe.COLUNA_UF,
                        TabelaParametroEquipe.COLUNA_NOME_CLASS
                };

                String selection = TabelaParametroEquipe._ID + " = " + id;

                cursor = db.query(
                        TabelaParametroEquipe.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                ParametroEquipe parametroEquipe = new ParametroEquipe();
                while (cursor.moveToNext()) {
                    parametroEquipe.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaParametroEquipe._ID)));
                    parametroEquipe.setIndicadorSelecao(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_INDICADOR_SELECAO)) == 1 ? Boolean.TRUE : Boolean.FALSE);
                    parametroEquipe.setNome(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_NOME)));
                    parametroEquipe.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_NUMERO)));
                    parametroEquipe.setNumeroPais(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_NUMERO_PAIS)));
                    parametroEquipe.setDescricaoCurta(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_DESCRICAO_CURTA)));
                    parametroEquipe.setDescricaoLonga(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_DESCRICAO_LONGA)));
                    parametroEquipe.setPais(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_PAIS)));
                    parametroEquipe.setSiglaPais(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_SIGLA_PAIS)));
                    parametroEquipe.setUf(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_UF)));
                    parametroEquipe.setNomeClass(cursor.getString(cursor.getColumnIndexOrThrow(TabelaParametroEquipe.COLUNA_NOME_CLASS)));
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

                values.put(TabelaPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES, idDbAposta);
                values.put(TabelaPartidaLotogol.COLUNA_ID_EQUIPE_UM, this.insertEquipe(partidaLotogolDTO.getEquipe1()));
                values.put(TabelaPartidaLotogol.COLUNA_ID_EQUIPE_DOIS, this.insertEquipe(partidaLotogolDTO.getEquipe2()));
                values.put(TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM, partidaLotogolDTO.getIndexPlacarEquipe1());
                values.put(TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS, partidaLotogolDTO.getIndexPlacarEquipe2());

                return db.insert(TabelaPartidaLotogol.NOME_TABELA, null, values);
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
                        TabelaPartidaLotogol._ID,
                        TabelaPartidaLotogol.COLUNA_ID_EQUIPE_UM,
                        TabelaPartidaLotogol.COLUNA_ID_EQUIPE_DOIS,
                        TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM,
                        TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS
                };

                String selection = TabelaPartidaLotogol.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " = " + idDbAposta;

                cursor = db.query(
                        TabelaPartidaLotogol.NOME_TABELA,
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
                    partidaLotogolDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLotogol._ID)));
                    partidaLotogolDTO.setEquipe1(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLotogol.COLUNA_ID_EQUIPE_UM))));
                    partidaLotogolDTO.setEquipe2(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLotogol.COLUNA_ID_EQUIPE_DOIS))));
                    partidaLotogolDTO.setIndexPlacarEquipe1(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_UM)));
                    partidaLotogolDTO.setIndexPlacarEquipe2(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaPartidaLotogol.COLUNA_INDEX_PLACAR_EQUIPE_DOIS)));

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

                values.put(TabelaPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES, idDbAposta);
                values.put(TabelaPartidaLoteca.COLUNA_ID_EQUIPE_UM, this.insertEquipe(partidaLotecaDTO.getEquipe1()));
                values.put(TabelaPartidaLoteca.COLUNA_ID_EQUIPE_DOIS, this.insertEquipe(partidaLotecaDTO.getEquipe2()));
                values.put(TabelaPartidaLoteca.COLUNA_EMPATE, partidaLotecaDTO.getEmpate());

                return db.insert(TabelaPartidaLoteca.NOME_TABELA, null, values);
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
                        TabelaPartidaLoteca._ID,
                        TabelaPartidaLoteca.COLUNA_ID_EQUIPE_UM,
                        TabelaPartidaLoteca.COLUNA_ID_EQUIPE_DOIS,
                        TabelaPartidaLoteca.COLUNA_EMPATE
                };

                String selection = TabelaPartidaLoteca.COLUNA_ID_IDENTIFICAO_DE_UMA_APOSTA_DAS_8_MODALIDADES + " = " + idDbAposta;

                cursor = db.query(
                        TabelaPartidaLoteca.NOME_TABELA,
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
                    partidaLotecaDTO.setIdDB(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLoteca._ID)));
                    partidaLotecaDTO.setEquipe1(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLoteca.COLUNA_ID_EQUIPE_UM))));
                    partidaLotecaDTO.setEquipe2(this.readEquipeDTO(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLoteca.COLUNA_ID_EQUIPE_DOIS))));
                    partidaLotecaDTO.setEmpate(cursor.getLong(cursor.getColumnIndexOrThrow(TabelaPartidaLoteca.COLUNA_EMPATE)) == 1 ? Boolean.TRUE : Boolean.FALSE);

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

    /**
     * Método que insere uma nova LotericaDTO na base
     *
     * @param mesDeSorte ParametroMesDeSorte
     * @return id
     */
    private Integer insertParametroMesDeSorte(ParametroMesDeSorte mesDeSorte) {

        if (mesDeSorte != null) {
            ParametroMesDeSorte mesDB = readParametroMesDeSorte(mesDeSorte.getNumero());
            if (mesDB != null && mesDB.getNumero() != null) {
                return mesDeSorte.getNumero();
            } else {
                this.initDBLoteriasHelper();
                try {

                    this.db = dbLoteriasHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    values.put(TabelaMesDeSorte.COLUNA_NUMERO, mesDeSorte.getNumero());
                    values.put(TabelaMesDeSorte.COLUNA_NOME, mesDeSorte.getNome());
                    values.put(TabelaMesDeSorte.COLUNA_ABREVIACAO, mesDeSorte.getAbreviacao());
                    db.insert(TabelaMesDeSorte.NOME_TABELA, null, values);

                    return mesDeSorte.getNumero();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return null;
                }

            }
        } else {
            return null;
        }
    }

    /**
     * Método responsável por ler ParametroMesDeSorte da base local  por ID
     *
     * @param id Long
     * @return ParametroMesDeSorte
     */
    private ParametroMesDeSorte readParametroMesDeSorte(Integer id) {

        if (id != null) {
            this.initDBLoteriasHelper();
            Cursor cursor = null;

            try {
                this.db = dbLoteriasHelper.getReadableDatabase();

                String[] projection = {
                        TabelaMesDeSorte.COLUNA_NUMERO,
                        TabelaMesDeSorte.COLUNA_NOME,
                        TabelaMesDeSorte.COLUNA_ABREVIACAO
                };

                String selection = TabelaParametroEquipe.COLUNA_NUMERO + " = " + id;

                cursor = db.query(
                        TabelaMesDeSorte.NOME_TABELA,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                ParametroMesDeSorte parametroMesDeSorte = new ParametroMesDeSorte();
                while (cursor.moveToNext()) {
                    parametroMesDeSorte.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(TabelaMesDeSorte.COLUNA_NUMERO)));
                    parametroMesDeSorte.setNome(cursor.getString(cursor.getColumnIndexOrThrow(TabelaMesDeSorte.COLUNA_NOME)));
                    parametroMesDeSorte.setAbreviacao(cursor.getString(cursor.getColumnIndexOrThrow(TabelaMesDeSorte.COLUNA_ABREVIACAO)));
                }

                return parametroMesDeSorte;
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
            this.deleteItem(lotericaDTO.getIdDB(), TabelaLoterica.NOME_TABELA);
        }
    }

    public void deleteIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
        try {
            if (aposta != null) {
                //this.deleteLoterica(aposta.getLoterica());
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

                if (aposta.getModalidade() == ModalidadeEnum.LOTOMANIA) {
                    List<IdentificaoDeUmaApostaDas8Modalidades> apostas = readAllIdentificaoDeUmaApostaDas8Modalidades();
                    for (IdentificaoDeUmaApostaDas8Modalidades apostaFor : apostas) {
                        if (apostaFor.getVinculoEspelho().compareTo(aposta.getVinculoEspelho()) == 0) {
                            if (!aposta.getEspelho()) {
                                ContentValues cv = new ContentValues();
                                cv.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_ESPELHO, 0);
                                cv.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_GERAR_ESPELHO, 0);
                                cv.put(TabelaIdentificaoDeUmaApostaDas8Modalidades.COLUNA_VINCULO_ESPELHO, "");


                                this.update(cv, TabelaIdentificaoDeUmaApostaDas8Modalidades._ID + "=" + apostaFor.getIdDB());
                            }
                        }
                    }
                    this.deleteItem(aposta.getIdDB(), TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
                } else {
                    this.deleteItem(aposta.getIdDB(), TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.closeDBLoteriasHelper();
        }
    }

    public void update(ContentValues cv, String whereClause) {
        this.initDBLoteriasHelper();
        this.db = dbLoteriasHelper.getWritableDatabase();
        db.update(TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA, cv, whereClause, null);
    }

    private void deleteEquipe(EquipeDTO equipeDTO) {
        if (equipeDTO != null) {
            this.deleteParametroEquipe(equipeDTO.getParametroEquipe());
            this.deleteItem(equipeDTO.getIdDB(), TabelaEquipe.NOME_TABELA);
        }
    }

    private void deleteParametroEquipe(ParametroEquipe parametroEquipe) {
        if (parametroEquipe != null) {
            this.deleteItem(parametroEquipe.getIdDB(), TabelaParametroEquipe.NOME_TABELA);
        }
    }

    private void deletePartidaLotogol(PartidaLotogolDTO partidaLotogolDTO) {
        if (partidaLotogolDTO != null) {
            this.deleteEquipe(partidaLotogolDTO.getEquipe1());
            this.deleteEquipe(partidaLotogolDTO.getEquipe2());
            this.deleteItem(partidaLotogolDTO.getIdDB(), TabelaPartidaLotogol.NOME_TABELA);
        }
    }

    private void deletePartidaLoteca(PartidaLotecaDTO partidaLotecaDTO) {
        if (partidaLotecaDTO != null) {
            this.deleteEquipe(partidaLotecaDTO.getEquipe1());
            this.deleteEquipe(partidaLotecaDTO.getEquipe2());
            this.deleteItem(partidaLotecaDTO.getIdDB(), TabelaPartidaLoteca.NOME_TABELA);
        }
    }

    private void deleteItem(Long id, String table) {
        this.initDBLoteriasHelper();
        try {
            this.db = dbLoteriasHelper.getWritableDatabase();
            String selection = TabelaLoterica._ID + "=" + id;
            db.delete(table, selection, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteAll() {
        this.initDBLoteriasHelper();
        try {
            this.db = dbLoteriasHelper.getWritableDatabase();
            db.delete(TabelaLoterica.NOME_TABELA, null, null);
            db.delete(TabelaIdentificaoDeUmaApostaDas8Modalidades.NOME_TABELA, null, null);
            db.delete(TabelaEquipe.NOME_TABELA, null, null);
            db.delete(TabelaParametroEquipe.NOME_TABELA, null, null);
            db.delete(TabelaPartidaLotogol.NOME_TABELA, null, null);
            db.delete(TabelaPartidaLoteca.NOME_TABELA, null, null);
            db.delete(TabelaMesDeSorte.NOME_TABELA, null, null);
            db.delete(TabelaCarrinhoFavorito.NOME_TABELA, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.closeDBLoteriasHelper();
        }
    }
}
