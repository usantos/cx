package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheDuvidaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

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
