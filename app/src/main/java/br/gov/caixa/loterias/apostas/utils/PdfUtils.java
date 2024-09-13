package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.net.Uri;
import android.print.PdfConverter;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;

/**
 * Created by joafilho on 16/04/2018.
 * Class PdfUtils
 */

public class PdfUtils {

    public static final String DIRETORIO_COMPROVANTE = "Comprovantes";
    public static final String ARQUIVO_COMPROV_PREMIO = "Premio";
    public static final String ARQUIVO_COMPROV_APOSTA = "Aposta";


    public static void createComprovanteApostaPdf(ComprovanteApostaDTO comprovanteApostaDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapeAposta(comprovanteApostaDTO, activity);

            // cria a estrutura de diretório
            File dir = new File(FileUtils.getDownloadsDir(activity), DIRETORIO_COMPROVANTE);
            dir.mkdirs();

            String pathArquivo = dir + "/" + ARQUIVO_COMPROV_APOSTA + ".pdf";
            createAndOpenPDF(activity, html, pathArquivo);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    public static void createComprovantePremioPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapePremio(detalhesPremioDTO, activity);

            // cria a estrutura de diretório
            File dir = new File(FileUtils.getDownloadsDir(activity), DIRETORIO_COMPROVANTE);
            dir.mkdirs();

            String pathArquivo = dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
            createAndOpenPDF(activity, html, pathArquivo);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    public static void createComprovantePremioPagoPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapePremioPago(detalhesPremioDTO, activity);

            // cria a estrutura de diretório
            File dir = new File(FileUtils.getDownloadsDir(activity), DIRETORIO_COMPROVANTE);
            dir.mkdirs();

            String pathArquivo = dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
            createAndOpenPDF(activity, html, pathArquivo);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    public static void createComprovanteHistoricoPremioPdf(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapePremioHistorico(detalhesHistoricoPremioDTO, activity);

            // cria a estrutura de diretório
            File dir = new File(FileUtils.getDownloadsDir(activity), DIRETORIO_COMPROVANTE);
            dir.mkdirs();

            String pathArquivo = dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
            createAndOpenPDF(activity, html, pathArquivo);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    public static void createAndOpenPDF(final AppCompatActivity appC, String html, String filePath) throws Exception {
        try {
            final File filePDF = new File(filePath);
            gerarPDF(html, filePDF, appC, () -> FileUtils.openFile(filePDF, "application/pdf", appC));

        } catch (Exception ex) {
            throw new Exception("Falha ao invocar PdfUtils.sharePDF(). Detalhes: " + ex);
        }
    }

    public static void gerarPDF(String html, File filePDF, AppCompatActivity context, PdfConverter.onWriteFinishedCallBack callBack) throws Exception {
        try {

            PdfConverter converter = PdfConverter.getInstance();
            converter.convert(context, html, filePDF, callBack);

        } catch (Exception exception) {
            exception.printStackTrace();

            throw new Exception(exception);
        }
    }

    public static Uri baixarPdf(String stringBase64, Activity activity) {
        return decodeBase64ToPdf(stringBase64, activity);
    }

    private static Uri decodeBase64ToPdf(String base64String, Activity activity) {
        try {
            byte[] pdfBytes = Base64.decode(base64String, Base64.DEFAULT);

            File file = new File(activity.getExternalFilesDir(null), "documento.pdf");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
                fos.flush();
            }

            Uri photoURI = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider", file);
            return photoURI;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
