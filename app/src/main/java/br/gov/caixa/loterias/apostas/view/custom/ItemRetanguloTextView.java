package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;

public class ItemRetanguloTextView extends RelativeLayout {

    private boolean alreadyInflated = false;

    private TextView descricaoText;

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

    public static ItemRetanguloTextView build(Context context) {
        ItemRetanguloTextView instance = new ItemRetanguloTextView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_retanglulo_texto, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.descricaoText = findViewById(R.id.descricaoText);
    }

    public void setLayout(ParametroMesDeSorte mes) {
        mesDeSorte = mes;
        descricaoText.setText(mes.getNome());
        descricaoText.setActivated(mes.isSelecionado());
        descricaoText.setTypeface(
                ResourcesCompat.getFont(
                        getContext(),
                        mes.isSelecionado()
                                ? R.font.caixa_std_semi_bold
                                : R.font.caixa_std_regular
                )
        );
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
