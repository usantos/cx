package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.print.PdfConverter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.androidannotations.annotations.EBean;

import java.io.File;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;

/**
 * Created by joafilho on 16/04/2018.
 * Class PdfUtils
 */

@EBean
public class PdfUtils {

    public static final String DIRETORIO_COMPROVANTE = "Comprovantes";
    public static final String ARQUIVO_COMPROV_PREMIO = "Premio";
    public static final String ARQUIVO_COMPROV_APOSTA = "Aposta";
    public static final int MY_WRITE_EXTERNAL_STORAGE = 10;



    public void createComprovanteApostaPdf(ComprovanteApostaDTO comprovanteApostaDTO, AppCompatActivity activity) {
        if (checkForPermission(activity)) {
            try {
                String html = HtmlUtils.getComprovanteCabecalhoRodapeAposta(comprovanteApostaDTO, activity);

                // cria a estrutura de diretório
                File dir = new File(FileUtils.getDownloadsDir(), DIRETORIO_COMPROVANTE);
                dir.mkdirs();

                String pathArquivo = dir + "/" + ARQUIVO_COMPROV_APOSTA + ".pdf";
                createAndOpenPDF(activity, html, pathArquivo);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void createComprovantePremioPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        if (checkForPermission(activity)) {
            try {
                String html = HtmlUtils.getComprovanteCabecalhoRodapePremio(detalhesPremioDTO, activity);

                // cria a estrutura de diretório
                File dir = new File(FileUtils.getDownloadsDir(), DIRETORIO_COMPROVANTE);
                dir.mkdirs();

                String pathArquivo = dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
                createAndOpenPDF(activity, html, pathArquivo);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createComprovanteHistoricoPremioPdf(DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO, AppCompatActivity activity) {
        if (checkForPermission(activity)) {
            try {
                String html = HtmlUtils.getComprovanteCabecalhoRodapePremioHistorico(detalhesHistoricoPremioDTO, activity);

                // cria a estrutura de diretório
                File dir = new File(FileUtils.getDownloadsDir(), DIRETORIO_COMPROVANTE);
                dir.mkdirs();

                String pathArquivo = dir + "/" + ARQUIVO_COMPROV_PREMIO + ".pdf";
                createAndOpenPDF(activity, html, pathArquivo);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(activity, "Ocorreu um erro inesperado, por favor tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static boolean checkForPermission(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
        } else {
            return true;
        }
        return false;
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
