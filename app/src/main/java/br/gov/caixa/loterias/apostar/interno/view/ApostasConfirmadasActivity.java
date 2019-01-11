package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ApostasConfirmadasFragment_;

/**
 * Created by pmotta on 13/03/2018.
 * Class ApostasConfirmadasActivity
 */

@OptionsMenu(R.menu.menu_apostas_confirmadas)
@EActivity(R.layout.activity_apostas_confirmadas)
public class ApostasConfirmadasActivity extends AppCompatActivity {

    private static final String CONFIRMADAS_FRAGMENT = "ConfirmadasFragment";
    private static final String HISTORICO_FRAGMENT = "HistoricoFragment";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById
    protected NestedScrollView scrollView;

    public interface OnScrollReachBottomListener{
        void onScrollReachBottom();
    }

    @AfterViews
    protected void init() {
        setupToolbar();
        initApostasFragment();
        setupScrollView();
    }

    private void setupScrollView() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (View) v.getChildAt(v.getChildCount() - 1);
                int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
                // if diff is zero, then the bottom has been reached
                if (diff == 0) {
                    Fragment confirmadasFragment = getSupportFragmentManager().findFragmentByTag(CONFIRMADAS_FRAGMENT);
                    Fragment historicoFragment = getSupportFragmentManager().findFragmentByTag(HISTORICO_FRAGMENT);
                    if(confirmadasFragment != null && confirmadasFragment.isVisible()){
                        if(confirmadasFragment instanceof OnScrollReachBottomListener){
                            ((OnScrollReachBottomListener) confirmadasFragment).onScrollReachBottom();
                        }
                    }else if(historicoFragment != null && historicoFragment.isVisible()){
                        if(historicoFragment instanceof OnScrollReachBottomListener){
                            ((OnScrollReachBottomListener) historicoFragment).onScrollReachBottom();
                        }
                    }
                }
            }
        });
    }

    private void initApostasFragment(){
        Fragment frag = getSupportFragmentManager().findFragmentByTag(CONFIRMADAS_FRAGMENT);
        if(frag == null){
            frag = ApostasConfirmadasFragment_.builder().build();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag, CONFIRMADAS_FRAGMENT)
                .commit();

        getSupportFragmentManager().executePendingTransactions();
    }

    @Click
    protected void verResultadosBtn(){
        initApostasFragment();
    }

    @Click
    protected void historicoApostasBtn(){
        Fragment frag = getSupportFragmentManager().findFragmentByTag(HISTORICO_FRAGMENT);
        if(frag == null){
            frag = ApostasConfirmadasFragment_.builder().isHistorico(true).build();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag, HISTORICO_FRAGMENT)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
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
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getTitle().toString()));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
