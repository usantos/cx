package br.gov.caixa.loterias.apostar.view.custom;

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

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostar.view.DetalheDuvidaActivity_;

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

    private ListSecaoDTO listSecaoDTO;

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

    public void setLayout(ListSecaoDTO secaoDTO) {
        this.listSecaoDTO = secaoDTO;
        this.textItemDuvida.setTag(secaoDTO.getId());
        this.textItemDuvida.setText(secaoDTO.getNome());
    }

    public LinearLayout getItemDuvida() {
        return itemDuvida;
    }

    @Click(R.id.itemDuvida)
    public void clickItemDuvida() {
        DetalheDuvidaActivity_.intent(getContext()).secaoDTO(listSecaoDTO).start();
    }
}
