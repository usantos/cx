package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.view.custom.AccessibleSpringDotsIndicator;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.view.fragment.Introducao1Fragment;
import br.gov.caixa.loterias.apostas.view.fragment.Introducao2Fragment;
import br.gov.caixa.loterias.apostas.view.fragment.Introducao3Fragment;


public class IntroducaoActivity extends AppCompatActivity {
    ViewPager2 vpPager;

    Boolean pulouTour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducao);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tour_tela_1));

        vpPager = findViewById(R.id.vpPager);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(this);
        vpPager.setAdapter(adapterViewPager);

        AccessibleSpringDotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        dotsIndicator.setViewPager2(vpPager);

        vpPager.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);


        Button button = findViewById(R.id.buttonPularIntroducao);
        button.setOnClickListener(v -> {
            if (!pulouTour) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tour_tela_4));
            }
            pulouTour = true;
            DadosUsuarioBO.updateFecharIntroducao(true);
            startActivity(new Intent(IntroducaoActivity.this, LoginActivity.class));
            finish();
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_pulou_tour));
        });

        vpPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Button button = findViewById(R.id.buttonPularIntroducao);

                String descricaoPagina = "Página " + (position + 1) + " de 3";

                vpPager.post(() -> vpPager.announceForAccessibility(descricaoPagina));

                ImageView logo = findViewById(R.id.imageViewLogoLoterias);
                logo.setFocusable(true);
                logo.setFocusableInTouchMode(true);
                logo.requestFocus();

                logo.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);


                switch (position) {
                    case 0:
                        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tour_tela_1));
                        button.setText(getString(R.string.pular_introdu_o));
                        button.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tour_tela_2));
                        button.setText(getString(R.string.pular_introdu_o));
                        button.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tour_tela_3));
                        button.setText(getString(R.string.acessar_aplicativo));
                        button.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }



    public class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return Introducao1Fragment.newInstance(0, "Página # 1 de 3");
                case 1: return Introducao2Fragment.newInstance(1, "Página # 2 de 3");
                case 2: return Introducao3Fragment.newInstance(2, "Página # 3 de 3");
                default: return new Fragment(); // fallback
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

}
