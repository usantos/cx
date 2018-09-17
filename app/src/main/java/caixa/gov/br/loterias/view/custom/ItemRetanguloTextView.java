package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroMesDeSorte;

@EViewGroup(R.layout.item_retanglulo_texto)
public class ItemRetanguloTextView extends RelativeLayout {

    @ViewById
    protected TextView descricaoText;

    private ParametroMesDeSorte mesDeSorte;

    public ItemRetanguloTextView(Context context) {
        super(context);
    }

    public ItemRetanguloTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemRetanguloTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(ParametroMesDeSorte mes) {
        mesDeSorte = mes;
        descricaoText.setText(mes.getNome());
//        descricaoText.setSelected(mes.isSelecionado());
        descricaoText.setActivated(mes.isSelecionado());
    }

    public ParametroMesDeSorte getMesDeSorte() {
        return mesDeSorte;
    }
}
