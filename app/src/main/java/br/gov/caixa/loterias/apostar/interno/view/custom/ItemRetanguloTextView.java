package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroMesDeSorte;

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
        descricaoText.setActivated(mes.isSelecionado());
        if(mes.isSelecionado()){
            animateView();
        }
    }

    public ParametroMesDeSorte getMesDeSorte() {
        return mesDeSorte;
    }

    private void animateView(){
        setAlpha(0.0f);
        animate().alpha(1.0f).setDuration(200).start();
    }
}
