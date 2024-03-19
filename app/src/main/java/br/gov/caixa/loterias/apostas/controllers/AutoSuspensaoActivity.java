package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoPerguntaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.BottomSheetFragment;

public class AutoSuspensaoActivity extends AppCompatActivity {

    private TextView tvSuspensao1,tvSuspensao2,tvSuspensao3,tvSuspensao4;
    private TextView tvPergunta;
    private RadioGroup rgAutosuspensao1,rgAutosuspensao2;
    private CheckBox chkSuspensaoCiente;
    private Button btnConfirmar;
    private Button btnEntendi;
    private Button btnDesistir, btnSuspender;
    private CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autosuspensao);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.autosuspensao_suspensao))));

        configurar();
        desabilitaConfirmar();
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

        btnEntendi = findViewById(R.id.btnSuspensaoEntendi);
        btnEntendi.setOnClickListener(v -> {
            finish();
        });

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
        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

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
        tvSuspensao2.setText(R.string.tv_confirma_suspensao);
        btnDesistir.setVisibility(View.VISIBLE);
        btnSuspender.setVisibility(View.VISIBLE);
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

    private void salvarSuspensao() {
//        AlertDialogUtils.show(this);
//        ApostaBO_.getInstance_(this).postAutoavaliacaoRespostas(listResposta, new RequestListener<AutoavaliacaoResponse>() {
//            @Override
//            public void onResponse(AutoavaliacaoResponse result) {
//                AlertDialogUtils.dismiss();
////                rgAutosuspensao.setVisibility(View.GONE);
////                if (verificaRespostasTudoNao()) {
////                    btnProximo.setVisibility(View.GONE);
////                } else {
////                    alteraBtnToAjuda();
////                }
////                alteraBtnToFechar();
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                AlertDialogUtils.dismiss();
//                RedirectNetwork.checkRedirect(error, AutoSuspensaoActivity.this);
//            }
//        });

        //cardView.set
        btnDesistir.setVisibility(View.GONE);
        btnSuspender.setVisibility(View.GONE);
        tvSuspensao2.setText(R.string.tv_suspenso_ate);
        btnEntendi.setVisibility(View.VISIBLE);
    }
}

