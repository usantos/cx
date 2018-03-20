package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 20/03/2018.
 * Class ItemRetirarPremio
 */

@EViewGroup(R.layout.item_retirar_premio_descricao)
public class ItemRetirarPremioDescricao extends LinearLayout {

    @ViewById
    protected ImageView imageViewItem;

    @ViewById
    protected TextView textViewItem;

    @AfterViews
    public void init(){

    }

    public ItemRetirarPremioDescricao(Context context) {
        super(context);
    }

    public ItemRetirarPremioDescricao(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemRetirarPremioDescricao(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(){

    }
}
