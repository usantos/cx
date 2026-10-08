package br.gov.caixa.loterias.apostas.controllers;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.*;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

public class ConfirmadoInclusaoCarrinhoActivity extends LoteriasBaseAppActivity implements OnClickListener {
    public ParametroJogoDTO parametroSimulacao;
    private EstiloModalidadeMKP estilo;
    private ModalidadeEnum modalidade;
    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    private BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
    private ConstraintLayout background;
    private Boolean isMega30 = false;
    String numeroConcurso;
    String value;
    private final ActivityResultLauncher<Intent> carrinhoLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode()
                                == ResultadoNavegacaoAposta.RESULT_RESET) {

                            setResult(
                                    ResultadoNavegacaoAposta.RESULT_RESET
                            );
                            finish();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_carrinho);
        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.modalidade)), ParametroJogoDTO.class);
        isMega30 = EspecialUtils.isMega30(parametroSimulacao);

        try {
            barraTituloDTO = (BarraTituloDTO) getIntent().getSerializableExtra("barraTituloModel");
        }catch (Exception e){
            Log.e("ERROR", "Não foi possível adiquirir os elementos da barra de título");
        }
        ((TextView) findViewById(R.id.txtVoltarAoInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.btn_title_voltarAoInicio)));
        background = findViewById(R.id.corpo_background);

        modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));

        preencheBarraTitulo();
        configuraActivityIncluidoApostaCarrinho();
        fragmentSomadorCarrinho();
        configurarBackPress();
    }
    private void configurarBackPress() {
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        setResult(
                                ResultadoNavegacaoAposta.RESULT_RESET
                        );
                        finish();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (modalidade == null || value == null || numeroConcurso == null) {
            return;
        }

        AnalyticsHelper.getInstance().logViewScreenApostaValor(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.Tela.CART,
                ModalidadeEnum.fromString(modalidade),
                value,
                numeroConcurso
        );

        AnalyticsHelper.getInstance().logOperationSuccess(
                AnalyticsHelper.StatusParams.SUCESSSO,
                AnalyticsHelper.Tela.CART,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                ModalidadeEnum.fromString(modalidade),
                numeroConcurso,
                value,
                false,
                AnalyticsHelper.AnalyticsFields.APOSTA_SIMPLES
        );

    }

    @Nullable
    private String getNumeroConcurso() {
        if (parametroSimulacao == null) return null;
        if (parametroSimulacao.getConcurso() == null) return null;
        if (parametroSimulacao.getConcurso().getNumero() == null) return null;

        return String.valueOf(parametroSimulacao.getConcurso().getNumero());
    }

    private void preencheBarraTitulo() {
        if (modalidade.equals(ModalidadeEnum.COMBO)) {
            FragmentUtils.startFragmentBarraTituloCombo(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                    modalidade,
                    getCorEscura());
        } else {
            try{
                numeroConcurso = getNumeroConcurso();
                FragmentUtils.startFragmentBarraTituloSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                        modalidade,
                        numeroConcurso,
                        getDataSorteio(),
                        parametroSimulacao.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)),
                        getCorClara(), true);
                        //getCorClara(), false);

            }catch (Exception e){
                if (barraTituloDTO != null && (barraTituloDTO.getDataSorteio() != null && barraTituloDTO.getNumeroConcurso() != null)){
                    FragmentUtils.startFragmentBarraTituloSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                            modalidade,
                            barraTituloDTO.getNumeroConcurso().toString(),
                            barraTituloDTO.getDataSorteio().toString(),
                            barraTituloDTO.isEspecial(),
                            getCorClara(), false);
                }
                else{
                    FragmentUtils.startFragmentBarraTituloSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                            modalidade,
                            "",
                            "",
                            false,
                            getCorClara(), false);
                }

            }
        }
    }

    private int getCorClara() {
        return new EstiloModalidadeMKP(modalidade, isMega30).getCorClara();
    }
    private int getCorEscura() {
        return new EstiloModalidadeMKP(modalidade, isMega30).getCorEscura();
    }

    private String getDataSorteio() {
        String dataSorteio = "";
        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            dataSorteio = formatterDateFinal.format(dataFormatada);
        } catch (ParseException e) {
        }
        return dataSorteio;
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(
                ResultadoNavegacaoAposta.RESULT_RESET
        );
        finish();
        return true;
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment,R.id.id_fg_somador_carrinho,"TELA CONFIRMACAO INCLUSAO");
        }
    }

    private void configuraActivityIncluidoApostaCarrinho() {
        BigDecimal valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));

        configuraLayoutTela(modalidade);
        TextView valorApostaText = findViewById(R.id.valorApostaConfirmacao);
        value = ViewUtils.getMoedaFormat(valorAposta);
        if (valorAposta.doubleValue() > 999.99){
            TextViewUtils.mudarTamanhoPorPorcentagem(valorApostaText, -30);
        }
        ViewUtils.setMoedaFormatHtml( valorAposta, valorApostaText );

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        String valorCarrinho = ViewUtils.getMoedaFormat(sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta());
        TextView textObservacaoConfirmacao = findViewById(R.id.textObservacaoConfirmacao);
        BigDecimal valorMinimo =  sessaoUsuario.getValorMinimimoAposta();
        if(valorCarrinho != null && valorMinimo != null && valorMinimo.compareTo(BigDecimal.ZERO) > 0){
            textObservacaoConfirmacao.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.infoValorMinimoCarrinho) + getResources().getString(R.string.underline_com_espaco) + valorCarrinho + getResources().getString(R.string.underline)));
            textObservacaoConfirmacao.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
        }else{
            textObservacaoConfirmacao.setVisibility(View.GONE);
        }

        TextView textTituloTela = findViewById(R.id.textTituloTela);
        textTituloTela.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
        if (modalidade.equals(ModalidadeEnum.COMBO)) {
            textTituloTela.setText(getString(R.string.tituloComboFeito));
        }

        ImageView imageSetaApontandoCarrinho = findViewById(R.id.imageSetaApontandoCarrinho);
        imageSetaApontandoCarrinho.setImageDrawable(VectorUtils.getShape(R.drawable.rectangle_5_copy, estilo.getCorFonteFundoEscuro()));

        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);
        findViewById(R.id.botaoLinearLayoutVoltarPrincipal).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        estilo = new EstiloModalidadeMKP(modalidadeEnum, isMega30);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        background.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorClara()));
        //getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        //gd.setStroke(2, Color.WHITE);
        gd.setStroke(2, estilo.getCorFonteFundoEscuro());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmarInclusaoApostaLayoutCarrinhoApostas: {
                AlertDialogUtils.show(this);
                ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        AlertDialogUtils.dismiss();
                        Bundle args = new Bundle();
                        args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                        Intent intent = new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtras(args);
                        carrinhoLauncher.launch(intent);
                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ConfirmadoInclusaoCarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialogUtils.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            Intent intent = new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true);
                            carrinhoLauncher.launch(intent);
                        } else {
                            RedirectNetwork.checkRedirect( error, ConfirmadoInclusaoCarrinhoActivity.this );
                        }
                    }
                });
            }
            break;
            case R.id.botaoLinearLayoutVoltarPrincipal: {
                //PrincipalActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                Intent intent = IntentUtil.getIntentOrigemDestino(this, PrincipalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("ORIGEM_TELA", "CARRINHO_INC");
                startActivity(intent);
            }
            break;
        }
    }
}
