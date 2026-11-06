package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PremioDTO;
import br.gov.caixa.loterias.apostas.view.custom.PartidaViewDetalhesResultado;

/**
 * Created by joafilho on 16/04/2018.
 * Class HtmlUtils
 */

public class HtmlUtils {

    private static final String FILE_PATH_COMPROVANTE_APOSTA = "htmls/comprovante_aposta.html";
    private static final String FILE_PATH_COMPROVANTE_APOSTA_BOLAO = "htmls/comprovante_aposta_bolao.html";
    private static final String FILE_PATH_COMPROVANTE_PREMIO = "htmls/comprovante_premio.html";
    private static final String FILE_PATH_COMPROVANTE_PREMIO_BOLAO = "htmls/comprovante_premio_bolao.html";
    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE = "htmls/comprovante_cabecalho_rodape.html";
    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE_PREMIO = "htmls/comprovante_cabecalho_rodape_premio.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO = "htmls/comprovante_lista_corpo_numerico.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA = "htmls/comprovante_lista_corpo_loteca.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA2 = "htmls/comprovante_lista_corpo_loteca2.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTOGOL = "htmls/comprovante_lista_corpo_lotogol.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_SUPER_7 = "htmls/comprovante_lista_corpo_super7.html";
    private static final String FILE_PATH_COMPROVANTE_CORPO_PREMIO_HISTORICO = "htmls/comprovante_corpo_premio_historico.html";

    private static final String HEADER_TITLE_APP_TXT = "{header_title_app_txt}";
    private static final String HEADER_TIPO_COMPROVANTE_TXT = "{header_tipo_comprovante_txt}";
    private static final String BASE_64_LOGO = "{base_64_logo}";
    private static final String HEADER_IMPRIME_PAGINA = "{header_imprime_pagina}";
    private static final String HEADER_NOME = "{header_nome}";
    private static final String HEADER_CPF = "{header_cpf}";
    private static final String HEADER_MODALIDADE = "{header_modalidade}";
    private static final String HEADER_CONCURSO_TITULO = "{header_concurso_titulo}";
    private static final String HEADER_CONCURSO_VALOR = "{header_concurso_valor}";
    private static final String HEADER_DATA_APOSTA = "{header_data_aposta}";
    private static final String HEADER_HORA_APOSTA = "{header_hora_aposta}";
    private static final String HEADER_SUBCANAL = "{header_subcanal}";
    private static final String HEADER_VALOR_TOTAL_APOSTA = "{header_valor_total_aposta}";
    private static final String HEADER_VALOR_COTA = "{header_valor_cota}";
    private static final String HEADER_VALOR_SERVICO = "{header_valor_servico}";
    private static final String HEADER_CODIGO_LOTERICO = "{header_codigo_loterico}";
    private static final String HEADER_TERMINAL_LOTERICO = "{header_terminal_loterico}";
    private static final String HEADER_COTA_BOLAO = "{header_cota_bolao}";
    private static final String HEADER_DATA_PAGAMENTO = "{header_data_pagamento}";
    private static final String HEADER_HORA_PAGAMENTO = "{header_hora_pagamento}";
    private static final String HEADER_VALOR_LIQUIDO_PREMIO = "{header_valor_liquido_premio}";
    private static final String HEADER_LOCAL_PAGAMENTO = "{header_local_pagamento}";

    private static final String BODY_LISTA_ITENS_APOSTA = "{body_lista_itens_aposta}";
    private static final String BODY_LISTA_TABELA_PREMIACAO = "{body_lista_tabela_premiacao}";
    private static final String BASE_64_CODIGO_BARRAS_APOSTA = "{base_64_codigo_barras}";
    private static final String BODY_CODIGO_NSBI = "{body_codigo_nsbi}";


