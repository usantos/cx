package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import br.gov.caixa.loterias.apostas.R;

public class TutorialPassoFragment extends Fragment {
	private static final String LAYOUT_KEY = "LAYOUT_KEY";
	private static final String START_ANIMACAO_KEY = "START_ANIMACAO_KEY";

	private int layout;
	private boolean startAnimacao;
	private LottieAnimationView animationView;

	public TutorialPassoFragment() { }

	public static TutorialPassoFragment newInstance(int layout, boolean startAnimacao) {
		TutorialPassoFragment fragment = new TutorialPassoFragment();
		Bundle                args     = new Bundle();
		args.putInt(LAYOUT_KEY,layout);
		args.putBoolean("START_ANIMACAO_KEY", startAnimacao);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {

		}catch (Exception e){

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null){
			layout = getArguments().getInt(LAYOUT_KEY);
			startAnimacao = getArguments().getBoolean(START_ANIMACAO_KEY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(layout, container, false);
		animationView = view.findViewById(R.id.animation_view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (startAnimacao){
			startAnimacao();
		}
	}

	public void startAnimacao(){
		if (animationView != null){
			animationView.playAnimation();
		}
	}
}