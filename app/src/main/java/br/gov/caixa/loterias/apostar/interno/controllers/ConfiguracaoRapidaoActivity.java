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
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
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
import br.gov.caixa.loterias.apostar.interno.view.fragment.ConfiguracaoRapidaoFragment8;
import me.relex.circleindicator.CircleIndicator;

public class ConfiguracaoRapidaoActivity extends AppCompatActivity {

	private CircleIndicator circleIndicator;
	private static ViewPager viewPager;
	private FragmentPagerAdapter adapterViewPager;
	private Toolbar toolbar;
	private static ConfiguracaoRapidaoFragment5 frag5;
	private static ConfiguracaoRapidaoFragment6 frag6;
	private static ConfiguracaoRapidaoFragment8 frag8;
	private static int scrollState;
	public  static final int FRAG_BEM_VINDO = 0, FRAG_PREMIO_MIN = 1, FRAG_VALOR_MIN_MAX = 2, FRAG_MODALIDADES = 3,
							 FRAG_NUM_OBRG = 4,  FRAG_NUM_PROIB = 5,  FRAG_APOSTAS_FAV = 6,   FRAG_CONFIG = 7;
	private Button btnSalvar;
	private AlertDialog loadViewProgress;
	private ApostaBO apostaBO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracao_rapidao);
		apostaBO  = ApostaBO_.getInstance_(ConfiguracaoRapidaoActivity.this);
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
		btnSalvar = findViewById(R.id.btn_salvar_rpd);
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
					switch (viewPager.getCurrentItem()){
						case FRAG_NUM_OBRG: frag5.setaMetodos();
							break;
						case FRAG_NUM_PROIB: frag6.setaMetodos();
							break;
						case FRAG_CONFIG: frag8.setaMetodos();
							if(btnSalvar.getVisibility() == View.GONE){
								btnSalvar.setVisibility(View.VISIBLE);
							}
							break;
					}
				}
			}
		});
		btnSalvar.setOnClickListener(view ->  salvarConfiguracao());
		if(DadosUsuarioBO.checarTutorialRapidao()){
			viewPager.setCurrentItem(FRAG_CONFIG);
			btnSalvar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private void callWebservice() {
		 loadViewProgress = LoadingViewLoterias.show(this);

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

	private void configuraLogicaTutorial(){
		if(DadosUsuarioBO.checarTutorialRapidao()){
			viewPager.setCurrentItem(FRAG_CONFIG);
			btnSalvar.setVisibility(View.VISIBLE);
		} else {
			//tutorial
		}

	}

	public static void scrollToFragment(int posicao){
		viewPager.setCurrentItem(posicao, true);
	}


	public static class RapidaoPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 8;

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
				case FRAG_BEM_VINDO:
					return ConfiguracaoRapidaoFragment1.newInstance();
				case FRAG_PREMIO_MIN:
					return ConfiguracaoRapidaoFragment2.newInstance();
				case FRAG_VALOR_MIN_MAX:
					return ConfiguracaoRapidaoFragment3.newInstance();
				case FRAG_MODALIDADES:
					return ConfiguracaoRapidaoFragment4.newInstance();
				case FRAG_NUM_OBRG:
					return frag5 = ConfiguracaoRapidaoFragment5.newInstance();
				case FRAG_NUM_PROIB:
					return frag6 = ConfiguracaoRapidaoFragment6.newInstance();
				case FRAG_APOSTAS_FAV:
					return ConfiguracaoRapidaoFragment7.newInstance();
				case FRAG_CONFIG:
					return frag8 = ConfiguracaoRapidaoFragment8.newInstance();
				default:
					return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}
	}

	private void salvarConfiguracao(){
		 loadViewProgress = LoadingViewLoterias.show(this);

		 apostaBO.postRapidoes(RapidaoConfigSingleton.getInstance().getRapidaoConfig(), new RequestListener<RapidaoDTOResponse>() {
			 @Override
			 public void onResponse(RapidaoDTOResponse result) {
				 loadViewProgress.dismiss();
				 DadosUsuarioBO.updateTutorialRapidao(true);
				 finish();
			 }

			 @Override
			 public void onErrorResponse(VolleyError error) {
				 loadViewProgress.dismiss();
				 RedirectNetwork.checkRedirect(error, ConfiguracaoRapidaoActivity.this);
			 }
		 });
	}
}
