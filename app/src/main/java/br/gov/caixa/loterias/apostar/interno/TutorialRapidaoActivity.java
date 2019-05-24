package br.gov.caixa.loterias.apostar.interno;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.fragment.TutorialRapidaoFragment;
import me.relex.circleindicator.CircleIndicator;

public class TutorialRapidaoActivity extends AppCompatActivity {

    private CircleIndicator circleIndicator;
    private ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_rapidao);
        setViews();
        setMethods();
    }

    private void setupToolbar(){
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setViews(){
        viewPager = findViewById(R.id.vp_rapidao);
        circleIndicator = findViewById(R.id.ci_rapidao);
        toolbar = findViewById(R.id.toolbar);

    }
    private void setMethods(){
        setupToolbar();
        adapterViewPager = new RapidaoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        circleIndicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(viewPager.getCurrentItem() == 2){
                    DadosUsuarioBO.updateTutorialRapidao(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public static class RapidaoPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public RapidaoPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TutorialRapidaoFragment.newInstance(0);
                case 1:
                    return TutorialRapidaoFragment.newInstance(1);
                case 2:
                    return TutorialRapidaoFragment.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
