package caixa.gov.br.loterias.view.custom;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.view.adapter.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 26/03/18.
 */

    @EViewGroup(R.layout.item_resultado_mega_sena)
    public class MegaSenaResultadoView extends ScrollView {


    private Modalidade modalidade;



    @ViewById
        protected RecyclerView recyclerNumerosResultado;

        @ViewById
        protected TextView numeroeDataConcursoResultadoMegaSena;


    @ViewById
        protected ScrollView scrollMegaResultado;

        @AfterViews
        protected void init() {

        }

        public void setLayout(Modalidade modalidade) {

            this.modalidade = modalidade;

            this.numeroeDataConcursoResultadoMegaSena.setText("xxxxx");
            NumerosResultadosAdapter numerosResultadosAdapter;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            int numberOfColumns = 3;
            this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
            numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio(), modalidade);
            this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

            this.scrollMegaResultado.setFocusableInTouchMode(true);
            this.scrollMegaResultado.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        }

        public MegaSenaResultadoView(Context context) {
            super(context);
        }

        public MegaSenaResultadoView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public MegaSenaResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }



    public ScrollView getScrollMegaResultado() {
        return scrollMegaResultado;
    }

    public void setScrollMegaResultado(ScrollView scrollMegaResultado) {
        this.scrollMegaResultado = scrollMegaResultado;
    }


    public RecyclerView getRecyclerNumerosResultado() {
        return recyclerNumerosResultado;
    }

    public void setRecyclerNumerosResultado(RecyclerView recyclerNumerosResultado) {
        this.recyclerNumerosResultado = recyclerNumerosResultado;
    }

    public TextView getNumeroeDataConcursoResultadoMegaSena() {
        return numeroeDataConcursoResultadoMegaSena;
    }

    public void setNumeroeDataConcursoResultadoMegaSena(TextView numeroeDataConcursoResultadoMegaSena) {
        this.numeroeDataConcursoResultadoMegaSena = numeroeDataConcursoResultadoMegaSena;
    }

    }