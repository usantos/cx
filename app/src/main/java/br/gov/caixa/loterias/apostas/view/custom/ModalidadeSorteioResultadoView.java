package br.gov.caixa.loterias.apostas.view.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;

/**
 * Created by cedesbr450 on 26/03/18.
 */

@EViewGroup(R.layout.item_resultado)
public class ModalidadeSorteioResultadoView extends ScrollView {

    private Modalidade modalidade;

    @ViewById
    protected TextView numeroeDataConcursoResultadoMegaSena;

    @ViewById
    protected LinearLayout contentResultadoItem;

    @ViewById
    protected ScrollView scrollResultados;

    @AfterViews
    protected void init() {}

    public void setLayout(Modalidade modalidade) {

        this.modalidade = modalidade;

        this.numeroeDataConcursoResultadoMegaSena.setText(ViewUtils.fromHtml("Nº <b>" + modalidade.getResultadoConcursoDTO().getConcurso().getNumero() + "</b> | " + modalidade.getResultadoConcursoDTO().getConcurso().getDataSorteio()));
        if (ModalidadeEnum.LOTECA.toString().equalsIgnoreCase(modalidade.getTipoModalidade().toString())) {
            EscudoBO.getInstance().carregaEscudos(getContext(), new OnEscudoListener() {
                @Override
                public void aposCarregarEscudos() {
                    setContentResultadoPorModalidade(modalidade);
                }
            });
        } else {
            setContentResultadoPorModalidade(modalidade);
        }
        this.scrollResultados.setFocusableInTouchMode(true);
        this.scrollResultados.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

    }

    public ModalidadeSorteioResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setContentResultadoPorModalidade(Modalidade modalidade) {
        switch (modalidade.getTipoModalidade()) {
            case DUPLA_SENA:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioItemDoisConcursosResultadoView view = ModalidadeSorteioItemDoisConcursosResultadoView_.build(getContext());
                view.setLayout(modalidade);
                this.contentResultadoItem.addView(view);
                break;
            case TIMEMANIA:
                this.contentResultadoItem.removeAllViews();
                ModalideSorteioItemTimeManiaResultadoView view3 = ModalideSorteioItemTimeManiaResultadoView.build(getContext());
                view3.setLayout(modalidade);
                this.contentResultadoItem.addView(view3);
                break;
            case LOTECA:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioLotecaResultadoView view4 = ModalidadeSorteioLotecaResultadoView_.build(getContext());
                view4.setLayout(modalidade);
                this.contentResultadoItem.addView(view4);
                break;
            case LOTOGOL:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioLotogolResultadoView view5 = ModalidadeSorteioLotogolResultadoView_.build(getContext());
                view5.setLayout(modalidade);
                this.contentResultadoItem.addView(view5);
                break;
            case DIA_DE_SORTE:
                this.contentResultadoItem.removeAllViews();
                ModalideSorteioItemTimeManiaResultadoView view6 = ModalideSorteioItemTimeManiaResultadoView.build(getContext());
                view6.setLayout(modalidade);
                this.contentResultadoItem.addView(view6);
                break;
            case SUPER_7:
                this.contentResultadoItem.removeAllViews();
                SuperSeteResultadoLayout view7 = SuperSeteResultadoLayout.build(getContext());
                view7.setLayout(modalidade);
                this.contentResultadoItem.addView(view7);
                break;
            case MAIS_MILIONARIA:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioItemUmConcursoResultadoView viewM = ModalidadeSorteioItemUmConcursoResultadoView_.build(getContext());
                viewM.setLayout(modalidade);
                this.contentResultadoItem.addView(viewM);
                break;
            default:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioItemUmConcursoResultadoView view2 = ModalidadeSorteioItemUmConcursoResultadoView_.build(getContext());
                view2.setLayout(modalidade);
                this.contentResultadoItem.addView(view2);
                break;
        }
    }

}

