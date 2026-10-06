package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import br.gov.caixa.loterias.apostas.R;

public class BotaoFavoritar extends AppCompatImageView {

    private boolean isSelected = false;
    private OnFavoritarStateChangeListener listener;

    public BotaoFavoritar(@NonNull Context context) {
        super(context);
        init();
    }

    public BotaoFavoritar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BotaoFavoritar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    private void init(){

        setImageResource(R.drawable.select_favoritar);

        setScaleType(ScaleType.FIT_CENTER);

        setOnClickListener(v -> alteraEstadoFavoritar());

    }

    private void alteraEstadoFavoritar(){

        if(listener != null){
            listener.onFavoritarStateChange(isSelected);
        }
    }

    public void setState(boolean isSelected){
        this.isSelected = isSelected;
        super.setSelected(isSelected);
    }

    public boolean getState(){
        return isSelected;
    }


    public interface OnFavoritarStateChangeListener{
        void onFavoritarStateChange(boolean isSelected);
    }

    public void setOnStateChangeListener(OnFavoritarStateChangeListener listener){
        this.listener = listener;
    }

}
