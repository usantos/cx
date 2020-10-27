package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by cedesbr450 on 02/04/18.
 */

@EViewGroup(R.layout.row_time_loteca_resultado)
public class PartidaViewLotecaResultado extends LinearLayout {

    @ViewById
    ItemEquipeViewResultado lotecaTimeUm, lotecaEmpate, lotecaTimeDois;

    @ViewById
    TextView paginaResultadoLotecaText;

    @ViewById
    TextView diaSemanaDataPartidaText;

    @AfterViews
    protected void init() {

    }


    public void setLayout(PartidaLotecaDTO parametroPartida) {
        int corVitoria = ContextCompat.getColor(getContext(), R.color.selecaoLotecaResultado);
        int corPadrao = ContextCompat.getColor(getContext(), R.color.transparente);

        setaCorFundo(lotecaTimeUm,corPadrao);
        setaCorFundo(lotecaTimeDois,corPadrao);
        setaCorFundo(lotecaEmpate,corPadrao);

        if (parametroPartida.getEquipe1().getVitoria()) {
            setaCorFundo(lotecaTimeUm,corVitoria);
        }
        if (parametroPartida.getEquipe2().getVitoria()) {
            setaCorFundo(lotecaTimeDois,corVitoria);
        }
        if (parametroPartida.getEmpate()) {
            setaCorFundo(lotecaEmpate,corVitoria);
        }

        lotecaTimeUm.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe1().getParametroEquipe(), lotecaTimeUm.getItemImage());
        lotecaTimeUm.getItemTitle().setText(parametroPartida.getEquipe1().getNome());
        lotecaTimeUm.getItemDescription().setText(Utils.getUfTime(parametroPartida.getEquipe1()));

        lotecaTimeDois.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2().getParametroEquipe(), lotecaTimeDois.getItemImage());
        lotecaTimeDois.getItemTitle().setText(parametroPartida.getEquipe2().getNome());
        lotecaTimeDois.getItemDescription().setText(Utils.getUfTime(parametroPartida.getEquipe2()));

        diaSemanaDataPartidaText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.texto_futura_bold, getResources().getString(R.string.label_domingo))));

    }
    private void setaCorFundo(ItemEquipeViewResultado item, int cor){
        item.getLinearLayoutContentItemLotecaResultado().setBackgroundColor(cor);
    }

    public PartidaViewLotecaResultado(Context context) {
        super(context);
    }

    public PartidaViewLotecaResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewLotecaResultado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getPaginaResultadoLotecaText() {
        return paginaResultadoLotecaText;
    }

    public void setPaginaResultadoLotecaText(TextView paginaResultadoLotecaText) {
        this.paginaResultadoLotecaText = paginaResultadoLotecaText;
    }

    public TextView getDiaSemanaDataPartidaText() {
        return diaSemanaDataPartidaText;
    }

    public void setDiaSemanaDataPartidaText(TextView diaSemanaDataPartidaText) {
        this.diaSemanaDataPartidaText = diaSemanaDataPartidaText;
    }

}