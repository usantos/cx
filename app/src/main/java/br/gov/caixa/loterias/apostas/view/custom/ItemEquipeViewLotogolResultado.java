package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;


/**
 * Created by cedesbr450 on 03/04/18.
 */

@EViewGroup(R.layout.item_lotogol_resultado)
public class ItemEquipeViewLotogolResultado extends LinearLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle;



    @ViewById
    TextView placarResultadoIremLotogolText;


    private ParametroEquipe parametroEquipe;


    @AfterViews
    protected void init() {

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
