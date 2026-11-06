package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by cedesbr450 on 02/04/18.
 */

public class PartidaViewLotecaResultado extends LinearLayout {

    private boolean alreadyInflated = false;

    private ItemEquipeViewResultado lotecaTimeUm, lotecaEmpate, lotecaTimeDois;
    private TextView paginaResultadoLotecaText;
    private TextView diaSemanaDataPartidaText;

    public static PartidaViewLotecaResultado build(Context context) {
        PartidaViewLotecaResultado instance = new PartidaViewLotecaResultado(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_time_loteca_resultado, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.lotecaTimeUm = findViewById(R.id.loteca_time_um);
        this.lotecaEmpate = findViewById(R.id.loteca_empate);
        this.lotecaTimeDois = findViewById(R.id.loteca_time_dois);
        this.paginaResultadoLotecaText = findViewById(R.id.paginaResultadoLotecaText);
        this.diaSemanaDataPartidaText = findViewById(R.id.diaSemanaDataPartidaText);
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
        //TODO//
        String time1 = Utils.getUfTime(parametroPartida.getEquipe1())+" "+parametroPartida.getLegendaAbreviada();
        lotecaTimeUm.getItemDescription().setText(time1);

        lotecaTimeDois.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2().getParametroEquipe(), lotecaTimeDois.getItemImage());
        lotecaTimeDois.getItemTitle().setText(parametroPartida.getEquipe2().getNome());
        //TODO//
        String time2 = Utils.getUfTime(parametroPartida.getEquipe2())+" "+parametroPartida.getLegendaAbreviada();
        lotecaTimeDois.getItemDescription().setText(time2);

        diaSemanaDataPartidaText.setText(ViewUtils.textCaixaSTDBold(getContext(), getResources().getString(R.string.texto_futura_bold, getResources().getString(R.string.label_domingo))));

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