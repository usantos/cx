package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

public class ConfirmadoInclusaoCarrinhoActivity extends AppCompatActivity implements OnClickListener {

    public ParametroJogoDTO parametroSimulacao;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_carrinho);

        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.modalidade)), ParametroJogoDTO.class);
        ((TextView) findViewById(R.id.txtVoltarAoInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.btn_title_voltarAoInicio)));

        invalidateOptionsMenu();
        configuraActivityIncluidoApostaCarrinho();
        fragmentSomadorCarrinho();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), "TELA CONFIRMACAO INCLUSAO");
            FragmentUtils.startFragment(getSupportFragmentManager(), R.id.id_fg_somador_carrinho, somadorCarrinhoFragment);
        }
    }

    private void configuraActivityIncluidoApostaCarrinho() {
        ModalidadeEnum tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
        BigDecimal valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));

        TextView valorApostaText = findViewById(R.id.valorApostaConfirmacao);
        valorApostaText.setText(ViewUtils.getMoedaFormat(valorAposta));

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        String valorCarrinho = ViewUtils.getMoedaFormat(sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta());
        TextView textObservacaoConfirmacao = findViewById(R.id.textObservacaoConfirmacao);
        textObservacaoConfirmacao.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.infoValorMinimoCarrinho) + getResources().getString(R.string.underline_com_espaco) + valorCarrinho + getResources().getString(R.string.underline)));

        configuraLayoutTela(tipoJogo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);
        findViewById(R.id.botaoLinearLayoutVoltarPrincipal).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        EstiloModalidade estilo = new EstiloModalidade(modalidadeEnum);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getResources().getString(R.string.underline) + ModalidadeEnum.fromString(modalidadeEnum) + getResources().getString(R.string.underline)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, estilo.getCorClara()));
        gd.setStroke(2, Color.WHITE);
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        configuraMenuItem(R.id.concursoAtual);
        configuraMenuItem(R.id.sorteioAtual);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itens_simular_actionbar_termos, menu);

        MenuItem concursoAtual = menu.findItem(R.id.concursoAtual);
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_titulo)).setText(getString(R.string.concurso));
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_descricao)).setText(parametroSimulacao.getConcurso().getNumero().toString());

        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));
        MenuItem dataSorteio = menu.findItem(R.id.sorteioAtual);

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            if (parametroSimulacao.getConcurso().getModalidade().toString() == "LOTECA"){
                ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.resultado));
            }else{
                ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.sorteio));
            }

            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_descricao)).setText(formatterDateFinal.format(dataFormatada));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_termos) {
            abrirTermos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configuraMenuItem(int id) {
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    private void abrirTermos() {
        Intent activity = new Intent(ConfirmadoInclusaoCarrinhoActivity.this, TermosUsoActivity.class);
        activity.putExtra(getResources().getString(R.string.ajuda), true);
        startActivity(activity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmarInclusaoApostaLayoutCarrinhoApostas: {
                DialogUtils.show(this);
                ApostaBO apostaBO = ApostaBO_.getInstance_(this);

                apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        DialogUtils.dismiss();
                        Bundle args = new Bundle();
                        args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                        startActivityForResult(new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtras(args),0);
                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ConfirmadoInclusaoCarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
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
                PrincipalActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();

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
