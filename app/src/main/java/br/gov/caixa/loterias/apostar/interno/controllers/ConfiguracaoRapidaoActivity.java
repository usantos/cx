package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment1;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment2;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment3;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment4;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment5;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment6;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment7;
import me.relex.circleindicator.CircleIndicator;

public class ConfiguracaoRapidaoActivity extends AppCompatActivity {

	private CircleIndicator circleIndicator;
	private ViewPager viewPager;
	FragmentPagerAdapter adapterViewPager;
	private Toolbar toolbar;
	private static ConfiguracaoRapidaoFragment5 frag5;
	private static ConfiguracaoRapidaoFragment6 frag6;
	private static int scrollState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracao_rapidao);
	}

	@Override
	protected void onResume() {
		super.onResume();
		callWebservice();
	}

	private void setupToolbar(){
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.conf_rapd_titulo_bold)));
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
		adapterViewPager = new ConfiguracaoRapidaoActivity.RapidaoPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapterViewPager);
		circleIndicator.setViewPager(viewPager);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				scrollState = state;
				if(state == RecyclerView.SCROLL_STATE_IDLE){
					if(viewPager.getCurrentItem() == 4){
						frag5.setaMetodos();
					} else if (viewPager.getCurrentItem() == 5){
						frag6.setaMetodos();
					}

				}
			}
		});
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private void callWebservice() {
		AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
		ApostaBO apostaBO = ApostaBO_.getInstance_(ConfiguracaoRapidaoActivity.this);

		apostaBO.getRapidoes(new RequestListener<RapidaoDTOResponse>() {
			@Override
			public void onResponse(final RapidaoDTOResponse response) {
				loadViewProgress.dismiss();
				RapidaoConfigSingleton.getInstance().setRapidaoConfig(response.getPayload());
				setViews();
				setMethods();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				loadViewProgress.dismiss();
				RedirectNetwork.checkRedirect(error, ConfiguracaoRapidaoActivity.this);
			}
		});
	}


	public static class RapidaoPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 7;

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
					return ConfiguracaoRapidaoFragment1.newInstance();
				case 1:
					return ConfiguracaoRapidaoFragment2.newInstance();
				case 2:
					return ConfiguracaoRapidaoFragment3.newInstance();
				case 3:
					return ConfiguracaoRapidaoFragment4.newInstance();
				case 4:
					return frag5 = ConfiguracaoRapidaoFragment5.newInstance();
				case 5:
					return frag6 = ConfiguracaoRapidaoFragment6.newInstance();
				case 6:
					return ConfiguracaoRapidaoFragment7.newInstance();
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
