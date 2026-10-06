package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosGridAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosResultadosAdapter;


/**
 * Created by cedesbr450 on 28/03/18.
 */

public class ModalideSorteioItemTimeManiaResultadoView extends LinearLayout {

    private boolean alreadyInflated = false;
    protected RecyclerView recyclerNumerosResultado;
    protected LinearLayout  item_resultado_content_view;
    protected View tracoViewDivisoriaResultado;
    protected TextView labelTimeCoracaoText, textTimeCoracao;
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;
    protected ImageView timeCoracaoImage;


    public static ModalideSorteioItemTimeManiaResultadoView build(Context context) {
        ModalideSorteioItemTimeManiaResultadoView instance = new ModalideSorteioItemTimeManiaResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado_timemania, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.recyclerNumerosResultado = findViewById(R.id.recyclerNumerosResultado);
        this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
        this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
        this.labelTimeCoracaoText = findViewById(R.id.labelTimeCoracaoText);
        this.textTimeCoracao = findViewById(R.id.textTimeCoracao);
        this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
        this.timeCoracaoImage = findViewById(R.id.timeCoracaoImage);
    }

    public void setLayout(Modalidade modalidade) {

        //EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());
        if (modalidade.getTipoModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            timeCoracaoImage.setVisibility(View.GONE);
            labelTimeCoracaoText.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
            labelTimeCoracaoText.setText(R.string.label_mes_desejado);
            if (modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte() != null
                    && modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte().getMesDeSorte() != null) {
                this.textTimeCoracao.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
                this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte().getMesDeSorte().getNome());
            }else{
                this.textTimeCoracao.setText(R.string.label_tres_interrogacoes);
            }

            tracoViewDivisoriaResultado.setBackground(getContext().getDrawable(estilo.getCorFonteFundoEscuro()));

        } else {
            this.textTimeCoracao.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
            this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" +
                    modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getUf());

            tracoViewDivisoriaResultado.setBackground(getContext().getDrawable(estilo.getCorFonteFundoClaro()));
        }
        //this.tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));

        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }
        //this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        //numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        //this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);
        if (modalidade.getTipoModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            setupGridNumeros(recyclerNumerosResultado, Shape.NO_SHAPE, numberOfColumns, StringUtils.formatToStringList(modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio()), getContext().getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
        } else {
            setupGridNumeros(recyclerNumerosResultado, Shape.NO_SHAPE, numberOfColumns, StringUtils.formatToStringList(modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio()), getContext().getColor(estilo.getCorFonteFundoClaro()), Color.TRANSPARENT, Color.TRANSPARENT);
        }

        this.partidaViewDetalhesResultadoView.setLayout(modalidade);
    }

    public ModalideSorteioItemTimeManiaResultadoView(Context context) {
        super(context);
    }

    public ModalideSorteioItemTimeManiaResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalideSorteioItemTimeManiaResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void setupGridNumeros(RecyclerView recyclerView, Shape shape, int columns, List<String> numbers, int textColor, int borderColor, int backgroudColor) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columns));
        recyclerView.setNestedScrollingEnabled(false);
        NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor);
        recyclerView.setAdapter(adapter);
    }

}
