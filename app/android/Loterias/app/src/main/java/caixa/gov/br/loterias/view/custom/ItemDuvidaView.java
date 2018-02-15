package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 15/02/2018.
 * Class ItemDuvidaView
 */

@EViewGroup(R.layout.item_duvida)
public class ItemDuvidaView extends LinearLayout {

    @ViewById
    TextView textItemDuvida;

    @ViewById
    ImageView imageItemDuvida;

    @ViewById
    LinearLayout itemDuvida;

    @AfterViews
    protected void init() {

    }

    public ItemDuvidaView(Context context) {
        super(context);
    }

    public ItemDuvidaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemDuvidaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(String titulo) {
        this.textItemDuvida.setText(titulo);
    }

    public LinearLayout getItemDuvida() {
        return itemDuvida;
    }
}
