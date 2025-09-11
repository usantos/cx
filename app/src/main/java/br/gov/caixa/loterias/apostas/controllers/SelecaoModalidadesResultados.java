package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TipoApostaMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class SelecaoModalidadesResultados extends LoteriasBaseAppActivity {

    public final static String MODALIDADE = "MODALIDADE";
    public final static String MODALIDADE_VALUE = "MODALIDADE_VALUE";

    private RecyclerView gridModalidades;
    private List<TipoAposta> tiposAposta = new ArrayList<>();
    List<ModalidadeDTO> modalidades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_modalidades_resultados);
        setViews();
        callWebservice();
    }

    private void setViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        gridModalidades = findViewById(R.id.ehgv_grid_modalidades);

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, "_Resultados_"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void callWebservice() {

        AlertDialogUtils.show(this);
        new ModalidadeModel(this).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
            @Override
            public void success(List<ModalidadeDTO> payload) {
                AlertDialogUtils.dismiss();
                modalidades = payload;
                atualizarLayout();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void atualizarLayout() {
        for (ModalidadeDTO item : modalidades) {
            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));
            TipoAposta tipoAposta = null;
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
                case MAIS_MILIONALIA:
                    tipoAposta = new TipoAposta("+Milionária", R.drawable.trevo_mais_milionaria_nao_selecionado, estilo.getTrevoFundoEscuro(), estilo.getCorClara(), true, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                default:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), true, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
            }
            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase());
                tiposAposta.add(tipoAposta);
            }
        }
        if (tiposAposta != null || tiposAposta.size() > 0) {
            gridViewTipoAposta();
        }
    }

    private void gridViewTipoAposta() {
        gridModalidades.setLayoutManager(new GridLayoutManager(SelecaoModalidadesResultados.this, 2));
        gridModalidades.setAdapter(new TipoApostaMultiplaSelecaoAdapter(tiposAposta, (item, position) -> {
            item.getDescricaoEspecial();
            Intent it = new Intent(SelecaoModalidadesResultados.this, VisualizarResultadosActivity.class);
            it.putExtra(MODALIDADE,item.getTitulo());
            it.putExtra(MODALIDADE_VALUE, item.getValor());
            startActivity(it);
        }));
    }

}
