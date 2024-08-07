package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */

public class ModalidadeSorteioItemDoisConcursosResultadoView extends LinearLayout {

    private boolean alreadyInflated = false;

    private RecyclerView recyclerNumerosResultado, recyclerNumerosResultadoSegundoSorteio;
    private LinearLayout linearlayoutPremiadosListaText, item_resultado_content_view;
    private View tracoViewDivisoriaResultado;
    private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;


    public static ModalidadeSorteioItemDoisConcursosResultadoView build(Context context) {
        ModalidadeSorteioItemDoisConcursosResultadoView instance = new ModalidadeSorteioItemDoisConcursosResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado_dois_concursos, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.recyclerNumerosResultado = findViewById(R.id.recyclerNumerosResultado);
        this.recyclerNumerosResultadoSegundoSorteio = findViewById(R.id.recyclerNumerosResultadoSegundoSorteio);
        this.linearlayoutPremiadosListaText = findViewById(R.id.linearlayoutPremiadosListaText);
        this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
        this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
        this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
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

        this.recyclerNumerosResultadoSegundoSorteio.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosSegundoSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultadoSegundoSorteio.setAdapter(numerosResultadosAdapter);

        this.partidaViewDetalhesResultadoView.setLayout(modalidade);
    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
