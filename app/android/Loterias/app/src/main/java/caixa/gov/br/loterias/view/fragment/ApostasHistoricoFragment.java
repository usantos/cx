package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTO;

/**
 * Created by pmotta on 16/03/2018.
 */

@EFragment(R.layout.fragment_apostas_historico)
public class ApostasHistoricoFragment extends Fragment {
    @ViewById
    protected TextView dateResultTxt;

    private ApostaBO apostaBO;
    private Calendar currentCalendar;
    private List<MesAnoDTO> mesesAnosDisponiveis;

}
