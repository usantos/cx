package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by cedesbr450 on 02/04/18.
 */

public class ItemEquipeViewResultado extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView itemImage;
    private TextView itemTitle, itemDescription;
    private LinearLayout linearLayoutContentItemLotecaResultado;

    public static ItemEquipeViewResultado build(Context context) {
        ItemEquipeViewResultado instance = new ItemEquipeViewResultado(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_loteca_resultado, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.itemImage = findViewById(R.id.item_image);
        this.itemTitle = findViewById(R.id.item_title);
        this.itemDescription = findViewById(R.id.item_description);
        this.linearLayoutContentItemLotecaResultado = findViewById(R.id.linearLayoutContentItemLotecaResultado);
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
