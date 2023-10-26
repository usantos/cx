package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotericaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class FiltraMarketplaceActivity extends LoteriasAppMarketPlaceActivity {

    private EditText editValMin, editValMax;
    private RangeSlider sliderQtdCotas;
    //private AppCompatImageView accordionQueroButton;
    //private LinearLayout accordionContentQuero;
    private EditText editLoterica;
    private RecyclerView recyclerViewLoterica;
    private LotericaAdapter lotericaAdapter;
    private Button btnLimpaFiltros;
    private Button btnAplicar;

    private List<LotericaDTO> listLotericaDTO = null;
    private LotericaDTO selectLotericaDTO = null;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

//    private AutoCompleteTextView autoCompleteLoterica;
//    private LotericaArrayAdapter lotericaArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtra_marketplace );
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getResources().getString(R.string.filtrar)));

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

        //accordionContentQuero = findViewById(R.id.accordionContentQuero);
        //accordionQueroButton = findViewById(R.id.img_accordion_quero);

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
//        accordionQueroButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (accordionContentQuero.getVisibility() == View.VISIBLE) {
//                    accordionContentQuero.setVisibility(View.GONE);
//                    accordionQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_plus));
//                } else {
//                    accordionContentQuero.setVisibility(View.VISIBLE);
//                    accordionQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_minus));
//                }
//            }
//        });

        btnLimpaFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaFiltros();
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltroMarketplace filtroMarketplace = new FiltroMarketplace(
                    editValMin.getText().toString(),
                    editValMax.getText().toString(),
                    sliderQtdCotas.getValueFrom(),
                    sliderQtdCotas.getValueTo(),
                    selectLotericaDTO);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("FILTRO_MARKETPLACE_RETORNO", filtroMarketplace);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        lotericaAdapter = new LotericaAdapter(listLotericaDTO, new LotericaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LotericaDTO lotericaDTO) {
                editLoterica.setText(lotericaDTO.getNome().trim());
                recyclerViewLoterica.setVisibility(View.GONE);
                selectLotericaDTO = lotericaDTO;
            }
        });
        recyclerViewLoterica.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLoterica.setAdapter(lotericaAdapter);

        editValMin.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().length() == 0) {
                    current = charSequence.toString();
                    return;
                }
                if (!charSequence.toString().equals(current)) {
                    editValMin.removeTextChangedListener(this);
                    String cleanString = charSequence.toString().replaceAll("[,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formated = StringUtils.formatToCurrency(parsed/100);
                    current = formated;
                    editValMin.setText(formated);
                    editValMin.setSelection(formated.length());
                    editValMin.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editValMax.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().length() == 0) {
                    current = charSequence.toString();
                    return;
                }
                if (!charSequence.toString().equals(current)) {
                    editValMax.removeTextChangedListener(this);
                    String cleanString = charSequence.toString().replaceAll("[,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formated = StringUtils.formatToCurrency(parsed/100);
                    current = formated;
                    editValMax.setText(formated);
                    editValMax.setSelection(formated.length());
                    editValMax.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editLoterica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editLoterica.removeTextChangedListener(this);

                filtraLoterica(charSequence.toString());

                editLoterica.setSelection(editLoterica.length());
                editLoterica.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

//        //Auto
//        listLotericaDTO = new ArrayList<>();
//        //lotericaArrayAdapter = new LotericaArrayAdapter(this, android.R.layout.simple_list_item_activated_1, listLotericaDTO);
//        lotericaArrayAdapter = new LotericaArrayAdapter(this, R.layout.item_loterica, listLotericaDTO);
//        autoCompleteLoterica.setAdapter(lotericaArrayAdapter);
//        autoCompleteLoterica.setThreshold(1);
////        autoCompleteLoterica.setOnClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                //pega o item selecionado
////            }
////        });
//        //*** ACERTAR ***
//        buscaLoterica( "maria ");
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
                final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
                DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(this);
                //dadosCorporativosBO.buscarLoteriasNomeFantasia(text, new RequestListener<LotericaDTOResponse>() {
                dadosCorporativosBO.buscarLoteriasNomeCodigo(text, new RequestListener<LotericaDTOResponse>() {
                    @Override
                    public void onResponse(LotericaDTOResponse response) {
                        loadViewProgress.dismiss();
                        listLotericaDTO = response.getPayload();
                        recyclerViewLoterica.setVisibility(View.VISIBLE);
                        lotericaAdapter.filterList(listLotericaDTO);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                    }
                });

            } else {

                if (listLotericaDTO != null && listLotericaDTO.size()>0) {
                    List<LotericaDTO> filteredList = new ArrayList<>();
                    for (LotericaDTO item : listLotericaDTO) {
                        if (item.getNome().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    recyclerViewLoterica.setVisibility(View.VISIBLE);
                    lotericaAdapter.filterList(filteredList);
                }
            }
        } else {
            recyclerViewLoterica.setVisibility(View.GONE);
        }
    }


//    private void buscaLoterica(String text) {
//        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
//        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(this);
//        //dadosCorporativosBO.buscarLoteriasNomeFantasia(text, new RequestListener<LotericaDTOResponse>() {
//        dadosCorporativosBO.buscarLoteriasNomeCodigo(text, new RequestListener<LotericaDTOResponse>() {
//            @Override
//            public void onResponse(LotericaDTOResponse response) {
//                loadViewProgress.dismiss();
//                listLotericaDTO = response.getPayload();
//                lotericaArrayAdapter.setLotericaDTO(listLotericaDTO);
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loadViewProgress.dismiss();
//            }
//        });
//    }
}
