package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.DuvidaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ListSecaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ListSecaoDTOResponse;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.DetalheDuvidaAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_detalhe_duvida)
public class DetalheDuvidaActivity extends AppCompatActivity {

    @ViewById
    ExpandableHeightRecyclerView recyclerViewDetalhesDuvida;

    @Extra
    ListSecaoDTO secaoDTO;

    @AfterViews
    public void init() {
        callWebservice();
        createToolBar();
    }

    private void callWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(getApplicationContext());

        dadosCorporativosBO.duvidasPerguntasRespostas(secaoDTO.getId(), new RequestListener<ListSecaoDTOResponse>() {
            @Override
            public void onResponse(ListSecaoDTOResponse response) {
                List<ListSecaoDTO> secaoDTOList = response.getPayload();
                if (secaoDTOList != null) {

                    createRecyclerView(secaoDTOList.get(0).getDuvidas());
                }
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalheDuvidaActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, DetalheDuvidaActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    private void createRecyclerView(List<DuvidaDTO> duvidas) {
        DetalheDuvidaAdapter detalheDuvidaAdapter = new DetalheDuvidaAdapter(duvidas);
        recyclerViewDetalhesDuvida.setAdapter(detalheDuvidaAdapter);
        recyclerViewDetalhesDuvida.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetalhesDuvida.setExpanded(Boolean.TRUE);
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, secaoDTO.getNome())));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return Boolean.TRUE;
    }
}
