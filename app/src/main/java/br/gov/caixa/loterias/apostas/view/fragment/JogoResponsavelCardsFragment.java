package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;


import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogoResponsavelCardsAdapter;
import me.relex.circleindicator.CircleIndicator2;

public class JogoResponsavelCardsFragment extends Fragment {

    public static final String TITULOS = "TITULOS";
    public static final String DESCRICOES = "DESCRICOES";
    public static final String IMAGENS = "IMAGENS";

    private DiscreteScrollView scrollCards;
    private ArrayList<String> titulos,descricoes;
    private ArrayList<Integer> idsImagens;
    private JogoResponsavelCardsAdapter adapter;
    private CircleIndicator2 indicator;

    public JogoResponsavelCardsFragment() { }

    public static JogoResponsavelCardsFragment newInstance(ArrayList<String> titulos,
                                                           ArrayList<String> descricoes,
                                                           ArrayList<Integer> idsImagens) {
        JogoResponsavelCardsFragment fragment = new JogoResponsavelCardsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(TITULOS, titulos);
        args.putStringArrayList(DESCRICOES, descricoes);
        args.putIntegerArrayList(IMAGENS, idsImagens);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_jogo_responsavel_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setaViews(view);
        pegaExtras();
        setaMetodos();
    }

    private void setaViews(View view){
        scrollCards = view.findViewById(R.id.sv_cards);
        indicator = view.findViewById(R.id.ci_jr);
    }

    private void setaMetodos(){
        adapter = new JogoResponsavelCardsAdapter(getActivity(),titulos,descricoes,idsImagens);
        scrollCards.setAdapter(adapter);
        scrollCards.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(scrollCards);
        indicator.attachToRecyclerView(scrollCards, pagerSnapHelper);
    }

    private void pegaExtras(){
        if (getArguments() != null) {
            titulos =  getArguments().getStringArrayList(TITULOS);
            descricoes =  getArguments().getStringArrayList(DESCRICOES);
            idsImagens = getArguments().getIntegerArrayList(IMAGENS);
        }
    }
}