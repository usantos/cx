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

    private TextView tvTitulo;
    private ApostaEquipeLotecaView apostasPartidaLotecaViewUm;
    private ApostaEquipeLotecaView apostasPartidaLotecaViewEmpate;
    private ApostaEquipeLotecaView apostasPartidaLotecaViewDois;
    private LinearLayout layoutRowApostasPartidasLotecaListView;
    public LinearLayout getLayoutRowApostasPartidasLotecaListView() {
        return layoutRowApostasPartidasLotecaListView;
    }
    public void setLayoutRowApostasPartidasLotecaListView(LinearLayout layoutRowApostasPartidasLotecaListView) {
        this.layoutRowApostasPartidasLotecaListView = layoutRowApostasPartidasLotecaListView;
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
        this.tvTitulo = findViewById(R.id.tv_titulo);
        this.apostasPartidaLotecaViewUm = findViewById(R.id.apostas_partida_loteca_view_um);
        this.apostasPartidaLotecaViewEmpate = findViewById(R.id.apostas_partida_loteca_view_empate);
        this.apostasPartidaLotecaViewDois = findViewById(R.id.apostas_partida_loteca_view_dois);
        this.layoutRowApostasPartidasLotecaListView = findViewById(R.id.layout_row_apostas_partidas_loteca_list_view);
    }
    public void setLayout(PartidaLotecaDTO parametroPartida, int position) {
        String jogoTitulo = "Jogo " + String.format("%02d", position + 1) + DescricaoLoteca.descricaoPartida(parametroPartida);
        boolean isVitoriaEquipe1 = parametroPartida.getEquipe1().getVitoria();
        boolean isEmpate = parametroPartida.getEmpate();
        boolean isVitoriaEquipe2 = parametroPartida.getEquipe2().getVitoria();

        tvTitulo.setText(jogoTitulo);
        layoutRowApostasPartidasLotecaListView.setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostasPartidaLotecaViewUm.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe1().getNome());
        apostasPartidaLotecaViewUm.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostasPartidaLotecaViewUm.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe1().getVitoria());
        apostasPartidaLotecaViewUm.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostasPartidaLotecaViewUm.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if (isVitoriaEquipe1){
            apostasPartidaLotecaViewUm.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.SEMI_BOLD));
        }
        apostasPartidaLotecaViewEmpate.getTextApostaPartidaLoteca().setText("X");
        apostasPartidaLotecaViewEmpate.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostasPartidaLotecaViewEmpate.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEmpate());
        apostasPartidaLotecaViewEmpate.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostasPartidaLotecaViewEmpate.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(isEmpate){
            apostasPartidaLotecaViewEmpate.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.SEMI_BOLD));
        }
        apostasPartidaLotecaViewDois.getTextApostaPartidaLoteca().setText(parametroPartida.getEquipe2().getNome());
        apostasPartidaLotecaViewDois.getTextApostaPartidaLoteca().setTextColor(getContext().getResources().getColor(R.color.branco));
        apostasPartidaLotecaViewDois.getImageApostaPartidaLoteca().setSelected(parametroPartida.getEquipe2().getVitoria());
        apostasPartidaLotecaViewDois.getImageApostaPartidaLoteca().setBackgroundColor(getContext().getResources().getColor(R.color.branco));
        apostasPartidaLotecaViewDois.getLayout_row_aposta_partida_loteca().setBackground(getContext().getResources().getDrawable(R.color.transparente));
        apostasPartidaLotecaViewDois.getImageApostaPartidaLoteca().setBackground(getContext().getResources().getDrawable(R.drawable.select_unselect_circle_generico));
        if(isVitoriaEquipe2){
            apostasPartidaLotecaViewDois.getTextApostaPartidaLoteca().setTypeface(FonteUtils.getFonte(FontCaixaEnum.SEMI_BOLD));
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
