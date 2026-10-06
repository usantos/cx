package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.ComboModel;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheComboAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.viewModel.DetalheComboViewModel;

public class DetalheComboActivity extends SettingToolbarActivity {

    private static final String ARG_TIPOAPOSTA = "tipoAposta";
    private static final String ARG_MODALIDADE = "modalidade";
    private static final String ARG_VALORAPOSTA = "valorAposta";
    private static final int QUANTIDADE_COLUNA_LISTA_COMBOS = 2;
    private Button btnAdicionarAoCarrinho;
    private TextView txtNomeCombo, txtQtdApostas, txtValorCombo, txtDescricaoCombo;
    private CheckBox checkMostrarNumeros;
    private ImageButton btnIncrementarAposta, btnDecrementarAposta;
    private RecyclerView DetalheComboRcv;
    private DetalheComboAdapter detalheComboAdapter;
    private Toolbar toolbar;

    private DetalheComboViewModel viewModel;

    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private final ActivityResultLauncher<Intent> carrinhoLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == ResultadoNavegacaoAposta.RESULT_RESET) {

                            setResult(
                                    ResultadoNavegacaoAposta.RESULT_RESET
                            );
                            finish();
                        }
                    }
            );

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

    private void getExtras() {
        if (getIntent() != null && getIntent().hasExtra(CombosActivity.ARG_COMBO)) {
            viewModel = new ViewModelProvider(this).get(DetalheComboViewModel.class);
            viewModel.carregarCombo(new Gson().fromJson(getIntent().getStringExtra(CombosActivity.ARG_COMBO), CombosDTO.class));
        } else {
            Log.d("DetalheComboActivity", "Nenhum combo recebido");
        }
    }

    private void setViews() {

        btnAdicionarAoCarrinho = findViewById(R.id.btnAdicionarAoCarrinho);
        txtNomeCombo = findViewById(R.id.txtNomeDetalheCombo);
        txtDescricaoCombo = findViewById(R.id.txtHeaderDetalheCombo);
        txtQtdApostas = findViewById(R.id.txtQtdDeApostas);
        txtValorCombo = findViewById(R.id.txtValorCombo);
        DetalheComboRcv = findViewById(R.id.rcvDetalheCombo);
        checkMostrarNumeros = findViewById(R.id.checkBoxNumerosEscolhidos);
        btnIncrementarAposta = findViewById(R.id.btnIncrementarApostas);
        btnDecrementarAposta = findViewById(R.id.btnDecrementarApostas);

        View view_id_fg_somador_carrinho = findViewById(R.id.id_somador_carrinho);
        view_id_fg_somador_carrinho.setOnClickListener(view -> vaiProCarrinho());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.titulo_combos)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void setMetodos() {

        if (Objects.equals(viewModel.getCombo().getTipoCombo().getCodigo(), CombosDTO.TipoComboEnum.ESPECIAL.getValue())) {
            ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(viewModel.getCombo().getModalidadesCombo().get(0).getModalidade().getValor());
            if (modalidade != null) {
                EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
                setDrawable(txtNomeCombo, estilo.getTrevoFundoClaro());
                txtNomeCombo.setTextColor(ContextCompat.getColor(DetalheComboActivity.this, estilo.getCorLetraLista()));
                String nomeEspecial = viewModel.getCombo().getTipoCombo().getNome() + " " + viewModel.getCombo().getModalidadesCombo().get(0).getModalidade().getDescricaoEspecial();
                txtNomeCombo.setText(nomeEspecial);
            }

        } else {
            txtNomeCombo.setText(viewModel.getCombo().getTipoCombo().getNome());
        }

        if (viewModel.getCombo() != null && viewModel.getCombo().getTipoCombo() != null
                && viewModel.getCombo().getTipoCombo().getDescricao() != null
                && !viewModel.getCombo().getTipoCombo().getDescricao().isEmpty()) {
            this.txtDescricaoCombo.setText(viewModel.getCombo().getTipoCombo().getDescricao());
        }

        if (viewModel.getValorComboLiveData() != null) {
            viewModel.getValorComboLiveData().observe(this, bigDecimal -> {
                if (bigDecimal != null) {
                    txtValorCombo.setText(ViewUtils.getMoedaFormat(bigDecimal));
                } else {
                    txtValorCombo.setText(getString(R.string.r_00_00));
                }
            });
        }

        btnAdicionarAoCarrinho.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.adicionarAoCarrinho)));

        if (viewModel.getQtdTotalApostasLiveData() != null) {
            viewModel.getQtdTotalApostasLiveData().observe(this, integer -> {
                if (integer != null) {
                    txtQtdApostas.setText(String.valueOf(integer));
                }
            });
        }

        btnAdicionarAoCarrinho.setOnClickListener(view -> incluirComboNoCarrinho());

        btnIncrementarAposta.setOnClickListener(view -> viewModel.incrementarApostas());

        btnDecrementarAposta.setOnClickListener(view -> viewModel.decrementarApostas());
    }

    private void configuraDetalheComboRcv() {

        if (viewModel.getModalidadesComboLiveData().getValue() != null && !viewModel.getModalidadesComboLiveData().getValue().isEmpty()) {

            GridLayoutManager layoutDetalheCombos = getGridLayoutManager(DetalheComboActivity.this, viewModel.getModalidadesComboLiveData().getValue().size());
            DetalheComboRcv.setLayoutManager(layoutDetalheCombos);
            DetalheComboRcv.setNestedScrollingEnabled(false);

            detalheComboAdapter = new DetalheComboAdapter();

            DetalheComboRcv.setAdapter(detalheComboAdapter);

            if (viewModel.getModalidadesComboLiveData() != null) {
                viewModel.getModalidadesComboLiveData().observe(this, modalidadeCombo -> {
                    if (modalidadeCombo != null) {
                        detalheComboAdapter.submitList(modalidadeCombo);
                    }
                });
            }

        } else {
            DialogUtils.dialogEntendiListener(DetalheComboActivity.this, getString(R.string.nao_concluiu_operacao),
                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
        }
    }

    private void incluirComboNoCarrinho() {

        Boolean isSurpresinha = !checkMostrarNumeros.isChecked();

        IncluirComboDTO incluirComboDTO = new IncluirComboDTO();

        incluirComboDTO.setId(viewModel.getCombo().getTipoCombo().getCodigo());
        incluirComboDTO.setQtdApostas(viewModel.getQtdTotalApostasLiveData().getValue());
        incluirComboDTO.setIsSurpresinha(isSurpresinha);
        incluirComboDTO.setLimparCarrinho(false);

        new ComboModel(this).adicionaComboCarrinho(incluirComboDTO, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                Intent intent = new Intent(
                        DetalheComboActivity.this,
                        CarrinhoActivity.class
                );
                carrinhoLauncher.launch(intent);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect(error, DetalheComboActivity.this);
            }
        });
    }


    private void fragmentSomadorCarrinho() {
        if (somadorCarrinhoFragment == null) {
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho, "TELA DETALHE COMBOS");
        } else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

    protected void vaiProCarrinho() {
        if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
            startActivity(new Intent(DetalheComboActivity.this, CarrinhoActivity.class));
        }
    }

    private void setDrawable(TextView textView, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable != null) {
            int widthPx = (int) (20 * getResources().getDisplayMetrics().density);
            int heightPx = (int) (20 * getResources().getDisplayMetrics().density);
            drawable.setBounds(0, 0, widthPx, heightPx);
            textView.setCompoundDrawablesRelative(drawable, null, null, null);
        }
    }

    private static @NotNull GridLayoutManager getGridLayoutManager(DetalheComboActivity detalheCombosActivity, Integer qtdItens) {
        GridLayoutManager layout = new GridLayoutManager(detalheCombosActivity, QUANTIDADE_COLUNA_LISTA_COMBOS);

        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (qtdItens % 2 != 0 && position == (qtdItens - 1)) {
                    return QUANTIDADE_COLUNA_LISTA_COMBOS;
                } else {
                    return 1;
                }
            }
        });
        return layout;
    }
}
