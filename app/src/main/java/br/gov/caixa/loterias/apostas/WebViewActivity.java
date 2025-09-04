package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class WebViewActivity extends LoteriasBaseAppActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(getString(R.string.bundle_key_url));

        myWebView = findViewById(R.id.webview);

        myWebView.setWebViewClient(webViewClient());
        myWebView.loadUrl(url);
    }

    private WebViewClient webViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                }
                if (url.startsWith("intent")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            myWebView.loadUrl(fallbackUrl);
                            return true;
                        }
                    } catch (URISyntaxException e) {
                        Log.d("WEBVIEW ERROR", "Nao foi possivel ler o endereco " + url);
                    }
                    return true;
                }
                return true;
            }
        };
    }
}