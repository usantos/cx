package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

public class ConfirmadoInclusaoCarrinhoActivity extends AppCompatActivity implements OnClickListener {

    public ParametroJogoDTO parametroSimulacao;

    private EstiloModalidadeMKP estilo;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_carrinho);

        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.modalidade)), ParametroJogoDTO.class);
        ((TextView) findViewById(R.id.txtVoltarAoInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.btn_title_voltarAoInicio)));

        preencheBarraTitulo();
        configuraActivityIncluidoApostaCarrinho();
        fragmentSomadorCarrinho();
    }
    private void preencheBarraTitulo() {
        FragmentUtils.startFragmentBarraTituloSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                parametroSimulacao.getConcurso().getModalidade(),
                parametroSimulacao.getConcurso().getNumero().toString(), getDataSorteio(),
                parametroSimulacao.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)),
                getCor(), false);
    }

    private int getCor() {
        return new EstiloModalidade(parametroSimulacao.getConcurso().getModalidade()).getCorClara();
    }

    private String getDataSorteio() {
        String dataSorteio = "";
        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            dataSorteio = formatterDateFinal.format(dataFormatada);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataSorteio;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment,R.id.id_fg_somador_carrinho,"TELA CONFIRMACAO INCLUSAO");
        }
    }

    private void configuraActivityIncluidoApostaCarrinho() {
        ModalidadeEnum tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
        BigDecimal valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));

        configuraLayoutTela(tipoJogo);

        TextView valorApostaText = findViewById(R.id.valorApostaConfirmacao);
        valorApostaText.setText(ViewUtils.getMoedaFormat(valorAposta));

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        String valorCarrinho = ViewUtils.getMoedaFormat(sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta());
        TextView textObservacaoConfirmacao = findViewById(R.id.textObservacaoConfirmacao);
        textObservacaoConfirmacao.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.infoValorMinimoCarrinho) + getResources().getString(R.string.underline_com_espaco) + valorCarrinho + getResources().getString(R.string.underline)));
        textObservacaoConfirmacao.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoEscuro()));

        TextView textTituloTela = findViewById(R.id.textTituloTela);
        textTituloTela.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoEscuro()));

        ImageView imageSetaApontandoCarrinho = findViewById(R.id.imageSetaApontandoCarrinho);
        imageSetaApontandoCarrinho.setImageDrawable(VectorUtils.getShape(R.drawable.rectangle_5_copy, estilo.getCorFonteFundoEscuro()));


        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);
        findViewById(R.id.botaoLinearLayoutVoltarPrincipal).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        estilo = new EstiloModalidadeMKP(modalidadeEnum);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorEscura())));


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
                ApostaBO.getInstance().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        AlertDialogUtils.dismiss();
                        Bundle args = new Bundle();
                        args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                        startActivityForResult(new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtras(args),0);
                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ConfirmadoInclusaoCarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialogUtils.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            startActivityForResult((new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true)),0);
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
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            finish();
        }
    }
}
