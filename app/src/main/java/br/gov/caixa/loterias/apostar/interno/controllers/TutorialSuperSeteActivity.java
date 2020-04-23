package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.view.fragment.TutorialSuperSete1Fragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.TutorialSuperSete2Fragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.TutorialSuperSete3Fragment;
import me.relex.circleindicator.CircleIndicator;

public class TutorialSuperSeteActivity extends AppCompatActivity {

    //region Constants
    public static final int FRAG_APOSTA_SIMPLES = 0, FRAG_APOSTA_MULTIPLA = 1, FRAG_CONFIRMAR = 2;
    //endregion

    //region Layout Variables
    private Toolbar toolbar;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private Button btnPular, btnAvancar;
    //endregion

    //region Variables
    private SuperSetePagerAdapter adapterViewPager;
    //endregion

    //region Life Cicle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_super_sete);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    //endregion

    //region Initialization Methods
    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        }
    }

    private void setaViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.vp_super_sete);
        circleIndicator = findViewById(R.id.ci_super_sete);
        btnPular = findViewById(R.id.btn_pular_s7);
        btnAvancar = findViewById(R.id.btn_aposta);

        btnPular.setVisibility(View.VISIBLE);
        setaToolbar();
    }


    private void setaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.como_jogar));
        toolbar.setBackgroundColor(getResources().getColor(R.color.supersetemedio));
    }


    private void setaMetodos() {
        adapterViewPager = new SuperSetePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(FRAG_CONFIRMAR == position){
                    btnAvancar.setBackgroundColor(getResources().getColor(R.color.supersetemedio));
                    btnAvancar.setTextColor(getResources().getColor(R.color.branco));
                    btnAvancar.setText(getResources().getString(R.string.iniciar_aposta));
                } else {
                    btnAvancar.setBackground(getResources().getDrawable(R.drawable.button_border_super_sete));
                    btnAvancar.setTextColor(getResources().getColor(R.color.supersetemedio));
                    btnAvancar.setText(getResources().getString(R.string.avancar));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnPular.setOnClickListener(view -> {
            DadosUsuarioBO.updateTutorialSuperSete(true);
            finish();
        });

        btnAvancar.setOnClickListener(view -> {
            switch (viewPager.getCurrentItem()) {
                case FRAG_APOSTA_SIMPLES:
                    viewPager.setCurrentItem(FRAG_APOSTA_MULTIPLA);
                    break;
                case FRAG_APOSTA_MULTIPLA:
                    viewPager.setCurrentItem(FRAG_CONFIRMAR);
                    break;
                case FRAG_CONFIRMAR:
                    DadosUsuarioBO.updateTutorialSuperSete(true);
                    finish();
                    break;
            }
        });
    }
    //endregion

    //region Layout Configuration Methods
    public static class SuperSetePagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public SuperSetePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAG_APOSTA_SIMPLES:
                    return TutorialSuperSete1Fragment.newInstance();
                case FRAG_APOSTA_MULTIPLA:
                    return TutorialSuperSete2Fragment.newInstance();
                case FRAG_CONFIRMAR:
                    return TutorialSuperSete3Fragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
    //endregion

    //region Support Methods

    //endregion


}