    private static final String HEADER_COLUNA1_LINHA1_VALOR = "{header_coluna1_linha1_valor}";
    private static final String HEADER_COLUNA2_LINHA1_VALOR = "{header_coluna2_linha1_valor}";
    private static final String HEADER_COLUNA2_LINHA1_TITULO = "{header_coluna2_linha1_titulo}";
    private static final String HEADER_COLUNA1_LINHA2_VALOR = "{header_coluna1_linha2_valor}";
    private static final String HEADER_COLUNA2_LINHA2_TEXTO = "{header_coluna2_linha2_texto}";
    private static final String HEADER_COLUNA2_LINHA2_VALOR = "{header_coluna2_linha2_valor}";
    private static final String HEADER_COLUNA1_LINHA3_VALOR = "{header_coluna1_linha3_valor}";
    private static final String HEADER_COLUNA2_LINHA3_TEXTO = "{header_coluna2_linha3_texto}";
    private static final String HEADER_COLUNA2_LINHA3_VALOR = "{header_coluna2_linha3_valor}";
    private static final String BODY_TITULO = "{body_titulo}";
    private static final String BODY_INFORMACAO_APOSTA = "{body_informacao_aposta}";
    private static final String BODY_ABREV = "{body_abrev}";
    private static final String BODY_PARTIDA1 = "{body_partida1}";
    private static final String BODY_EMPATE = "{body_empate}";
    private static final String BODY_PARTIDA2 = "{body_partida2}";
    private static final String BODY_CONTEUDO = "{body_conteudo}";
    private static final String BODY_COLUNA1_LINHA1_VALOR_PREMIO = "{body_coluna1_linha1_valor}";
    private static final String BODY_COLUNA2_LINHA1_VALOR_PREMIO = "{body_coluna2_linha1_valor}";
    private static final String BODY_COLUNA3_LINHA1_VALOR_PREMIO = "{body_coluna3_linha1_valor}";
    private static final String BODY_COLUNA1_LINHA2_VALOR_PREMIO = "{body_coluna1_linha2_valor}";
    private static final String BODY_SEUS_NUMEROS = "{body_seus_numeros}";
    private static final String SUPER7_COLUNA_1 = "{COLUNA_1}";
    private static final String SUPER7_COLUNA_2 = "{COLUNA_2}";
    private static final String SUPER7_COLUNA_3 = "{COLUNA_3}";
    private static final String SUPER7_COLUNA_4 = "{COLUNA_4}";
    private static final String SUPER7_COLUNA_5 = "{COLUNA_5}";
    private static final String SUPER7_COLUNA_6 = "{COLUNA_6}";
    private static final String SUPER7_COLUNA_7 = "{COLUNA_7}";

    private static final String PONTO_MARCADOR = "•";

