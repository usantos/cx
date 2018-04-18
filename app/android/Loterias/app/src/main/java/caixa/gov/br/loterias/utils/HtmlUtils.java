package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.util.Base64;
import android.view.View;

import org.androidannotations.annotations.EBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ComprovanteApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;

/**
 * Created by joafilho on 16/04/2018.
 * Class HtmlUtils
 */

@EBean
public class HtmlUtils {

    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE = "htmls/comprovante_cabecalho_rodape.html";

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

        return html;
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
}
