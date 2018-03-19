package caixa.gov.br.loterias.view;

import android.app.Activity;
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
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.mock.ItemListaFaixaPremiacaoMock;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import caixa.gov.br.loterias.utils.EstiloModalidadeAposta;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.FaixaPremiacaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_ler_bilhetes)
public class ResultadoLerBilhetesActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    LinearLayout corpoBilhetePremiadoLinearLayout, corpoBilheteNaoPremiadoLinearLayout, topoModalidadeResultadoLinearLayout, topo2ModalidadeResultadoLinearLayout,
            premioGanhoLinearLayout, mensagemNaoPremiadoLinearLayout, fundoBackgroundTrapezeResultadoLinearLayout;

    @ViewById
    TextView tituloModalidadeTextView, limiteDiarioResultadoTextView, premioResultadoTextView, mensagemNaoPremiadoTextView, statusResultadoNaoPremiadTextView;

    @ViewById
    ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView;

    @Extra
    String codBarras;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;

    @AfterViews
    public void init() {
        createToolBar();
        postLerBilhetes(ApostaBO_.getInstance_(this), this, codBarras);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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


    private void postLerBilhetes(ApostaBO apostaBO, Activity activity, String CodBarra) {
        final AlertDialog alert = LoadingViewLoterias.show(activity);

        apostaBO.postConferirBilhetes(CodBarra, new SilceClient.Listener<ResultadoConsultaBilheteDTOResponse>() {
            @Override
            public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
                alert.dismiss();
                resultadoConsultaBilheteDTO = response.getPayload();
                if (resultadoConsultaBilheteDTO.getSituacao().getDescricao().equals("Premiada")) {
                    atualizarLayoutPremiado();
                }else{
                    atualizarLayoutNaoPremiado();
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alert.dismiss();
                ViewUtils.alert(ResultadoLerBilhetesActivity.this, MensagensNetwork.setMensagem(error, ResultadoLerBilhetesActivity.this));
            }
        });
    }

    private void atualizarLayoutPremiado() {

        corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

        ApostaDTO.ModalidadeEnum modalidadeEnum = ApostaDTO.toStringModalidade(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao());
        EstiloModalidadeAposta estiloModalidade = new EstiloModalidadeAposta(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();

        topoModalidadeResultadoLinearLayout.setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        topo2ModalidadeResultadoLinearLayout.setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        fundoBackgroundTrapezeResultadoLinearLayout.setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        tituloModalidadeTextView.setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao());
        limiteDiarioResultadoTextView.setText(ViewUtils.getMoedaFormat(sessaoUsuario.getParametrosSimulacao().getValorLimiteDiario()));
        createListe();

        if (resultadoConsultaBilheteDTO.getValorPremio() != null) {
            if (resultadoConsultaBilheteDTO.getValorPremio() != null) {
                premioResultadoTextView.setText(ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getValorPremio()));
            }
        }
    }

    private void atualizarLayoutNaoPremiado(){
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
//        if(resultadoConsultaBilheteDTO.getSituacao().getDescricao())
        statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListe() {
        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(mock()));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //TODO mock
    private List<ItemListaFaixaPremiacaoMock> mock() {
        List<ItemListaFaixaPremiacaoMock> mockList = new ArrayList<>();

        ItemListaFaixaPremiacaoMock item = new ItemListaFaixaPremiacaoMock();

        item.setConcurso("2587");
        item.setFaixaPremiacao("Quadra");
        item.setSorteio("01");
        item.setPremiacao("R$ 200,00");

        for (int i = 0; i < 4; i++) {
            mockList.add(item);
        }

        return mockList;
    }
}
