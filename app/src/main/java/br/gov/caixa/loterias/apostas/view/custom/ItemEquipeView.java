package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 28/12/2017.
 * Class ItemEquipeView
 */
public class ItemEquipeView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView itemImage;

    private TextView itemTitle, itemDescription;

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.itemImage = findViewById(R.id.item_image);
        this.itemTitle = findViewById(R.id.item_title);
        this.itemDescription = findViewById(R.id.item_description);
    }

    public ItemEquipeView(Context context) {
        super(context);
    }

    public ItemEquipeView(Context context, @Nullable AttributeSet attrs) {
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

}
