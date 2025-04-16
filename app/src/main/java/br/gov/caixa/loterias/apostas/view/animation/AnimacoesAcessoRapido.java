package br.gov.caixa.loterias.apostas.view.animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnAnimacaoViewListener;
import br.gov.caixa.loterias.apostas.utils.AnimacaoUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

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
    private RelativeLayout relativeLayoutContentRapidao;
    private RelativeLayout relativeLayoutContentLerBilhetes;
    private RelativeLayout relativeLayoutJogoResponsavel;
    private RelativeLayout relativeLayoutContentRepassados;
    private RelativeLayout relativeLayoutMsgDisque180;
    private RelativeLayout relativeBlankView;
    private LinearLayout linearAtalhoRapidoLoterias;
    private Button buttonHomeBet;
    private Activity activity;
    private View trevoToolbar;
    private OnAnimacaoViewListener listener;

    private EstiloModalidadeMKP estilo;

    //init
    public AnimacoesAcessoRapido(View v, RelativeLayout relativePrincipal,
                                 RelativeLayout relativeLayoutContentRapidao,
                                 RelativeLayout relativeLayoutContentLerBilhetes,
                                 RelativeLayout rlJogoResponsavel,
                                 RelativeLayout relativeLayoutContentRepassados,
                                 RelativeLayout rlMsgDisque180,
                                 RelativeLayout relativeBlankView,
                                 LinearLayout linearAtalhoRapidoLoterias,
                                 Button buttonHomeBet,
                                 View trevoToolbar,
                                 Activity activity, EstiloModalidadeMKP estilo, OnAnimacaoViewListener listener) {
        this.v = v;
        this.relativePrincipal = relativePrincipal;
        this.relativeLayoutContentRapidao = relativeLayoutContentRapidao;
        this.relativeLayoutContentLerBilhetes = relativeLayoutContentLerBilhetes;
        this.relativeLayoutJogoResponsavel = rlJogoResponsavel;
        this.relativeLayoutContentRepassados = relativeLayoutContentRepassados;
        this.relativeLayoutMsgDisque180 = rlMsgDisque180;
        this.linearAtalhoRapidoLoterias = linearAtalhoRapidoLoterias;
        this.relativeBlankView = relativeBlankView;
        this.buttonHomeBet = buttonHomeBet;
        this.trevoToolbar = trevoToolbar;
        this.activity = activity;
        this.estilo = estilo;
        this.listener = listener;
    }

    //public

    public void animacaoDescerContent(int positionBottom){
        buttonHomeBet.setEnabled(false);
        translatePosicaoConteudoAbaixar(positionBottom);
    }

    public void animacaoSubirContent(){
        animacaoFadeInMsgDisque180();
    }

    //privates
    // Animacoes fade out
    private void animacaoFadeOutRapidao(){
        AnimacaoUtils.fadeOut(relativeLayoutContentRapidao, DURACAO_CURTA, () -> {
            animacaoFadeOutLerBilhetes();
        });
    }

    private void animacaoFadeOutLerBilhetes(){
        AnimacaoUtils.fadeOut(relativeLayoutContentLerBilhetes, DURACAO_CURTA, () -> {
            animacaoFadeOutJogoResponsavel();
        });
    }

    private void animacaoFadeOutJogoResponsavel(){
        AnimacaoUtils.fadeOut(relativeLayoutJogoResponsavel, DURACAO_CURTA, () -> {
            animacaoFadeOutRepassados();
        });
    }

    private void animacaoFadeOutRepassados(){
        AnimacaoUtils.fadeOut(relativeLayoutContentRepassados, DURACAO_CURTA, () -> {
            animacaoFadeOutMsgDisque180();
        });
    }

    private void animacaoFadeOutMsgDisque180(){
        AnimacaoUtils.fadeOut(relativeLayoutMsgDisque180, DURACAO_CURTA, () -> {
            trevoToolbar.setEnabled(true);
        });
    }

    // animacoes fade in
    private void animacaoFadeInRepassados(){
        AnimacaoUtils.fadeIn(relativeLayoutContentRepassados, DURACAO_CURTA, () -> {
            animacaoFadeInJr();
        });
    }

    private void animacaoFadeInJr(){
        AnimacaoUtils.fadeIn(relativeLayoutJogoResponsavel, DURACAO_CURTA, () -> {
            animacaoFadeInLerBilhetes();
        });
    }

    private void animacaoFadeInLerBilhetes(){
        AnimacaoUtils.fadeIn(relativeLayoutContentLerBilhetes, DURACAO_CURTA, () -> {
            animacaoFadeInRapidao();
        });
    }

    private void animacaoFadeInRapidao(){
        AnimacaoUtils.fadeIn(relativeLayoutContentRapidao, DURACAO_CURTA, () -> {
            buttonHomeBet.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), estilo.getCorClara()));
            buttonHomeBet.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.branco));
            buttonHomeBet.setEnabled(true);
            translatePosicaoConteudoSubir();
        });
    }
    
    private void animacaoFadeInMsgDisque180(){
        AnimacaoUtils.fadeIn(relativeLayoutMsgDisque180, DURACAO_CURTA, () -> {
            animacaoFadeInRepassados();
        });
    }

    //mudar conteudo de posicao
    private void translatePosicaoConteudoAbaixar(int positionBottom){
        startAnimator(v,"rotation", v.getRotation() + 360);
        startAnimator(relativeBlankView, "alpha", 0.8f);

        AnimacaoUtils.fadeOut(relativePrincipal, TRANSLATION_X, positionBottom, DURACAO_LONGA, () -> {
            animacaoFadeOutRapidao();
        });
    }

    private void translatePosicaoConteudoSubir(){
        startAnimator(v,"rotation", v.getRotation() - 360);

        AnimacaoUtils.fadeIn(relativePrincipal, TRANSLATION_X, TRANSLATION_Y, DURACAO_LONGA, () -> {
//                relativeBlankView.setVisibility(View.GONE);

            linearAtalhoRapidoLoterias.setVisibility(View.GONE);
            trevoToolbar.setEnabled(true);
            listener.animacaoFinalizada();            
        });

        startAnimator(relativeBlankView, "alpha", 0.0f);
    }

    private void startAnimator(View view, String properyBane, float ofFloat) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, properyBane, ofFloat).setDuration(DURACAO_LONGA);
        animator.start();
    }

}
