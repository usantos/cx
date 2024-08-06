package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by joafilho on 16/03/2018.
 * Class ItemListaFaixaPremiacao
 */

//@EViewGroup(R.layout.item_lista_faixa_premiacao)
public class ItemListaFaixaPremiacao extends LinearLayout {

    private boolean alreadyInflated = false;

    private TextView concursoTextView;
    private TextView faixaPremiacaoTextView;
    private TextView sorteioTextView;
    private TextView premiacaoTextView;
    private TextView situacaoTextView;
    private TextView quantidadePremiacaoTextView;
    private LinearLayout conteudoLinha;

    public static ItemListaFaixaPremiacao build(Context context) {
        ItemListaFaixaPremiacao instance = new ItemListaFaixaPremiacao(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_lista_faixa_premiacao, this);
        }
        super.onFinishInflate();
        init();
    }

    //@AfterViews
    public void init() {
        this.concursoTextView = findViewById(R.id.concursoTextView);
        this.faixaPremiacaoTextView = findViewById(R.id.faixaPremiacaoTextView);
        this.sorteioTextView = findViewById(R.id.sorteioTextView);
        this.premiacaoTextView = findViewById(R.id.premiacaoTextView);
        this.situacaoTextView = findViewById(R.id.situacaoTextView);
        this.quantidadePremiacaoTextView = findViewById(R.id.quantidadePremiacaoTextView);
        this.conteudoLinha = findViewById(R.id.conteudoLinha);
    }

    public void setLayout(FaixaPremiadaDTO faixaPremiadaDTO, int color) {
        conteudoLinha.setBackgroundColor(color);

        concursoTextView.setText(String.format(Locale.getDefault(), "%d", faixaPremiadaDTO.getId()));
        situacaoTextView.setText(faixaPremiadaDTO.getSituacao());

        if (faixaPremiadaDTO.getNome() != null && !faixaPremiadaDTO.getNome().isEmpty()){
            faixaPremiacaoTextView.setText(faixaPremiadaDTO.getNome());
            faixaPremiacaoTextView.setTextSize(10);
        } else {
            faixaPremiacaoTextView.setText("-");
        }

        if (faixaPremiadaDTO.getQtd() >= 0){
            quantidadePremiacaoTextView.setText(String.valueOf(faixaPremiadaDTO.getQuantidadeAcertos()));
        } else {
            quantidadePremiacaoTextView.setText("-");
        }

        if (faixaPremiadaDTO.getSorteio() != null){
            sorteioTextView.setText(String.format(Locale.getDefault(), "%d", faixaPremiadaDTO.getSorteio()));
        } else {
            sorteioTextView.setText("-");
        }

        if (faixaPremiadaDTO.getValorPremioCota() != null){
            premiacaoTextView.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorPremioCota()));
        } else if (faixaPremiadaDTO.getValorLiquido() != null){
            premiacaoTextView.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorLiquido()));
        } else {
            premiacaoTextView.setText("-");
        }

    }

    public ItemListaFaixaPremiacao(Context context) {
        super(context);
    }

    public ItemListaFaixaPremiacao(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemListaFaixaPremiacao(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
