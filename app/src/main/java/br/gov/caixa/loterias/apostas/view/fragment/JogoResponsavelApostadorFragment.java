package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;




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
        AppCenterManager.registraEvento( getActivity().getResources().getString(R.string.evento_jr_aba_3));
    }

}
