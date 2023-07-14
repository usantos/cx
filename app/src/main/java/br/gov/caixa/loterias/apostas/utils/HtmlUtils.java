package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.androidannotations.annotations.EBean;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import br.gov.caixa.loterias.apostas.view.custom.PartidaViewDetalhesResultado;

/**
 * Created by joafilho on 16/04/2018.
 * Class HtmlUtils
 */

@EBean
public class HtmlUtils {

    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE = "htmls/comprovante_cabecalho_rodape.html";
    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE_PREMIO = "htmls/comprovante_cabecalho_rodape_premio.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO = "htmls/comprovante_lista_corpo_numerico.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA = "htmls/comprovante_lista_corpo_loteca.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTOGOL = "htmls/comprovante_lista_corpo_lotogol.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_SUPER_7 = "htmls/comprovante_lista_corpo_super7.html";
    private static final String FILE_PATH_COMPROVANTE_CORPO_PREMIO_HISTORICO = "htmls/comprovante_corpo_premio_historico.html";

    private static final String HEADER_TITLE_APP_TXT = "{header_title_app_txt}";
    private static final String HEADER_TIPO_COMPROVANTE_TXT = "{header_tipo_comprovante_txt}";
    private static final String BASE_64_LOGO = "{base_64_logo}";
    private static final String HEADER_COLUNA1_LINHA1_VALOR = "{header_coluna1_linha1_valor}";
    private static final String HEADER_COLUNA2_LINHA1_VALOR = "{header_coluna2_linha1_valor}";
    private static final String HEADER_NOME = "{header_nome}";
    private static final String HEADER_CPF = "{header_cpf}";
    private static final String HEADER_COLUNA2_LINHA1_TITULO = "{header_coluna2_linha1_titulo}";
    private static final String HEADER_COLUNA1_LINHA2_VALOR = "{header_coluna1_linha2_valor}";
    private static final String HEADER_COLUNA2_LINHA2_TEXTO = "{header_coluna2_linha2_texto}";
    private static final String HEADER_COLUNA2_LINHA2_VALOR = "{header_coluna2_linha2_valor}";
    private static final String HEADER_COLUNA1_LINHA3_VALOR = "{header_coluna1_linha3_valor}";
    private static final String HEADER_COLUNA2_LINHA3_TEXTO = "{header_coluna2_linha3_texto}";
    private static final String HEADER_COLUNA2_LINHA3_VALOR = "{header_coluna2_linha3_valor}";
    private static final String BODY_TITULO = "{body_titulo}";
    private static final String BODY_LISTA_ITENS_APOSTA = "{body_lista_itens_aposta}";
    private static final String BODY_INFORMACAO_APOSTA = "{body_informacao_aposta}";
    private static final String BODY_PARTIDA1 = "{body_partida1}";
    private static final String BODY_EMPATE = "{body_empate}";
    private static final String BODY_PARTIDA2 = "{body_partida2}";
    private static final String BODY_CONTEUDO = "{body_conteudo}";
    private static final String BASE_64_CODIGO_BARRAS_APOSTA = "{base_64_codigo_barras}";
    private static final String BODY_CODIGO_NSBI_APOSTA = "{body_codigo_nsbi}";
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
        String dataSorteio = DateUtils.getDateToString(
                DateUtils.stringToDate(comprovanteApostaDTO.getDataSorteio(), DateUtils.PATTERN_DD_MMM_YYYY)
                , DateUtils.PATTERN_DDMM_YYYY);

        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE, context);
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_aposta, modalidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, comprovanteApostaDTO.getAposta().getDataEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_NOME, nomeUsuario);
        html = ViewUtils.replaceNullable(html, HEADER_CPF, AppUtils.mascaraCPF(DadosUsuarioBO.obterCpf()));
        if(comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas()!= null && comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas() > 0){
            int concursoFinal = (comprovanteApostaDTO.getAposta().getConcursoInicial() + comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas()) - 1;
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concursos");
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, comprovanteApostaDTO.getAposta().getConcursoInicial() +
                    " a "+
                    concursoFinal);
        } else {
            html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(comprovanteApostaDTO.getAposta().getConcursoInicial()));
            html = ViewUtils.replaceNullable(html,HEADER_COLUNA2_LINHA1_TITULO,"Concurso");
        }
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, comprovanteApostaDTO.getAposta().getHoraEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_data_sorteio));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, dataSorteio);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, modalidade);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_valor_aposta));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getValor()));
        html = ViewUtils.replaceNullable(html, BODY_TITULO, modalidade);
        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(comprovanteApostaDTO.getAposta(), context));
        //html = ViewUtils.replaceNullable(html, BODY_CONTEUDO, getBodyAposta(comprovanteApostaDTO, context));
        html = ViewUtils.replaceNullable(html, BASE_64_CODIGO_BARRAS_APOSTA, getBarcode(comprovanteApostaDTO.getNsbi()));
        html = ViewUtils.replaceNullable(html, BODY_CODIGO_NSBI_APOSTA, comprovanteApostaDTO.getNsbi());
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

    private static String getBodyList(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta, Context context) {

        StringBuilder sbHtmlBodyList = new StringBuilder();
        String htmlBodyList = "";
        switch (aposta.getModalidade()) {
            case LOTECA:
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA, context);
                for (PartidaLotecaDTO partidaLotecaDTO : aposta.getPartidasLoteca()) {
                    EquipeDTO equipe1 = partidaLotecaDTO.getEquipe1();
                    EquipeDTO equipe2 = partidaLotecaDTO.getEquipe2();

                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA1,
                                                             context.getString(R.string.label_corpo_equipe, equipe1.getVitoria() ? PONTO_MARCADOR : " ", Utils.getEquipeComUf(partidaLotecaDTO.getEquipe1()) + " " + partidaLotecaDTO.getLegendaAbreviada()));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_EMPATE,
                                                             context.getString(R.string.label_corpo_equipe, partidaLotecaDTO.getEmpate() ? PONTO_MARCADOR : " ",
                                                                               context.getString(R.string.label_empate)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA2,
                                                             context.getString(R.string.label_corpo_equipe, equipe2.getVitoria() ? PONTO_MARCADOR : " ", Utils.getEquipeComUf(partidaLotecaDTO.getEquipe2()) + " " + partidaLotecaDTO.getLegendaAbreviada()));

                    sbHtmlBodyList.append(htmlBodyList);
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA, context);
                }
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
                return sbHtmlBodyList.toString();
            default:
                String numerosSelecionados = aposta.getListaNumerosSelecionados().toString().replaceAll("[\\[,\\]]", "");
                if (numerosSelecionados.contains("100")){
                    numerosSelecionados = numerosSelecionados.replace("100", "00");
                }
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA, numerosSelecionados);

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
                } else if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && aposta.getTrevosSelecionados() != null){
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA,
                                                             context.getString(R.string.trevos) + " " +
                                                                     getTrevosText(aposta));
                    sbHtmlBodyList.append(htmlBodyList);
                }
                return sbHtmlBodyList.toString();
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
        for (Integer num: numeros) {
            string += String.format(
                    "<span style=\"color: #64778a; width:22px; height:22px; margin-top: 7px; display: flex; justify-content: center; align-items: center; border-radius: 11px; border: 1px solid #64778a;\">%d</span>",
                    num);
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
            e.printStackTrace();
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
            BitMatrix bitMatrix = multiFormatWriter.encode(valor, BarcodeFormat.CODE_128, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            return Base64.encodeToString(ViewUtils.bitmapToByte(bitmap), Base64.DEFAULT);
        }
        return "";
    }
}