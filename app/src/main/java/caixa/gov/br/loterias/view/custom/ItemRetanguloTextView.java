package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.MesDTOResponse;

@EViewGroup(R.layout.item_retanglulo_texto)
public class ItemRetanguloTextView extends RelativeLayout {

    @ViewById
    protected TextView descricaoText;

    private MesDTOResponse mesDTOResponse;

    public ItemRetanguloTextView(Context context) {
        super(context);
    }

    public ItemRetanguloTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemRetanguloTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(MesDTOResponse mes) {
        descricaoText.setText(mes.getNome());
        mesDTOResponse = mes;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        descricaoText.setActivated(selected);
    }

    public MesDTOResponse getMesDTOResponse() {
        return mesDTOResponse;
    }
}
