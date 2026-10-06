package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import java.util.concurrent.atomic.AtomicBoolean;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoResponse;
import br.gov.caixa.loterias.apostas.model.model.LoginSilceModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class AutoSuspensaoActivity extends LoteriasBaseAppActivity {
    private TextView tvSuspensao1,tvSuspensao2,tvSuspensao3,tvSuspensao4;
    private TextView tvPergunta;
    private RadioGroup rgAutosuspensao1,rgAutosuspensao2;
    private CheckBox chkSuspensaoCiente;
    private Button btnConfirmar;
    //private Button btnEntendi;
    private Button btnDesistir, btnSuspender;
    private CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autosuspensao);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.autosuspensao_suspensao))));

        configurar();
        desabilitaConfirmar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void configurar() {
        cardView = findViewById(R.id.cardSuspensao);
        tvSuspensao1 = findViewById(R.id.tvSuspensao1);
        tvSuspensao2 = findViewById(R.id.tvSuspensao2);
        tvSuspensao3 = findViewById(R.id.tvSuspensao3);
        tvSuspensao4 = findViewById(R.id.tvSuspensao4);
        tvPergunta   = findViewById(R.id.tvPerguntaSuspensao);

        rgAutosuspensao1 = findViewById(R.id.rgSuspensao1);
        rgAutosuspensao2 = findViewById(R.id.rgSuspensao2);

        btnDesistir = findViewById(R.id.btnDesistir);
        btnDesistir.setOnClickListener(v -> {
            finish();
        });

//        btnEntendi = findViewById(R.id.btnSuspensaoEntendi);
//        btnEntendi.setOnClickListener(v -> {
//            finish();
//        });

        btnSuspender = findViewById(R.id.btnSuspender);
        btnSuspender.setOnClickListener(v -> {
            salvarSuspensao();
        });

        btnConfirmar = findViewById(R.id.btnSuspensaoConfimar);
        btnConfirmar.setOnClickListener(v -> {
            clickConfirmar();
        });

        chkSuspensaoCiente = findViewById(R.id.chkSuspensaoCiente);
        chkSuspensaoCiente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    habilitaConfirmar();
                } else {
                    desabilitaConfirmar();
                }
            }
        });

        AtomicBoolean isClearingCheck = new AtomicBoolean(false);
        //Controla RadioGroups
        RadioGroup.OnCheckedChangeListener listener = (group, checkedId) -> {

            if (isClearingCheck.get()) return;

            isClearingCheck.set(true);
            if (group == rgAutosuspensao1 && checkedId != -1) {
                rgAutosuspensao2.clearCheck();
            } else if (group == rgAutosuspensao2 && checkedId != -1) {
                rgAutosuspensao1.clearCheck();
            }
            isClearingCheck.set(false);

            if (checkedId != -1) {
                habilitaConfirmar();
            }
        };
        rgAutosuspensao1.setOnCheckedChangeListener(listener);
        rgAutosuspensao2.setOnCheckedChangeListener(listener);
    }

    private void clickConfirmar() {
        btnConfirmar.setVisibility(View.GONE);
        tvSuspensao1.setVisibility(View.GONE);
        tvSuspensao3.setVisibility(View.GONE);
        tvSuspensao4.setVisibility(View.GONE);
        tvPergunta.setVisibility(View.GONE);
        rgAutosuspensao1.setVisibility(View.GONE);
        rgAutosuspensao2.setVisibility(View.GONE);
        chkSuspensaoCiente.setVisibility(View.GONE);
        btnDesistir.setVisibility(View.VISIBLE);
        btnSuspender.setVisibility(View.VISIBLE);

        tvSuspensao2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String texto = getString(R.string.tv_confirma_suspensao);
        texto = texto.replace("XX", textoSelecionadoRB());
        tvSuspensao2.setText(ViewUtils.textCaixaSTDBold(this, texto));
        //diminuiCardView();
    }

    private void desabilitaConfirmar() {
        btnConfirmar.setEnabled(false);
        btnConfirmar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_rounded_cinza));
    }

    private void habilitaConfirmar() {
        //Habilita apenas se tiver RadioGroup selecionado e se chkSuspensaoCiente está checado
        if ((rgAutosuspensao1.getCheckedRadioButtonId() != -1 ||
            rgAutosuspensao2.getCheckedRadioButtonId() != -1) &&
            chkSuspensaoCiente.isChecked()) {
            btnConfirmar.setEnabled(true);
            btnConfirmar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_rounded_verdeazul));
        }
    }

    private String textoSelecionadoRB() {
        int selcionadoRg1 = rgAutosuspensao1.getCheckedRadioButtonId();
        if (selcionadoRg1 != -1) {
            RadioButton selecionado = findViewById(selcionadoRg1);
            return selecionado.getText().toString();
        }
        int selcionadoRg2 = rgAutosuspensao2.getCheckedRadioButtonId();
        if (selcionadoRg2 != -1) {
            RadioButton selecionado = findViewById(selcionadoRg2);
            return selecionado.getText().toString();
        }
        return null;
    }

    private int textoSelecionadoToHoras(String texto) {
        if (texto.equalsIgnoreCase(getString(R.string.tv_24_horas))) {
            return 24;
        } else if (texto.equalsIgnoreCase(getString(R.string.tv_48_horas))) {
            return 48;
        } else if (texto.equalsIgnoreCase(getString(R.string.tv_7_dias))) {
            return 168;
        } else if (texto.equalsIgnoreCase(getString(R.string.tv_15_dias))) {
            return 360;
        } else if (texto.equalsIgnoreCase(getString(R.string.tv_30_dias))) {
            return 720;
        }
        return 0;
    }

