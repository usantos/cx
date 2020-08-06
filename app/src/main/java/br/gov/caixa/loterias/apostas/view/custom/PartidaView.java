package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.view.adapter.LotecaAdapter;

/**
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.row_time_loteca)
public class PartidaView extends LinearLayout {

    @ViewById
    public ItemEquipeView lotecaTimeUm, lotecaEmpate, lotecaTimeDois;

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

    @Click(R.id.loteca_time_dois)
    public void clickTimeDois() {
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

    @Click(R.id.loteca_empate)
    public void clickEmpate() {
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

    public void setListener(LotecaAdapter.LocateAdapterListener listener) {
        this.listener = listener;
    }

    public void setLayout(ParametroPartida parametroPartida) {

        this.parametroPartida = parametroPartida;

        lotecaTimeUm.setSelected(parametroPartida.getEquipe1().isSelecionado());
//        lotecaTimeUm.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe1(), lotecaTimeUm.getItemImage());

        lotecaTimeUm.getItemTitle().setText(parametroPartida.getEquipe1().getNome());
        lotecaTimeUm.getItemDescription().setText(parametroPartida.getEquipe1().getIndicadorSelecao() ? "" : parametroPartida.getEquipe1().getSiglaPais());

        lotecaTimeDois.setSelected(parametroPartida.getEquipe2().isSelecionado());
//        lotecaTimeDois.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2(), lotecaTimeDois.getItemImage());
        lotecaTimeDois.getItemTitle().setText(parametroPartida.getEquipe2().getNome());
        lotecaTimeDois.getItemDescription().setText(parametroPartida.getEquipe2().getIndicadorSelecao() ? "" : parametroPartida.getEquipe2().getSiglaPais());

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
