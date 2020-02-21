package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_um_concurso)
public class ModalidadeSorteioItemUmConcursoResultadoView extends LinearLayout {

    @ViewById
    protected RecyclerView recyclerNumerosResultado;

    @ViewById
    protected LinearLayout  item_resultado_content_view;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;


    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));

        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio().size()>6){
            numberOfColumns = 4;
        }
        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

        this.partidaViewDetalhesResultadoView.setLayout(modalidade);

    }

    public ModalidadeSorteioItemUmConcursoResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioItemUmConcursoResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioItemUmConcursoResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
