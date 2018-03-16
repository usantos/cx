package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.fragment.ApostasConfirmadasFragment_;
import caixa.gov.br.loterias.view.fragment.ApostasHistoricoFragment_;

/**
 * Created by pmotta on 13/03/2018.
 */

@OptionsMenu(R.menu.menu_apostas_confirmadas)
@EActivity(R.layout.activity_apostas_confirmadas)
public class ApostasConfirmadasActivity extends AppCompatActivity {

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @AfterViews
    protected void init(){
        setupToolbar();
        initApostasFragment();
    }

    private void initApostasFragment(){
        Fragment frag = ApostasConfirmadasFragment_.builder().build();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }

    @Click
    protected void verResultadosBtn(){
        initApostasFragment();
    }

    @Click
    protected void historicoApostasBtn(){
        Fragment frag = ApostasHistoricoFragment_.builder().build();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }

    @OptionsItem(R.id.action_duvida)
    protected void onMenuDuvida(){
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getTitle().toString()));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
