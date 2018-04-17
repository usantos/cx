package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.view.View;

import org.androidannotations.annotations.EBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 16/04/2018.
 * Class HtmlUtils
 */

@EBean
public class HtmlUtils {

    private static final String FILE_PATH_COMPROVANTE_CABECALHO_RODAPE = "htmls/comprovante_cabecalho_rodape.html";

    private static final String HEADER_TITLE_APP_TXT = "{header_title_app_txt}";
    private static final String HEADER_TIPO_COMPROVANTE_TXT = "{header_tipo_comprovante_txt}";
    private static final String HEADER_COLUNA1_LINHA1_VALOR = "{header_coluna1_linha1_valor}";
    private static final String HEADER_COLUNA2_LINHA1_VALOR = "{header_coluna2_linha1_valor}";
    private static final String HEADER_COLUNA1_LINHA2_VALOR = "{header_coluna1_linha2_valor}";
    private static final String HEADER_COLUNA2_LINHA2_TEXTO = "{header_coluna2_linha2_texto}";
    private static final String HEADER_COLUNA2_LINHA2_VALOR = "{header_coluna2_linha2_valor}";
    private static final String HEADER_COLUNA1_LINHA3_VALOR = "{header_coluna1_linha3_valor}";
    private static final String HEADER_COLUNA2_LINHA3_TEXTO = "{header_coluna2_linha3_texto}";
    private static final String HEADER_COLUNA2_LINHA3_VALOR = "{header_coluna2_linha3_valor}";
    private static final String BODY_TITULO = "{body_titulo}";

    public String getComprovanteCabecalhoRodapeAposta(Context context) {
        String html = getContentOfFile(FILE_PATH_COMPROVANTE_CABECALHO_RODAPE, context);
        html = ViewUtils.replaceNullable(html, HEADER_TITLE_APP_TXT, context.getString(R.string.app_name));
        html = ViewUtils.replaceNullable(html, HEADER_TIPO_COMPROVANTE_TXT, context.getString(R.string.label_comprovante_aposta, "Mega da Virada"));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA1_VALOR, "20/02/2018");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA1_VALOR, "1234");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA2_VALOR, "13:00");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_TEXTO, context.getString(R.string.label_data_sorteio));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA2_VALOR, "20/03/2018");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA1_LINHA3_VALOR, "Mega da Virada");
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_TEXTO, context.getString(R.string.label_valor_aposta));
        html = ViewUtils.replaceNullable(html, HEADER_COLUNA2_LINHA3_VALOR, ViewUtils.getMoedaFormat(BigDecimal.TEN));
        html = ViewUtils.replaceNullable(html, BODY_TITULO, "Mega da Virada");

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
}
