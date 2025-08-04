package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.ComboModel;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheComboAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetalheComboActivity extends SettingToolbarActivity  {
    private static final String ARG_TIPOAPOSTA = "tipoAposta";
    private static final String ARG_MODALIDADE = "modalidade";
    private static final String ARG_VALORAPOSTA = "valorAposta";
    private static final int QUANTIDADE_COLUNA_LISTA_COMBOS = 2;
    private CombosDTO combo = new CombosDTO();
    private List<ModalidadeComboDTO> modalidadesCombo;
    private SessaoUsuario sessaoUsuario;
    private Button btnAdicionarAoCarrinho;
    private TextView txtNomeCombo, txtQtdApostas, txtValorCombo;
    private CheckBox checkMostrarNumeros;
    private ImageButton btnIncrementarAposta, btnDecrementarAposta;
    private Integer qtdInicialApostas = 0;
    private Integer qtdTotalApostas = 0;
    private Integer index = 0;
    private BigDecimal valorCombo = BigDecimal.ZERO;
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
        if(getIntent() !=null && getIntent().hasExtra(CombosActivity.ARG_COMBO)){
            combo = new Gson().fromJson(getIntent().getStringExtra(CombosActivity.ARG_COMBO), CombosDTO.class);
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
        btnIncrementarAposta = findViewById(R.id.btnIncrementarApostas);
        btnDecrementarAposta = findViewById(R.id.btnDecrementarApostas);

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

        sessaoUsuario = SessaoUsuario.getInstance();

        qtdInicialApostas = getQtdApostas(modalidadesCombo);
        qtdTotalApostas = qtdInicialApostas;

        valorCombo = combo.getValor() != null ? combo.getValor() : BigDecimal.ZERO;

        txtQtdApostas.setText(String.valueOf(getQtdApostas(modalidadesCombo) != null ? getQtdApostas(modalidadesCombo) : 0));

        btnAdicionarAoCarrinho.setOnClickListener(view -> incluirComboNoCarrinho());

        btnIncrementarAposta.setOnClickListener(view -> incrementarApostas());

        btnDecrementarAposta.setOnClickListener(view -> decrementarApostas());
    }

    private void configuraDetalheComboRcv(){

        if(modalidadesCombo != null && !modalidadesCombo.isEmpty()){

            RecyclerView.LayoutManager layoutListacombos = new GridLayoutManager(this, QUANTIDADE_COLUNA_LISTA_COMBOS);
            DetalheComboRcv.setLayoutManager(layoutListacombos);
            DetalheComboRcv.setNestedScrollingEnabled(false);

            detalheComboAdapter = new DetalheComboAdapter(DetalheComboActivity.this);

            DetalheComboRcv.setAdapter(detalheComboAdapter);

            detalheComboAdapter.submitList(modalidadesCombo);

        }else{
            ViewUtils.alertTitleButton(DetalheComboActivity.this, R.string.label_atencao, getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface -> finish());
        }
    }

    private void incluirComboNoCarrinho(){

        Boolean isSurpresinha = !checkMostrarNumeros.isChecked();

        IncluirComboDTO incluirComboDTO = new IncluirComboDTO();

        incluirComboDTO.setId(combo.getTipoCombo().getCodigo());
        incluirComboDTO.setQtdApostas(qtdTotalApostas);
        incluirComboDTO.setIsSurpresinha(isSurpresinha);
        incluirComboDTO.setLimparCarrinho(false);

        new ComboModel(this).adicionaComboCarrinho(incluirComboDTO, new OnSilceListener() {
            @Override
            public void success(Object payload) {
                Intent activity = new Intent(DetalheComboActivity.this, ConfirmadoInclusaoCarrinhoActivity.class);
                activity.putExtra(ARG_TIPOAPOSTA, ModalidadeEnum.COMBO);
                activity.putExtra(ARG_VALORAPOSTA, valorCombo);
                int qtdParametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().size()-1;
                activity.putExtra(ARG_MODALIDADE, (new Gson()).toJson(SessaoUsuario.getInstance().getParametrosSimulacao().getParametros().get(qtdParametros).getParametroJogo()));
                startActivity(activity);
            }

            @Override
            public void error(VolleyError error) {
                RedirectNetwork.checkRedirect(error,DetalheComboActivity.this);
            }
        });


    }

    private Integer getQtdApostas(List<ModalidadeComboDTO> modalidadesCombo) {
        int quantidadeApostas = 0;
        if (modalidadesCombo != null) {
            for (ModalidadeComboDTO modalidade :modalidadesCombo) {
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

    private void incrementarApostas() {

        if(modalidadesCombo.get(index).getModalidade().getValor() == 16){
            qtdTotalApostas++;
            atualizarQtdJogosItem(index , true);
        }
        qtdTotalApostas++;
        atualizarQtdJogosItem(index , true);
        index = (index + 1) % modalidadesCombo.size();
        txtQtdApostas.setText(String.valueOf(qtdTotalApostas));
        atualizarValor();

    }

    private void decrementarApostas(){
        if(qtdTotalApostas > qtdInicialApostas){
            qtdTotalApostas--;
            index = index == 0 ? modalidadesCombo.size() - 1 : index - 1;
            atualizarQtdJogosItem(index  , false);
            if(modalidadesCombo.get(index).getModalidade().getValor() == 16){
                qtdTotalApostas--;
                atualizarQtdJogosItem(index, false);
            }
        }else{
            qtdTotalApostas = qtdInicialApostas;
        }
        txtQtdApostas.setText(String.valueOf(qtdTotalApostas));
        atualizarValor();
    }

    private void atualizarQtdJogosItem(int indexItem, boolean incrementar){
        ModalidadeComboDTO itemOriginal = modalidadesCombo.get(indexItem);
        int novaQuantidade = incrementar ? itemOriginal.getQuantidadeApostas() + 1 : itemOriginal.getQuantidadeApostas() - 1;

        ModalidadeComboDTO itemQtdApostasAlteradas = getModalidadeComboDTO(itemOriginal, novaQuantidade);

        List<ModalidadeComboDTO> novaLista = new ArrayList<>(modalidadesCombo);
        novaLista.set(indexItem, itemQtdApostasAlteradas);
        detalheComboAdapter.submitList(novaLista);
        modalidadesCombo = novaLista;
    }

    private static @NotNull ModalidadeComboDTO getModalidadeComboDTO(ModalidadeComboDTO itemOriginal, int novaQuantidade) {
        ModalidadeComboDTO itemQtdApostasAlteradas = new ModalidadeComboDTO();
        itemQtdApostasAlteradas.setModalidade(itemOriginal.getModalidade());
        itemQtdApostasAlteradas.setTipoConcurso(itemOriginal.getTipoConcurso());
        itemQtdApostasAlteradas.setQuantidadeApostas(novaQuantidade);
        itemQtdApostasAlteradas.setQuantidadeTeimosinhas(itemOriginal.getQuantidadeTeimosinhas());
        itemQtdApostasAlteradas.setNumero(itemOriginal.getNumero());
        itemQtdApostasAlteradas.setQuantidadeDezenas(itemOriginal.getQuantidadeDezenas());
        itemQtdApostasAlteradas.setQuantidadeDezenasTrevos(itemOriginal.getQuantidadeDezenasTrevos());
        return itemQtdApostasAlteradas;
    }


    private void atualizarValor(){
        valorCombo = BigDecimal.ZERO;

        for (ModalidadeComboDTO modalidadeCombo : modalidadesCombo) {
            ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(modalidadeCombo.getModalidade().getValor());
            ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade);

            if (parametroSimulacao != null) {
                List<ParametroValorApostaDTO> valores = parametroSimulacao.getParametroJogo().getValoresAposta();
                BigDecimal valorAposta = valores.get(0).getValor(); // Valor padrão
                int qtdApostas = modalidadeCombo.getQuantidadeApostas();
                int qtdTeimosinhas = Math.max(modalidadeCombo.getQuantidadeTeimosinhas(), 1);

                for (ParametroValorApostaDTO valor : valores) {
                    if (modalidadeCombo.getModalidade().getValor() != 9) {
                        if (Objects.equals(modalidadeCombo.getQuantidadeDezenas(), valor.getNumeroPrognosticos())) {
                            valorAposta = valor.getValor();
                            break;
                        }
                    } else {
                        // +milionária
                        if (Objects.equals(modalidadeCombo.getQuantidadeDezenas(), valor.getNumeroPrognosticos()) &&
                                Objects.equals(modalidadeCombo.getQuantidadeDezenasTrevos(), valor.getNumeroTrevos())) {
                            valorAposta = valor.getValor();
                            break;
                        }
                    }
                }

                BigDecimal soma = valorAposta
                        .multiply(BigDecimal.valueOf(qtdApostas))
                        .multiply(BigDecimal.valueOf(qtdTeimosinhas));
                valorCombo = valorCombo.add(soma);
            }

            txtValorCombo.setText(ViewUtils.getMoedaFormat(valorCombo));
        }
    }

}
