package br.gov.caixa.loterias.apostas.view.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;

/**
 * Created by cedesbr450 on 26/03/18.
 */

public class ModalidadeSorteioResultadoView extends ScrollView {
    private boolean alreadyInflated = false;
    private TextView numeroeDataConcursoResultadoMegaSena;
    private LinearLayout contentResultadoItem;
    private ScrollView scrollResultados;

    public static ModalidadeSorteioResultadoView build(Context context) {
        ModalidadeSorteioResultadoView instance = new ModalidadeSorteioResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.numeroeDataConcursoResultadoMegaSena = findViewById(R.id.numeroeDataConcursoResultadoMegaSena);
        this.contentResultadoItem = findViewById(R.id.contentResultadoItem);
        this.scrollResultados = findViewById(R.id.scrollResultados);
    }

    public void setLayout(Modalidade modalidade) {
        //TODO: MEGA 30 ANOS//
        boolean isMega30 = EspecialUtils.isMega30(modalidade.getConcurso().getNumero(), modalidade.getConcurso().getTipoConcurso());
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade(), isMega30);

        this.numeroeDataConcursoResultadoMegaSena.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
        //this.numeroeDataConcursoResultadoMegaSena.setText(ViewUtils.fromHtml("Nº <b>" + modalidade.getResultadoConcursoDTO().getConcurso().getNumero() + "</b> | " + modalidade.getResultadoConcursoDTO().getConcurso().getDataSorteio()));
        String sorteio = modalidade.getResultadoConcursoDTO().getConcurso().getDataSorteio();
        String[] p = sorteio.split(" ");
        String resultado = p[0] + " " + Character.toUpperCase(p[1].charAt(0)) + p[1].substring(1) + " " + p[2];
        this.numeroeDataConcursoResultadoMegaSena.setText(ViewUtils.textCaixaSTDBold(getContext(), "_Nº " + modalidade.getResultadoConcursoDTO().getConcurso().getNumero() + "_ | " + resultado));
        //title1.setText(ViewUtils.textCaixaSTDBold(getContext(), getResources().getString(R.string.label_premiacao_bold)));

        if (ModalidadeEnum.LOTECA.toString().equalsIgnoreCase(modalidade.getTipoModalidade().toString())) {
            EscudoBO.getInstance().carregaEscudos(getContext(), () -> setContentResultadoPorModalidade(modalidade));
        } else {
            setContentResultadoPorModalidade(modalidade);
        }
        this.scrollResultados.setFocusableInTouchMode(true);
        this.scrollResultados.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }

    private ModalidadeSorteioResultadoView(Context context) {
        super(context);
    }

    private void setContentResultadoPorModalidade(Modalidade modalidade) {
        switch (modalidade.getTipoModalidade()) {
            case DUPLA_SENA:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioItemDoisConcursosResultadoView view = ModalidadeSorteioItemDoisConcursosResultadoView.build(getContext());
                view.setLayout(modalidade);
                this.contentResultadoItem.addView(view);
                break;
            case TIMEMANIA:
            case DIA_DE_SORTE:
                this.contentResultadoItem.removeAllViews();
                ModalideSorteioItemTimeManiaResultadoView view3 = ModalideSorteioItemTimeManiaResultadoView.build(getContext());
                view3.setLayout(modalidade);
                this.contentResultadoItem.addView(view3);
                break;
            case LOTECA:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioLotecaResultadoView view4 = ModalidadeSorteioLotecaResultadoView.build(getContext());
                view4.setLayout(modalidade);
                this.contentResultadoItem.addView(view4);
                break;
            case LOTOGOL:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioLotogolResultadoView view5 = ModalidadeSorteioLotogolResultadoView.build(getContext());
                view5.setLayout(modalidade);
                this.contentResultadoItem.addView(view5);
                break;
            case SUPER_7:
                this.contentResultadoItem.removeAllViews();
                SuperSeteResultadoLayout view7 = SuperSeteResultadoLayout.build(getContext());
                view7.setLayout(modalidade);
                this.contentResultadoItem.addView(view7);
                break;
            case MEGA_SENA:
                //TODO: MEGA 30 ANOS//
                boolean isMega30 = EspecialUtils.isMega30(modalidade.getConcurso().getNumero(), modalidade.getConcurso().getTipoConcurso());
                if (isMega30){
                    this.contentResultadoItem.removeAllViews();
                    ModalidadeSorteioItemUmConcursoResultadoView view8 = ModalidadeSorteioItemUmConcursoResultadoView.build(getContext());
                    view8.setLayout(modalidade);
                    this.contentResultadoItem.addView(view8);
                } else {
                    this.contentResultadoItem.removeAllViews();
                    ModalidadeSorteioItemUmConcursoResultadoView view6 = ModalidadeSorteioItemUmConcursoResultadoView.build(getContext());
                    view6.setLayout(modalidade);
                    this.contentResultadoItem.addView(view6);
                }
                break;
            default:
                this.contentResultadoItem.removeAllViews();
                ModalidadeSorteioItemUmConcursoResultadoView view2 = ModalidadeSorteioItemUmConcursoResultadoView.build(getContext());
                view2.setLayout(modalidade);
                this.contentResultadoItem.addView(view2);
                break;
        }
    }

}

