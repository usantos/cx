package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotecaDTO;

/**
 * Created by cedesbr450 on 15/03/18.
 */

@EViewGroup(R.layout.row_apostas_partidas_loteca)
public class PartidaViewLoteca extends LinearLayout {

    @ViewById
    protected ApostaEquipeLotecaView apostas_partida_loteca_view_um, apostas_partida_loteca_view_empate, apostas_partida_loteca_view_dois;

    @ViewById
    protected LinearLayout layout_row_apostas_partidas_loteca_list_view;

    public LinearLayout getLayout_row_apostas_partidas_loteca_list_view() {
        return layout_row_apostas_partidas_loteca_list_view;
    }

    public void setLayout_row_apostas_partidas_loteca_list_view(LinearLayout layout_row_apostas_partidas_loteca_list_view) {
        this.layout_row_apostas_partidas_loteca_list_view = layout_row_apostas_partidas_loteca_list_view;
    }

    @AfterViews
    protected void init() {
        Log.d("dd", "dd");
    }

    public void setLayout(PartidaLotecaDTO parametroPartida) {

        layout_row_apostas_partidas_loteca_list_view.setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe1().getNome());
        apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_um.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe1().getVitoria());
        apostas_partida_loteca_view_um.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_um.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if (parametroPartida.getEquipe1().getVitoria()){
            apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setTypeface(Typeface.DEFAULT_BOLD);
        }
        apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setText("X");
        apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_empate.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEmpate());
        apostas_partida_loteca_view_empate.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_empate.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(parametroPartida.getEmpate()){
            apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setTypeface(Typeface.DEFAULT_BOLD);
        }
        apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe2().getNome());
        apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe2().getVitoria());
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setBackgroundColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_dois.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(parametroPartida.getEquipe2().getVitoria()){
            apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public PartidaViewLoteca(Context context) {
        super(context);
    }

    public PartidaViewLoteca(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewLoteca(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
