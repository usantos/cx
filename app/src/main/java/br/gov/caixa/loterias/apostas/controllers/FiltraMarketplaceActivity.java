package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FiltroMarketplaceModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaModalidadesAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotericaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.MoedaMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class FiltraMarketplaceActivity extends LoteriasAppMarketPlaceActivity {

    public static final String FILTRO_MARKETPLACE_RETORNO = "FILTRO_MARKETPLACE_RETORNO";

    private RecyclerView listView;
    private ListaModalidadesAdapter listAdapter;

    private EditText editValMin, editValMax;
    private RangeSlider sliderQtdCotas;

    private EditText editLoterica;
    private RecyclerView recyclerViewLoterica;
    private LotericaAdapter lotericaAdapter;
    private Button btnLimpaFiltros;
    private Button btnAplicar;

    private List<LotericaDTO> listLotericaDTO = new ArrayList<>();
    private LotericaDTO selectLotericaDTO = null;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    private TextWatcher textWatcher;

    private FiltroMarketplaceModel model;

//    private AutoCompleteTextView autoCompleteLoterica;
//    private LotericaArrayAdapter lotericaArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtra_marketplace );
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getResources().getString(R.string.filtrar)));

        model =  new FiltroMarketplaceModel(this);

        setaViews();
        setaRangeComponentes();
        limpaFiltros();
        setaMetodos();
        startFragmentSomadorCarrinho();
    }

    private void startFragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
        }else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == 1) {
//            //callWebservice();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setaViews() {

        listView = findViewById(R.id.lista_modalidades);

        editValMin = findViewById(R.id.edit_val_min);
        editValMax = findViewById(R.id.edit_val_max);

        sliderQtdCotas = findViewById(R.id.sliderQuantidadeCotas);
        editLoterica = findViewById(R.id.edit_loterica);
        recyclerViewLoterica = findViewById(R.id.recyclerViewLoterica);

//        autoCompleteLoterica = findViewById(R.id.autoComplete_loterica);

        btnLimpaFiltros = findViewById(R.id.btn_limpar_filtros);
        btnAplicar = findViewById(R.id.btn_aplicar);
    }

    private void setaMetodos() {
        btnLimpaFiltros.setOnClickListener(v -> limpaFiltros());

        btnAplicar.setOnClickListener(v -> aplicarFiltro());

        List<ModalidadeDisponivelCota> listModalidadeDisponivel = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        listAdapter = new ListaModalidadesAdapter(listModalidadeDisponivel, onItemClickListenerModalidade(),this);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(listAdapter);

        lotericaAdapter = new LotericaAdapter(listLotericaDTO, onItemClickListener());
        recyclerViewLoterica.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLoterica.setAdapter(lotericaAdapter);

        editValMin.addTextChangedListener(new MoedaMaskTextWatcher(editValMin));

        editValMax.addTextChangedListener(new MoedaMaskTextWatcher(editValMax));

        textWatcher = MaskEditUtil.textWatcherOnTextTChangedListener(charSequence -> {
            onTextChanged(charSequence);
        });

        editLoterica.addTextChangedListener(textWatcher);
    }

    private OnItemClickListener onItemClickListenerModalidade() {
        return (holder, position) -> {
        };
    }

    private OnItemClickListener onItemClickListener() {
        return (holder, position) -> {
            LotericaDTO lotericaDTO = listLotericaDTO.get(position);
            editLoterica.setText(lotericaDTO.getNome().trim());
            recyclerViewLoterica.setVisibility(View.GONE);
            selectLotericaDTO = lotericaDTO;
        };
    }

    private void onTextChanged(String charSequence){
        editLoterica.removeTextChangedListener(textWatcher);

        filtraLoterica(charSequence.toString());

        editLoterica.setSelection(editLoterica.length());
        editLoterica.addTextChangedListener(textWatcher);
    }

    private void aplicarFiltro() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILTRO_MARKETPLACE_RETORNO, getExtraFiltro());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private FiltroAplicadoMarketplace getExtraFiltro() {
        return model.getFiltroAplicado(editValMin.getText().toString(),
                editValMax.getText().toString(),
                sliderQtdCotas.getValues().get(0),
                sliderQtdCotas.getValues().get(1),
                selectLotericaDTO);
    }

    private void setaRangeComponentes() {
        sliderQtdCotas.setValueFrom(2);
        sliderQtdCotas.setValueTo(100);
    }

    private void limpaFiltros() {
        sliderQtdCotas.setValues(2F,100F);
        editValMin.setText("");
        editValMax.setText("");
        editLoterica.setText("");
        selectLotericaDTO = null;
    }

    private void filtraLoterica(String text) {

        if (text.length() >= 6) {

            if (text.length() == 6) {
                AlertDialogUtils.show(this);
                model.buscaLotericas(text, onBuscaLotericasListener());
            } else {
                if (listLotericaDTO != null && listLotericaDTO.size()>0) {
                    apresentaListaFiltrada(model.getListaFiltrada(text, listLotericaDTO));
                }
            }
        } else {
            recyclerViewLoterica.setVisibility(View.GONE);
        }
    }

    private OnSilceListener<List<LotericaDTO>> onBuscaLotericasListener() {
        return new OnSilceListener<List<LotericaDTO>>() {
            @Override
            public void success(List<LotericaDTO> payload) {
                AlertDialogUtils.dismiss();
                listLotericaDTO = payload;
                apresentaListaFiltrada(listLotericaDTO);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void apresentaListaFiltrada(List<LotericaDTO> filteredList) {
        if (filteredList.isEmpty()){
            TestVisao.toast("Sua busca não retornou resultados.");
            return;
        }
        recyclerViewLoterica.setVisibility(View.VISIBLE);
        lotericaAdapter.filterList(filteredList);
    }

    private void callWebservice() {
        AlertDialogUtils.show(this);

        if(SessaoUsuarioUtil.precisaAtualizar()){
            final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(this);

            new ParametrosSimulacaoModel(this)
                    .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                        @Override
                        public void success(ParametrosSimulacao payload) {
                            AlertDialogUtils.dismiss();
                            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_modalidades));

                            //modalidadesParaListar = payload.getParametros();

                            //if (modalidadesParaListar == null || modalidadesParaListar.isEmpty()) {
                            //    Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                            //    appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                            //    startActivityForResult(appIndis, 1);
                            //}

                            SessaoUsuarioUtil.atualizaParametrosSingleton(payload);
                            //paramatroSimulacaoSuccess();
                        }

                        @Override
                        public void error(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            Intent appIndis = new Intent(FiltraMarketplaceActivity.this, AppIndisponivelActivity.class);
                            appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                            startActivityForResult(appIndis, 1);
                        }
                    });
//        }
//        else if(SessaoUsuario.getInstance().isPrecisaMontarCarrossel()){
//            modalidadesParaListar = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
//            paramatroSimulacaoSuccess();
//            AlertDialogUtils.dismiss();
        }else {
            AlertDialogUtils.dismiss();
        }

    }

}
