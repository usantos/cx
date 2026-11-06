package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosGridAdapter;
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

        //TODO: MEGA 30 ANOS//
        boolean isMega30 = EspecialUtils.isMega30(modalidade.getConcurso().getNumero(), modalidade.getConcurso().getTipoConcurso());
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade(), isMega30);
        //tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        tracoViewDivisoriaResultado.setBackground(getContext().getDrawable(estilo.getCorFonteFundoClaro()));

        NumerosResultadosAdapter numerosResultadosAdapter;

        int numberOfColumns = 3;
        if(!modalidade.getNome().equalsIgnoreCase("Mais Milionária".toLowerCase())){
            if (modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio().size()>6){
                numberOfColumns = 4;
            }
        }

        //this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        //numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        //this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);
        int color;
        if (EspecialUtils.isParametrosOutubroRosa() && modalidade.getTipoModalidade() == ModalidadeEnum.MEGA_SENA){
            color = getContext().getColor(estilo.getCorFonteFundoEscuro());
        } else {
            color = getContext().getColor(estilo.getCorFonteFundoClaro());
        }
        setupGridNumeros(recyclerNumerosResultado,Shape.NO_SHAPE,numberOfColumns, StringUtils.formatToStringList(modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio()), color, Color.TRANSPARENT, Color.TRANSPARENT);

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


    private void setupGridNumeros(RecyclerView recyclerView, Shape shape, int columns, List<String> numbers, int textColor, int borderColor, int backgroudColor) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columns));
        recyclerView.setNestedScrollingEnabled(false);
        NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor);
        recyclerView.setAdapter(adapter);
    }
}
