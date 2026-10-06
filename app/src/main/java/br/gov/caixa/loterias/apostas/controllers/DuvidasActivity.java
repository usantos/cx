package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ItemDuvidaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class DuvidasActivity extends LoteriasBaseAppActivity {

    private LinearLayout layoutDuvidas;
    private ExpandableHeightRecyclerView recyclerViewDuvidas;
    private List<ListSecaoDTO> secaoDuvidas;

    ImageButton customButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViews();
        selectDuvidas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AlertDialogUtils.isShow()){
            AlertDialogUtils.dismiss();
        }
    }

    private void setViews() {
        setContentView(R.layout.activity_duvidas);
        configurarToolbar();
        layoutDuvidas = findViewById(R.id.layoutDuvidas2);
        recyclerViewDuvidas = findViewById(R.id.recyclerViewDuvidas2);
    }

    private void configurarToolbar(){
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.duvidas));
        customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onBackPressed());
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            setViews();
            selectDuvidas();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void selectDuvidas() {
        layoutDuvidas.setVisibility(View.VISIBLE);
        if (secaoDuvidas == null) {
            initReciclerViewDuvidas();
        }
    }

    private void initReciclerViewDuvidas() {
        if (secaoDuvidas == null) {
            AlertDialogUtils.show(this);
            DadosCorporativosSilceBO.getInstance().secaoDuvidas(new RequestListener<ListSecaoDTOResponse>() {
                @Override
                public void onResponse(ListSecaoDTOResponse response) {
                    AlertDialogUtils.dismiss();
                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DuvidasActivity.this);
                    }
                    secaoDuvidas = response.getPayload();
                    createRecyclerView();
                    AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_duvidas));
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    RedirectNetwork.checkRedirect(error, DuvidasActivity.this);
                }
            });
        } else {
            createRecyclerView();
        }
    }

    private void createRecyclerView() {
        ItemDuvidaAdapter itemDuvidaAdapter = new ItemDuvidaAdapter(secaoDuvidas);
        recyclerViewDuvidas.setAdapter(itemDuvidaAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerViewDuvidas.setLayoutManager(layoutManager);
        recyclerViewDuvidas.setExpanded(Boolean.TRUE);
        recyclerViewDuvidas.setNestedScrollingEnabled(false);
    }
}
