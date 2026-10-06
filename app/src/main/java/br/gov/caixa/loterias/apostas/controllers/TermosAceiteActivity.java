package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class TermosAceiteActivity extends LoteriasBaseAppActivity {

    private WebView webViewConteudo;
    private String termoHtml;
    private ConstraintLayout clTermoAceite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViews();
        setWebview();
        getTermo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AlertDialogUtils.isShow()){
            AlertDialogUtils.dismiss();
        }
    }

    private void setWebview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webViewConteudo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(getResources().getString(R.string.mailto_dois_pontos))) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(intent, getString(R.string.enviando_email)));
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
            }
        });

        //webViewConteudo.getSettings().setJavaScriptEnabled(true);
        //webViewConteudo.getSettings().setStandardFontFamily("caixa_std_regular");
    }

    private void setViews() {
        setContentView(R.layout.activity_termos_aceite);
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.termos_de_uso_sem_));
        textTitulo.setHint(getString(R.string.titulo));
        ImageButton customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onBackPressed());
        webViewConteudo = findViewById(R.id.webViewConteudo2);
        clTermoAceite = findViewById(R.id.cl_termo_aceite2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            setViews();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getTermo() {
        AlertDialogUtils.show(this);
        DadosCorporativosSilceBO.getInstance().termo(new RequestListener<TermoDeUsoDTOResponse>() {
            @Override
            public void onResponse(TermoDeUsoDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosAceiteActivity.this);
                }
                String corpoHtml = response.getPayload().getConteudo();
                String base64Font = "";
                try {
                    InputStream is = getAssets().open("fonts/caixa_std_regular.ttf");
                    base64Font = convertToBase64(is);
                } catch (IOException e) {
                }
                termoHtml = "<html>" +
                        "<head>" +
                        "<style type=\"text/css\">" +
                        "@font-face {" +
                        "    font-family: 'FonteCaixa';" +
                        "    src: url('data:font/ttf;base64,"+base64Font+"');" +
                        "}" +
                        "body, p, div, span, h1, h2, h3, h4, h5, h6, a {" +
                        "    font-family: 'FonteCaixa' !important;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        corpoHtml +
                        "</body>" +
                        "</html>";

                webViewConteudo.getSettings().setJavaScriptEnabled(true);
                //webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
                selectTermoUso();
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_termos_de_uso));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, TermosAceiteActivity.this);
            }
        });
    }

    private void selectTermoUso() {
        clTermoAceite.setVisibility(View.VISIBLE);
        webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
    }

    private String convertToBase64(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int byteRead;
        try {
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
        } catch (IOException e) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
        return "";
    }
}
