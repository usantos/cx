package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.adapter.fragment.TutorialPagerAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnTutorialListener;
import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialNovaModalidadeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialNovaModalidadeFragment extends Fragment {

	private ViewPager viewPager;
	private CircleIndicator circleIndicator;
	private Button btnPrincipal, btnPular;
	private AppCompatCheckBox checkBox;

	private OnTutorialListener listener;
	private TutorialPagerAdapter adapter;

	public TutorialNovaModalidadeFragment() {
		// Required empty public constructor
	}

	public static TutorialNovaModalidadeFragment newInstance() {
		TutorialNovaModalidadeFragment fragment = new TutorialNovaModalidadeFragment();
		Bundle                         args     = new Bundle();

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			listener = (OnTutorialListener) context;
		}catch (Exception e){
			Log.e("TUTORIAL","A classe precisa extender OnTutorialListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial_nova_modalidade, container, false);

		setViews(view);
		setMetodos();

		return view;
	}

	private void setViews(View view) {
		viewPager 		= view.findViewById(R.id.vp_tutorial);
		circleIndicator = view.findViewById(R.id.ci_tutorial);
		btnPrincipal 	= view.findViewById(R.id.btn_principal);
		btnPular 		= view.findViewById(R.id.tv_pular);
		checkBox 		= view.findViewById(R.id.checkboxMostrar);
	}

	private void setMetodos() {
		adapter = new TutorialPagerAdapter(getActivity().getSupportFragmentManager(), listener.getFragments());
		viewPager.setAdapter(adapter);
		circleIndicator.setViewPager(viewPager);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

			@Override
			public void onPageSelected(int position) {
				if(isIntroducao(position)){
					btnPrincipal.setText(getResources().getString(R.string.vamos_comecar));
					btnPrincipal.setVisibility(View.VISIBLE);
					btnPular.setText(R.string.tutorial_pular);
					btnPular.setText(R.string.tutorial_fechar);
					checkBox.setVisibility(View.GONE);
				} else if (isFinal(position)){
					btnPrincipal.setText(getResources().getString(R.string.label_aposte_agora));
					btnPrincipal.setVisibility(View.VISIBLE);
					btnPular.setText(R.string.tutorial_fechar);
					checkBox.setVisibility(View.VISIBLE);
				} else {
					btnPrincipal.setVisibility(View.GONE);
					btnPular.setText(R.string.tutorial_pular);
					checkBox.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		btnPular.setOnClickListener(v -> {
			if (btnPular.getText().toString().contains(getString(R.string.tutorial_pular))){
				viewPager.setCurrentItem(listener.getFragments().size() - 1);
			} else {
				getActivity().finish();
			}
		});
		btnPrincipal.setOnClickListener(v -> {
			if (viewPager.getCurrentItem() == 0){
				viewPager.setCurrentItem(1);
			} else {
				listener.apostar(checkBox.isChecked());
			}
		});
	}

	private boolean isFinal(int position) {
		return position == (listener.getFragments().size() - 1);
	}

	private boolean isIntroducao(int position) {
		return 0 == position;
	}
}