package caixa.gov.br.loterias.view.custom;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;

/**
 * Created by cedesbr450 on 04/04/18.
 */

@EViewGroup(R.layout.item_partida_view_detalhes_resultado)
public class PartidaViewDetalhesResultado extends LinearLayout {

    @ViewById
    protected Button voltarButton;

    @AfterViews
    protected void init() {

    }

    public void setLayout( ) {

    }

    public PartidaViewDetalhesResultado(Context context) {
        super(context);
    }

    public PartidaViewDetalhesResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewDetalhesResultado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void mostrarDetalhes(boolean mostrarDetalhes, LinearLayout item_resultado_content_view, PartidaViewDetalhesResultado partidaViewDetalhesResultadoView){
        if (mostrarDetalhes == true){
            animacaoFadeOutResultado(item_resultado_content_view, partidaViewDetalhesResultadoView);
        }else {
            animacaoFadeOutDetalhes(partidaViewDetalhesResultadoView, item_resultado_content_view);
        }
    }

    public Button getVoltarButton() {
        return voltarButton;
    }

    public void setVoltarButton(Button voltarButton) {
        this.voltarButton = voltarButton;
    }

    private void animacaoFadeInDetalhes(View view){
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }




    private void animacaoFadeOutDetalhes(final View view, final View view2){
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInResultado(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void animacaoFadeInResultado(View view){
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }




    private void animacaoFadeOutResultado(final View view, final View view2){
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInDetalhes(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

}