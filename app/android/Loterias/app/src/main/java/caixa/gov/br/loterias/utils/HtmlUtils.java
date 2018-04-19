package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.androidannotations.annotations.EBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ComprovanteApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by joafilho on 16/04/2018.
 * Class HtmlUtils
 */

@EBean
public class HtmlUtils {

    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE = "htmls/comprovante_cabecalho_rodape.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO = "htmls/comprovante_lista_corpo_numerico.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA = "htmls/comprovante_lista_corpo_loteca.html";
    private static final String FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTOGOL = "htmls/comprovante_lista_corpo_lotogol.html";
    private static final String FILE_PATH_COMPROVANTE_CORPO_APOSTA = "htmls/comprovante_corpo_aposta.html";
    private static final String FILE_PATH_COMPROVANTE_CORPO_PREMIO = "htmls/comprovante_corpo_premio.html";

    private static final String HEADER_TITLE_APP_TXT = "{header_title_app_txt}";
    private static final String HEADER_TIPO_COMPROVANTE_TXT = "{header_tipo_comprovante_txt}";
    private static final String BASE_64_LOGO = "{base_64_logo}";
    private static final String HEADER_COLUNA1_LINHA1_VALOR = "{header_coluna1_linha1_valor}";
    private static final String HEADER_COLUNA2_LINHA1_VALOR = "{header_coluna2_linha1_valor}";
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
    private static final String BODY_CODIGO_NBU_APOSTA = "{body_nbu_valor}";

    private static final String PONTO_MARCADOR = "•";

