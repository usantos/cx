package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 25/01/2018.
 * Class ApostaEquipeLotecaView
 */
@EViewGroup(R.layout.row_aposta_equipe_loteca)
public class ApostaEquipeLotecaView extends LinearLayout {

    @ViewById
    protected ImageView imageApostaPartidaLoteca;
    @ViewById
    protected TextView textApostaPartidaLoteca;
    @ViewById
    protected LinearLayout layout_row_aposta_partida_loteca;

    public void setImageApostaPartidaLoteca(ImageView imageApostaPartidaLoteca) {
        this.imageApostaPartidaLoteca = imageApostaPartidaLoteca;
    }

    public ApostaEquipeLotecaView(Context context) {
        super(context);
    }

    public ApostaEquipeLotecaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaEquipeLotecaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ApostaEquipeLotecaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ImageView getImageApostaPartidaLoteca() {
        return imageApostaPartidaLoteca;
    }

    public TextView getTextApostaPartidaLoteca() {
        return textApostaPartidaLoteca;
    }

    public void setTextApostaPartidaLoteca(TextView textApostaPartidaLoteca) {
        this.textApostaPartidaLoteca = textApostaPartidaLoteca;
    }

    public LinearLayout getLayout_row_aposta_partida_loteca() {
        return layout_row_aposta_partida_loteca;
    }

    public void setLayout_row_aposta_partida_loteca(LinearLayout layout_row_aposta_partida_loteca) {
        this.layout_row_aposta_partida_loteca = layout_row_aposta_partida_loteca;
    }
}
