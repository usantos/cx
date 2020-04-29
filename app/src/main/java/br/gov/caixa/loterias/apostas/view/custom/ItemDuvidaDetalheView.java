package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoHeight;

/**
 * Created by joafilho on 16/02/2018.
 * Class ItemDividaDetalheView
 */
@EViewGroup(R.layout.item_duvida_detalhe)
public class ItemDuvidaDetalheView extends LinearLayout {

    @ViewById
    LinearLayout layoutCabecalhoDuvidaDetalhe, layoutItemDetalheDuvida, layoutTextDetalhe;

    @ViewById
    TextView textDuvidaDetalhe, textDetalhe;

    @ViewById
    ImageView imageDuvidaDetalhe;

    public ItemDuvidaDetalheView(Context context) {
        super(context);
    }

    public ItemDuvidaDetalheView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemDuvidaDetalheView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void init() {
    }

    public LinearLayout getLayoutItemDetalheDuvida() {
        return layoutItemDetalheDuvida;
    }

    public void setLayout(DuvidaDTO duvidaDTO) {
        textDuvidaDetalhe.setText(duvidaDTO.getPergunta());
        textDetalhe.setText(duvidaDTO.getResposta());
    }

    @Click(R.id.layoutCabecalhoDuvidaDetalhe)
    public void openCloseDetalhe() {
        if (layoutCabecalhoDuvidaDetalhe.isSelected()) {
            layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.FALSE);
            textDuvidaDetalhe.setTypeface(ViewUtils.getFontArialMT(getContext()));
            textDuvidaDetalhe.setTextSize(14);
            textDuvidaDetalhe.setTextColor(getContext().getResources().getColor(R.color.cinza));
            AnimacaoHeight.collapse(layoutTextDetalhe);

        } else {
            layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.TRUE);
            textDuvidaDetalhe.setTypeface(ViewUtils.getFontFutura(getContext()));
            textDuvidaDetalhe.setTextSize(16);
            textDuvidaDetalhe.setTextColor(getContext().getResources().getColor(R.color.verdeazul));
            AnimacaoHeight.expand(layoutTextDetalhe);
        }
    }
}
