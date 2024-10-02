package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.VisualizarModalidadesAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class SelecaoModalidadesResultados extends AppCompatActivity {

    public final static String MODALIDADE = "MODALIDADE";

    private ExpandableHeightGridView gridModalidades;
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

        setTitle(getString(R.string.label_resultados));
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
                case QUINA:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case TIMEMANIA:
                    //tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.timemaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case DIA_DE_SORTE:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                case MAIS_MILIONALIA:
                    tipoAposta = new TipoAposta("+milionária", estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
                    break;
                default:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
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
        VisualizarModalidadesAdapter.OnModalidadeSelecionadaListener listener = item -> {
            item.getDescricaoEspecial();
            Intent it = new Intent(SelecaoModalidadesResultados.this, VisualizarResultadosActivity.class);
            it.putExtra(MODALIDADE,item.getTitulo());
            startActivity(it);
        };
        VisualizarModalidadesAdapter modalidadesAdapter = new VisualizarModalidadesAdapter( this, tiposAposta,listener);
        gridModalidades.setAdapter(modalidadesAdapter);
        gridModalidades.setExpanded(true);
    }

}
