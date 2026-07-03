package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment1;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment2;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment3;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment4;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment5;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment6;
import br.gov.caixa.loterias.apostas.view.fragment.ConfiguracaoRapidaoFragment8;
import me.relex.circleindicator.CircleIndicator;

public class ConfiguracaoRapidaoActivity extends LoteriasBaseAppActivity {

	private CircleIndicator circleIndicator;
	private static ViewPager viewPager;
	private FragmentPagerAdapter adapterViewPager;
	private Toolbar toolbar;
	private RapidaoDTO rapidao;
	private static ConfiguracaoRapidaoFragment5 frag5;
	private static ConfiguracaoRapidaoFragment6 frag6;
	private static ConfiguracaoRapidaoFragment8 frag8;
	private static int scrollState;
	public  static final int FRAG_BEM_VINDO = 0, FRAG_PREMIO_MIN = 1, FRAG_VALOR_MIN_MAX = 2, FRAG_MODALIDADES = 3,
							 FRAG_NUM_OBRG = 4,  FRAG_NUM_PROIB = 5,  FRAG_CONFIG = 6;//FRAG_APOSTAS_FAV = 6,   FRAG_CONFIG = 7;
	private Button btnSalvar;
	private AlertDialog loadViewProgress;
	private ApostaSilceBO apostaSilceBO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracao_rapidao);
		setViews();
		setupToolbar();
		apostaSilceBO = ApostaSilceBO.getInstance();
	}

	@Override
	protected void onResume() {
		super.onResume();
		callWebservice();
	}

	private void setupToolbar(){
		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.conf_rapd_titulo_bold)));
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
//		 loadViewProgress = LoadingViewLoterias.show(this);
//
//		apostaSilceBO.getRapidoes(new RequestListener<RapidaoDTOResponse>() {
//			@Override
//			public void onResponse(final RapidaoDTOResponse response) {
//				loadViewProgress.dismiss();
//				processarRegistro(response);
//				setMethods();
//			}
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				loadViewProgress.dismiss();
//				RedirectNetwork.checkRedirect(error, ConfiguracaoRapidaoActivity.this);
//			}
//		});
	}

	private void processarRegistro(RapidaoDTOResponse response) {
		 rapidao = response.getPayload();
		if (rapidao.getValorMaximo() == null) {
			rapidao.setValorMaximo(SessaoUsuario.getInstance().parametrosSimulacao.getValorLimiteDiario());
		}
		if (rapidao.getValorMinimo() == null) {
			rapidao.setValorMinimo(SessaoUsuario.getInstance().parametrosSimulacao.getValorMinimoCarrinho());
		}
		if (rapidao.getPrognosticosObrigatorios() == null) {
			rapidao.setPrognosticosObrigatorios(new ArrayList<>());
		}
		if (rapidao.getPrognosticosProibidos() == null) {
			rapidao.setPrognosticosProibidos(new ArrayList<>());
		}
		if (rapidao.getValorMinimoPremioPrincipal() == null) {
			rapidao.setValorMinimoPremioPrincipal(new BigDecimal(getResources().getString(R.string.zero_ponto_zero)));
		}
		if (rapidao.getApostasFavoritas() == null) {
			rapidao.setApostasFavoritas(new ArrayList<>());
		}

		if (rapidao.getModalidades() == null) {
			rapidao.setModalidades(new ArrayList<>());
		}
		RapidaoConfigSingleton.getInstance().setRapidaoConfig(rapidao);
	}

	public static void scrollToFragment(int posicao){
		viewPager.setCurrentItem(posicao, true);
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
				/*case FRAG_APOSTAS_FAV:
					return ConfiguracaoRapidaoFragment7.newInstance();*/
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
//		 loadViewProgress = LoadingViewLoterias.show(this);
//
//		 apostaSilceBO.postRapidoes(RapidaoConfigSingleton.getInstance().getRapidaoConfig(), new RequestListener<RapidaoDTOResponse>() {
//			 @Override
//			 public void onResponse(RapidaoDTOResponse result) {
//				 loadViewProgress.dismiss();
//				 if(!DadosUsuarioBO.checarTutorialRapidao()) {
//					 DadosUsuarioBO.updateTutorialRapidao(true);
//					 startActivity(new Intent(ConfiguracaoRapidaoActivity.this,ListaRapidaoActivity.class));
//					 finish();
//				 }else {
//					 finish();
//				 }
//			 }
//
//			 @Override
//			 public void onErrorResponse(VolleyError error) {
//				 loadViewProgress.dismiss();
//				 RedirectNetwork.checkRedirect(error, ConfiguracaoRapidaoActivity.this);
//			 }
//		 });
	}
}
