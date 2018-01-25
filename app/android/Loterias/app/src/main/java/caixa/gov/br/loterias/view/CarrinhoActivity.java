package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bean.SessaoUsuario;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

public class CarrinhoActivity extends SettingToolbarActivity {

    public CarrinhoDTO carrinho;
    private ExpandableHeightRecyclerView recyclerView;
    private List<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private TextView valorTotalCarrinho;
    TextView infoValorMinimoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        valorTotalCarrinho = findViewById(R.id.valorTotalCarrinho);
        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, this.valorTotalCarrinho);

        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_carrinho)));

        recyclerView = findViewById(R.id.apostasListaRecycleView);

        callWebservice();
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        infoValorMinimoTextView = (TextView) findViewById(R.id.infoValorMinimo);
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimoTextView, getResources().getString(R.string.label_lembrar_valor_minimo).replace("_", "<br>") + " ");
    }

    private void callWebservice() {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new SilceClient.Listener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                carrinho = response.getPayload();

                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CarrinhoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    public void atualizaLayout() {
        montarLayout();
        if (carrinho.getApostas() != null) {
            configuraRecyclerViewCarrinhoAposta();
            setValorTotalCarrinho();
        }
        configuraListeners();
    }

    private void configuraListeners() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));


        findViewById(R.id.botaoFazerNovaAposta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2);

                finish();
            }
        });


        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);

                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) carrinho.getApostas(), this));
        recyclerView.setExpanded(Boolean.TRUE);
    }

    private void setValorTotalCarrinho() {

        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }

        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.valorTotalCarrinho);
    }

}
