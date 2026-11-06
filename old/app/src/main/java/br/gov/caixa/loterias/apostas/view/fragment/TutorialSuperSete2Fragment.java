package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import br.gov.caixa.loterias.apostas.R;


public class TutorialSuperSete2Fragment extends Fragment {
    //region Layout Variables
    private View view;
    private ImageView viewGif;
    //endregion

    //region Variables
    //endregion

    //region Life Cicle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial_super_sete2, container, false);
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
    public TutorialSuperSete2Fragment() { }

    public static TutorialSuperSete2Fragment newInstance() {
        TutorialSuperSete2Fragment fragment = new TutorialSuperSete2Fragment();
        return fragment;
    }

    private void setaViews(View view){
        viewGif = view.findViewById(R.id.iv_gif);
    }

    private void configuraGif() {
        Glide.with(getActivity())
                .load(R.drawable.super7_passo2)
                .into(viewGif);
    }
    //endregion
}
