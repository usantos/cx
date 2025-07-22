package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheComboAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetalheComboActivity extends SettingToolbarActivity {

    //public static final String ARG_COMBOS = "ARG_COMBOS";
    private static final int QUANTIDADE_COLUNA_LISTA_COMBOS = 2;
    private CombosDTO combo = new CombosDTO();
    private List<ModalidadeComboDTO> modalidadesCombo;
    private Button btnAdicionarAoCarrinho;
    private TextView txtNomeCombo, txtQtdApostas, txtValorCombo;
    private CheckBox checkMostrarNumeros;

    RecyclerView DetalheComboRcv;
    DetalheComboAdapter detalheComboAdapter;

    Toolbar toolbar;

    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_combo);

        getExtras();
        setViews();
        setMetodos();
        configuraDetalheComboRcv();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentSomadorCarrinho();
    }

    private void getExtras(){
        if(getIntent() != null && getIntent().hasExtra(CombosActivity.ARG_COMBO)){
            combo = (CombosDTO) getIntent().getSerializableExtra(CombosActivity.ARG_COMBO);
            Log.d("DetalheComboActivity", "Combo recebido: " + combo);
        }else{
            Log.d("DetalheComboActivity", "Nenhum combo recebido");
        }
    }

    private void setViews(){

        btnAdicionarAoCarrinho = findViewById(R.id.btnAdicionarAoCarrinho);
        txtNomeCombo = findViewById(R.id.txtNomeDetalheCombo);
        txtQtdApostas = findViewById(R.id.txtQtdDeApostas);
        txtValorCombo = findViewById(R.id.txtValorCombo);
        DetalheComboRcv = findViewById(R.id.rcvDetalheCombo);
        checkMostrarNumeros = findViewById(R.id.checkBoxNumerosEscolhidos);

        View view_id_fg_somador_carrinho = findViewById(R.id.id_fg_somador_carrinho);
        view_id_fg_somador_carrinho.setOnClickListener(view -> vaiProCarrinho());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.titulo_combos)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void setMetodos(){
        if(Objects.equals(combo.getTipoCombo().getCodigo(), CombosDTO.TipoComboEnum.ESPECIAL.getValue())){
            ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(combo.getModalidadesCombo().get(0).getModalidade().getValor());
            if(modalidade != null){
                EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
                setDrawable(txtNomeCombo, estilo.getTrevoFundoClaro(), 20, 20);
                txtNomeCombo.setTextColor(ContextCompat.getColor(DetalheComboActivity.this, estilo.getCorLetraLista()));
                String nomeEspecial = combo.getTipoCombo().getNome() + " " + combo.getModalidadesCombo().get(0).getModalidade().getDescricaoEspecial();
                txtNomeCombo.setText(nomeEspecial);
            }

        }else{
            txtNomeCombo.setText(combo.getTipoCombo().getNome());
        }
        txtValorCombo.setText(combo.getValor() != null ? ViewUtils.getMoedaFormat(combo.getValor())  : "R$ 0,00");
        btnAdicionarAoCarrinho.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.adicionarAoCarrinho)));

        if(combo.getModalidadesCombo() != null && !combo.getModalidadesCombo().isEmpty()){
            modalidadesCombo = new ArrayList<>(combo.getModalidadesCombo());
        }

        txtQtdApostas.setText(getQuantidadeApostas().toString());

        btnAdicionarAoCarrinho.setOnClickListener(view -> incluirComboNoCarrinho());

    }


    private void configuraDetalheComboRcv(){
        if(modalidadesCombo != null && !modalidadesCombo.isEmpty()){
            detalheComboAdapter = new DetalheComboAdapter(modalidadesCombo,DetalheComboActivity.this);

            DetalheComboRcv.setAdapter(detalheComboAdapter);
            RecyclerView.LayoutManager layoutListacombos = new GridLayoutManager(this, QUANTIDADE_COLUNA_LISTA_COMBOS);
            DetalheComboRcv.setLayoutManager(layoutListacombos);

            DetalheComboRcv.setNestedScrollingEnabled(false);

        }else{
            ViewUtils.alertTitleButton(DetalheComboActivity.this, R.string.label_atencao, getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface -> finish());
        }
    }

    private void incluirComboNoCarrinho(){

        Boolean isSurpresinha = checkMostrarNumeros.isChecked();

        IncluirComboDTO incluirComboDTO = new IncluirComboDTO();
        incluirComboDTO.setId(combo.getTipoCombo().getCodigo());
        incluirComboDTO.setQtdApostas(getQuantidadeApostas());
        incluirComboDTO.setIsSurpresinha(isSurpresinha);
        incluirComboDTO.setLimparCarrinho(false);


    }

    private Integer getQuantidadeApostas() {
        int quantidadeApostas = 0;
        if (combo.getModalidadesCombo() != null) {
            for (ModalidadeComboDTO modalidade : combo.getModalidadesCombo()) {
                quantidadeApostas += modalidade.getQuantidadeApostas();
            }
        }
        return quantidadeApostas;
    }


    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho,"TELA DETALHE COMBOS" );
        }else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }

    }

    protected void vaiProCarrinho() {
        if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
            startActivity(new Intent(DetalheComboActivity.this, CarrinhoActivity.class));
        }
    }

    private void setDrawable(TextView textView, int drawableId, int larguraDp, int alturaDp) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable != null) {
            int widthPx = (int) (larguraDp * getResources().getDisplayMetrics().density);
            int heightPx = (int) (alturaDp * getResources().getDisplayMetrics().density);
            drawable.setBounds(0, 0, widthPx, heightPx);
            textView.setCompoundDrawablesRelative(drawable, null, null, null);
        }
    }
}
