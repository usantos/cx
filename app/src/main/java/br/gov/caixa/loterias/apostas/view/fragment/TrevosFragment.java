package br.gov.caixa.loterias.apostas.view.fragment;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

@EFragment(R.layout.fragment_trevos)
public class TrevosFragment extends Fragment {

    @ViewById
    protected TextView escolhaNumerosText;

    @FragmentArg
    protected ArrayList<Integer> dezenasSelecionadas;

    @AfterViews
    protected void init() {
        String                 tituloSemBold      = getString(R.string.voce_escolheu_x_numeros)
                .replace("x", String.valueOf(dezenasSelecionadas.size()));
        SpannableStringBuilder titulo = ViewUtils.textFuturaAndFuturaBold(getActivity(), tituloSemBold);

        escolhaNumerosText.setText(titulo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
