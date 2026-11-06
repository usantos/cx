package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;


/**
 * Created by cedesbr450 on 03/04/18.
 */

public class ItemEquipeViewLotogolResultado extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView itemImage;
    private TextView itemTitle;
    private TextView placarResultadoIremLotogolText;

    private ParametroEquipe parametroEquipe;

    public static ItemEquipeViewLotogolResultado build(Context context) {
        ItemEquipeViewLotogolResultado instance = new ItemEquipeViewLotogolResultado(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_lotogol_resultado, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.itemImage = findViewById(R.id.item_image);
        this.itemTitle = findViewById(R.id.item_title);
        this.placarResultadoIremLotogolText = findViewById(R.id.placarResultadoIremLotogolText);
    }

    public ItemEquipeViewLotogolResultado(Context context) {
        super(context);
    }

    public ItemEquipeViewLotogolResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView getItemImage() {
        return itemImage;
    }

    public TextView getItemTitle() {
        return itemTitle;
    }

    public void setParametroEquipe(ParametroEquipe parametroEquipe) {
        this.parametroEquipe = parametroEquipe;
    }



    public TextView getPlacarResultadoIremLotogolText() {
        return placarResultadoIremLotogolText;
    }

    public void setPlacarResultadoIremLotogolText(TextView placarResultadoIremLotogolText) {
        this.placarResultadoIremLotogolText = placarResultadoIremLotogolText;
    }




}
