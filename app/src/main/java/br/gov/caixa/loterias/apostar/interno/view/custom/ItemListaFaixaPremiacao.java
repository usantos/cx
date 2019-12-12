package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by joafilho on 16/03/2018.
 * Class ItemListaFaixaPremiacao
 */

@EViewGroup(R.layout.item_lista_faixa_premiacao)
public class ItemListaFaixaPremiacao extends LinearLayout {

    @ViewById
    protected TextView concursoTextView, faixaPremiacaoTextView, sorteioTextView, premiacaoTextView;

    @AfterViews
    public void init() {

    }

    public void setLayout(FaixaPremiadaDTO faixaPremiadaDTO) {

        concursoTextView.setText(String.format(Locale.getDefault(), "%d", faixaPremiadaDTO.getId()));

        faixaPremiacaoTextView.setText(faixaPremiadaDTO.getNome());

        sorteioTextView.setText(String.format(Locale.getDefault(), "%d", faixaPremiadaDTO.getSorteio()));

        premiacaoTextView.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorLiquido()));
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
