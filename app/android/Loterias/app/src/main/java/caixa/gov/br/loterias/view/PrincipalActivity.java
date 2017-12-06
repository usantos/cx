package caixa.gov.br.loterias.view;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

import caixa.gov.br.loterias.mock.ModalidadesMock;
import caixa.gov.br.loterias.model.Modalidade;
import caixa.gov.br.loterias.utils.AnimacoesAcessoRapido;
import caixa.gov.br.loterias.utils.TipoModalidadeEnum;
import caixa.gov.br.loterias.view.adapter.HomeAdapter;


import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import caixa.gov.br.loterias.R;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DiscreteScrollView.ScrollListener<HomeAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<HomeAdapter.ViewHolder>,
        View.OnClickListener {

    private ArgbEvaluator evaluator;
    private int currentOverlayColor;
    private int overlayColor;
    private Toolbar toolbar;
    private RelativeLayout relativePrincipal;
    private RelativeLayout relativeBlankView;
    private Button buttonHomeBet;

    private View itemToolbarTrevo;
    private View itemToolbarSettings;


    private LinearLayout linearAtalhoRapidoLoterias;
    private RelativeLayout relativeLayoutContentRapidao;
    private RelativeLayout relativeLayoutContentLerBilhetes;
    private RelativeLayout relativeLayoutContentFavoritas;
    private RelativeLayout relativeLayoutContentRepassados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        evaluator = new ArgbEvaluator();
        currentOverlayColor = ContextCompat.getColor(this, R.color.verdesenaclaro);
        overlayColor = ContextCompat.getColor(this, R.color.cinza);

        ModalidadesMock modalidade = ModalidadesMock.get();
        List<Modalidade> data = modalidade.getData();
        DiscreteScrollView itemCarousel = (DiscreteScrollView) findViewById(R.id.home_carousel);
        buttonHomeBet = (Button) findViewById(R.id.buttonHomeBet);
        itemCarousel.setAdapter(new HomeAdapter(this, data, findViewById(R.id.buttonHomeBet)));
        itemCarousel.addScrollListener(this);
        itemCarousel.addOnItemChangedListener(this);
        itemCarousel.scrollToPosition(0);
        itemCarousel.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        findViewById(R.id.buttonHomeBet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity_.class);
                activity.putExtra("tipoAposta", TipoModalidadeEnum.MEGASENA);
                startActivity(activity);
            }
        });

        toolbarListener();
        listenerRepassados();

    }

    private void toolbarListener(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        relativePrincipal = (RelativeLayout) findViewById(R.id.RelativeContentPrincipal);
        relativeBlankView = (RelativeLayout) findViewById(R.id.RelativeBlankView);
        linearAtalhoRapidoLoterias = (LinearLayout) findViewById(R.id.atalhoRapidoLoterias);
        relativeLayoutContentRapidao = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRapidao);
        relativeLayoutContentLerBilhetes = (RelativeLayout) findViewById(R.id.RelativeLayoutContentLerBilhetes);
        relativeLayoutContentFavoritas = (RelativeLayout) findViewById(R.id.RelativeLayoutContentFavoritas);
        relativeLayoutContentRepassados = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRepassados);

        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                itemToolbarTrevo = toolbar.findViewById(R.id.action_apresentar_trevo);
                if (itemToolbarTrevo != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    itemToolbarTrevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (relativePrincipal.isEnabled()){
                                AnimacaoAbrirMenuRapido(v);
                            }else{
                                AnimacaoFecharMenuRapido(v);
                            }
                        }
                    });
                }
                itemToolbarSettings = toolbar.findViewById(R.id.action_settings);
                if (itemToolbarSettings != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    itemToolbarSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activity = new Intent(PrincipalActivity.this, TermosUsoActivity.class);
                            startActivity(activity);
                        }
                    });
                }
            }
        });
    }

    private void AnimacaoAbrirMenuRapido(View v){
        relativePrincipal.setEnabled(false);
        itemToolbarTrevo.setEnabled(false);
        linearAtalhoRapidoLoterias.setVisibility(View.VISIBLE);
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativePrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                linearAtalhoRapidoLoterias,
                buttonHomeBet,
                itemToolbarTrevo,
                PrincipalActivity.this
                );
        int positionTopBotao = (int)buttonHomeBet.getY();

        animacoesAcessoRapido.AnimacaoDescerContent(positionTopBotao - 105);
    }



    private void AnimacaoFecharMenuRapido(View v){
        relativePrincipal.setEnabled(true);
        itemToolbarTrevo.setEnabled(false);
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativePrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                linearAtalhoRapidoLoterias,
                buttonHomeBet,
                itemToolbarTrevo,
                PrincipalActivity.this
        );
        animacoesAcessoRapido.AnimacaoSubirContent();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apresentar_trevo) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//
//    private void acaoApresentarTrevo(){
//        RotateAnimation rotate = new RotateAnimation(0, 360,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//
//        rotate.setDuration(4000);
//        rotate.setRepeatCount(Animation.INFINITE);
//        item.setAnimation(rotate);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apostar) {

        } else if (id == R.id.nav_bilhetes) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_minhaarea) {

        } else if (id == R.id.nav_rapidao) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.home:
//                finish();
//                break;
//            case R.id.fab_share:
//                share(v);
//                break;
//        }
    }

    @Override
    public void onScroll(
            float currentPosition,
            int currentIndex, int newIndex,
            @Nullable HomeAdapter.ViewHolder currentHolder,
            @Nullable HomeAdapter.ViewHolder newCurrent) {
        if (currentHolder != null && newCurrent != null) {
            float position = Math.abs(currentPosition);
            currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor));
            newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor));
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable HomeAdapter.ViewHolder viewHolder, int adapterPosition) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (viewHolder != null) {
            viewHolder.setOverlayColor(currentOverlayColor);
        }
    }

    private void share(View view) {
//        Snackbar.make(view, R.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show();
    }

    private int interpolate(float fraction, int c1, int c2) {
        return (int) evaluator.evaluate(fraction, c1, c2);
    }

    private void listenerRepassados(){

        RelativeLayout relativerepassados=(RelativeLayout)findViewById(R.id.RelativeLayoutContentRepassados);
        relativerepassados.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class) );
            }
        });
    }
}
