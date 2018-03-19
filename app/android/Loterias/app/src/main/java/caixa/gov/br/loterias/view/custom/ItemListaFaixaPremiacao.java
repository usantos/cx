package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.mock.ItemListaFaixaPremiacaoMock;

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

    public void setLayout(ItemListaFaixaPremiacaoMock itemListaFaixaPremiacaoMock) {
        concursoTextView.setText(itemListaFaixaPremiacaoMock.getConcurso());
        faixaPremiacaoTextView.setText(itemListaFaixaPremiacaoMock.getFaixaPremiacao());
        sorteioTextView.setText(itemListaFaixaPremiacaoMock.getSorteio());
        premiacaoTextView.setText(itemListaFaixaPremiacaoMock.getPremiacao());
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