    public String getComprovanteCabecalhoRodapeAposta(ComprovanteApostaDTO comprovanteApostaDTO, Context context) {

        String modadlidade = ModalidadeEnum.fromStringComprovante(comprovanteApostaDTO.getAposta().getModalidade());
        String dataSorteio = DateUtils.getDateToString(
                DateUtils.stringToDate(comprovanteApostaDTO.getDataSorteio(), DateUtils.PATTERN_DD_MMM_YYYY)
                , DateUtils.PATTERN_DDMM_YYYY);

        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE, context);
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_aposta, modadlidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, comprovanteApostaDTO.getAposta().getDataEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(comprovanteApostaDTO.getAposta().getConcursoAlvo()));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, comprovanteApostaDTO.getAposta().getHoraEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_data_sorteio));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, dataSorteio);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, modadlidade);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_valor_aposta));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, ViewUtils.getMoedaFormat(comprovanteApostaDTO.getAposta().getValor()));
        html = ViewUtils.replaceNullable(html, BODY_TITULO, modadlidade);
        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(comprovanteApostaDTO.getAposta(), context));
        html = ViewUtils.replaceNullable(html, BODY_CONTEUDO, getBodyAposta(comprovanteApostaDTO, context));

        return html;
    }

    public String getComprovanteCabecalhoRodapePremio(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, Context context) {

        String modadlidade = ModalidadeEnum.fromStringComprovante(detalhesHistoricoPremioDTO.getAposta().getModalidade());

        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE, context);
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_aposta, modadlidade));
        html = ViewUtils.replaceNullable(html, BASE_64_LOGO, getBase64Logo(context));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, detalhesHistoricoPremioDTO.getAposta().getDataEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, String.valueOf(detalhesHistoricoPremioDTO.getAposta().getConcursoAlvo()));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, detalhesHistoricoPremioDTO.getAposta().getHoraEfetivacao());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_local_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, detalhesHistoricoPremioDTO.getPremioDTO().getLocalPagamento());
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, modadlidade);
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_nsu_pagamento));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, String.valueOf(detalhesHistoricoPremioDTO.getPremioDTO().getNsuPagamento()));
        html = ViewUtils.replaceNullable(html, BODY_TITULO, modadlidade);
        html = ViewUtils.replaceNullable(html, BODY_LISTA_ITENS_APOSTA, getBodyList(detalhesHistoricoPremioDTO.getAposta(), context));
        html = ViewUtils.replaceNullable(html, BODY_CONTEUDO, getBodyPremio(detalhesHistoricoPremioDTO, context));

        return html;
    }

    private static String getBodyList(IdentificaoDeUmaApostaDas8Modalidades aposta, Context context) {

        StringBuilder sbHtmlBodyList = new StringBuilder();
        String htmlBodyList = "";
        switch (aposta.getModalidade()) {
            case LOTECA:
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_LOTECA, context);
                for (PartidaLotecaDTO partidaLotecaDTO : aposta.getPartidasLoteca()) {
                    EquipeDTO equipe1 = partidaLotecaDTO.getEquipe1();
                    EquipeDTO equipe2 = partidaLotecaDTO.getEquipe2();

                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA1,
                            context.getString(R.string.label_corpo_equipe, equipe1.getVitoria() ? PONTO_MARCADOR : " ",
                                    equipe1.getIndicadorSelecao() ? equipe1.getNome() + "/" + equipe1.getSiglaPais() : equipe1.getNome()));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_EMPATE,
                            context.getString(R.string.label_corpo_equipe, partidaLotecaDTO.getEmpate() ? PONTO_MARCADOR : " ",
                                    context.getString(R.string.label_empate)));
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_PARTIDA2,
                            context.getString(R.string.label_corpo_equipe, equipe2.getVitoria() ? PONTO_MARCADOR : " ",
                                    equipe2.getIndicadorSelecao() ? equipe2.getNome() + "/" + equipe2.getSiglaPais() : equipe2.getNome()));

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
            default:
                String numerosSelecionados = aposta.getNumerosSelecionados().toString().replaceAll("[\\[,\\]]", "");
                htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA, numerosSelecionados);

                sbHtmlBodyList.append(htmlBodyList);
                if (aposta.getTimeDoCoracao() != null) {
                    htmlBodyList = getContentOfFile(FILE_PATH_COMPROVANTE_LISTA_CORPO_NUMERICO, context);
                    htmlBodyList = ViewUtils.replaceNullable(htmlBodyList, BODY_INFORMACAO_APOSTA,
                            aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf());
                    sbHtmlBodyList.append(htmlBodyList);
                }
                return sbHtmlBodyList.toString();
        }
    }

    private static String getBodyAposta(ComprovanteApostaDTO comprovanteApostaDTO, Context context) {
        String htmlBody = getContentOfFile(FILE_PATH_COMPROVANTE_CORPO_APOSTA, context);
        htmlBody = ViewUtils.replaceNullable(htmlBody, BASE_64_CODIGO_BARRAS_APOSTA, getBase64Logo(context));
//        htmlBody = ViewUtils.replaceNullable(htmlBody, BASE_64_CODIGO_BARRAS_APOSTA, getBarcode(context, comprovanteApostaDTO.getNsbi()));
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_CODIGO_NSBI_APOSTA, comprovanteApostaDTO.getNsbi());

        return htmlBody;
    }

    private static String getBodyPremio(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, Context context) {
        String htmlBody = getContentOfFile(FILE_PATH_COMPROVANTE_CORPO_PREMIO, context);
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA1_LINHA1_VALOR_PREMIO, String.valueOf(detalhesHistoricoPremioDTO.getPremioDTO().getConcursos().size()));
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA2_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesHistoricoPremioDTO.getAposta().getValor()));
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_COLUNA3_LINHA1_VALOR_PREMIO, ViewUtils.getMoedaFormat(detalhesHistoricoPremioDTO.getPremioDTO().getValorLiquido()));
        htmlBody = ViewUtils.replaceNullable(htmlBody, BODY_CODIGO_NSBI_APOSTA, String.valueOf(detalhesHistoricoPremioDTO.getPremioDTO().getNsuPagamento()));
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

    private static String getBarcode(Context context, String valor){

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(valor, BarcodeFormat.EAN_13,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if(bitmap!= null){
            return Base64.encodeToString(bitmap.getNinePatchChunk(), Base64.DEFAULT);
        }

        return "";
    }
}
