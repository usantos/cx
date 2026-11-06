package br.gov.caixa.loterias.apostas.utils;

import android.content.Intent;
import android.net.Uri;
import android.print.PdfConverter;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;

/**
 * Created by joafilho on 16/04/2018.
 * Class PdfUtils
 */

public class PdfUtils{
    public static final String DIRETORIO_COMPROVANTE = "Comprovantes";
    public static final String ARQUIVO_COMPROV_PREMIO = "Premio";
    public static final String ARQUIVO_COMPROV_APOSTA = "Aposta";

    public static void abrirPdf(Uri fileUri, AppCompatActivity context) {
        if (fileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            context.startActivity(intent);
        }
    }

    private static String getFileApostaPath(AppCompatActivity activity, String diretorio) throws Exception {
        // cria a estrutura de diretório
        File dir = new File(FileUtils.getDownloadsDir(activity), diretorio);
        dir.mkdirs();

        return dir + "/" + ARQUIVO_COMPROV_APOSTA + ".pdf";
    }


    public static Uri baixarPdfAposta(String stringBase64, AppCompatActivity activity) {
        Uri fileUri = null;
        try {
            File file = new File(getFileApostaPath(activity, DIRETORIO_COMPROVANTE));

            byte[] pdfBytes = Base64.decode(stringBase64, Base64.DEFAULT);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
                fos.flush();
            }

            fileUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider" , file);
        } catch (Exception ex) {
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }

        return fileUri;
    }

    private static String getFilePremioPath(AppCompatActivity activity, String diretorio) throws Exception {
        // cria a estrutura de diretório
        File dir = new File(FileUtils.getDownloadsDir(activity), diretorio);
        dir.mkdirs();

        return dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
    }

    public static Uri baixarPdfPremio(String stringBase64, AppCompatActivity activity) {
        Uri fileUri = null;
        try {
            File file = new File(getFilePremioPath(activity, DIRETORIO_COMPROVANTE));

            byte[] pdfBytes = Base64.decode(stringBase64, Base64.DEFAULT);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
                fos.flush();
            }

            fileUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider" , file);
        } catch (Exception ex) {
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }

        return fileUri;
    }

//codigo legado. Relacionado a geração dos comprovantes de aposta e de premio pelo App, quando não havia serviço de geração dos comprovantes.
    private static String getFilePath(AppCompatActivity activity, String diretorio) throws Exception {
        // cria a estrutura de diretório
        File dir = new File(FileUtils.getDownloadsDir(activity), diretorio);
        dir.mkdirs();

        return dir + "/" + ARQUIVO_COMPROV_APOSTA + ".pdf";
    }

        public static void createComprovanteApostaPdf(ComprovanteApostaDTO comprovanteApostaDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapeAposta(comprovanteApostaDTO, activity);
            createAndOpenPDF(activity, html, getFilePath(activity, DIRETORIO_COMPROVANTE));
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }


    public static void createComprovantePremioPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapePremio(detalhesPremioDTO, activity);
            createAndOpenPDF(activity, html, getFilePath(activity, ARQUIVO_COMPROV_PREMIO));
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    public static void createComprovantePremioPagoPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        try {
            String html = HtmlUtils.getComprovanteCabecalhoRodapePremioPago(detalhesPremioDTO, activity);
            createAndOpenPDF(activity, html, getFilePath(activity, ARQUIVO_COMPROV_PREMIO));
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
}
