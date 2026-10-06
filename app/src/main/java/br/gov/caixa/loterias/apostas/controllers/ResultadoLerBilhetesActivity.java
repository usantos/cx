package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StatusBilhete;
import br.gov.caixa.loterias.apostas.model.enums.LeituraBilheteEnum;
import br.gov.caixa.loterias.apostas.model.model.ConferirBilheteModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class ResultadoLerBilhetesActivity extends LoteriasBaseAppActivity {
    public final static String COD_BARRAS_EXTRA = "codBarras";
    public final static String TIPO_LEITURA_EXTRA = "tipoLeitura";
    public final static String NSB_EXTRA = "nsb";

    private Toolbar toolbar;
    private ScrollView resultadoApostaScrollView;
    private LinearLayout corpoBilhetePremiadoLinearLayout, corpoBilheteNaoPremiadoLinearLayout, premioGanhoLinearLayout, mensagemNaoPremiadoLinearLayout,
            informacoesPremioZeradoLayout, informacoesPremioLayout, backgroundTopoNaoPremiadoLayout, aposteAgoraNaoPremiadoLinearLayout;
    private TextView mensagemNaoPremiadoTextView, statusResultadoNaoPremiadTextView;
    private ContentResultadoPremiado contentResultadoPremiado;
    private Button botaoAposteAgora2;
    private Button botaoAposteAgora;
    private String codBarras;
    private LeituraBilheteEnum tipoLeitura;
    private String nsb;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;
    private ModalidadeEnum modalidadeEnum;

    private ConferirBilheteModel model;
    private EstiloModalidadeMKP estiloModalidade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_ler_bilhetes);
        init();
    }

    public void init() {
        getExtras();
        setViews();
        contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.GONE);
        contentResultadoPremiado.getButtonVerDetalheAposta().setVisibility(View.GONE);
        createToolBar();
        model = new ConferirBilheteModel(ResultadoLerBilhetesActivity.this);
    }

    private void getExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(COD_BARRAS_EXTRA)) {
                this.codBarras = extras_.getString(COD_BARRAS_EXTRA);
            }
            if (extras_.containsKey(TIPO_LEITURA_EXTRA)) {
                this.tipoLeitura = ((LeituraBilheteEnum) extras_.getSerializable(TIPO_LEITURA_EXTRA));
            }
            if (extras_.containsKey(NSB_EXTRA)) {
                this.nsb = extras_.getString(NSB_EXTRA);
            }
        }
    }

    private void setViews() {
        this.toolbar = findViewById(R.id.toolbar);
        this.resultadoApostaScrollView = findViewById(R.id.resultadoApostaScrollView);
        this.corpoBilhetePremiadoLinearLayout = findViewById(R.id.corpoBilhetePremiadoLinearLayout);
        this.corpoBilheteNaoPremiadoLinearLayout = findViewById(R.id.corpoBilheteNaoPremiadoLinearLayout);
        this.premioGanhoLinearLayout = findViewById(R.id.premioGanhoLinearLayout);
        this.mensagemNaoPremiadoLinearLayout = findViewById(R.id.mensagemNaoPremiadoLinearLayout);
        this.informacoesPremioZeradoLayout = findViewById(R.id.informacoesPremioZeradoLayout);
        this.informacoesPremioLayout = findViewById(R.id.informacoesPremioLayout);
        this.backgroundTopoNaoPremiadoLayout = findViewById(R.id.backgroundTopoNaoPremiadoLayout);
        this.aposteAgoraNaoPremiadoLinearLayout = findViewById(R.id.aposteAgoraNaoPremiadoLinearLayout);
        this.mensagemNaoPremiadoTextView = findViewById(R.id.mensagemNaoPremiadoTextView);
        this.statusResultadoNaoPremiadTextView = findViewById(R.id.statusResultadoNaoPremiadTextView);
        this.contentResultadoPremiado = findViewById(R.id.contentResultadoPremiado);
        this.botaoAposteAgora2 = findViewById(R.id.botaoAposteAgora2);
        this.botaoAposteAgora = findViewById(R.id.botaoAposteAgora);

        if (botaoAposteAgora != null) {
            botaoAposteAgora.setOnClickListener(view -> clickBotaoAposteAgora());
        }
        if (this.botaoAposteAgora2 != null) {
            this.botaoAposteAgora2 .setOnClickListener(view -> clickBotaoAposteAgora2());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        postLerBilhetes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void postLerBilhetes() {
        AlertDialogUtils.show(this);
        switch (tipoLeitura){
            case CODIGO_BARRAS:
                conferirCodBarras();
                break;
            case QR_CODE:
                conferirQrCode();
                break;
        }
    }

    private void conferirQrCode() {
        model.conferirBilheteQrCode(codBarras, new OnSilceListener<ResultadoConsultaBilheteDTO>() {
            @Override
            public void success(ResultadoConsultaBilheteDTO payload) {
                resultadoConsultaBilheteDTO = payload;
                if (isPremiado(payload)) {
                    List<FaixaPremiadaDTO> faixas = ViewUtils.getFaixaPremiadoNaoPremiado(resultadoConsultaBilheteDTO.getPremioDTO());
                    atualizarLayoutPremiado(faixas);
                    RateUtils.solicitarReview(ResultadoLerBilhetesActivity.this);
                } else if (isPreescrito(payload)){
                    atualizarLayoutApenasMensagem(R.string.label_concurso_prescrito);
                } else if (isPremioPago(payload)){
                    atualizarLayoutApenasMensagem(R.string.label_premio_pago);
                    botaoAposteAgora2.setText(ViewUtils.textCaixaSTDBold(ResultadoLerBilhetesActivity.this, getString(R.string.botao_aposte_agora)));
                } else {
                    atualizarLayoutNaoPremiado();
                }
                if (contentResultadoPremiado.getTipoApostaLinhaView() != null){
                    contentResultadoPremiado.getTipoApostaLinhaView().setVisibility(View.GONE);
                }
                AlertDialogUtils.dismiss();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private boolean isPremioPago(ResultadoConsultaBilheteDTO payload) {
        return payload != null && payload.getSituacao().getValor() == 11;
    }

    private boolean isPreescrito(ResultadoConsultaBilheteDTO payload) {
        return payload != null && payload.getSituacao().getValor() == 10;
    }

    private boolean isPremiado(ResultadoConsultaBilheteDTO payload) {
        return payload != null && payload.getSituacao().getValor() == 100;
    }

    private void conferirCodBarras() {
        model.conferirBilhete(codBarras, new OnSilceListener<ResultadoConsultaBilheteDTO>() {
            @Override
            public void success(ResultadoConsultaBilheteDTO payload) {
                AlertDialogUtils.dismiss();
                resultadoConsultaBilheteDTO = payload;
                if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PREMIADA.getValor())) {
                    atualizarLayoutPremiado(null);
                    RateUtils.solicitarReview(ResultadoLerBilhetesActivity.this);
                } else {
                    atualizarLayoutNaoPremiado();
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void atualizarLayoutPremiado(List<FaixaPremiadaDTO> faixas) {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

        atualizaTituloeBackground();

        contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textCaixaSTDBold(this ,getString(R.string.botao_aposte_agora)));

        if (resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()) == 0)) {
            String label = getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()));
            contentResultadoPremiado.getValorPremioTituloTextView().setText(label);
            contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.VISIBLE);

            atualizaLayoutLista(faixas);
        }
        else {
            String label = getResources().getString(R.string.label_voce_foi_premiado_exclamacao);
            contentResultadoPremiado.getValorPremioTituloTextView().setText(label);
        }
        contentResultadoPremiado.getValorPremioTituloTextView().setTextColor(ContextCompat.getColor(this, estiloModalidade.getCorFonteFundoClaro()));

        checkNsb();

        atualizaProximoSorteioValorEstimativa();
    }

    private void atualizaProximoSorteioValorEstimativa() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);
        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contentResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));
                contentResultadoPremiado.getValorEstimativaConcursoAtualTextView().setText(ViewUtils.getMoedaFormat(parametroSimulacao.getParametroJogo().getConcurso().getEstimativa()));
            }
        }
    }

    private void atualizaTituloeBackground() {
        initEstiloModalidade();

        contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));

        contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrapezio()));
        contentResultadoPremiado.getImgTrevoResultado().setImageDrawable(ContextCompat.getDrawable(this, estiloModalidade.getTrevoFundoEscuro()));

        if (modalidadeEnum == ModalidadeEnum.MAIS_MILIONARIA){
            contentResultadoPremiado.getTituloModalidadeTextView().setText(getResources().getString(R.string.label_mais_milionaria));
        } else {
            contentResultadoPremiado.getTituloModalidadeTextView().setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao().toLowerCase());
        }
        contentResultadoPremiado.getTituloModalidadeTextView().setTextColor(ContextCompat.getColor(this, estiloModalidade.getCorFonteFundoClaro()));
        contentResultadoPremiado.getPodeComemorar().setTextColor(ContextCompat.getColor(this, estiloModalidade.getCorFonteFundoClaro()));
    }

    private void initEstiloModalidade() {
        if (estiloModalidade == null && resultadoConsultaBilheteDTO != null) {
            modalidadeEnum = ModalidadeEnum.fromInteger(resultadoConsultaBilheteDTO.getModalidadeDTO().getValor());
            estiloModalidade = new EstiloModalidadeMKP(modalidadeEnum);
        }
    }

    private void atualizaLayoutLista(List<FaixaPremiadaDTO> faixas) {
        informacoesPremioZeradoLayout.setVisibility(View.GONE);
        informacoesPremioLayout.setVisibility(View.VISIBLE);

        if (faixas == null){
            faixas = ViewUtils.getFaixaPremiadoNaoPremiado(resultadoConsultaBilheteDTO.getPremioDTO());
        }
        createListPremiacao(faixas);
    }

    private void checkNsb() {
        if (nsb != null && !nsb.isEmpty()){
            contentResultadoPremiado.getNSBTextView().setVisibility(View.GONE);
            contentResultadoPremiado.getNSBTextView().setText("\nNSB\n" + nsb);
        } else {
            contentResultadoPremiado.getNSBTextView().setVisibility(View.GONE);
        }
    }

    protected void clickBotaoAposteAgora() {
        Intent it = new Intent(ResultadoLerBilhetesActivity.this, PrincipalActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);
    }

    protected void clickBotaoAposteAgora2() {
        Intent it = new Intent(ResultadoLerBilhetesActivity.this, PrincipalActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);
    }

    private void atualizarLayoutApenasMensagem(int label) {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
        statusResultadoNaoPremiadTextView.setText(label);
    }

    private void atualizarLayoutNaoPremiado() {
        if (resultadoConsultaBilheteDTO != null &&
                resultadoConsultaBilheteDTO.getPremioDTO() != null &&
                resultadoConsultaBilheteDTO.getPremioDTO().getConcursosNaoPremiado() != null &&
                !resultadoConsultaBilheteDTO.getPremioDTO().getConcursosNaoPremiado().isEmpty()){

            contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.VISIBLE);
            corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
            contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setVisibility(View.VISIBLE);
            contentResultadoPremiado.getPodeComemorar().setVisibility(View.GONE);
            contentResultadoPremiado.getValorPremioTituloTextView().setVisibility(View.INVISIBLE);
            contentResultadoPremiado.getNSBTextView().setVisibility(View.GONE);
            corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

            contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textCaixaSTDBold(this ,getString(R.string.botao_aposte_agora)));

            atualizaTituloeBackground();

            List<FaixaPremiadaDTO> faixas = ViewUtils.getFaixaNaoPremiadoDTO(resultadoConsultaBilheteDTO.getPremioDTO());
            atualizaLayoutLista(faixas);

            atualizaProximoSorteioValorEstimativa();
        } else {
            corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
            corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
            int status = -1;
            if(resultadoConsultaBilheteDTO != null &&
                    resultadoConsultaBilheteDTO.getSituacao() != null &&
                    resultadoConsultaBilheteDTO.getSituacao().getValor() != null) {
                status  = resultadoConsultaBilheteDTO.getSituacao().getValor().intValue();
            }
            switch (status){
                case StatusBilhete.PRESCRITO://10
                    statusResultadoNaoPremiadTextView.setText(R.string.label_concurso_prescrito);
                    break;
                case StatusBilhete.PREMIO_PAGO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_premio_pago);
                    botaoAposteAgora2.setText(ViewUtils.textCaixaSTDBold(this ,getString(R.string.botao_aposte_agora)));
                    break;
                case StatusBilhete.NAO_PREMIADO:
                case StatusBilhete.NAO_APURADO:
                    aposteAgoraNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
                    botaoAposteAgora2.setText(ViewUtils.textCaixaSTDBold(this ,getString(R.string.botao_aposte_agora)));
                    statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
                    break;
                default:
                    statusResultadoNaoPremiadTextView.setText(R.string.string_vazia);
            }
        }
    }

    private void createToolBar() {
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListPremiacao(List<FaixaPremiadaDTO> faixa) {
        contentResultadoPremiado.aplicaCorColunasTabela(ContextCompat.getColor(this, estiloModalidade.getCorFonteFundoClaro()));
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();
        initEstiloModalidade();
        FaixaPremiacaoAdapter adapter = new FaixaPremiacaoAdapter(faixa, ContextCompat.getColor(this, estiloModalidade.getCorEscura()),
                ContextCompat.getColor(this, estiloModalidade.getCorFonteFundoClaro()));

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(adapter);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
