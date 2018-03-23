package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaNumeroDetalhes_;

/**
 * Created by pmotta on 22/03/2018.
 */

@EActivity(R.layout.activity_aposta_confirmada_detalhes)
@OptionsMenu(R.menu.menu_apostas_confirmadas)
public class ApostaConfirmadaDetalhesActivity extends AppCompatActivity {

    @Extra
    protected ApostaDTO apostaDTO;

    @ViewById
    protected FrameLayout fragmentContainer;

    @ViewById
    protected Toolbar toolbar;
    private EstiloModalidade estilo;

    @Click
    protected void carrinhoApostasBtn(){
        //TODO:
    }

    @Click
    protected void favoriteBtn(){
        //TODO:
    }

    @Click
    protected void editarApostaBtn(){
        //TODO:
    }

    @Click
    protected void adicionarCarrinhoBtn(){
        //TODO:
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        estilo = new EstiloModalidade(apostaDTO.getModalidade());
        setTheme(estilo.getThemeId());
        setTitle(AppUtils.capitalizeFirstChar(getString(estilo.getStringId())));
    }

    @OptionsMenuItem(R.id.action_duvida)
    protected void menuDuvida(MenuItem menuDuvida){
        Drawable drawable = menuDuvida.getIcon();
        if (drawable != null && estilo != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, estilo.getMenuItemColor()),
                    PorterDuff.Mode.SRC_ATOP);
        }
    }

    @OptionsItem(R.id.action_duvida)
    protected void onMenuDuvida(){
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @AfterViews
    protected void init(){
        setupToolbar();

        Fragment fragment;

        switch (apostaDTO.getModalidade()){
            case LOTOGOL:
                //TODO:
                fragment = new Fragment();
                break;
            case LOTECA:
                //TODO:
                fragment = new Fragment();
                break;
            default:
                fragment = ApostaConfirmadaNumeroDetalhes_
                        .builder()
                        .apostaDTO(apostaDTO)
                        .build();
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }
    }


}
