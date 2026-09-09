package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;

/**
 * Created by joafilho on 02/04/2018.
 * Class ContentResultadoPremiado
 */

public class ContentResultadoPremiado extends LinearLayout {

    private boolean alreadyInflated = false;

    private LinearLayout premioInfoLinearLayout,topoModalidadeResultadoLinearLayout,
            fundoBackgroundTrapezeResultadoLinearLayout, topoResultadoPremioLinearLayout, premioGanhoLinearLayout,
            cabecalhoListaFaixaPremiacaoLinearLayout, cabecalhoListaFaixaPremiacaoBolao;
    private TextView valorEstimativaConcursoAtualTextView,valorPremioTituloTextView,
            tituloModalidadeTextView, proximoSorteioTextView, nsbBilhete, idPodeComemorar,
            tvFaixaPremiacao, tvValorPremioCota, tvValorPremioLiquido, tvColunaConcurso,
            tvColunaSituacao, tvColunaFaixa, tvColunaQtd, tvColunaSorteio, tvColunaValor;
    private ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView;
    private ConstraintLayout clVerDetalheAposta;
    private Button botaoAposteAgora ;
    private ImageView imgTrevoResultado; //, imgTagBolao;

    private TipoApostaLinhaView tipoApostaLinhaView;

    public ContentResultadoPremiado(Context context) {
        super(context);
    }

    public ContentResultadoPremiado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentResultadoPremiado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static ContentResultadoPremiado build(Context context) {
        ContentResultadoPremiado instance = new ContentResultadoPremiado(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.content_resultado_premiado, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.premioInfoLinearLayout = findViewById(R.id.premioInfoLinearLayout);
        this.topoModalidadeResultadoLinearLayout = findViewById(R.id.topoModalidadeResultadoLinearLayout);
        this.fundoBackgroundTrapezeResultadoLinearLayout = findViewById(R.id.fundoBackgroundTrapezeResultadoLinearLayout);
        this.topoResultadoPremioLinearLayout = findViewById(R.id.clTopoResultadoPremio);
        this.premioGanhoLinearLayout = findViewById(R.id.premioGanhoLinearLayout);
        this.tipoApostaLinhaView = findViewById(R.id.talvHeader);
        this.cabecalhoListaFaixaPremiacaoLinearLayout = findViewById(R.id.cabecalhoListaFaixaPremiacaoLinearLayout);
        this.cabecalhoListaFaixaPremiacaoBolao = findViewById(R.id.cabecalhoListaFaixaPremiacaoBolao);
        this.valorEstimativaConcursoAtualTextView = findViewById(R.id.valorEstimativaConcursoAtualTextView);
        this.valorPremioTituloTextView = findViewById(R.id.tvPremioTitulo);
        this.tituloModalidadeTextView = findViewById(R.id.tituloModalidadeTextView);
        this.proximoSorteioTextView = findViewById(R.id.proximoSorteioTextView);
        this.nsbBilhete = findViewById(R.id.nsbBilhete);
        this.idPodeComemorar = findViewById(R.id.tvPodeComemorar);
        this.tvFaixaPremiacao = findViewById(R.id.tvFaixaPremiacao);
        this.tvValorPremioCota = findViewById(R.id.tvValorPremioCota);
        this.tvValorPremioLiquido = findViewById(R.id.tvValorPremioLiquido);
        this.listaFaixaPremiacaoExpandableHeightRecyclerView = findViewById(R.id.listaFaixaPremiacaoExpandableHeightRecyclerView);
        this.clVerDetalheAposta = findViewById(R.id.clVerDetalheAposta);
        this.botaoAposteAgora = findViewById(R.id.botaoAposteAgora);
        this.imgTrevoResultado = findViewById(R.id.ivTrevoResultado);
        //this.imgTagBolao = findViewById(R.id.imgTagBolao);
        this.tvColunaConcurso = findViewById(R.id.concursoTextView);
        this.tvColunaSituacao = findViewById(R.id.situacaoTextView);
        this.tvColunaFaixa = findViewById(R.id.faixaPremiacaoTextView);
        this.tvColunaQtd = findViewById(R.id.quantidadePremiacaoTextView);
        this.tvColunaSorteio = findViewById(R.id.sorteioTextView);
        this.tvColunaValor = findViewById(R.id.premiacaoTextView);
    }

    public LinearLayout getTopoModalidadeResultadoLinearLayout() {
        return topoModalidadeResultadoLinearLayout;
    }

    public LinearLayout getFundoBackgroundTrapezeResultadoLinearLayout() {
        return fundoBackgroundTrapezeResultadoLinearLayout;
    }

    public LinearLayout getTopoResultadoPremioLinearLayout() {
        return topoResultadoPremioLinearLayout;
    }

    public LinearLayout getPremioInfoLinearLayout() {
        return premioInfoLinearLayout;
    }

    public TextView getTituloModalidadeTextView() {
        return tituloModalidadeTextView;
    }

    public TextView getValorPremioTituloTextView() { return valorPremioTituloTextView; }

    public TextView getNSBTextView() { return nsbBilhete; }

    public TextView getPodeComemorar() { return idPodeComemorar; }

    public TextView getValorEstimativaConcursoAtualTextView() { return valorEstimativaConcursoAtualTextView; }

    public TextView getProximoSorteioTextView() {
        return proximoSorteioTextView;
    }

    public Button getBotaoAposteAgora(){ return botaoAposteAgora; }

    public ImageView getImgTrevoResultado() {
        return imgTrevoResultado;
    }

//    public ImageView getImgTagBolao() {
//        return imgTagBolao;
//    }

    public ExpandableHeightRecyclerView getListaFaixaPremiacaoExpandableHeightRecyclerView() {
        return listaFaixaPremiacaoExpandableHeightRecyclerView;
    }

    public LinearLayout getPremioGanhoLinearLayout() {
        return premioGanhoLinearLayout;
    }
    public TipoApostaLinhaView getTipoApostaLinhaView() {
        return tipoApostaLinhaView;
    }

    public ConstraintLayout getButtonVerDetalheAposta() {
        return clVerDetalheAposta;
    }

    public void setCabecalhoBolao() {
        cabecalhoListaFaixaPremiacaoLinearLayout.setVisibility(GONE);
        cabecalhoListaFaixaPremiacaoBolao.setVisibility(VISIBLE);
    }

    public TextView getTvFaixaPremiacao() {
        return tvFaixaPremiacao;
    }

    public TextView getTvValorPremioCota() {
        return tvValorPremioCota;
    }

    public TextView getTvValorPremioLiquido() {
        return tvValorPremioLiquido;
    }

    public void aplicaCorColunasTabela(int corFonte) {
        this.tvColunaConcurso.setTextColor(corFonte);
        this.tvColunaSituacao.setTextColor(corFonte);
        this.tvColunaFaixa.setTextColor(corFonte);
        this.tvColunaQtd.setTextColor(corFonte);
        this.tvColunaSorteio.setTextColor(corFonte);
        this.tvColunaValor.setTextColor(corFonte);
    }
}
