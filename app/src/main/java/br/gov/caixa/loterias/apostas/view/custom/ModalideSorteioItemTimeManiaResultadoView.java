package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosResultadosAdapter;


/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_timemania)
public class ModalideSorteioItemTimeManiaResultadoView extends LinearLayout {

    @ViewById
    protected RecyclerView recyclerNumerosResultado;

    @ViewById
    protected LinearLayout  item_resultado_content_view;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected TextView labelTimeCoracaoText, textTimeCoracao;

    @ViewById
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    @ViewById
    protected ImageView timeCoracaoImage;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        if (modalidade.getTipoModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            timeCoracaoImage.setVisibility(View.GONE);
            labelTimeCoracaoText.setText(R.string.label_mes_desejado);
            if (modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte() != null
                    && modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte().getMesDeSorte() != null) {
                this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte().getMesDeSorte().getNome());
            }else{
                this.textTimeCoracao.setText(R.string.label_tres_interrogacoes);
            }
        } else {
            this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" +
                    modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getUf());
        }
        this.tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }
        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getListaSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);


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

}
