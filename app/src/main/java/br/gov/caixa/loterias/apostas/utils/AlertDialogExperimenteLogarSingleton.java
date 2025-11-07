package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AutoSuspensaoActivity;
import br.gov.caixa.loterias.apostas.controllers.AutoavaliacaoFormActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.ConfiguracaoRapidaoActivity;
import br.gov.caixa.loterias.apostas.controllers.DadosPessoaisActivity;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaRapidaoActivity;
import br.gov.caixa.loterias.apostas.controllers.MeusCartoesActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;

public class AlertDialogExperimenteLogarSingleton {
    private static Dialog alertDialog;

    public static void show(Activity activity, boolean isRedirectHome, Bundle bolao) {
        try{
            if (isShow()){
                dismissDialog();
            }

            alertDialog = DialogUtils.showDialogCustom(activity,
                    activity.getString(R.string.label_atencao),
                    activity.getString(R.string.confirmacao_continuar_experimente),
                    activity.getString(R.string.continuar_experimente),
                    activity.getString(R.string.efetuar_login),
                    null, new OnDialog3ButtonsListener() {
                @Override
                public void leftButtom() {
                    if (isRedirectHome && activity instanceof MinhasApostasActivity
                            || activity instanceof ListaComprasActivity
                            || activity instanceof ConfiguracaoRapidaoActivity
                            || activity instanceof ListaRapidaoActivity
                            || activity instanceof FavoritasActivity
                            || activity instanceof CarrinhosFavoritosActivity
                            || activity instanceof MeusCartoesActivity
                            || activity instanceof DadosPessoaisActivity
                            || activity instanceof AutoavaliacaoFormActivity
                            || activity instanceof AutoSuspensaoActivity) {
                        dismissDialog();
                        activity.finish();
                    }
                }

                @Override
                public void centerButtom() {
                    dismissDialog();
                    RedirectNetwork.connectKeycloak(activity, bolao);
                }

                @Override
                public void rightButtom() {}

                        }, true);
        }catch (Exception e){}
    }

    private static boolean isShow(){
        if(alertDialog != null){
            return alertDialog.isShowing();
        }
        return false;
    }

    public static void dismissDialog() {
        if (isShow()) {
            alertDialog.dismiss();
        }
        alertDialog = null;
    }
}
