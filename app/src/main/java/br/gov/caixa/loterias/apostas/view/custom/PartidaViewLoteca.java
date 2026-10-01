package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.DescricaoLoteca;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;

/**
 * Created by cedesbr450 on 15/03/18.
 */

public class PartidaViewLoteca extends LinearLayout {

    private boolean alreadyInflated = false;

    private TextView tv_titulo;
    private ApostaEquipeLotecaView apostas_partida_loteca_view_um, apostas_partida_loteca_view_empate, apostas_partida_loteca_view_dois;
    private LinearLayout layout_row_apostas_partidas_loteca_list_view;

    public LinearLayout getLayout_row_apostas_partidas_loteca_list_view() {
        return layout_row_apostas_partidas_loteca_list_view;
    }

    public void setLayout_row_apostas_partidas_loteca_list_view(LinearLayout layout_row_apostas_partidas_loteca_list_view) {
        this.layout_row_apostas_partidas_loteca_list_view = layout_row_apostas_partidas_loteca_list_view;
    }

    public static PartidaViewLoteca build(Context context) {
        PartidaViewLoteca instance = new PartidaViewLoteca(context);
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

    protected void init() {
        this.tv_titulo = findViewById(R.id.tv_titulo);
        this.apostas_partida_loteca_view_um = findViewById(R.id.apostas_partida_loteca_view_um);
        this.apostas_partida_loteca_view_empate = findViewById(R.id.apostas_partida_loteca_view_empate);
        this.apostas_partida_loteca_view_dois = findViewById(R.id.apostas_partida_loteca_view_dois);
        this.layout_row_apostas_partidas_loteca_list_view = findViewById(R.id.layout_row_apostas_partidas_loteca_list_view);

    }

    public void setLayout(PartidaLotecaDTO parametroPartida, int position) {
        String jogoTitulo = "Jogo " + String.format("%02d", position + 1) + DescricaoLoteca.descricaoPartida(parametroPartida);
        tv_titulo.setText(jogoTitulo);
        layout_row_apostas_partidas_loteca_list_view.setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe1().getNome());
        apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_um.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe1().getVitoria());
        apostas_partida_loteca_view_um.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_um.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if (parametroPartida.getEquipe1().getVitoria()){
            apostas_partida_loteca_view_um.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
        }
        apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setText("X");
        apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_empate.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEmpate());
        apostas_partida_loteca_view_empate.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_empate.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(parametroPartida.getEmpate()){
            apostas_partida_loteca_view_empate.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
        }
        apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe2().getNome());
        apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe2().getVitoria());
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setBackgroundColor(getContext().getResources().getColor(R.color.branco));
        apostas_partida_loteca_view_dois.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostas_partida_loteca_view_dois.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(parametroPartida.getEquipe2().getVitoria()){
            apostas_partida_loteca_view_dois.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
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
