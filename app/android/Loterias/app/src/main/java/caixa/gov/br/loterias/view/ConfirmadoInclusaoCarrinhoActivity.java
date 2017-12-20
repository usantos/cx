package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.Modalidade;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

public class ConfirmadoInclusaoCarrinhoActivity extends AppCompatActivity implements OnClickListener {
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

        TextView valorApostaText = findViewById(R.id.valorApostaConfirmacao);
        valorApostaText.setText(ViewUtils.getMoedaFormat(valorAposta));

        TextView textObservacaoConfirmacao = findViewById(R.id.textObservacaoConfirmacao);
        textObservacaoConfirmacao.setText(getString(R.string.infoValorMinimoCarrinho) + " " + ViewUtils.getMoedaFormat(parametroSimulacao.getValorApostaMinima() == null ? new BigDecimal(0) : parametroSimulacao.getValorApostaMinima()));

        configuraLayoutTela(tipoJogo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.botaoNovaAposta).setOnClickListener(this);
        findViewById(R.id.botaoVerCarrinhoAposta).setOnClickListener(this);
        findViewById(R.id.carrinhoApostas).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        EstiloModalidade estilo = new EstiloModalidade(modalidadeEnum);

        setTitle(ModalidadeEnum.fromString(modalidadeEnum));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, estilo.getCorClara()));
        gd.setCornerRadius(5);
        gd.setStroke(2, Color.WHITE);

        findViewById(R.id.botaoNovaAposta).setBackground(gd);

        ((Button) findViewById(R.id.botaoVerCarrinhoAposta)).setText(ViewUtils.configuraButtonText(this, getString(R.string.verCarrinho), 12));
        ((Button) findViewById(R.id.botaoAdicionarCompletarCartela)).setText(ViewUtils.configuraButtonText(this, getString(R.string.adicionarAoCarrinho), 9));

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
        getMenuInflater().inflate(R.menu.itens_simular_actionbar, menu);

        MenuItem concursoAtual = menu.findItem(R.id.concursoAtual);
        concursoAtual.setTitle("Concurso \n " + parametroSimulacao.getConcurso().getNumero());

        SimpleDateFormat formatterDateFinal = new SimpleDateFormat("dd/MM");
        DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        MenuItem dataSorteio = menu.findItem(R.id.sorteioAtual);

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            dataSorteio.setTitle("Sorteio \n " + formatterDateFinal.format(dataFormatada));
        } catch (ParseException e) {
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
            case R.id.carrinhoApostas: {
                Intent activity = new Intent(this, CarrinhoActivity.class);

                startActivityForResult(activity, 0);
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
