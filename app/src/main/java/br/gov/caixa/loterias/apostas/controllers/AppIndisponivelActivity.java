package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
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
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class AppIndisponivelActivity extends LoteriasBaseAppActivity {

    private Boolean isBuscaParametro;
    private AlertDialog loadViewProgress;
    private TextView txtVersion, tvMensagem;

    private TextView tvMensagem1, tvMensagem2;
    private Spinner ambienteSpinner;

    private View buttonTentarNovamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_indisponivel);
        setContentView(R.layout.activity_app_indisponivel_novo);

        pegaExtras();
        setaViews();
        atualizaMsg();
        setaMetodos();

        if (!(BuildConfig.FLAVOR.equals("prd"))){
            //Ambiente Selecao
            ambienteSpinner.setVisibility(View.VISIBLE);
            AmbienteEnum[] ambienteEnums = AmbienteEnum.values();
            ArrayAdapter<AmbienteEnum> adapter = new ArrayAdapter<>(AppIndisponivelActivity.this,
                    R.layout.spinner_item_ambiente, ambienteEnums);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambienteSpinner.setAdapter(adapter);
            //Carrega selecao salva no SharedPreferences
            String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO_ESTEIRA.name());
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
        DialogUtils.dialogTituloEntendiListener(AppIndisponivelActivity.this,
                "Mudança de Ambiente",
                "O aplicativo será encerrado para aplicar as mudanças de ambiente.",
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                }
        );
    }

    private void setaViews(){
        tvMensagem = findViewById(R.id.tv_mensagem);
        tvMensagem1 = findViewById(R.id.tv_mensagem1);
        tvMensagem2 = findViewById(R.id.tv_mensagem2);

        buttonTentarNovamente = findViewById(R.id.tenteNovamenteButton);
        ambienteSpinner = findViewById(R.id.spin_ambiente);

        txtVersion = findViewById(R.id.txtVersao);
        txtVersion.setText( TextUtils.concat( getResources().getString(R.string.v) ,BuildConfig.VERSION_NAME));
    }

    private void pegaExtras() {
        isBuscaParametro = getIntent().getBooleanExtra(getResources().getString(R.string.extra_is_busca_param), false);
    }

    private void atualizaMsg() {
        //Tela antiga activity_app_indisponivel
        if (tvMensagem != null) {
            if (!AppUtils.isNetworkAvailable(AppIndisponivelActivity.this)) {
                tvMensagem.setText(getResources().getString(R.string.seminternet));
            } else {
                tvMensagem.setText(getResources().getString(R.string.label_app_indisponivel));
            }
        }
        //Tela nova activity_app_indisponivel_novo
        if (tvMensagem1 != null) {
            if (!AppUtils.isNetworkAvailable(AppIndisponivelActivity.this)) {
                tvMensagem1.setText(getResources().getString(R.string.internet_instavel));
                tvMensagem2.setText(getResources().getString(R.string.verifique_conexao));
            } else {
                tvMensagem1.setText(getResources().getString(R.string.app_indisponivel));
                tvMensagem2.setText(getResources().getString(R.string.pedido_desculpas));
            }
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
                                    DialogUtils.dialogEntendi(AppIndisponivelActivity.this, getResources().getString(R.string.nao_concluiu_operacao));
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

}
