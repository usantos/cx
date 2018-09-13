package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

@EFragment(R.layout.fragment_meses)
public class MesesFragment extends Fragment {

    @ViewById
    protected TextView escolhaNumerosText, paginaNumeroText, paginaQuantidadeText;

    @FragmentArg
    protected ModalidadeEnum modalidadeEnum;

    @AfterViews
    protected void init() {

        switch (modalidadeEnum) {
            case DIA_DE_SORTE:
                setTelaDiaSorte();
                break;
            default:
                break;
        }
    }

    private void setTelaDiaSorte(){
        escolhaNumerosText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.dia_sorte_escolha_mes)));
        escolhaNumerosText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaNumeroText.setText(R.string.label_numerico_dois);
        paginaNumeroText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaQuantidadeText.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
        paginaQuantidadeText.setTextColor(getResources().getColor(R.color.diasortemedio));
    }
}
