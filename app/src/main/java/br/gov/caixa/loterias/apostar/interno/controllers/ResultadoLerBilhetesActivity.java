package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.Objects;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.StatusBilhete;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.RateUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_ler_bilhetes)
public class ResultadoLerBilhetesActivity extends AppCompatActivity {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ScrollView resultadoApostaScrollView;

    @ViewById
    protected LinearLayout corpoBilhetePremiadoLinearLayout, corpoBilheteNaoPremiadoLinearLayout, premioGanhoLinearLayout, mensagemNaoPremiadoLinearLayout,
            informacoesPremioZeradoLayout, informacoesPremioLayout, backgroundTopoNaoPremiadoLayout;

    @ViewById
    protected TextView mensagemNaoPremiadoTextView, statusResultadoNaoPremiadTextView;

    @ViewById
    protected ContentResultadoPremiado contentResultadoPremiado;

    @Extra
    String codBarras;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;
    private ModalidadeEnum modalidadeEnum;

    @AfterViews
    public void init() {
        contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.GONE);
        createToolBar();
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
        final AlertDialog alert = LoadingViewLoterias.show(this);

        apostaBO.postConferirBilhetes(codBarras, new RequestListener<ResultadoConsultaBilheteDTOResponse>() {
            @Override
            public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
                alert.dismiss();
                resultadoConsultaBilheteDTO = response.getPayload();
                if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PREMIADA.getValor())) {
                    atualizarLayoutPremiado();
                    RateUtils.avaliarAplicativo(ResultadoLerBilhetesActivity.this, true);
                } else {
                    atualizarLayoutNaoPremiado();
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ResultadoLerBilhetesActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alert.dismiss();
                RedirectNetwork.checkRedirect( error, ResultadoLerBilhetesActivity.this );
            }
        });
    }

    private void atualizarLayoutPremiado() {

        corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

        modalidadeEnum = ModalidadeEnum.fromInteger( resultadoConsultaBilheteDTO.getModalidadeDTO().getValor());
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        contentResultadoPremiado.getTituloModalidadeTextView().setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao());
        contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textFuturaAndFuturaBold(this ,getString(R.string.botao_aposte_agora)));

        if (resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()) == 0)) {
            informacoesPremioZeradoLayout.setVisibility(View.GONE);
            informacoesPremioLayout.setVisibility(View.VISIBLE);

            contentResultadoPremiado.getValorPremioTituloTextView().setText( getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido())));

            contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.VISIBLE);
            createListPremiacao();
        }
        else {
            contentResultadoPremiado.getValorPremioTituloTextView().setText(R.string.label_voce_foi_premiado_exclamacao);
        }

        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contentResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getProximoConcurso().getConcurso().getDataAbertura()));
                contentResultadoPremiado.getValorEstimativaConcursoAtualTextView().setText(ViewUtils.getMoedaFormat(parametroSimulacao.getParametroJogo().getConcurso().getEstimativa() ));
            }
        }
    }

    @Click(R.id.botaoAposteAgora)
    protected void clickBotaoAposteAgora() {
        Intent it = new Intent(ResultadoLerBilhetesActivity.this, PrincipalActivity_.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);

    }


    private void atualizarLayoutNaoPremiado() {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
        int status = -1;
        if(resultadoConsultaBilheteDTO != null &&
                resultadoConsultaBilheteDTO.getSituacao() != null &&
                resultadoConsultaBilheteDTO.getSituacao().getValor() != null) {
            status  = resultadoConsultaBilheteDTO.getSituacao().getValor().intValue();
        }
        switch (status){
                case StatusBilhete.PRESCRITO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_concurso_prescrito);
                    break;
                case StatusBilhete.PREMIO_PAGO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_premio_pago);
                    break;
                case StatusBilhete.NAO_PREMIADO:
                case StatusBilhete.NAO_APURADO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
                    break;
                    default:
                        statusResultadoNaoPremiadTextView.setText(R.string.string_vazia);
        }
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(resultadoConsultaBilheteDTO.getPremioDTO())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
