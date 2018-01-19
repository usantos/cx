package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
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
//        mockCarrinho();

    }

    private void mockCarrinho() {
        carrinho = new CarrinhoDTO();

        apostas = new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>();

        IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();
        aposta.setId(new Long(0));
        aposta.setModalidade(ModalidadeEnum.MEGA_SENA);

        DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

        String valorTipoConcurso = "1";
        tipoConcurso.setValor(valorTipoConcurso);
        tipoConcurso.setDescricao("Teste");
        aposta.setTipoConcurso(tipoConcurso);
        aposta.setPartidasLotogol(null);
        aposta.setPartidasLoteca(null);

        List<Integer> thisIsAnIntArray = new ArrayList<Integer>();
        thisIsAnIntArray.add(1);
        thisIsAnIntArray.add(2);
        thisIsAnIntArray.add(3);
        thisIsAnIntArray.add(4);
        thisIsAnIntArray.add(6);
        thisIsAnIntArray.add(5);

        aposta.setNumerosSelecionados(thisIsAnIntArray);

        DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
        indicadorSurpresinha.setValor(1);
        indicadorSurpresinha.setDescricao("");

        aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        aposta.setValor(new BigDecimal(10.0));
        aposta.setConcursoAlvo(12334);
        aposta.setQuantidadeTeimosinhas(1);
        aposta.setQuantidadeApostas(1);
        aposta.setEspelho(false);

        aposta.setTimeDoCoracao(null);

        aposta.setConcursoInicial(0);
        aposta.setSituacao(null);
        aposta.setTroca(false);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Date hour = new Date();

        aposta.setDataEfetivacao(dateFormat.format(date));
        aposta.setHoraEfetivacao(hourFormat.format(hour));
        aposta.setVinculoEspelho(new Long(0));
        aposta.setQuantidadeNumeros(6);
        aposta.setSurpresinha(false);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);

        carrinho.setApostas(apostas);

        atualizaLayout();
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        infoValorMinimoTextView = (TextView) findViewById(R.id.infoValorMinimo);
        ViewUtils.setMoedaFormatHtml(valorCarrinho,infoValorMinimoTextView, getResources().getString(R.string.label_lembrar_valor_minimo) + " ");
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
