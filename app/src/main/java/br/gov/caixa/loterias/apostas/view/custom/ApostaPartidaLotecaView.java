package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by joafilho on 25/01/2018.
 * Class ApostaPartidaLotecaView
 */
public class ApostaPartidaLotecaView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ApostaEquipeLotecaView apostasPartidaLotecaViewUm, apostasPartidaLotecaViewEmpate, apostasPartidaLotecaViewDois;

    public static ApostaPartidaLotecaView build(Context context) {
        ApostaPartidaLotecaView instance = new ApostaPartidaLotecaView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_apostas_partidas_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    //@AfterViews
    protected void init() {
        this.apostasPartidaLotecaViewUm = findViewById(R.id.apostas_partida_loteca_view_um);
        this.apostasPartidaLotecaViewEmpate = findViewById(R.id.apostas_partida_loteca_view_empate);
        this.apostasPartidaLotecaViewDois = findViewById(R.id.apostas_partida_loteca_view_dois);
    }

    public ApostaPartidaLotecaView(Context context) {
        super(context);
    }

    public ApostaPartidaLotecaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaPartidaLotecaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ApostaPartidaLotecaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLayout(PartidaLotecaDTO partidaLotecaDTO) {
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewUm, partidaLotecaDTO.getEquipe1(),
                partidaLotecaDTO.getLegendaAbreviada());
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewDois, partidaLotecaDTO.getEquipe2(),
                partidaLotecaDTO.getLegendaAbreviada());

        EquipeDTO equipeDTOEmpate = new EquipeDTO();
        equipeDTOEmpate.setVitoria(partidaLotecaDTO.getEmpate());
        equipeDTOEmpate.setNome(Constantes.X_STRING);
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewEmpate, equipeDTOEmpate, null);
    }

    private void setValueApostaEquipeLotecaView(ApostaEquipeLotecaView apostaEquipeLotecaView,
                                                EquipeDTO equipeDTO, String legenda) {
        apostaEquipeLotecaView.getImageApostaPartidaLoteca().setSelected(equipeDTO.getVitoria());
        if (legenda != null) {
            apostaEquipeLotecaView.getTextApostaPartidaLoteca().setText(Utils.getEquipeComUf(equipeDTO) + " " + legenda);
        } else {
            apostaEquipeLotecaView.getTextApostaPartidaLoteca().setText(Utils.getEquipeComUf(equipeDTO));
        }
        if (equipeDTO.getVitoria()) {
            //apostaEquipeLotecaView.getTextApostaPartidaLoteca().setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
            apostaEquipeLotecaView.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
        }
    }
}
