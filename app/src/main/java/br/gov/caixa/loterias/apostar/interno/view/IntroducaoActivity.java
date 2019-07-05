package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.view.fragment.Introducao1Fragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.Introducao2Fragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.Introducao3Fragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.LoginFragment;
import me.relex.circleindicator.CircleIndicator;

public class IntroducaoActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;

    ViewPager vpPager;

    Boolean pulouTour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducao);
        if (DadosUsuarioBO.checarFecharIntroducao(this)){
            startActivity(new Intent(IntroducaoActivity.this, LoginActivity.class));
            finish();
        }
        FirebaseManager.registraEvento(IntroducaoActivity.this  ,"ENTROU_TOUR_TELA1");

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager = findViewById(R.id.vpPager);
        CircleIndicator indicator = findViewById(R.id.indicator_custom);
        vpPager.setAdapter(adapterViewPager);
        indicator.setViewPager(vpPager);


        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1: {
                        FirebaseManager.registraEvento(IntroducaoActivity.this ,"ENTROU_TOUR_TELA2");
                    }
                    case 2: {
                        FirebaseManager.registraEvento(IntroducaoActivity.this ,"ENTROU_TOUR_TELA3");
                    }
                    case 3: {
                        if (!pulouTour) {
                            FirebaseManager.registraEvento(IntroducaoActivity.this ,"ENTROU_TOUR_TELA4");
                        }
                    }
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return Introducao1Fragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return Introducao2Fragment.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return Introducao3Fragment.newInstance(2, "Page # 3");
                case 3: // Fragment # 1 - This will show SecondFragment
                    return LoginFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    public void pularIntroducao(){
        FirebaseManager.registraEvento(IntroducaoActivity.this ,"PULOU_TOUR");
        pulouTour = true;
        vpPager.setCurrentItem(3, true);
    }
}
