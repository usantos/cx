package br.gov.caixa.loterias.apostas.view.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalheDuvidaActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;

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
        //DetalheDuvidaActivity_.intent(getContext()).secaoDTO(listSecaoDTO).start();

        Intent intent = IntentUtil.getIntentOrigemDestino((Activity) getContext(), DetalheDuvidaActivity.class);
        intent.putExtra(DetalheDuvidaActivity.SECAO_DTO_EXTRA, ((Serializable) listSecaoDTO));
        ((Activity) getContext()).startActivity(intent);

//        Intent intent = new Intent(getContext(), DetalheDuvidaActivity.class);
//        intent.putExtra(DetalheDuvidaActivity.SECAO_DTO_EXTRA, ((Serializable) listSecaoDTO));
//        getContext().startActivity(intent);
    }
}
