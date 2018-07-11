package caixa.gov.br.loterias.view.custom;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by cedesbr450 on 26/03/18.
 */

    @EViewGroup(R.layout.item_resultado)
    public class ModalidadeSorteioResultadoView extends ScrollView {


    private Modalidade modalidade;


    @ViewById
    protected TextView numeroeDataConcursoResultadoMegaSena;

    public LinearLayout getContentResultadoItem() {
        return contentResultadoItem;
    }

    public void setContentResultadoItem(LinearLayout contentResultadoItem) {
        this.contentResultadoItem = contentResultadoItem;
    }

    @ViewById
    protected LinearLayout contentResultadoItem;


    @ViewById
        protected ScrollView scrollResultados;

        @AfterViews
        protected void init() {

        }

        public void setLayout(Modalidade modalidade) {

            this.modalidade = modalidade;

            this.numeroeDataConcursoResultadoMegaSena.setText(ViewUtils.fromHtml("Nº <b>"+ modalidade.getConcurso().getNumero()+"</b> | " + modalidade.getConcurso().getDataSorteio()));
            setContentResultadoPorModalide(modalidade);

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


    public ScrollView getScrollResultados() {
        return scrollResultados;
    }

    public void setScrollResultados(ScrollView scrollResultados) {
        this.scrollResultados = scrollResultados;
    }


    public TextView getNumeroeDataConcursoResultadoMegaSena() {
        return numeroeDataConcursoResultadoMegaSena;
    }

    public void setNumeroeDataConcursoResultadoMegaSena(TextView numeroeDataConcursoResultadoMegaSena) {
        this.numeroeDataConcursoResultadoMegaSena = numeroeDataConcursoResultadoMegaSena;
    }

    private void setContentResultadoPorModalide(Modalidade modalidade){
            switch (modalidade.getTipoModalidade()){
                case DUPLA_SENA:
                    this.contentResultadoItem.removeAllViews();
                    ModalidadeSorteioItemDoisConcursosResultadoView view = ModalidadeSorteioItemDoisConcursosResultadoView_.build(getContext());
                    view.setLayout(modalidade);
                    this.contentResultadoItem.addView(view);
                    break;
                case TIMEMANIA:
                    this.contentResultadoItem.removeAllViews();
                    ModalideSorteioItemTimeManiaResultadoView view3 = ModalideSorteioItemTimeManiaResultadoView_.build(getContext());
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
                    default:
                        this.contentResultadoItem.removeAllViews();
                        ModalidadeSorteioItemUmConcursoResultadoView view2 = ModalidadeSorteioItemUmConcursoResultadoView_.build(getContext());
                        view2.setLayout(modalidade);
                        this.contentResultadoItem.addView(view2);
                        break;
            }
    }

    }

