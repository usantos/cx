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
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class AppIndisponivelActivity extends AppCompatActivity {

    private Button buttonTentarNovamente;
    private Boolean isBuscaParametro;
    private AlertDialog loadViewProgress;
    private TextView txtVersion, tvMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_indisponivel);

        setaViews();
        pegaExtras();
        setaMetodos();
    }

    private void setaViews(){
        txtVersion = findViewById(R.id.txtVersao);
        tvMensagem = findViewById(R.id.tv_mensagem);
        buttonTentarNovamente = findViewById(R.id.tenteNovamenteButton);

        txtVersion.setText( TextUtils.concat( getResources().getString(R.string.v) ,BuildConfig.VERSION_NAME));
        atualizaMsg();
    }

    private void pegaExtras() {
        isBuscaParametro = getIntent().getBooleanExtra(getResources().getString(R.string.extra_is_busca_param), false);
    }

    private void atualizaMsg() {
        if (!AppUtils.isNetworkAvailable(AppIndisponivelActivity.this)) {
            tvMensagem.setText(getResources().getString(R.string.seminternet));
        } else {
            tvMensagem.setText(getResources().getString(R.string.label_app_indisponivel));
        }
    }

    private void setaMetodos() {
        buttonTentarNovamente.setOnClickListener(view -> {
            atualizaMsg();
            if (isBuscaParametro) {
                loadViewProgress = LoadingViewLoterias.show(this);


                new ParametrosSimulacaoModel(AppIndisponivelActivity.this)
                        .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacaoResponse>() {
                            @Override
                            public void success(ParametrosSimulacaoResponse response) {
                                if (response.getPayload().getParametros().isEmpty()) {
                                    loadViewProgress.dismiss();
                                    ViewUtils.alert(AppIndisponivelActivity.this, AppIndisponivelActivity.this.getString(R.string.nao_concluiu_operacao));
                                } else {
                                    SessaoUsuarioUtil.atualizaParametrosSingleton(response);
                                    loadViewProgress.dismiss();
                                    finish();
                                }
                            }

                            @Override
                            public void error(VolleyError error) {
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
