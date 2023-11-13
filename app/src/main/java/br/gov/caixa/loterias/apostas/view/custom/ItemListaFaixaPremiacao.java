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

@EViewGroup(R.layout.item_lista_faixa_premiacao)
public class ItemListaFaixaPremiacao extends LinearLayout {

    @ViewById
    protected TextView concursoTextView;
    @ViewById
    protected TextView faixaPremiacaoTextView;
    @ViewById
    protected TextView sorteioTextView;
    @ViewById
    protected TextView premiacaoTextView;
    @ViewById
    protected TextView situacaoTextView;
    @ViewById
    protected TextView quantidadePremiacaoTextView;
    @ViewById
    protected LinearLayout conteudoLinha;

    @AfterViews
    public void init() {

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
