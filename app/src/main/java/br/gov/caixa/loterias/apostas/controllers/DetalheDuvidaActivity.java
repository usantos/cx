package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheDuvidaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class DetalheDuvidaActivity extends LoteriasAppActivity {

    public final static String SECAO_DTO_EXTRA = "secaoDTO";

    private ExpandableHeightRecyclerView recyclerViewDetalhesDuvida;
    private ListSecaoDTO secaoDTO;
    ImageButton customButton;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_duvida);

        init();
    }

    public void init() {
        getExtras();
        setViews();
        callWebservice();
        configurarToolbar();
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(SECAO_DTO_EXTRA)) {
                this.secaoDTO = ((ListSecaoDTO) bundle.getSerializable(SECAO_DTO_EXTRA));
            }
        }
    }

    private void setViews() {
        recyclerViewDetalhesDuvida = findViewById(R.id.recyclerViewDetalhesDuvida);
    }

    private void callWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosSilceBO.getInstance().duvidasPerguntasRespostas(secaoDTO.getId(), new RequestListener<ListSecaoDTOResponse>() {
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

    private void configurarToolbar(){
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(StringUtils.capitalizerNovo(secaoDTO.getNome()));
        customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onSupportNavigateUp());
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return Boolean.TRUE;
    }
}
