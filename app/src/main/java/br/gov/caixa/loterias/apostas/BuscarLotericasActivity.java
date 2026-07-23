package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaComposition;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.model.FiltroMarketplaceModel;
import br.gov.caixa.loterias.apostas.model.model.LotericaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.BuscaLotericaAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class BuscarLotericasActivity extends LoteriasBaseAppActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private TextInputEditText etLottery;
    private RecyclerView rvFavorites;
    private ImageButton btnDuvidas;
    private TextView toolbarTitle, tvLotericas;
    private FiltroMarketplaceModel model;
    private List<LotericaComposition> listLotericaDTO = new ArrayList<>();
    private List<LotericaFavoritaDTO> listLoteriaFav = new ArrayList<>();
    private BuscaLotericaAdapter buscaLotericaAdapter;
    private Toolbar toolbar;

    private LotericaFavoritaModel lotericaFavoritaModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_lotericas);

        model = new FiltroMarketplaceModel(this);
        lotericaFavoritaModel = new LotericaFavoritaModel(this);

        AlertDialogUtils.show(BuscarLotericasActivity.this);
        searchForLotericasFavoritas();

        setViews();
        setMethods();
    }

    private void searchForLotericasFavoritas() {
        lotericaFavoritaModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
            @Override
            public void success(List<LotericaFavoritaDTO> payload) {
                listLoteriaFav = payload;
                AlertDialogUtils.dismiss();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void setMethods() {
        etLottery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                final String query = s.toString().trim();

                searchRunnable = () -> {
                    if (query.length() < 2) {
                        listLotericaDTO.clear();
                        buscaLotericaAdapter.submitList(new ArrayList<>(listLotericaDTO));
                        rvFavorites.setVisibility(View.GONE);
                    }

                    rvFavorites.setVisibility(View.VISIBLE);
                    model.buscaLotericas(query, onBuscaLotericasListener(query));
                };

                handler.postDelayed(searchRunnable, 400);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnDuvidas.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
            startActivity(intent);
        });
    }

    private void setViews() {
        etLottery = findViewById(R.id.et_lottery);
        rvFavorites = findViewById(R.id.rv_body);
        btnDuvidas = findViewById(R.id.btn_duvidas_mkp);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvLotericas = findViewById(R.id.tv_lotericas);
        ViewCompat.setAccessibilityHeading(toolbarTitle,true);
        ViewCompat.setAccessibilityHeading(tvLotericas,true);

        configToolbar();

        buscaLotericaAdapter = new BuscaLotericaAdapter(listLotericaDTO, onItemClickListener());
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(buscaLotericaAdapter);
    }

    private void configToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.seta_esquerda);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setHomeActionContentDescription(R.string.txt_voltar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private OnSilceListener<List<LotericaDTO>> onBuscaLotericasListener(String query) {
        return new OnSilceListener<List<LotericaDTO>>() {
            @Override
            public void success(List<LotericaDTO> payload) {
                String textoAtual = etLottery.getText().toString().trim();

                if(!query.equals(textoAtual)) {
                    return;
                }

                List<LotericaComposition> itens = new ArrayList<>();
                for (LotericaDTO each : payload) {
                    LotericaComposition model = new LotericaComposition(each);
                    for (LotericaFavoritaDTO favorita : listLoteriaFav) {
                        if (favorita.getCodigo().equals(each.getId())) {
                            model.setFavorite(true);
                        }
                    }
                    itens.add(model);
                }
                listLotericaDTO = itens;
                AlertDialogUtils.dismiss();
                apresentaLista(listLotericaDTO);
            }

            @Override
            public void error(VolleyError error) {
                String textoAtual = etLottery.getText().toString().trim();

                if(!query.equals(textoAtual)) {
                    return;
                }

                AlertDialogUtils.dismiss();
            }
        };
    }

    private void apresentaLista(List<LotericaComposition> list) {
        if (list.isEmpty()){
            TestVisao.toast("Sua busca não retornou resultados.");
            return;
        }
        rvFavorites.setVisibility(View.VISIBLE);
        buscaLotericaAdapter.submitList(list);
    }

    private OnItemClickListener onItemClickListener() {
        return (holder, position) -> {
            LotericaComposition lotericaDTO = listLotericaDTO.get(position);
            if (!lotericaDTO.isFavorite()) {
                favoritar(lotericaDTO, position);
            } else {
                desfavoritar(lotericaDTO, position);
            }

        };
    }

    private void desfavoritar(LotericaComposition lotericaDTO, int position) {
        DialogUtils.dialogTituloConfirmar(this, "Remover lotérica favorita",
                "Deseja excluir "+ lotericaDTO.getLotericaDTO().getNomeFantasia() + " da sua lista de favoritas?",
                (dialog, which) -> {
                    AlertDialogUtils.show(this);
                    lotericaFavoritaModel.excluirLotericaFavorita(lotericaDTO.getLotericaDTO().getId(), new OnSilceListener<RetornoPadraoResponse>() {
                        @Override
                        public void success(RetornoPadraoResponse payload) {
                            lotericaDTO.setFavorite(!lotericaDTO.isFavorite());
                            buscaLotericaAdapter.notifyItemChanged(position);
                            AlertDialogUtils.dismiss();
                        }

                        @Override
                        public void error(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            Toast.makeText(BuscarLotericasActivity.this, "Não foi possível remover de favoritas.", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    private void favoritar(LotericaComposition lotericaDTO, int position) {
        DialogUtils.dialogTituloConfirmar(this, "Favoritar lotérica",
                "Deseja incluir "+ lotericaDTO.getLotericaDTO().getNomeFantasia() + " na sua lista de favoritas?",
                (dialog, which) -> {
                    AlertDialogUtils.show(this);
                    lotericaFavoritaModel.incluirLotericaFavorita(lotericaDTO.getLotericaDTO().getId(), new OnSilceListener<RetornoPadraoResponse>() {
                        @Override
                        public void success(RetornoPadraoResponse payload) {
                            lotericaDTO.setFavorite(!lotericaDTO.isFavorite());
                            buscaLotericaAdapter.notifyItemChanged(position);
                            AlertDialogUtils.dismiss();
                        }

                        @Override
                        public void error(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            Toast.makeText(BuscarLotericasActivity.this, "Não foi possível favoritar.", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }
}