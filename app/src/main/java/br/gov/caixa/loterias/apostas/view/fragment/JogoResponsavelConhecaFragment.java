package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class JogoResponsavelConhecaFragment extends Fragment {

    private TextView tvAlemDisso;
    private Button btnSaibaMais;
    private ArrayList<String> titulos,descricoes;
    private ArrayList<Integer> idsImagens;


    public JogoResponsavelConhecaFragment() { }

    public static JogoResponsavelConhecaFragment newInstance() {
        return new JogoResponsavelConhecaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogo_responsavel_conheca, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setaViews(view);
        setaMetodos();
    }

    private void setaViews(View view){
        btnSaibaMais = view.findViewById(R.id.btn_saiba_mais);
        tvAlemDisso = view.findViewById(R.id.tv_alem_disso);
        tvAlemDisso.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),getResources().getString(R.string.jr_conheca_alem_disso)));
        setaFragmentCards();
    }

    private void setaMetodos(){
        FirebaseManager.registraEvento(getActivity(), getActivity().getResources().getString(R.string.evento_jr_aba_1));
        btnSaibaMais.setOnClickListener(view1 -> {
            FirebaseManager.registraEvento(getActivity(), getActivity().getResources().getString(R.string.evento_jr_saiba_mais));
            Uri uri = Uri.parse(getActivity().getResources().getString(R.string.jr_endereco));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    private void setaFragmentCards(){
        setaDadosCards();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment_cards, JogoResponsavelCardsFragment.newInstance(titulos,descricoes,idsImagens));
        fragmentTransaction.commit();
    }

    private void setaDadosCards(){
        titulos = new ArrayList<>();
        titulos.add(getResources().getString(R.string.jr_conheca_tit_card_1));
        titulos.add(getResources().getString(R.string.jr_conheca_tit_card_2));
        titulos.add(getResources().getString(R.string.jr_conheca_tit_card_3));

        descricoes = new ArrayList<>();
        descricoes.add(getResources().getString(R.string.jr_conheca_desc_card_1));
        descricoes.add(getResources().getString(R.string.jr_conheca_desc_card_2));
        descricoes.add(getResources().getString(R.string.jr_conheca_desc_card_3));

        idsImagens = new ArrayList<>();
        idsImagens.add(R.drawable.ic_money);
        idsImagens.add(R.drawable.ic_people);
        idsImagens.add(R.drawable.ic_nuvem);
    }

}