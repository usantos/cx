package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;

/**
 * Created by joafilho on 28/12/2017.
 */
public class PartidaView extends LinearLayout {

    private boolean alreadyInflated = false;

    public ItemEquipeView lotecaTimeUm, lotecaEmpate, lotecaTimeDois;

    private ParametroPartida parametroPartida;
    private ParametroJogoDTO parametroJogoDTO;

    private LocateAdapterListener listener;

    private static final float DOIS = 2;

    private static final float ZERO= 0;

    public static PartidaView build(Context context) {
        PartidaView instance = new PartidaView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_time_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.lotecaTimeUm = findViewById(R.id.loteca_time_um);
        this.lotecaEmpate = findViewById(R.id.loteca_empate);
        this.lotecaTimeDois = findViewById(R.id.loteca_time_dois);

        lotecaTimeUm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeUm();
            }
        });

        lotecaTimeDois.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeDois();
            }
        });

        lotecaEmpate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEmpate();
            }
        });
    }

    private boolean verificaAberto(){
        if (parametroJogoDTO != null &&
                parametroJogoDTO.getConcurso() != null &&
                parametroJogoDTO.getConcurso().getAberto() != null &&
                !parametroJogoDTO.getConcurso().getAberto()) {
            ViewUtils.alertTitleButton(getContext(),
                                       R.string.label_atencao ,
                                       getResources().getString(R.string.msg_conc_nao_aberto).replace("{num_concurso}",
                                                                                                      parametroJogoDTO.getConcurso().getNumero().toString()),
                                       getResources().getString(R.string.title_entendi));
            return false;
        } else {
            return true;
        }

    }

    public void clickTimeUm() {
        if(!verificaAberto()){
            return;
        }
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaTimeUm.isSelected()) {
            lotecaTimeUm.setSelected(Boolean.FALSE);
            lotecaTimeUm.getItemTitle().setTextColor(getResources().getColor(R.color.cinza));
            lotecaTimeUm.getItemDescription().setTextColor(getResources().getColor(R.color.cinza));
            parametroPartida.getEquipe1().setSelecionado(Boolean.FALSE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() -1);

            if (!parametroPartida.isEmpate() && !parametroPartida.getEquipe2().isSelecionado()) {
                parametroPartida.setSelecionado(Boolean.FALSE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeUm.setTranslationZ(ZERO);
            }
        } else {
            lotecaTimeUm.setSelected(Boolean.TRUE);
            lotecaTimeUm.getItemTitle().setTextColor(getResources().getColor(R.color.lotecaEscuroToolbar));
            lotecaTimeUm.getItemDescription().setTextColor(getResources().getColor(R.color.lotecaEscuroToolbar));
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.getEquipe1().setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeUm.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    public void clickTimeDois() {
        if(!verificaAberto()){
            return;
        }
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaTimeDois.isSelected()) {
            lotecaTimeDois.setSelected(Boolean.FALSE);
            lotecaTimeDois.getItemTitle().setTextColor(getResources().getColor(R.color.cinza));
            lotecaTimeDois.getItemDescription().setTextColor(getResources().getColor(R.color.cinza));
            parametroPartida.getEquipe2().setSelecionado(Boolean.FALSE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() -1);

            if (!parametroPartida.isEmpate() && !parametroPartida.getEquipe1().isSelecionado()) {
                parametroPartida.setSelecionado(Boolean.FALSE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeDois.setTranslationZ(ZERO);
            }
        } else {
            lotecaTimeDois.setSelected(Boolean.TRUE);
            lotecaTimeDois.getItemTitle().setTextColor(getResources().getColor(R.color.lotecaEscuroToolbar));
            lotecaTimeDois.getItemDescription().setTextColor(getResources().getColor(R.color.lotecaEscuroToolbar));
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.getEquipe2().setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeDois.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    public void clickEmpate() {
        if(!verificaAberto()){
            return;
        }
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaEmpate.isSelected()) {
            lotecaEmpate.setSelected(Boolean.FALSE);
            lotecaEmpate.getItemTitle().setTextColor(getResources().getColor(R.color.cinza));
            parametroPartida.setEmpate(Boolean.FALSE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() -1);

            if (!parametroPartida.getEquipe1().isSelecionado() && !parametroPartida.getEquipe2().isSelecionado()) {
                parametroPartida.setSelecionado(Boolean.FALSE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaEmpate.setTranslationZ(ZERO);
            }
        } else {
            lotecaEmpate.setSelected(Boolean.TRUE);
            lotecaEmpate.getItemTitle().setTextColor(getResources().getColor(R.color.lotecaEscuroToolbar));
            parametroPartida.setEmpate(Boolean.TRUE);
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaEmpate.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    public void setListener(LocateAdapterListener listener) {
        this.listener = listener;
    }

    public void setLayout(ParametroPartida parametroPartida, ParametroJogoDTO parametroJogoDTO) {

        this.parametroPartida = parametroPartida;
        this.parametroJogoDTO = parametroJogoDTO;

        lotecaTimeUm.setSelected(parametroPartida.getEquipe1().isSelecionado());
//        lotecaTimeUm.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe1(), lotecaTimeUm.getItemImage());

        lotecaTimeUm.getItemTitle().setText(parametroPartida.getEquipe1().getNome());
        //TODO//
//        String nomeTime = Utils.getUfTime(parametroPartida.getEquipe1())+" "+parametroPartida.getLegendaAbreviada();
        lotecaTimeUm.getItemDescription().setText(Utils.getUfTime(parametroPartida.getEquipe1()));

        lotecaTimeDois.setSelected(parametroPartida.getEquipe2().isSelecionado());
//        lotecaTimeDois.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2(), lotecaTimeDois.getItemImage());
        lotecaTimeDois.getItemTitle().setText(parametroPartida.getEquipe2().getNome());
        //TODO//
        // String nomeTime2 = Utils.getUfTime(parametroPartida.getEquipe2())+" "+parametroPartida.getLegendaAbreviada();
        lotecaTimeDois.getItemDescription().setText(Utils.getUfTime(parametroPartida.getEquipe2()));

        lotecaEmpate.setSelected(parametroPartida.isEmpate());
//        lotecaEmpate.getItemImage().setPadding(14,18,14,18);
    }

    public PartidaView(Context context) {
        super(context);
    }

    public PartidaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
