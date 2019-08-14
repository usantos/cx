package br.gov.caixa.loterias.apostar.interno.view;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

import static br.gov.caixa.loterias.apostar.interno.view.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostar.interno.view.CarrinhoActivity.LER_CARRINHO_LOCAL;

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
        ((TextView) findViewById(R.id.txtVoltarAoInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.btn_title_voltarAoInicio)));

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

//        findViewById(R.id.botaoNovaAposta).setOnClickListener(this);
        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);
        findViewById(R.id.botaoLinearLayoutVoltarPrincipal).setOnClickListener(this);
//        findViewById(R.id.confirmarInclusaoApostaLayoutCarrinhoApostas).setOnClickListener(this);

    }

    private void configuraLayoutTela(ModalidadeEnum modalidadeEnum) {
        EstiloModalidade estilo = new EstiloModalidade(modalidadeEnum);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_" + ModalidadeEnum.fromString(modalidadeEnum) + "_"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, estilo.getCorClara())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, estilo.getCorClara()));
        gd.setStroke(2, Color.WHITE);

//        Button botaoNovaAposta = (Button) findViewById(R.id.botaoNovaAposta);
//
//        Button botaoVerCarrinhoAposta = (Button) findViewById(R.id.botaoVerCarrinhoAposta);
//        Button botaoAdicionarCompletarCartela = (Button) findViewById(R.id.botaoAdicionarCompletarCartela);

        LinearLayout botaoLinearLayoutVoltarPrincipal = findViewById(R.id.botaoLinearLayoutVoltarPrincipal);

//        if (isLotecaOrLotogol) {
//            botaoNovaAposta.setVisibility(View.INVISIBLE);
//            botaoVerCarrinhoAposta.setVisibility(View.INVISIBLE);
//            botaoAdicionarCompletarCartela.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
//            botaoAdicionarCompletarCartela.setBackgroundColor(getResources().getColor(R.color.cinza));
//            botaoAdicionarCompletarCartela.setOnClickListener(this);
//        } else {
//            botaoNovaAposta.setBackground(gd);
//            botaoVerCarrinhoAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.verCarrinho)));
//            botaoAdicionarCompletarCartela.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarAoCarrinho)));
//        }

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
        activity.putExtra("ajuda", true);
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
                        //CarrinhoActivity_.intent(ConfirmadoInclusaoCarrinhoActivity.this).carrinho(response.getPayload()).startForResult(0);
                        Bundle args = new Bundle();
                        args.putSerializable(CARRINHO, response.getPayload());
                        startActivityForResult(new Intent(ConfirmadoInclusaoCarrinhoActivity.this, CarrinhoActivity.class).putExtras(args),0);
                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ConfirmadoInclusaoCarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            //CarrinhoActivity_.intent(ConfirmadoInclusaoCarrinhoActivity.this).lerCarrinhoLocal(true).startForResult(0);
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
