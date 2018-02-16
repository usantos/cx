package caixa.gov.br.loterias.view;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.DetalheDuvidaAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

@EActivity(R.layout.activity_detalhe_duvida)
public class DetalheDuvidaActivity extends AppCompatActivity {

    @ViewById
    ExpandableHeightRecyclerView recyclerViewDetalhesDuvida;

    @Extra
    String nome;

    @AfterViews
    public void init() {

        //TODO mock
        List<DTOEnumCharacter> mockList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DTOEnumCharacter dtoEnumCharacterItem = new DTOEnumCharacter();
            dtoEnumCharacterItem.setValor("Bibendum laoreet. Proin 0" + i);
            dtoEnumCharacterItem.setDescricao("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce vel lorem quis eros fringilla fermentum. Cras mattis cursus pellentesque. Sed euismod sapien a lacus laoreet cursus. Morbi sit amet ipsum ac nunc iaculis euismod. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nullam sed nisl et neque consectetur maximus at at sapien.");
            mockList.add(dtoEnumCharacterItem);
        }

        DetalheDuvidaAdapter detalheDuvidaAdapter = new DetalheDuvidaAdapter(mockList);
        recyclerViewDetalhesDuvida.setAdapter(detalheDuvidaAdapter);
        recyclerViewDetalhesDuvida.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetalhesDuvida.setExpanded(Boolean.TRUE);

        createToolBar();
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBold(getBaseContext(), "_" + nome + "_"));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return Boolean.TRUE;
    }
}
