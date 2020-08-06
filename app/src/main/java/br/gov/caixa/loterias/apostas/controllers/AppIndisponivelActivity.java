package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class AppIndisponivelActivity extends AppCompatActivity {

    private Button buttonTentarNovamente;
    private Boolean isBuscaParametro;
    private AlertDialog loadViewProgress;
    private TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_indisponivel);

        txtVersion = findViewById(R.id.txtVersao);

        txtVersion.setText( TextUtils.concat( getResources().getString(R.string.v) ,BuildConfig.VERSION_NAME));

        isBuscaParametro = getIntent().getBooleanExtra(getResources().getString(R.string.extra_is_busca_param), false);

        buttonTentarNovamente = findViewById(R.id.tenteNovamenteButton);
        buttonTentarNovamente.setOnClickListener(view -> {
            if (isBuscaParametro) {
                loadViewProgress = LoadingViewLoterias.show(this);

                final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(AppIndisponivelActivity.this);

                dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
                    @Override
                    public void onResponse(ParametrosSimulacaoResponse response) {
                        if (response.getPayload().getParametros().isEmpty()) {
                            loadViewProgress.dismiss();
                            ViewUtils.alert(AppIndisponivelActivity.this, AppIndisponivelActivity.this.getString(R.string.nao_concluiu_operacao));
                        } else {
                            loadViewProgress.dismiss();
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        RedirectNetwork.checkRedirect(error, AppIndisponivelActivity.this);
                    }
                });
            } else {
                loadViewProgress = LoadingViewLoterias.show(this);

                AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
                    @Override
                    public void onResponse(VersionResponse response) {
                        loadViewProgress.dismiss();

                        LoginSP.limpar();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        RedirectNetwork.checkRedirect(error, AppIndisponivelActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() { }
}
