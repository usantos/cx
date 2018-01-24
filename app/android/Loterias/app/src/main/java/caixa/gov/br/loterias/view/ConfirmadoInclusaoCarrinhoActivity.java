package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bean.SessaoUsuario;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

public class ConfirmadoInclusaoCarrinhoActivity extends AppCompatActivity implements OnClickListener {

    private boolean isLotecaOrLotogol;
    private static int RESULT_FINISH = 3;
    public ParametroJogoDTO parametroSimulacao;

    public ParametroJogoDTO getParametroSimulacao() {
        return parametroSimulacao;
    }

    public void setParametroSimulacao(ParametroJogoDTO parametroSimulacao) {
        this.parametroSimulacao = parametroSimulacao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_carrinho);

        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra("modalidade"), ParametroJogoDTO.class);

        invalidateOptionsMenu();
        configuraActivityIncluidoApostaCarrinho();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    private void configuraActivityIncluidoApostaCarrinho() {
        ModalidadeEnum tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra("tipoAposta");
        BigDecimal valorAposta = (BigDecimal) getIntent().getSerializableExtra("valorAposta");

        isLotecaOrLotogol = ((tipoJogo == ModalidadeEnum.LOTECA) || (tipoJogo == ModalidadeEnum.LOTOGOL));

        TextView valorApostaText = findViewById(R.id.valorApostaConfirmacao);
        valorApostaText.setText(ViewUtils.getMoedaFormat(valorAposta));

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        String valorCarrinho = ViewUtils.getMoedaFormat(sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta());
        TextView textObservacaoConfirmacao = findViewById(R.id.textObservacaoConfirmacao);
        textObservacaoConfirmacao.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.infoValorMinimoCarrinho) + " _" + valorCarrinho + "_"));

        configuraLayoutTela(tipoJogo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        findViewById(R.id.botaoNovaAposta).setOnClickListener(this);
        findViewById(R.id.botaoVerCarrinhoAposta).setOnClickListener(this);
        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        EstiloModalidade estilo = new EstiloModalidade(modalidadeEnum);

        setTitle(ViewUtils.textFuturaAndFuturaBold(this, "_" + ModalidadeEnum.fromString(modalidadeEnum) + "_"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, estilo.getCorClara()));
        gd.setStroke(2, Color.WHITE);

        Button botaoNovaAposta = (Button) findViewById(R.id.botaoNovaAposta);

        Button botaoVerCarrinhoAposta = (Button) findViewById(R.id.botaoVerCarrinhoAposta);
        Button botaoAdicionarCompletarCartela = (Button) findViewById(R.id.botaoAdicionarCompletarCartela);
        if (isLotecaOrLotogol) {
            botaoNovaAposta.setVisibility(View.INVISIBLE);
            botaoVerCarrinhoAposta.setVisibility(View.INVISIBLE);
            botaoAdicionarCompletarCartela.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
            botaoAdicionarCompletarCartela.setBackgroundColor(getResources().getColor(R.color.cinza));
            botaoAdicionarCompletarCartela.setOnClickListener(this);
        } else {
            botaoNovaAposta.setBackground(gd);
            botaoVerCarrinhoAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.verCarrinho)));
            botaoAdicionarCompletarCartela.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarAoCarrinho)));
        }

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
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_titulo)).setText("Concurso");
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_descricao)).setText(parametroSimulacao.getConcurso().getNumero().toString());

        SimpleDateFormat formatterDateFinal = new SimpleDateFormat("dd/MM");
        DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        MenuItem dataSorteio = menu.findItem(R.id.sorteioAtual);

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText("Sorteio");
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_descricao)).setText(formatterDateFinal.format(dataFormatada));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void configuraMenuItem(int id) {
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botaoNovaAposta: {
                finish();
            }

            break;

            case R.id.botaoVerCarrinhoAposta: {
                Intent activity = new Intent(this, CarrinhoActivity.class);
                startActivityForResult(activity, 0);
            }
            break;
            case R.id.confirmarInclusaoApostaLayoutCarrinhoApostas: {
                Intent activity = new Intent(this, CarrinhoActivity.class);
                startActivityForResult(activity, 0);
            }
            case R.id.botaoAdicionarCompletarCartela: {
                if (isLotecaOrLotogol) {
                    setResult(RESULT_FINISH);
                    finish();
                }
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
