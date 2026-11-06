package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class TutorialSuperSete1Fragment extends Fragment {

	//region Layout Variables
	private View view;
	private ImageView viewGif;
	private TextView tvBemVindo, tvJogoSimples;
	//endregion

	//region Variables
	//endregion

	//region Life Cicle
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_tutorial_super_sete1, container, false);
		setaViews(view);

		return view;
	}

	@Override
	public void onResume() {
		configuraGif();
		super.onResume();
	}
	//endregion

	//region Initialization Methods
	public TutorialSuperSete1Fragment() { }

	public static TutorialSuperSete1Fragment newInstance() {
		TutorialSuperSete1Fragment fragment = new TutorialSuperSete1Fragment();
		return fragment;
	}

	private void setaViews(View view){
		tvBemVindo = view.findViewById(R.id.tv_bem_vindo);
		tvJogoSimples = view.findViewById(R.id.tv_jogo_simples);
		viewGif = view.findViewById(R.id.iv_gif);

		tvBemVindo.setText(ViewUtils.textColor(getActivity(),getResources().getString(R.string.bem_vindo_super_sete),R.color.supersetemedio));
		tvJogoSimples.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),getString(R.string.jogo_simples)));
	}

	private void configuraGif() {
		Glide.with(getActivity())
				.load(R.drawable.super7_passo1)
				.into(viewGif);
	}
	//endregion
}
