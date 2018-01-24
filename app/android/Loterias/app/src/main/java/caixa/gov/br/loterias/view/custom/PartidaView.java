package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.view.adapter.LotecaAdapter;

/**
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.row_time_loteca)
public class PartidaView extends LinearLayout {

    @ViewById
    ItemEquipeView lotecaTimeUm, lotecaEmpate, lotecaTimeDois;

    private ParametroPartida parametroPartida;

    private LotecaAdapter.LocateAdapterListener listener;

    private static final float DOIS = 2;

    private static final float ZERO= 0;

    @AfterViews
    protected void init() {

    }

    @Click(R.id.loteca_time_um)
    public void clickTimeUm() {
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaTimeUm.isSelected()) {
            lotecaTimeUm.setSelected(Boolean.FALSE);
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
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.getEquipe1().setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeUm.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    @Click(R.id.loteca_time_dois)
    public void clickTimeDois() {
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaTimeDois.isSelected()) {
            lotecaTimeDois.setSelected(Boolean.FALSE);
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
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.getEquipe2().setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaTimeDois.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    @Click(R.id.loteca_empate)
    public void clickEmpate() {
        parametroPartida.setQtdItensSelecionadosAnterior(parametroPartida.getQtdItensSelecionados());
        if (lotecaEmpate.isSelected()) {
            lotecaEmpate.setSelected(Boolean.FALSE);
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
            parametroPartida.setEmpate(Boolean.TRUE);
            parametroPartida.setSelecionado(Boolean.TRUE);
            parametroPartida.setQtdItensSelecionados(parametroPartida.getQtdItensSelecionados() +1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lotecaEmpate.setTranslationZ(DOIS);
            }
        }
        listener.clickEquipeLoteca(parametroPartida);
    }

    public void setListener(LotecaAdapter.LocateAdapterListener listener) {
        this.listener = listener;
    }

    public void setLayout(ParametroPartida parametroPartida) {

        this.parametroPartida = parametroPartida;

        lotecaTimeUm.setSelected(parametroPartida.getEquipe1().isSelecionado());
        lotecaTimeUm.getItemImage().setImageResource(R.drawable.time);
        lotecaTimeUm.getItemTitle().setText(parametroPartida.getEquipe1().getNome());
        lotecaTimeUm.getItemDescription().setText(parametroPartida.getEquipe1().getIndicadorSelecao() ? "" : parametroPartida.getEquipe1().getPais());

        lotecaTimeDois.setSelected(parametroPartida.getEquipe2().isSelecionado());
        lotecaTimeDois.getItemImage().setImageResource(R.drawable.time);
        lotecaTimeDois.getItemTitle().setText(parametroPartida.getEquipe2().getNome());
        lotecaTimeDois.getItemDescription().setText(parametroPartida.getEquipe2().getIndicadorSelecao() ? "" : parametroPartida.getEquipe2().getPais());

        lotecaEmpate.setSelected(parametroPartida.isEmpate());
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
