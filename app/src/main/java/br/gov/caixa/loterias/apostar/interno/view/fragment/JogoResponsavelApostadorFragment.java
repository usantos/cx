package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;


public class JogoResponsavelApostadorFragment extends Fragment {

    public JogoResponsavelApostadorFragment() { }

    public static JogoResponsavelApostadorFragment newInstance() {
        return new JogoResponsavelApostadorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogo_responsavel_apostador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseManager.registraEvento(getActivity(), getActivity().getResources().getString(R.string.evento_jr_aba_3));
    }

}
