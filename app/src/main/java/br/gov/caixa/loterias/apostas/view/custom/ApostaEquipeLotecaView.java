package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 25/01/2018.
 * Class ApostaEquipeLotecaView
 */
public class ApostaEquipeLotecaView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView imageApostaPartidaLoteca;
    private TextView textApostaPartidaLoteca;
    private LinearLayout layout_row_aposta_partida_loteca;

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

    public static ApostaEquipeLotecaView build(Context context) {
        ApostaEquipeLotecaView instance = new ApostaEquipeLotecaView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_aposta_equipe_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.imageApostaPartidaLoteca = findViewById(R.id.image_aposta_partida_loteca);
        this.textApostaPartidaLoteca = findViewById(R.id.text_aposta_partida_loteca);
        this.layout_row_aposta_partida_loteca = findViewById(R.id.layout_row_aposta_partida_loteca);
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
