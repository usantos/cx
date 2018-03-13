package caixa.gov.br.loterias.view.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.EstiloModalidade;

/**
 * Created by igorvilar on 09/11/17.
 */

public class AnimacoesAcessoRapido {

    private View v;
    private RelativeLayout relativePrincipal;
    private RelativeLayout relativeLayoutContentRapidao;
    private RelativeLayout relativeLayoutContentLerBilhetes;
    private RelativeLayout relativeLayoutContentFavoritas;
    private RelativeLayout relativeLayoutContentRepassados;
    private RelativeLayout relativeBlankView;
    private LinearLayout linearAtalhoRapidoLoterias;
    private Button buttonHomeBet;
    private Activity activity;
    private View trevoToolbar;
    private EstiloModalidade estilo;


    //init

    public AnimacoesAcessoRapido(View v, RelativeLayout relativePrincipal,
                                 RelativeLayout relativeLayoutContentRapidao,
                                 RelativeLayout relativeLayoutContentLerBilhetes,
                                 RelativeLayout relativeLayoutContentFavoritas,
                                 RelativeLayout relativeLayoutContentRepassados,
                                 RelativeLayout relativeBlankView,
                                 LinearLayout linearAtalhoRapidoLoterias,
                                 Button buttonHomeBet,
                                 View trevoToolbar,
                                 Activity activity, EstiloModalidade estilo) {
        this.v = v;
        this.relativePrincipal = relativePrincipal;
        this.relativeLayoutContentRapidao = relativeLayoutContentRapidao;
        this.relativeLayoutContentLerBilhetes = relativeLayoutContentLerBilhetes;
        this.relativeLayoutContentFavoritas = relativeLayoutContentFavoritas;
        this.relativeLayoutContentRepassados = relativeLayoutContentRepassados;
        this.linearAtalhoRapidoLoterias = linearAtalhoRapidoLoterias;
        this.relativeBlankView = relativeBlankView;
        this.buttonHomeBet = buttonHomeBet;
        this.trevoToolbar = trevoToolbar;
        this.activity = activity;
        this.estilo = estilo;
    }

    //public

    public void AnimacaoDescerContent(int positionBottom){
        buttonHomeBet.setEnabled(false);
        translatePosicaoConteudoAbaixar(positionBottom);
    }

    public void AnimacaoSubirContent(){
        AnimacaoFadeInRepassados();
    }

    //privates

    // Animacoes fade out

    private void AnimacaoFadeOutRapidao(){
        relativeLayoutContentRapidao.animate().alpha(1.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeOutLerBilhetes();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void AnimacaoFadeOutLerBilhetes(){
            relativeLayoutContentLerBilhetes.animate().alpha(1.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    AnimacaoFadeOutFavoritas();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
    }


    private void AnimacaoFadeOutFavoritas(){
        relativeLayoutContentFavoritas.animate().alpha(1.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeOutRepassados();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void AnimacaoFadeOutRepassados(){
        relativeLayoutContentRepassados.animate().alpha(1.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                trevoToolbar.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }



    // animacoes fade in

    private void AnimacaoFadeInRepassados(){
        relativeLayoutContentRepassados.animate().alpha(0.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeInFavoritas();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void AnimacaoFadeInFavoritas(){
        relativeLayoutContentFavoritas.animate().alpha(0.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeInLerBilhetes();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void AnimacaoFadeInLerBilhetes(){
        relativeLayoutContentLerBilhetes.animate().alpha(0.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeInRapidao();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void AnimacaoFadeInRapidao(){
        relativeLayoutContentRapidao.animate().alpha(0.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

                buttonHomeBet.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), estilo.getCorClara()));
                buttonHomeBet.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.branco));
                buttonHomeBet.setEnabled(true);
                translatePosicaoConteudoSubir();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    //mudar conteudo de posicao

    private void translatePosicaoConteudoAbaixar(int positionBottom){
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(v, "rotation", v.getRotation() + 360).setDuration(500);
        animator.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(relativeBlankView,"alpha",0.8f);
        animator2.setDuration(500); // duration 3 seconds
        animator2.start();

        ColorDrawable[] color = {new ColorDrawable(ContextCompat.getColor(activity.getApplicationContext(), estilo.getCorClara())), new ColorDrawable(ContextCompat.getColor(activity, R.color.cinzabotaoapostedesativado))};
        TransitionDrawable trans = new TransitionDrawable(color);
        buttonHomeBet.setBackground(trans);
        trans.startTransition(500); //


        relativePrincipal.animate().translationX(0).translationY(positionBottom).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimacaoFadeOutRapidao();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void translatePosicaoConteudoSubir(){
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(v, "rotation", v.getRotation() + 360).setDuration(500);
        animator.start();
        relativePrincipal.animate().translationX(0).translationY(0).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
//                relativeBlankView.setVisibility(View.GONE);

                linearAtalhoRapidoLoterias.setVisibility(View.GONE);
                trevoToolbar.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(relativeBlankView,"alpha",0.0f);
        animator2.setDuration(500); // duration 3 seconds
        animator2.start();
    }

}
