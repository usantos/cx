package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class JogoResponsavelPrincipiosFragment extends Fragment {

    private ArrayList<String> titulos,descricoes;
    private TextView tvCert;

    public JogoResponsavelPrincipiosFragment() { }

    public static JogoResponsavelPrincipiosFragment newInstance() {
        return new JogoResponsavelPrincipiosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogo_responsavel_principios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_jr_aba_2));
        setaViews(view);
    }

    private void setaViews(View view){
        tvCert = view.findViewById(R.id.tv_cert);
        //tvCert.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),getResources().getString(R.string.jr_principios_cert)));
        tvCert.setText(ViewUtils.textCaixaSTDBold(getActivity(), getResources().getString(R.string.jr_principios_cert)));
        setaFragmentCards();
    }


    private void setaFragmentCards(){
        setaDadosCards();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment_cards, JogoResponsavelCardsFragment.newInstance(titulos,descricoes,null));
        fragmentTransaction.commit();
    }

    private void setaDadosCards(){
        titulos = new ArrayList<>();
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_1));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_2));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_3));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_4));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_5));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_6));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_7));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_8));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_9));
        titulos.add(getResources().getString(R.string.jr_principios_tit_card_10));

        descricoes = new ArrayList<>();
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_1));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_2));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_3));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_4));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_5));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_6));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_7));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_8));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_9));
        descricoes.add(getResources().getString(R.string.jr_principios_desc_card_10));

    }

}
