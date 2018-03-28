package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by pmotta on 23/03/2018.
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

    @ViewById
    protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt, apostaTrocaTxt, timeCoracaoTxt;

    @ViewById
    protected Button conferirResultadoButton;

    @ViewById
    protected RelativeLayout timeCoracaoLayout;

    @FragmentArg
    protected ApostaDTO apostaDTO;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", apostaDTO.getConcursoAlvo()));
        dataApostaTxt.setText(ViewUtils.getDateAndHour(apostaDTO.getDataEfetivacao(), apostaDTO.getHoraEfetivacao()));
        resultTxt.setText(apostaDTO.getSituacao().getDescricao());

        if (apostaDTO.getTroca()) {
            apostaTrocaTxt.setVisibility(View.VISIBLE);
        }
        if (apostaDTO.getQuantidadeTeimosinhas() > 0) {
            modalidadeTxt.setVisibility(View.VISIBLE);
        }
        if (apostaDTO.getTimeDoCoracao() != null) {
            timeCoracaoLayout.setVisibility(View.VISIBLE);
            timeCoracaoTxt.setText(apostaDTO.getTimeDoCoracao().getNome());
        }
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(apostaDTO.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }
    }

}
