package br.gov.caixa.loterias.apostar.interno.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.print.PdfConverter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.File;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTO;

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

    @Bean
    protected HtmlUtils htmlUtils;

    public void createComprovanteApostaPdf(ComprovanteApostaDTO comprovanteApostaDTO, AppCompatActivity activity) {
        if (checkForPermission(activity)) {
            try {
                String html = htmlUtils.getComprovanteCabecalhoRodapeAposta(comprovanteApostaDTO, activity);

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

    public void createComprovantePremioPdf(DetalhesPremioDTO detalhesPremioDTO, AppCompatActivity activity) {
        if (checkForPermission(activity)) {
            try {
                String html = htmlUtils.getComprovanteCabecalhoRodapePremio(detalhesPremioDTO, activity);

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
                String html = htmlUtils.getComprovanteCabecalhoRodapePremioHistorico(detalhesHistoricoPremioDTO, activity);

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

    private boolean checkForPermission(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
        } else {
            return true;
        }
        return false;
    }

    public void createAndOpenPDF(final AppCompatActivity appC, String html, String filePath) throws Exception {
        try {
            final File filePDF = new File(filePath);
            gerarPDF(html, filePDF, appC, new PdfConverter.onWriteFinishedCallBack() {
                @Override
                public void onWriteFinished() {
                    FileUtils.openFile(filePDF, "application/pdf", appC);
                }
            });

        } catch (Exception ex) {
            throw new Exception("Falha ao invocar PdfUtils.sharePDF(). Detalhes: " + ex);
        }
    }

    public void gerarPDF(String html, File filePDF, AppCompatActivity context, PdfConverter.onWriteFinishedCallBack callBack) throws Exception {
        try {

            PdfConverter converter = PdfConverter.getInstance();
            converter.convert(context, html, filePDF, callBack);

        } catch (Exception exception) {
            exception.printStackTrace();

            throw new Exception(exception);
        }
    }
}
