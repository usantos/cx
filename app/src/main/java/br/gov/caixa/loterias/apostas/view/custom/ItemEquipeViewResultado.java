package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by cedesbr450 on 02/04/18.
 */

@EViewGroup(R.layout.item_loteca_resultado)
public class ItemEquipeViewResultado extends LinearLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle, itemDescription;


    @ViewById
    LinearLayout linearLayoutContentItemLotecaResultado;

    @AfterViews
    protected void init() {

    }

    public ItemEquipeViewResultado(Context context) {
        super(context);
    }

    public ItemEquipeViewResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    public ImageView getItemImage() {
        return itemImage;
    }

    public TextView getItemTitle() {
        return itemTitle;
    }

    public TextView getItemDescription() {
        return itemDescription;
    }

    public LinearLayout getLinearLayoutContentItemLotecaResultado() {
        return linearLayoutContentItemLotecaResultado;
    }

    public void setLinearLayoutContentItemLotecaResultado(LinearLayout linearLayoutContentItemLotecaResultado) {
        this.linearLayoutContentItemLotecaResultado = linearLayoutContentItemLotecaResultado;
    }

}
