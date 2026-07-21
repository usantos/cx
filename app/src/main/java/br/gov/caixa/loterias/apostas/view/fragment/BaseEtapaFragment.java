package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public abstract class BaseEtapaFragment
        extends Fragment
        implements EtapaFragment {

    protected SimulaViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel =
                new ViewModelProvider(
                        requireActivity()
                ).get(SimulaViewModel.class);
    }
}