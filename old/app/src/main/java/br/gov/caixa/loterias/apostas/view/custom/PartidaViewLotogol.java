package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;

/**
 * Created by joafilho on 09/01/2018.
 */
public class PartidaViewLotogol extends RelativeLayout implements LotogolListener {

    private boolean alreadyInflated = false;

    private RelativeLayout rowPartidaLotogol;
    private ItemEquipeViewLotogol lotogolTimeUm, lotogolTimeDois;

    private ParametroPartida parametroPartida;
    private LotogolListener listener;

    public static PartidaViewLotogol build(Context context) {
        PartidaViewLotogol instance = new PartidaViewLotogol(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_partida_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.rowPartidaLotogol = findViewById(R.id.row_partida_lotogol);
        this.lotogolTimeUm = findViewById(R.id.lotogol_time_um);
        this.lotogolTimeDois = findViewById(R.id.lotogol_time_dois);
    }

    public PartidaViewLotogol(Context context) {
        super(context);
    }

    public PartidaViewLotogol(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewLotogol(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(ParametroPartida parametroPartida) {

        this.parametroPartida = parametroPartida;
        lotogolTimeUm.setListerner(this);
        lotogolTimeDois.setListerner(this);
        lotogolTimeUm.setParametroEquipe(parametroPartida.getEquipe1());
        lotogolTimeDois.setParametroEquipe(parametroPartida.getEquipe2());

        String paisTime1 = (parametroPartida.getEquipe1().getIndicadorSelecao() ? "" : " / " + parametroPartida.getEquipe1().getSiglaPais());
        String paisTime2 = (parametroPartida.getEquipe2().getIndicadorSelecao() ? "" : " / " + parametroPartida.getEquipe2().getSiglaPais());

        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe1(), lotogolTimeUm.getItemImage());
        lotogolTimeUm.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe1().getNome() + "</strong>" + paisTime1));

        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2(), lotogolTimeDois.getItemImage());
        lotogolTimeDois.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe2().getNome() + "</strong>" + paisTime2));

    }

    public void setListener(LotogolListener listener) {
        this.listener = listener;
    }

    @Override
    public void partidaSelecionada() {
        if (StringUtils.isNotEmpty(parametroPartida.getEquipe1().getPlacar()) && StringUtils.isNotEmpty(parametroPartida.getEquipe2().getPlacar())) {
            parametroPartida.setSelecionado(Boolean.TRUE);
        } else {
            parametroPartida.setSelecionado(Boolean.FALSE);
        }
        listener.atualizarParametrosLotogol(parametroPartida);
    }

    @Override
    public void atualizarParametrosLotogol(ParametroPartida parametroPartida) {

    }

    public RelativeLayout getRowPartidaLotogol() {
        return rowPartidaLotogol;
    }
}
