package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AmbienteEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class AppIndisponivelActivity extends AppCompatActivity {

    private Button buttonTentarNovamente;
    private Boolean isBuscaParametro;
    private AlertDialog loadViewProgress;
    private TextView txtVersion, tvMensagem;
    private Spinner ambienteSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_indisponivel);

        pegaExtras();
        setaViews();
        atualizaMsg();
        logAppCenter();
        setaMetodos();

        if (!BuildConfig.FLAVOR.equals("prd")) {
            //Ambiente Selecao
            ambienteSpinner.setVisibility(View.VISIBLE);
            AmbienteEnum[] ambienteEnums = AmbienteEnum.values();
            ArrayAdapter<AmbienteEnum> adapter = new ArrayAdapter<>(AppIndisponivelActivity.this,
                    R.layout.spinner_item_ambiente, ambienteEnums);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambienteSpinner.setAdapter(adapter);
            //Carrega selecao salva no SharedPreferences
            String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO.name());
            AmbienteEnum ambienteAtual = AmbienteEnum.valueOf(ambienteSalvo);
            ambienteSpinner.setSelection(adapter.getPosition(ambienteAtual));
            //Listener salvar a selecao
            ambienteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AmbienteEnum selectedAmbiente = (AmbienteEnum) parent.getItemAtPosition(position);
                    SharedPreferencesUtils.setValor("AMBIENTE_SELECIONADO", selectedAmbiente.name());
                    if (!selectedAmbiente.name().equals(ambienteSalvo)) {
                        showAlertAndExit(selectedAmbiente);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        }

    private void showAlertAndExit(AmbienteEnum selectedAmbiete) {
        new android.app.AlertDialog.Builder(AppIndisponivelActivity.this)
                .setTitle("Mudança de Ambiente")
                .setMessage("O aplicativo será encerrado para aplicar as mudanças de ambeinte.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .show();
    }

    private void setaViews(){
        txtVersion = findViewById(R.id.txtVersao);
        tvMensagem = findViewById(R.id.tv_mensagem);
        buttonTentarNovamente = findViewById(R.id.tenteNovamenteButton);
        ambienteSpinner = findViewById(R.id.spin_ambiente);

        txtVersion.setText( TextUtils.concat( getResources().getString(R.string.v) ,BuildConfig.VERSION_NAME));
    }

    private void logAppCenter() {
        if (!AppUtils.isNetworkConnected(AppIndisponivelActivity.this)){

        } else if (isBuscaParametro){

        } else {

        }
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
                        .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                            @Override
                            public void success(ParametrosSimulacao payload) {
                                if (payload.getParametros().isEmpty()) {
                                    loadViewProgress.dismiss();
                                    ViewUtils.alert(AppIndisponivelActivity.this, AppIndisponivelActivity.this.getString(R.string.nao_concluiu_operacao));
                                } else {
                                    SessaoUsuarioUtil.atualizaParametrosSingleton(payload);
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

                AcessoBO.getInstance().getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
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
