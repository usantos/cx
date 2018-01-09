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

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.view.adapter.NumerosLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

/**
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.item_lotogol)
public class ItemEquipeViewLotogol extends LinearLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle;

    @ViewById
    ExpandableHeightGridView gridViewPlacarTresColunas, gridViewPlacarDuasColunas;

    private List<String> placarListUm;
    private List<String> placarListDois;

    private static final String VALORES_PLACAR_LINHA_UM = "0_1_2";
    private static final String VALORES_PLACAR_LINHA_DOIS = "3_+";

    @AfterViews
    protected void init() {
        placarListUm = new ArrayList<>();
        placarListDois = new ArrayList<>();
        criarListaPlacar();
    }

    public ItemEquipeViewLotogol(Context context) {
        super(context);
    }

    public ItemEquipeViewLotogol(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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


    private void criarListaPlacar() {
        String[] arrayValor = VALORES_PLACAR_LINHA_UM.split("_");
        for (int i = 0; i < arrayValor.length; i++) {
            placarListUm.add(arrayValor[i]);
        }

        gridViewPlacarTresColunas.setAdapter(new NumerosLotogolAdapter(getContext(), placarListUm));
        gridViewPlacarTresColunas.setExpanded(true);

        arrayValor = VALORES_PLACAR_LINHA_DOIS.split("_");
        for (int i = 0; i < arrayValor.length; i++) {
            placarListDois.add(arrayValor[i]);
        }

        gridViewPlacarDuasColunas.setAdapter(new NumerosLotogolAdapter(getContext(), placarListDois));
        gridViewPlacarDuasColunas.setExpanded(true);
    }
}
