package br.gov.caixa.loterias.apostas.controllers;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.zxing.WriterException;

import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

import static br.gov.caixa.loterias.apostas.view.activity.ResultadoApostaConfirmadaActivity.CODIGO_RESGATE;
import static br.gov.caixa.loterias.apostas.view.activity.ResultadoApostaConfirmadaActivity.COMPROVANTE;
import static br.gov.caixa.loterias.apostas.view.activity.ResultadoApostaConfirmadaActivity.DETALHES;

public class ResgatePremioLotericaActivity extends LoteriasAppActivity {

    private static Integer SEG_INICIO_COR = 60;
    private static Integer SEG_INICIO_ANIMACAO = 10;
    private static Integer SEG_INICIO_VIBRACAO = 3;

    private TextView tvTimer, tvDescQrCode, tvTopico3;
    private Button btnNovaAposta;
    private ImageView ivQrCode;
    private CodigoResgateDTO codigoResgateDTO;
    private DetalhesPremioDTO detalhesPremioDTO;
    private ComprovanteApostaDTO comprovante;
    private Integer tempoQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgate_premio_loterica);
        configToolbar(R.id.toolbar);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            comprovante = (ComprovanteApostaDTO) bundle.getSerializable(COMPROVANTE);
            detalhesPremioDTO = (DetalhesPremioDTO) bundle.getSerializable(DETALHES);
            codigoResgateDTO = (CodigoResgateDTO) bundle.getSerializable(CODIGO_RESGATE);
        }
    }

    private void setaViews(){
        tvTimer = findViewById(R.id.tv_timer);
        tvTopico3 = findViewById(R.id.tv_topico_3);
        tvDescQrCode = findViewById(R.id.tv_desc_qr_code);
        btnNovaAposta = findViewById(R.id.btn_nova_aposta);
        ivQrCode = findViewById(R.id.iv_qr_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.label_loterica_titulo)));
        tvTopico3.setText(ViewUtils.textColor(this,getResources().getString(R.string.rsgt_lt_msg_info_3),R.color.verde_card));
    }

    private void setaMetodos(){
        atualizaCodigoResgate();
        btnNovaAposta.setOnClickListener( v ->atualizaCodigoResgate());
    }

    private void configuraTimer(Integer tempoQrCode){
        try {
            generateQrCode();
        } catch (WriterException e) {
        }
        String textoValido;
        AnimatorSet scaleIn, scaleOut;

        textoValido = getResources().getString(R.string.rsgt_lt_msg_qr_valido).replace(getResources().getString(R.string.chave_tempo_chave),tempoQrCode.toString());

        scaleIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scale_in_anim);
        scaleOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scale_out_anim);
        scaleIn.setTarget(tvTimer);
        scaleOut.setTarget(tvTimer);

        configDescQrCode(textoValido,R.color.pretoLinhaFormNovoTipoPagamento);
        trocaVisibilidadeTimer();

        new CountDownTimer(TimeUnit.MINUTES.toMillis(tempoQrCode), 1000) {
            public void onTick(long millisegs) {
                long min = TimeUnit.MILLISECONDS.toMinutes(millisegs) % TimeUnit.HOURS.toMinutes(1);
                long seg = TimeUnit.MILLISECONDS.toSeconds(millisegs) % TimeUnit.MINUTES.toSeconds(1);
                long minSeg = TimeUnit.MINUTES.toSeconds(min) + seg;
                String tempo = String.format(getResources().getString(R.string.mm_ss_percent_d2),min,seg);
                if(minSeg <= SEG_INICIO_COR){
                    tvTimer.setTextColor(getResources().getColor(R.color.mp_color_red_error));
                }
                if(minSeg <= SEG_INICIO_ANIMACAO) {
                    scaleIn.start();
                    scaleOut.start();
                    if(minSeg  <= SEG_INICIO_VIBRACAO){
                        Utils.vibra();
                    }
                }
                tvTimer.setText(tempo);
            }

            public void onFinish() {
                scaleIn.start();
                tvTimer.setTextColor(getResources().getColor(R.color.verde_card));
                configDescQrCode(getResources().getString(R.string.rsgt_lt_msg_qr_invalido),R.color.mp_error_red_pink);
                trocaVisibilidadeTimer();
            }
        }.start();
    }

    private void generateQrCode() throws WriterException {
        QRGEncoder qrgEncoder = new QRGEncoder(codigoResgateDTO.getQrCode(), null, QRGContents.Type.TEXT, ViewUtils.getSmallSizeQrCode(this));
        ivQrCode.setImageBitmap(qrgEncoder.encodeAsBitmap());
        ivQrCode.setVisibility(View.VISIBLE);
    }

    private void configDescQrCode(String texto, int idCor){
        tvDescQrCode.setText(texto);
        tvDescQrCode.setTextColor(getResources().getColor(idCor));
    }

    private void trocaVisibilidadeTimer(){
        if(tvTimer.getVisibility() == View.GONE){
            tvTimer.setVisibility(View.VISIBLE);
            btnNovaAposta.setVisibility(View.GONE);
        }else {
            tvTimer.setVisibility(View.GONE);
            btnNovaAposta.setVisibility(View.VISIBLE);
        }
    }

    protected void atualizaCodigoResgate() {
        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().getApostaGerarCodigoResgate(detalhesPremioDTO.getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
            @Override
            public void onResponse(CodigoResgateDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ResgatePremioLotericaActivity.this);
                }
                codigoResgateDTO = response.getPayload();
                configuraTimer(codigoResgateDTO.getValidade());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ResgatePremioLotericaActivity.this);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
