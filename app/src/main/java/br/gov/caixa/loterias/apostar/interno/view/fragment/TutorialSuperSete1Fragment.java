package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class TutorialSuperSete1Fragment extends Fragment {
	//region Constants
	//endregion

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

		GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(viewGif);
		Glide.with(getActivity()).load(R.drawable.super7_passo1).into(imageViewTarget);
	}

	//endregion

	//region Network Methods
	//endregion

	//region Layout Configuration Methods
	//endregion

	//region Support Methods
	//endregion
}
