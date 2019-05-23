package br.gov.caixa.loterias.apostar.interno;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.gov.caixa.loterias.apostar.interno.view.fragment.TutorialRapidaoFragment;
import me.relex.circleindicator.CircleIndicator;

public class TutorialRapidaoActivity extends AppCompatActivity {

    private CircleIndicator circleIndicator;
    private ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_rapidao);
        setViews();
        setMethods();
    }

    private void setViews(){
        viewPager = findViewById(R.id.vp_rapidao);
        circleIndicator = findViewById(R.id.ci_rapidao);

    }
    private void setMethods(){
        adapterViewPager = new RapidaoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        circleIndicator.setViewPager(viewPager);
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
