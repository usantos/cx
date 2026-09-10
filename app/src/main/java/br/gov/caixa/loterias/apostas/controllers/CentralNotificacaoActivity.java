package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

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

public class CentralNotificacaoActivity extends LoteriasBaseAppActivity {

    private Button btnMarcarTodas, btnExcluirTodas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_notificacao);

        setTitle();
        setViews();
        desabilitaBtn(btnMarcarTodas);
        desabilitaBtn(btnExcluirTodas);
        getServico();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AlertDialogUtils.isShow()){
            AlertDialogUtils.dismiss();
        }
    }

    private void setTitle() {
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.central_notificacoes));
        textTitulo.setHint(getString(R.string.titulo));
        ImageView imageTrevo = findViewById(R.id.icon_loterias);
        imageTrevo.setVisibility(View.VISIBLE);
    }

    private void setViews() {
        btnMarcarTodas = findViewById(R.id.btnMarcarTodas);
        btnExcluirTodas = findViewById(R.id.btnExcluirTodas);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getServico() {
        //AlertDialogUtils.show(this);

    }

    private void habilitaBtn(Button btn) {
        btn.setBackground(getDrawable(R.drawable.btn_rounded_azul));
        btn.setTextColor(getColor(R.color.branco));
    }
    private void desabilitaBtn(Button btn) {
        btn.setBackground(getDrawable(R.drawable.btn_rounded_disable));
        btn.setTextColor(getColor(R.color.cinza70));
    }
}