    public static String getComprovanteCabecalhoRodapeAposta(ComprovanteApostaDTO comprovanteApostaDTO, Context context) {
        String nome = DadosUsuarioBO.obterNome().toLowerCase(new Locale(context.getResources().getString(R.string.pt), context.getResources().getString(R.string.br)));
        String nomeUsuario = StringUtils.capitalizer(nome);
        String modalidade = ViewUtils.getNomeModalidadePorAposta(comprovanteApostaDTO.getAposta());
        String dataSorteio = DateUtils.getDateToString(DateUtils.stringToDate(
                        comprovanteApostaDTO.getDataSorteio(), DateUtils.PATTERN_DD_MMM_YYYY),
                DateUtils.PATTERN_DDMM_YYYY);

        String html;
        if (comprovanteApostaDTO.getAposta().getIndicadorCotaBolao()) {
            html = getContentOfFile(FILE_PATH_COMPROVANTE_APOSTA_BOLAO, context);
            html = ViewUtils.replaceNullable(html, HEADER_VALOR_TOTAL_APOSTA, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getReservaCotaBolao().getVrTotalCota()));
            html = ViewUtils.replaceNullable(html, HEADER_VALOR_COTA, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getReservaCotaBolao().getVrCotaReservada()));
            html = ViewUtils.replaceNullable(html, HEADER_VALOR_SERVICO, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getReservaCotaBolao().getVrTarifaServico()));
            html = ViewUtils.replaceNullable(html, HEADER_CODIGO_LOTERICO, comprovanteApostaDTO.getAposta().getReservaCotaBolao().getNumeroLoterica().getCodigo());
            html = ViewUtils.replaceNullable(html, HEADER_TERMINAL_LOTERICO, String.valueOf(comprovanteApostaDTO.getAposta().getReservaCotaBolao().getNumeroTerminalLoterico()));
            html = ViewUtils.replaceNullable(html, HEADER_COTA_BOLAO, comprovanteApostaDTO.getAposta().getReservaCotaBolao().getNumeroCotaReservada() + "/"
                    + comprovanteApostaDTO.getAposta().getReservaCotaBolao().getQtdCotaTotalBolao());
        } else {
            html = getContentOfFile(FILE_PATH_COMPROVANTE_APOSTA, context);
            html = ViewUtils.replaceNullable(html, HEADER_VALOR_TOTAL_APOSTA, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getValor()));
        }

        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_aposta, modalidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));

        html = ViewUtils.replaceNullable(html, HEADER_NOME, nomeUsuario);
        html = ViewUtils.replaceNullable(html, HEADER_CPF, AppUtils.mascaraCPF(DadosUsuarioBO.obterCpf()));
        html = ViewUtils.replaceNullable(html, HEADER_MODALIDADE, modalidade);
        if(comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas()!= null && comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas() > 0){
            int concursoFinal = (comprovanteApostaDTO.getAposta().getConcursoInicial() + comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas()) - 1;
            html = ViewUtils.replaceNullable(html,HEADER_CONCURSO_TITULO,"Concursos:");
            html = ViewUtils.replaceNullable(html, HEADER_CONCURSO_VALOR, comprovanteApostaDTO.getAposta().getConcursoInicial() +
                    " a " + concursoFinal);
        } else {
            html = ViewUtils.replaceNullable(html,HEADER_CONCURSO_TITULO,"Concurso:");
            html = ViewUtils.replaceNullable(html, HEADER_CONCURSO_VALOR, String.valueOf(comprovanteApostaDTO.getAposta().getConcursoInicial()));
        }
        if (comprovanteApostaDTO.getAposta().getIndicadorCotaBolao()){
            html = ViewUtils.replaceNullable(html, HEADER_DATA_APOSTA, comprovanteApostaDTO.getAposta().getReservaCotaBolao().getDataRegistroBolao());
            html = ViewUtils.replaceNullable(html, HEADER_HORA_APOSTA, comprovanteApostaDTO.getAposta().getReservaCotaBolao().getHoraRegistroBolao());
        }else {
            html = ViewUtils.replaceNullable(html, HEADER_DATA_APOSTA, comprovanteApostaDTO.getAposta().getDataEfetivacao());
            html = ViewUtils.replaceNullable(html, HEADER_HORA_APOSTA, comprovanteApostaDTO.getAposta().getHoraEfetivacao());
        }
        html = ViewUtils.replaceNullable(html, HEADER_SUBCANAL, comprovanteApostaDTO.getAposta().getSubcanalPagamento());

        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(comprovanteApostaDTO.getAposta(), context));
        html = ViewUtils.replaceNullable(html, BASE_64_CODIGO_BARRAS_APOSTA, getBarcode(comprovanteApostaDTO.getNsbi()));
        html = ViewUtils.replaceNullable(html, BODY_CODIGO_NSBI, comprovanteApostaDTO.getNsbi());

        int totalPaginas = contaPaginas(html, "#X");
        html = substituiNumeracaoPaginas(html, totalPaginas, "#X", "#Y");

        return html;
    }

    public static String getComprovanteCabecalhoRodapePremioHistorico(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, Context context) {
        String modalidade = ViewUtils.getNomeModalidadePorAposta(detalhesHistoricoPremioDTO.getAposta());
        String hora = DateUtils.getDateToString(
                DateUtils.stringToDate(detalhesHistoricoPremioDTO.getAposta().getHoraEfetivacao(), DateUtils.PATTERN_HH_MM_SS)
                , DateUtils.PATTERN_HH_MM);

        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE_PREMIO, context);
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_premio, modalidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, detalhesHistoricoPremioDTO.getAposta().getDataEfetivacao());
        html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concurso");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(detalhesHistoricoPremioDTO.getAposta().getConcursoInicial()));
        /*if(detalhesHistoricoPremioDTO.getAposta().getQuantidadeTeimosinhas() > 0){
            int concursoFinal = (detalhesHistoricoPremioDTO.getAposta().getConcursoInicial() + detalhesHistoricoPremioDTO.getAposta().getQuantidadeTeimosinhas()) - 1;
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concursos");
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, detalhesHistoricoPremioDTO.getAposta().getConcursoInicial() +
                    " a "+
                    concursoFinal);
        } else {
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(detalhesHistoricoPremioDTO.getAposta().getConcursoInicial()));
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concurso");
        }*/
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, detalhesHistoricoPremioDTO.getAposta().getHoraEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_local_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, detalhesHistoricoPremioDTO.getPremioDTO().getLocalPagamento());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, modalidade);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_nsu_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, String.valueOf(detalhesHistoricoPremioDTO.getPremioDTO().getNsuPagamento()));
        html = ViewUtils.replaceNullable(html, BODY_TITULO, modalidade);
        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(detalhesHistoricoPremioDTO.getAposta(), context));
        html = ViewUtils.replaceNullable(html, BODY_CONTEUDO, getBodyPremioHistorico(detalhesHistoricoPremioDTO, context));

        return html;
    }

    public static String getComprovanteCabecalhoRodapePremio(DetalhesPremioDTO detalhesPremioDTO, Context context) {
        String nomeFaixa = context.getResources().getString(R.string.string_vazia);
        LinkedHashMap<String,String> listaFaixas = PartidaViewDetalhesResultado.obterMapFaixa(new Modalidade(detalhesPremioDTO.getAposta().getModalidade()));

        if (detalhesPremioDTO.getPremio() != null && CollectionUtils.isNotEmpty(detalhesPremioDTO.getPremio().getConcursos())) {
            for (ConcursoPremiadoDTO concursoPremiadoDTO : detalhesPremioDTO.getPremio().getConcursos()) {
                if (CollectionUtils.isNotEmpty(concursoPremiadoDTO.getFaixas())) {
                    for (FaixaPremiadaDTO faixa: concursoPremiadoDTO.getFaixas()) {
                        nomeFaixa = listaFaixas.get(context.getResources().getString(R.string._000) + faixa.getNumero());
                    }
                }
            }
        }
        String nome = DadosUsuarioBO.obterNome().toLowerCase(new Locale(context.getResources().getString(R.string.pt), context.getResources().getString(R.string.br)));
        String nomeUsuario = StringUtils.capitalizer(nome);

        String modalidade = ViewUtils.getNomeModalidadePorAposta(detalhesPremioDTO.getAposta());
        String hora = DateUtils.getDateToString(
                DateUtils.stringToDate(detalhesPremioDTO.getAposta().getHoraEfetivacao(), DateUtils.PATTERN_HH_MM_SS)
                , DateUtils.PATTERN_HH_MM);

        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE_PREMIO, context);
        html = ViewUtils.replaceNullable(html, HEADER_NOME, nomeUsuario);
        html = ViewUtils.replaceNullable(html, HEADER_CPF, AppUtils.mascaraCPF(DadosUsuarioBO.obterCpf()));
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_premio, modalidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, detalhesPremioDTO.getAposta().getDataEfetivacao());
        html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concurso");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(detalhesPremioDTO.getAposta().getConcursoInicial()));
        /*if(detalhesPremioDTO.getAposta().getQuantidadeTeimosinhas() > 0){
            int concursoFinal = (detalhesPremioDTO.getAposta().getConcursoInicial() + detalhesPremioDTO.getAposta().getQuantidadeTeimosinhas()) - 1;
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concursos");
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, detalhesPremioDTO.getAposta().getConcursoInicial() +
                    " a "+
                    concursoFinal);
        } else {
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(detalhesPremioDTO.getAposta().getConcursoInicial()));
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concurso");
        }*/
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, hora);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_local_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, detalhesPremioDTO.getPremio().getLocalPagamento());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, modalidade);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_nsu_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, String.valueOf(detalhesPremioDTO.getPremio().getNsuPagamento()));
        //html = ViewUtils.replaceNullable(html, BODY_TITULO, modalidade);
        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(detalhesPremioDTO.getAposta(), context));
        html = ViewUtils.replaceNullable(html, BODY_COLUNA1_LINHA1_VALOR_PREMIO, nomeFaixa);
        html = ViewUtils.replaceNullable(html, BODY_COLUNA2_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesPremioDTO.getAposta().getValor()));
        html = ViewUtils.replaceNullable(html, BODY_COLUNA1_LINHA2_VALOR_PREMIO, detalhesPremioDTO.getNsbi());
        html = ViewUtils.replaceNullable(html, BODY_COLUNA3_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
        //TODO - Tratativa Loteca
        /*if(modalidade!= null){
            if( modalidade.equalsIgnoreCase("loteca")){
                //t
                html = ViewUtils.replaceNullable(html, BODY_SEUS_NUMEROS, "Seus números:");
            } else {
                html = ViewUtils.replaceNullable(html, BODY_SEUS_NUMEROS, "Seus números:");
            }
        } else {
            html = ViewUtils.replaceNullable(html, BODY_SEUS_NUMEROS, "Seus números:");
        }*/
        html = ViewUtils.replaceNullable(html, BODY_SEUS_NUMEROS, "Seus números:");


        return html;
    }

    public static String getComprovanteCabecalhoRodapePremioPago(DetalhesPremioDTO detalhesPremioDTO, Context context) {
        String nome = DadosUsuarioBO.obterNome().toLowerCase(new Locale(context.getResources().getString(R.string.pt), context.getResources().getString(R.string.br)));
        String nomeUsuario = StringUtils.capitalizer(nome);

        String modalidade = ViewUtils.getNomeModalidadePorAposta(detalhesPremioDTO.getAposta());
        String hora = DateUtils.getDateToString(
                DateUtils.stringToDate(detalhesPremioDTO.getPremio().getHoraPagamento(), DateUtils.PATTERN_HH_MM_SS)
                , DateUtils.PATTERN_HH_MM);

        String html;
        if (detalhesPremioDTO.getAposta().getIndicadorCotaBolao()) {
            html = getContentOfFile(FILE_PATH_COMPROVANTE_PREMIO_BOLAO, context);
            html = ViewUtils.replaceNullable(html, HEADER_COTA_BOLAO, detalhesPremioDTO.getAposta().getReservaCotaBolao().getNumeroCotaReservada() + "/"
                    + detalhesPremioDTO.getAposta().getReservaCotaBolao().getQtdCotaTotalBolao());
        } else {
            html = getContentOfFile(FILE_PATH_COMPROVANTE_PREMIO, context);
        }

        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_premio, modalidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_NOME, nomeUsuario);
        html = ViewUtils.replaceNullable(html, HEADER_CPF, AppUtils.mascaraCPF(DadosUsuarioBO.obterCpf()));
        html = ViewUtils.replaceNullable(html, HEADER_MODALIDADE, modalidade);
        html = ViewUtils.replaceNullable(html, HEADER_CONCURSO_VALOR, String.valueOf(detalhesPremioDTO.getAposta().getConcursoInicial()));
        html = ViewUtils.replaceNullable(html, HEADER_DATA_PAGAMENTO, detalhesPremioDTO.getPremio().getDataPagamento());
        html = ViewUtils.replaceNullable(html, HEADER_HORA_PAGAMENTO, hora);
        html = ViewUtils.replaceNullable(html, HEADER_VALOR_LIQUIDO_PREMIO, ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
        html = ViewUtils.replaceNullable(html, HEADER_LOCAL_PAGAMENTO, detalhesPremioDTO.getPremio().getLocalPagamento());

        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(detalhesPremioDTO.getAposta(), context));

        html = ViewUtils.replaceNullable(html, BODY_LISTA_TABELA_PREMIACAO, getBodyListaTabelaPremiacao(detalhesPremioDTO.getPremio(), detalhesPremioDTO.getPremio().getValorLiquido()));

        html = ViewUtils.replaceNullable(html, BODY_CODIGO_NSBI, detalhesPremioDTO.getNsbi());

        int totalPaginas = contaPaginas(html, "#X");
        html = substituiNumeracaoPaginas(html, totalPaginas, "#X", "#Y");

        return html;
    }

    private static String getBodyList(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta, Context context) {
        StringBuilder sbHtmlBodyList = new StringBuilder();
        String htmlBodyList = "";
        switch (aposta.getModalidade()) {
            case LOTECA:
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA2, context);
                List<PartidaLotecaDTO> partidaLotecaList;
                if (aposta.getIndicadorCotaBolao()){
                    partidaLotecaList = aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca();
                } else {
                    partidaLotecaList = aposta.getPartidasLoteca();
                }
                for (PartidaLotecaDTO partidaLotecaDTO : partidaLotecaList) {
                    EquipeDTO equipe1 = partidaLotecaDTO.getEquipe1();
                    EquipeDTO equipe2 = partidaLotecaDTO.getEquipe2();

                    String abrev = partidaLotecaDTO.getLegendaAbreviada();
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_ABREV,abrev);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA1,
                            context.getString(R.string.label_corpo_equipe, equipe1.getVitoria() ? PONTO_MARCADOR : " ", getNomeEquipe(partidaLotecaDTO.getEquipe1(), aposta.getIndicadorCotaBolao())));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_EMPATE,
                            context.getString(R.string.label_corpo_equipe, partidaLotecaDTO.getEmpate() ? PONTO_MARCADOR : " ",
                                    context.getString(R.string.label_empate)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA2,
                            context.getString(R.string.label_corpo_equipe, equipe2.getVitoria() ? PONTO_MARCADOR : " ", getNomeEquipe(partidaLotecaDTO.getEquipe2(), aposta.getIndicadorCotaBolao())));
                    sbHtmlBodyList.append(htmlBodyList);
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA2, context);
                }

                sbHtmlBodyList.append(forcaQuebraPagina());
                sbHtmlBodyList.append(imprimePagina());

                return sbHtmlBodyList.toString();

            case LOTOGOL:
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTOGOL, context);
                for (PartidaLotogolDTO partidaLotogolDTO : aposta.getPartidasLotogol()) {
                    EquipeDTO equipe1 = partidaLotogolDTO.getEquipe1();
                    EquipeDTO equipe2 = partidaLotogolDTO.getEquipe2();

                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA1,
                            context.getString(R.string.label_corpo_equipe, equipe1.getPlacar(),
                                    equipe1.getIndicadorSelecao() ? equipe1.getNome() + "/" + equipe1.getSiglaPais() : equipe1.getNome()));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA2,
                            context.getString(R.string.label_corpo_equipe, equipe2.getPlacar(),
                                    equipe2.getIndicadorSelecao() ? equipe2.getNome() + "/" + equipe2.getSiglaPais() : equipe2.getNome()));

                    sbHtmlBodyList.append(htmlBodyList);
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTOGOL, context);
                }
                return sbHtmlBodyList.toString();

            case SUPER_7:
                if (! aposta.getIndicadorCotaBolao()) {
                    ArrayList<ArrayList<Integer>> matrizSelecionados = aposta.getMatrizNumerosSelecionados();
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_SUPER_7, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_1, montaColunasNumericas(matrizSelecionados.get(0)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_2, montaColunasNumericas(matrizSelecionados.get(1)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_3, montaColunasNumericas(matrizSelecionados.get(2)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_4, montaColunasNumericas(matrizSelecionados.get(3)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_5, montaColunasNumericas(matrizSelecionados.get(4)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_6, montaColunasNumericas(matrizSelecionados.get(5)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_7, montaColunasNumericas(matrizSelecionados.get(6)));
                    sbHtmlBodyList.append(htmlBodyList);
                } else {
                    int elementosPagina = 0;
                    for (int i = 0; i < aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().size(); i++) {
                        List<List<Integer>> matrizSelecionados = aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getDezenas();
                        htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_SUPER_7, context);
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_1, montaColunasNumericas(matrizSelecionados.get(0)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_2, montaColunasNumericas(matrizSelecionados.get(1)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_3, montaColunasNumericas(matrizSelecionados.get(2)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_4, montaColunasNumericas(matrizSelecionados.get(3)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_5, montaColunasNumericas(matrizSelecionados.get(4)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_6, montaColunasNumericas(matrizSelecionados.get(5)));
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, SUPER7_COLUNA_7, montaColunasNumericas(matrizSelecionados.get(6)));
                        //htmlBodyList = "Aposta "+(i+1)+":<br/>" + htmlBodyList;
                        htmlBodyList = "<div><span style=\"all: unset; font-family: monospace;\">Aposta "+(i+1)+":<br/></span></div>" + htmlBodyList;
                        elementosPagina++;
                        if (elementosPagina % 3 == 0) {
                            htmlBodyList += forcaQuebraPagina();
                            htmlBodyList += imprimePaginacaoBolao()+"<br/><br/>";
                        }
                        sbHtmlBodyList.append(htmlBodyList);
                    }
                }
                return sbHtmlBodyList.toString();

            default:
                String numerosSelecionados = "";
                if (aposta.getIndicadorCotaBolao()) {
                    int elementosPagina = 0;
                    for (int i = 0; i < aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().size(); i++) {
                        numerosSelecionados += "Aposta "+(i+1)+":<br/>"+
                                AppUtils.listaIntToString(aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getDezenas()) +
                                "<br/><br/>";
                        if (aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
                            numerosSelecionados += "Mês d3 sorte: " +
                                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getMesSorte().getNome() +
                                    "<br/><br/>";
                        }
                        if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA) {
                            numerosSelecionados += "Time do coração: " +
                                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getTimeCoracao().getNome() + "-" +
                                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getTimeCoracao().getUf() +
                                    "<br/><br/>";
                        }
                        if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA) {
                            numerosSelecionados += "Trevos: " +
                                    AppUtils.listaIntToString(aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(i).getTrevos()) +
                                    "<br/><br/>";
                        }
                        elementosPagina++;
                        if (elementosPagina % 4 == 0) {
                            numerosSelecionados += forcaQuebraPagina();
                            numerosSelecionados += imprimePaginacaoBolao()+"<br/><br/>";
                        }
                        htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                        htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA, numerosSelecionados);
                    }
                } else {
                    numerosSelecionados = AppUtils.listaIntToString(aposta.getListaNumerosSelecionados());
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA, numerosSelecionados);
                }

                sbHtmlBodyList.append(htmlBodyList);

                if (aposta.getTimeDoCoracao() != null) {
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA,
                            aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf());
                    sbHtmlBodyList.append(htmlBodyList);
                } else if (aposta.getMesDeSorte() != null) {
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA,
                            context.getString(R.string.label_mes_sorte_dois_pontos) + aposta.getMesDeSorte().getNome());
                    sbHtmlBodyList.append(htmlBodyList);
                } else if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && aposta.getTrevosSelecionados() != null
                        && !aposta.getIndicadorCotaBolao()){
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA,
                            context.getString(R.string.trevos) + " " +
                                    getTrevosText(aposta));
                    sbHtmlBodyList.append(htmlBodyList);
                }
                return sbHtmlBodyList.toString();
        }
    }

    private static String getNomeEquipe(EquipeDTO equipe, Boolean isBolao) {
        if (isBolao){
            return Utils.getEquipeComUfBolao(equipe);
        } else {
            return Utils.getEquipeComUf(equipe);
        }
    }

    private static String getTrevosText(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
        List<Integer> trevos = AppUtils.converteListaInteiros(aposta.getTrevosSelecionados());
        StringBuilder sb = new StringBuilder();

        if (!trevos.isEmpty()){
            sb.append(trevos.get(0));
            for (int i = 1; i < trevos.size(); i++){
                sb.append(" - " + trevos.get(i));
            }
        }

        return sb.toString();
    }

    private static String  montaColunasNumericas(List<Integer> numeros){
        String string = "";
        for (Number num: numeros) {
            Integer intNum;
            if (num instanceof Double) {
                intNum = ((Double) num).intValue();
            } else {
                intNum = (Integer) num;
            }
            string += String.format(
                    "<span style=\"color: #64778a; width:22px; height:22px; margin-top: 7px; display: flex; justify-content: center; align-items: center; border-radius: 11px; border: 1px solid #64778a;\">%d</span>",
                    intNum);
        }
        return string;
    }

    private static String getBodyPremioHistorico(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, Context context) {
        String nomeFaixa = context.getResources().getString(R.string.string_vazia);
        LinkedHashMap<String,String> listaFaixas = PartidaViewDetalhesResultado.obterMapFaixa(new Modalidade(detalhesHistoricoPremioDTO.getAposta().getModalidade()));
        String htmlBody = getContentOfFile(FILE_PATH_COMPROVANTE_CORPO_PREMIO_HISTORICO, context);

        if (detalhesHistoricoPremioDTO.getPremioDTO() != null && CollectionUtils.isNotEmpty(detalhesHistoricoPremioDTO.getPremioDTO().getConcursos())) {
            for (ConcursoPremiadoDTO concursoPremiadoDTO : detalhesHistoricoPremioDTO.getPremioDTO().getConcursos()) {
                if (CollectionUtils.isNotEmpty(concursoPremiadoDTO.getFaixas())) {
                    for (FaixaPremiadaDTO faixa: concursoPremiadoDTO.getFaixas()) {
                        nomeFaixa = listaFaixas.get(context.getResources().getString(R.string._000) + faixa.getNumero());
                    }
                }
            }
        }
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA1_LINHA1_VALOR_PREMIO, nomeFaixa);
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA2_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesHistoricoPremioDTO.getAposta().getValor()));
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA3_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesHistoricoPremioDTO.getPremioDTO().getValorLiquido()));
        return htmlBody;
    }

    private static String getContentOfFile(String filePath, Context context) {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = context.getAssets().open(filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();

            return buf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static String getBase64Logo(Context context) {
        return Base64.encodeToString(ViewUtils.drawableInByte(context, R.drawable.logo_caixa_pdf_copy), Base64.DEFAULT);
    }

    private static String getBarcode(String valor) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(valor, BarcodeFormat.CODE_128, 200, 50);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
        }

        if (bitmap != null) {
            return Base64.encodeToString(ViewUtils.bitmapToByte(bitmap), Base64.DEFAULT);
        }
        return "";
    }

    private static String getBodyListaTabelaPremiacao(PremioDTO premioDTO, BigDecimal valorLiquidoPremio) {
        if (premioDTO.getConcursos() == null || premioDTO.getConcursos().size() == 0) {
            return "";
        }

        List<ConcursoPremiadoDTO> listConcursoPremiadoDTO = premioDTO.getConcursos(); //.get(0);
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstTime = true;

        if (listConcursoPremiadoDTO != null) {
            for (ConcursoPremiadoDTO concursoPremiadoDTO: listConcursoPremiadoDTO) {
                FaixaPremiadaDTO faixaPremiadaDTO = concursoPremiadoDTO.getFaixas().get(0);
                stringBuilder.append("<tr style=\"height: 30px;\">");
                stringBuilder.append(String.format("<td style=\"border-right: 1px solid orange; border-bottom: 1px solid orange;\">%s</td>", faixaPremiadaDTO.getNome()));
                stringBuilder.append(String.format("<td style=\"border-right: 1px solid orange; border-bottom: 1px solid orange;\">%s</td>", ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorPremioCota())));
                if (firstTime) {
                    firstTime = false;
                    stringBuilder.append(String.format("<th rowspan=\"%d\" style=\"border-right: 1px solid orange; text-align: center; vertical-align: middle;\">%s</th>", concursoPremiadoDTO.getFaixas().size(), ViewUtils.getMoedaFormat(valorLiquidoPremio)));
                    //stringBuilder.append(String.format("<th rowspan=\"%d\" style=\"border-right: 1px solid orange;\">%s</th>", concursoPremiadoDTO.getFaixas().size(), ViewUtils.getMoedaFormat(concursoPremiadoDTO.getValorLiquido())));
                }
            }
            stringBuilder.append("</tr>");
        }

        return stringBuilder.toString();
    }

    private static String forcaQuebraPagina() {
        return "<div class=\"page-break\"></div>" +
                "<br/><br/><br/>";
    }

    private static String imprimePagina() {
        return "<div style=\"color:#4E596B; text-align: right; width: 100%; margin-right: 10px; \">" +
                "<p>Página #X de #Y</p>" +
                "</div>";
    }

    private static String imprimePaginacaoBolao() {
        return "<div style=\"color:#4E596B; text-align: left; float: right; width: 185px;\">" +
                "<p>Página #X de #Y</p>" +
                "</div>";
    }

    private static int contaPaginas(String html, String texto) {
        int count = 0;
        int index = 0;
        while ((index = html.indexOf(texto, index)) != -1) {
            count++;
            index += texto.length();
        }
        return count;
    }

    private static String substituiNumeracaoPaginas(String html, int totalPaginas,
                                                    String texto_numero_pagina,
                                                    String texto_pagina_total) {

        for (int i = 1; i <= totalPaginas; i++) {
            html = html.replaceFirst(texto_numero_pagina, String.valueOf(i));
        }

        html = html.replaceAll(texto_pagina_total, String.valueOf(totalPaginas));

        return html;
    }
}