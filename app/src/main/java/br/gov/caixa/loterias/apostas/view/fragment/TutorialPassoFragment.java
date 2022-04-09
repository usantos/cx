package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class TutorialPassoFragment extends Fragment {
	private static final String LAYOUT_KEY = "LAYOUT_KEY";

	private int layout;

	public TutorialPassoFragment() { }

	public static TutorialPassoFragment newInstance(int layout) {
		TutorialPassoFragment fragment = new TutorialPassoFragment();
		Bundle                args     = new Bundle();
		args.putInt(LAYOUT_KEY,layout);
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
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(layout, container, false);
	}

}