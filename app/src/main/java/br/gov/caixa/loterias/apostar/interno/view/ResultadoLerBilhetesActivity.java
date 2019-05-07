package br.gov.caixa.loterias.apostar.interno.view;

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
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ContenteResultadoPremiado;
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
    protected ContenteResultadoPremiado contenteResultadoPremiado;

    @Extra
    String codBarras;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;

    @AfterViews
    public void init() {
        resultadoApostaScrollView.fullScroll(View.FOCUS_UP);
        contenteResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.GONE);
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

        ModalidadeEnum modalidadeEnum = ModalidadeEnum.fromInteger( resultadoConsultaBilheteDTO.getModalidadeDTO().getValor());
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contenteResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        contenteResultadoPremiado.getTopo2ModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contenteResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        contenteResultadoPremiado.getTituloModalidadeTextView().setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao());
        contenteResultadoPremiado.getLimiteDiarioResultadoTextView().setText(ViewUtils.getMoedaFormat(sessaoUsuario.getParametrosSimulacao().getValorLimiteDiario()));

        if (resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()) == 0)) {
            informacoesPremioZeradoLayout.setVisibility(View.GONE);
            informacoesPremioLayout.setVisibility(View.VISIBLE);
            contenteResultadoPremiado.getPremioResultadoTextView().setText(ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()));
            contenteResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.VISIBLE);
            createListPremiacao();
        }

        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contenteResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getProximoConcurso().getConcurso().getDataAbertura()));
                contenteResultadoPremiado.getAberturaApostaResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataAbertura()));
                contenteResultadoPremiado.getEncerramentoResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));
            }
        }
    }

    private void atualizarLayoutNaoPremiado() {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);

        if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA.getValor())) {
            statusResultadoNaoPremiadTextView.setText(R.string.label_premio_pago);
            if (resultadoConsultaBilheteDTO != null && resultadoConsultaBilheteDTO.getModalidadeDTO() != null) {
                ModalidadeEnum modalidadeEnum = ModalidadeEnum.toString(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao().toLowerCase());
                EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
                backgroundTopoNaoPremiadoLayout.setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
            }
        } else {
            statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
        }
        mensagemNaoPremiadoTextView.setText(resultadoConsultaBilheteDTO.getSituacao().getDescricao());
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contenteResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(resultadoConsultaBilheteDTO.getPremioDTO())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
