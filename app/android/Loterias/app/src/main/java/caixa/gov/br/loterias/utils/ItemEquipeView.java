package caixa.gov.br.loterias.utils;

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
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.item_loteca)
public class ItemEquipeView extends LinearLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle, itemDescription;

    @AfterViews
    protected void init() {

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

    public void setItemImage(ImageView itemImage) {
        this.itemImage = itemImage;
    }

    public TextView getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(TextView itemTitle) {
        this.itemTitle = itemTitle;
    }

    public TextView getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(TextView itemDescription) {
        this.itemDescription = itemDescription;
    }
}
