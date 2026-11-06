package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaComposition;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.model.FiltroMarketplaceModel;
import br.gov.caixa.loterias.apostas.model.model.LotericaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.BuscaLotericaAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class BuscarLotericasActivity extends LoteriasBaseAppActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private EditText edtNome;
    private RecyclerView rvSearchResults;
    private ImageButton btnDuvidas;
    private TextView toolbarTitle;
    private TextView tvLotericas;
    private ImageButton btnBack;
    private ConstraintLayout clEmptyState;
    private BuscaLotericaAdapter adapter;
    private List<LotericaComposition> searchResults = new ArrayList<>();
    private List<LotericaFavoritaDTO> favoriteLotericas = new ArrayList<>();
    private FiltroMarketplaceModel marketplaceModel;
    private LotericaFavoritaModel favoriteModel;
    private static final int SEARCH_DELAY = 400;
    private static final int MIN_SEARCH_LENGTH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_lotericas);

        marketplaceModel = new FiltroMarketplaceModel(this);
        favoriteModel = new LotericaFavoritaModel(this);

        AlertDialogUtils.show(BuscarLotericasActivity.this);

        loadFavoriteLotericas();

        setViews();
        setMethods();
    }

    private void loadFavoriteLotericas() {
        favoriteModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
            @Override
            public void success(List<LotericaFavoritaDTO> payload) {
                favoriteLotericas = payload;
                AlertDialogUtils.dismiss();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void setMethods() {
        edtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                scheduleSearch(s.toString().trim());
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

    private void scheduleSearch(String query) {
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }

        searchRunnable = () -> {
            executeSearch(query);
        };

        handler.postDelayed(searchRunnable, SEARCH_DELAY);
    }

    private void executeSearch(String query) {
        if (query.length() < MIN_SEARCH_LENGTH) {
            clearResults();
            return;
        }

        showResults(true);
        marketplaceModel.buscaLotericas(query, onBuscaLotericasListener(query));
    }

    private void clearResults() {
        searchResults.clear();
        adapter.submitList(new ArrayList<>(searchResults));
        showResults(false);
    }

    private boolean isCurrentSearch(String query) {
        return query.equals(edtNome.getText().toString().trim());
    }

    private void setViews() {
        edtNome = findViewById(R.id.edt_nome);
        rvSearchResults = findViewById(R.id.rv_body);
        btnDuvidas = findViewById(R.id.btn_duvidas_mkp);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvLotericas = findViewById(R.id.tv_lotericas);
        clEmptyState = findViewById(R.id.cl_body);
        ViewCompat.setAccessibilityHeading(toolbarTitle,true);
        ViewCompat.setAccessibilityHeading(tvLotericas,true);

        configToolbar();

        adapter = new BuscaLotericaAdapter(searchResults, onItemClickListener());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(adapter);
    }

    private void configToolbar() {
        btnBack = findViewById(R.id.customButton);
        btnBack.setOnClickListener(view -> finish());
    }

    private OnSilceListener<List<LotericaDTO>> onBuscaLotericasListener(String query) {
        return new OnSilceListener<List<LotericaDTO>>() {
            @Override
            public void success(List<LotericaDTO> payload) {
                if(!isCurrentSearch(query)) {
                    return;
                }

                AlertDialogUtils.dismiss();

                apresentaLista(mapLotericas(payload));
            }

            @Override
            public void error(VolleyError error) {
                if(!isCurrentSearch(query)) {
                    return;
                }

                AlertDialogUtils.dismiss();
            }
        };
    }

    private void apresentaLista(List<LotericaComposition> list) {
        searchResults.clear();
        searchResults.addAll(list);
        adapter.submitList(list);
        showResults(!searchResults.isEmpty());
    }

    private Set<Long> getFavoritesIds() {
        Set<Long> favoriteIds = new HashSet<>();

        for (LotericaFavoritaDTO favorite : favoriteLotericas) {
            favoriteIds.add(favorite.getCodigo());
        }

        return favoriteIds;
    }

    private List<LotericaComposition> mapLotericas(List<LotericaDTO> payload) {
        Set<Long> favoriteIds = getFavoritesIds();

        List<LotericaComposition> result = new ArrayList<>();

        for (LotericaDTO dto : payload) {
            LotericaComposition composition = new LotericaComposition(dto);

            composition.setFavorite(favoriteIds.contains(dto.getId()));

            result.add(composition);
        }

        return result;
    }

    private OnItemClickListener onItemClickListener() {
        return (holder, position) -> {
            LotericaComposition item = searchResults.get(position);
            toggleFavorite(item, position);
        };
    }

    private void toggleFavorite(LotericaComposition item, int position) {
        updateFavorite(item, position, !item.isFavorite());
    }

    private void updateFavorite(LotericaComposition item, int position, boolean shouldFavorite) {
        DialogUtils.dialogTituloConfirmar(
                this,
                getDialogTitle(shouldFavorite),
                getDialogMessage(item, shouldFavorite),
                (dialog, which) -> {
                    AlertDialogUtils.show(this);
                    executeFavoriteRequest(
                            item, position, shouldFavorite
                    );
                }
        );
    }

    private String getDialogMessage(LotericaComposition item, boolean shouldFavorite) {
        String nome = item.getLotericaDTO().getNomeFantasia().trim();
        if(shouldFavorite) {
            return "Deseja incluir " + nome.toUpperCase() + " na sua lista de favoritas?";
        }
        return "Deseja excluir " + nome.toUpperCase() + " da sua lista de favoritas?";
    }

    private String getDialogTitle(boolean shouldFavorite) {
        return shouldFavorite? "Favoritar lotérica" : "Remover lotérica favorita";
    }

    private void executeFavoriteRequest(LotericaComposition item, int position, boolean shouldFavorite) {
        OnSilceListener<RetornoPadraoResponse> listener = createFavoriteListener(item, position);

        if(shouldFavorite) {
            favoriteModel.incluirLotericaFavorita(item.getLotericaDTO().getId(), listener);
        } else {
            favoriteModel.excluirLotericaFavorita(item.getLotericaDTO().getId(), listener);
        }
    }

    private OnSilceListener<RetornoPadraoResponse> createFavoriteListener(LotericaComposition item, int position) {
        return new OnSilceListener<RetornoPadraoResponse>() {
            @Override
            public void success(RetornoPadraoResponse payload) {
                item.setFavorite(!item.isFavorite());
                adapter.notifyItemChanged(position);
                AlertDialogUtils.dismiss();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                Toast.makeText(BuscarLotericasActivity.this, getErrorMessage(item.isFavorite()), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private String getErrorMessage(boolean currentFavorite) {
        return currentFavorite ? "Não foi possível remover de favoritas." : "Não foi possível favoritar";
    }


    private void showResults(boolean showList) {
        rvSearchResults.setVisibility(showList ? View.VISIBLE : View.GONE);
        clEmptyState.setVisibility(showList ? View.GONE : View.VISIBLE);
    }
}