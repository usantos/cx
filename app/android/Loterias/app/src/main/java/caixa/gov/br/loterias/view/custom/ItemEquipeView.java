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
 * Created by joafilho on 28/12/2017.
 * Class ItemEquipeView
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

    public TextView getItemTitle() {
        return itemTitle;
    }

    public TextView getItemDescription() {
        return itemDescription;
    }

}
