package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.List;

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
 * Created by joafilho on 26/03/2018.
 * Class PartidasLotecaApostasConfirmadasView
 */

public class PartidasLotecaApostasConfirmadasView extends LinearLayout {

    private boolean alreadyInflated = false;

    private TextView textApostaPartidaLotecaUm, textApostaPartidaLotecaDois, textApostaPartidaLotecaEmpate;
    private ImageView imageApostaPartidaLotecaUm, imageApostaPartidaLotecaDois, imageApostaPartidaLotecaEmpate;

    protected List<PartidaLotecaDTO> listaResultados;

    public PartidasLotecaApostasConfirmadasView(Context context) {
        super(context);
    }

    public PartidasLotecaApostasConfirmadasView(Context context, List<PartidaLotecaDTO> listaResultados) {
        super(context);
        this.listaResultados = listaResultados;
    }

    public PartidasLotecaApostasConfirmadasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidasLotecaApostasConfirmadasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public static PartidasLotecaApostasConfirmadasView build(Context context, List<PartidaLotecaDTO> listaResultados) {
        PartidasLotecaApostasConfirmadasView instance = new PartidasLotecaApostasConfirmadasView(context, listaResultados);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_apostas_confirmadas_partidas_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.textApostaPartidaLotecaUm = findViewById(R.id.textApostaPartidaLotecaUm);
        this.textApostaPartidaLotecaDois = findViewById(R.id.textApostaPartidaLotecaDois);
        this.textApostaPartidaLotecaEmpate = findViewById(R.id.textApostaPartidaLotecaEmpate);
        this.imageApostaPartidaLotecaUm = findViewById(R.id.imageApostaPartidaLotecaUm);
        this.imageApostaPartidaLotecaDois = findViewById(R.id.imageApostaPartidaLotecaDois);
        this.imageApostaPartidaLotecaEmpate = findViewById(R.id.imageApostaPartidaLotecaEmpate);
    }

    public void setLayout(PartidaLotecaDTO partidaLotecaDTO) {
        //TODO//
        String legenda = " " + partidaLotecaDTO.getLegendaAbreviada();

        if (partidaLotecaDTO.getEquipe1().getParametroEquipe().getIndicadorSelecao() != null && partidaLotecaDTO.getEquipe1().getParametroEquipe().getIndicadorSelecao()) {
            this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaUm, textApostaPartidaLotecaUm, partidaLotecaDTO.getEquipe1(), Utils.getEquipeNome(partidaLotecaDTO.getEquipe1()) + legenda, partidaLotecaDTO);
        } else {
            this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaUm, textApostaPartidaLotecaUm, partidaLotecaDTO.getEquipe1(), Utils.getEquipeComUf(partidaLotecaDTO.getEquipe1()) + legenda, partidaLotecaDTO);
        }
        if (partidaLotecaDTO.getEquipe2().getParametroEquipe().getIndicadorSelecao() != null && partidaLotecaDTO.getEquipe2().getParametroEquipe().getIndicadorSelecao()) {
            this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaDois, textApostaPartidaLotecaDois, partidaLotecaDTO.getEquipe2(), Utils.getEquipeNome(partidaLotecaDTO.getEquipe2()) + legenda, partidaLotecaDTO);
        } else {
            this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaDois, textApostaPartidaLotecaDois, partidaLotecaDTO.getEquipe2(), Utils.getEquipeComUf(partidaLotecaDTO.getEquipe2()) + legenda, partidaLotecaDTO);
        }

        EquipeDTO equipeDTOEmpate = new EquipeDTO();
        equipeDTOEmpate.setVitoria(partidaLotecaDTO.getEmpate());
        equipeDTOEmpate.setNome(Constantes.X_STRING);
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaEmpate, textApostaPartidaLotecaEmpate, equipeDTOEmpate, equipeDTOEmpate.getNome(), partidaLotecaDTO);
    }

    private void setValueApostaEquipeLotecaView(ImageView imageView, TextView apostaEquipeLotecaView, EquipeDTO equipeDTO, String equipeNome, PartidaLotecaDTO partidaLotecaDTO) {
        if (listaResultados != null){
            for (PartidaLotecaDTO partida:listaResultados) {
                if(partida.customEquals(partidaLotecaDTO)){
                    if(partida.getEquipe1().equals(equipeDTO) && partida.getEquipe1().getVitoria()){
                        preencheAcerto(imageView,apostaEquipeLotecaView );
                    }
                    if(partida.getEquipe2().equals(equipeDTO) && partida.getEquipe2().getVitoria()){
                        preencheAcerto(imageView,apostaEquipeLotecaView );
                    }
                    if(equipeDTO.getNome() != null && equipeDTO.getNome().equalsIgnoreCase(Constantes.X_STRING) && partida.getEmpate()){
                        preencheAcerto(imageView,apostaEquipeLotecaView );
                    }
                }
            }
        }
        imageView.setSelected(equipeDTO.getVitoria());
        apostaEquipeLotecaView.setText(equipeNome);
        if (equipeDTO.getVitoria()) {
            //apostaEquipeLotecaView.setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
            apostaEquipeLotecaView.setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
        }
    }
    private void preencheAcerto(ImageView imageView, TextView apostaEquipeLotecaView){
        int corLoteca = ContextCompat.getColor(getContext(),R.color.loteca_claro_mkp);
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.navegacao_branco);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable,ContextCompat.getColor(getContext(),R.color.branco));
        imageView.setBackground(wrappedDrawable);
        apostaEquipeLotecaView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.branco));
        apostaEquipeLotecaView.setTextColor(corLoteca);
        //apostaEquipeLotecaView.setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
        apostaEquipeLotecaView.setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
    }
}