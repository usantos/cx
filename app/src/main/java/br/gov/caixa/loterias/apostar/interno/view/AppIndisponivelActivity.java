package br.gov.caixa.loterias.apostar.interno.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class AppIndisponivelActivity extends AppCompatActivity {

    public Button buttonTentarNovamente;
    public Boolean isBuscaParametro;
    private AlertDialog loadViewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_indisponivel);

        isBuscaParametro = getIntent().getBooleanExtra("IS_BUSCA_PARAM", false);

        buttonTentarNovamente = findViewById(R.id.tenteNovamenteButton);
        buttonTentarNovamente.setOnClickListener(view -> {
            if (isBuscaParametro) {
                loadViewProgress = LoadingViewLoterias.show(this);

                final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(AppIndisponivelActivity.this);

                dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
                    @Override
                    public void onResponse(ParametrosSimulacaoResponse response) {
                        if (response.getPayload().getParametros().size() == 0) {
                            loadViewProgress.dismiss();
                            ViewUtils.alert(AppIndisponivelActivity.this, AppIndisponivelActivity.this.getString(R.string.erro_500));
                        } else {
                            loadViewProgress.dismiss();
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        ViewUtils.alert( AppIndisponivelActivity.this, error.getMessage());
                    }
                });
            } else {
                loadViewProgress = LoadingViewLoterias.show(this);

                AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
                    @Override
                    public void onResponse(VersionResponse response) {
                        loadViewProgress.dismiss();
                        if (!response.getPayload()) {
                            ViewUtils.alert(AppIndisponivelActivity.this, AppIndisponivelActivity.this.getString(R.string.erro_500));
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        ViewUtils.alert( AppIndisponivelActivity.this, error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() { }
}
