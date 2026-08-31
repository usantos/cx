package br.gov.caixa.loterias.apostas.view.animation;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnAnimacaoViewListener;
import br.gov.caixa.loterias.apostas.utils.AnimacaoUtils;

/**
 * Created by igorvilar on 09/11/17.
 */

public class AnimacoesAcessoRapido {
    private static final int DURACAO_CURTA = 200;
    private static final int DURACAO_LONGA = 500;
    private static final int TRANSLATION_X = 0;
    private static final int TRANSLATION_Y = 0;

    private View v;
    private RelativeLayout relativePrincipal;
    private List<View> animationViewList;
    private RelativeLayout relativeBlankView;
    private View containerView;
    private Button buttonHomeBet;
    private View trevoToolbar;
    private boolean isAberto;

    //init
    public AnimacoesAcessoRapido(View v, RelativeLayout relativePrincipal,
                                 List<View> viewList,
                                 RelativeLayout relativeBlankView,
                                 View containerView,
                                 Button buttonHomeBet,
                                 View trevoToolbar) {
        this.v = v;
        this.relativePrincipal = relativePrincipal;
        this.animationViewList = viewList;
        this.containerView = containerView;
        this.relativeBlankView = relativeBlankView;
        this.buttonHomeBet = buttonHomeBet;
        this.trevoToolbar = trevoToolbar;
        this.isAberto = false;
    }

    //public

    public void animacaoDescerContent(int positionBottom){
        buttonHomeBet.setEnabled(false);
        translatePosicaoConteudoAbaixar(positionBottom);
    }

    public void animacaoSubirContent(OnAnimacaoViewListener listener) {
        nextAnimacaoFadeIn(animationViewList.size() - 1, listener);
    }

    //mudar conteudo de posicao
    private void translatePosicaoConteudoAbaixar(int positionBottom){
        startAnimator(v,"rotation", v.getRotation() + 360);
        startAnimator(relativeBlankView, "alpha", 0.8f);

        AnimacaoUtils.fadeOut(relativePrincipal, TRANSLATION_X, positionBottom, DURACAO_LONGA, () -> nextAnimacaoFadeOut(0)); //animacaoFadeOutRapidao());
    }

    private void nextAnimacaoFadeOut(int position) {
        if (animationViewList != null && !animationViewList.isEmpty()){
            AnimacaoUtils.fadeOut(animationViewList.get(position), DURACAO_CURTA, () -> {
                if (position < animationViewList.size() - 1){
                    nextAnimacaoFadeOut(position + 1);
                } else {
                    trevoToolbar.setEnabled(true);
                }
            });
        }
    }

    private void nextAnimacaoFadeIn(int position, OnAnimacaoViewListener listener) {
        if (animationViewList != null && !animationViewList.isEmpty()){
            AnimacaoUtils.fadeIn(animationViewList.get(position),DURACAO_CURTA, () -> {
                if (position > 0){
                    nextAnimacaoFadeIn(position - 1, listener);
                } else {
                    buttonHomeBet.setEnabled(true);
                    translatePosicaoConteudoSubir(listener);
                }
            });
        }
    }

    private void translatePosicaoConteudoSubir(OnAnimacaoViewListener listener){
        startAnimator(v,"rotation", v.getRotation() - 360);

        AnimacaoUtils.fadeIn(relativePrincipal, TRANSLATION_X, TRANSLATION_Y, DURACAO_LONGA, () -> {
            containerView.setVisibility(View.GONE);
            trevoToolbar.setEnabled(true);
            if (listener != null ){
                listener.animacaoFinalizada();
            }
        });

        startAnimator(relativeBlankView, "alpha", 0.0f);
    }

    private void startAnimator(View view, String properyBane, float ofFloat) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, properyBane, ofFloat).setDuration(DURACAO_LONGA);
        animator.start();
    }

    public void setAberto(boolean isAberto) {
        this.isAberto = isAberto;
    }

    public boolean isAberto(){
        return this.isAberto;
    }

}
