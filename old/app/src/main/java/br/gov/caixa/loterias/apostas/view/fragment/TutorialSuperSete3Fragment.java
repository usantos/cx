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


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialSuperSete3Fragment extends Fragment {

    //region Layout Variables
    private View view;
    private ImageView viewGif;
    private TextView tvConfirmarCarrinho;
    //endregion

    //region Life Cicle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial_super_sete3, container, false);
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
    public TutorialSuperSete3Fragment() { }

    public static TutorialSuperSete3Fragment newInstance() {
        TutorialSuperSete3Fragment fragment = new TutorialSuperSete3Fragment();
        return fragment;
    }

    private void setaViews(View view){
        tvConfirmarCarrinho = view.findViewById(R.id.tv_confirmar_carrinho);
        viewGif = view.findViewById(R.id.iv_gif);

        tvConfirmarCarrinho.setText(ViewUtils.textColor(getActivity(),getResources().getString(R.string.confirmar_carrinho),R.color.supersetemedio,false));
    }

    private void configuraGif() {
        Glide.with(getActivity())
                .load(R.drawable.super7_passo3)
                .into(viewGif);
    }
    //endregion
}
