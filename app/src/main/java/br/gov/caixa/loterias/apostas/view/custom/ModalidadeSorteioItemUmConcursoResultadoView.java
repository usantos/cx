package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TrevosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


public class ModalidadeSorteioItemUmConcursoResultadoView extends LinearLayout {

    private boolean alreadyInflated = false;

    private RecyclerView recyclerNumerosResultado;
    private LinearLayout  item_resultado_content_view;
    private View tracoViewDivisoriaResultado;
    private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;
    private RecyclerView recyclerTrevosResultado;
    private TextView tvTrevos;

    public static ModalidadeSorteioItemUmConcursoResultadoView build(Context context) {
        ModalidadeSorteioItemUmConcursoResultadoView instance = new ModalidadeSorteioItemUmConcursoResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado_um_concurso, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.recyclerNumerosResultado = findViewById(R.id.recyclerNumerosResultado);
        this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
        this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
        this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
        this.recyclerTrevosResultado = findViewById(R.id.recyclerTrevosResultado);
        this.tvTrevos = findViewById(R.id.tvTrevos);
    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));

        NumerosResultadosAdapter numerosResultadosAdapter;

        int numberOfColumns = 3;
        if(!modalidade.getNome().equalsIgnoreCase("Mais Milionária".toLowerCase())){
            if (modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio().size()>6){
                numberOfColumns = 4;
            }
        }

        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

        if(modalidade.getNome().equalsIgnoreCase("Mais Milionária".toLowerCase())){
            tvTrevos.setVisibility(VISIBLE);
            recyclerTrevosResultado.setVisibility(VISIBLE);

            TrevosResultadosAdapter numerosResultadosAdapter2;
            this.recyclerTrevosResultado.setLayoutManager(new GridLayoutManager(getContext(), 3));
            numerosResultadosAdapter2 = new TrevosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getTrevosSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
            this.recyclerTrevosResultado.setAdapter(numerosResultadosAdapter2);
        }


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