//    private void diminuiCardView() {
//        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
//        layoutParams.bottomMargin = 1100;
//        cardView.setLayoutParams(layoutParams);
//    }

    private void salvarSuspensao() {

        //*** Verificar usuario Logado***
        String cpf = DadosUsuarioBO.obterCpf();

        String textoSelecionado = textoSelecionadoRB();
        int horasSelecionada = textoSelecionadoToHoras(textoSelecionado);

        AutoSuspensaoRequest autoSuspensaoRequest = new AutoSuspensaoRequest(cpf, horasSelecionada);

        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().postAutoSuspensaoApostador(autoSuspensaoRequest, new RequestListener<AutoSuspensaoResponse>() {
            @Override
            public void onResponse(AutoSuspensaoResponse result) {
                AlertDialogUtils.dismiss();
                SessaoUsuario.getInstance().setSuspensaoTemporariaApostador(true);

                String texto = getString(R.string.tv_suspenso_ate_novo);
                String dataHora = DateUtils.calculaDataHoraFutura(horasSelecionada);
                texto = texto.replace("XX", dataHora.substring(0, 10));
                texto = texto.replace("YY", dataHora.substring(11));
                //texto = ViewUtils.textCaixaSTDBold(this, texto);

                DialogUtils.dialogEntendiListener(AutoSuspensaoActivity.this,
                        texto,

                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                AlertDialogUtils.show(AutoSuspensaoActivity.this);
                                new LoginSilceModel(AutoSuspensaoActivity.this).sair(new OnSilceListener<NetworkResponse>() {
                                    @Override
                                    public void success(NetworkResponse payload) {
                                        sairRedirecionar();
                                    }
                                    @Override
                                    public void error(VolleyError error) {
                                        sairRedirecionar();
                                    }

                                    private void sairRedirecionar() {
                                        AlertDialogUtils.dismiss();
                                        //**Aguarda 2 segundos para redirecionar
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                DadosUsuarioBO.updateUsuarioLogado(AutoSuspensaoActivity.this, false);
                                                Intent intent = new Intent(AutoSuspensaoActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, 2000); //2 segundos
                                    }
                                });

                            }
                        }
                );

//                btnDesistir.setVisibility(View.GONE);
//                btnSuspender.setVisibility(View.GONE);
//                btnEntendi.setVisibility(View.VISIBLE);
//                String texto = getString(R.string.tv_suspenso_ate);
//                texto = texto.replace("XX", textoSelecionado);
//                texto = texto.replace("YY", DateUtils.calculaDataHoraFutura(horasSelecionada));
//                tvSuspensao2.setText(texto);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, AutoSuspensaoActivity.this);
            }
        });
    }
}

