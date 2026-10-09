package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AutoSuspensaoActivity;
import br.gov.caixa.loterias.apostas.controllers.AutoavaliacaoFormActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.ConfiguracaoRapidaoActivity;
import br.gov.caixa.loterias.apostas.novo.features.dadospessoais.DadosPessoaisActivity;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaRapidaoActivity;
import br.gov.caixa.loterias.apostas.controllers.MeusCartoesActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

public class AlertDialogExperimenteLogarSingleton {
    private static Dialog alertDialog;

    public static void show(Activity activity, boolean isRedirectHome, Bundle bolao) {
        try{
            if (isShow()){
                dismissDialog();
            }

            alertDialog = DialogUtils.dialogTituloDoisBotoesReturn(activity,
                    "Atenção",
                    activity.getString(R.string.confirmacao_continuar_experimente),
                    "Sim",
                    "Não",
                    new OnDialogDoisBotoesListener() {

                        @Override
                        public void PositiveButton(DialogInterface dialog, int which) {
                            dismissDialog();
                            RedirectNetwork.connectKeycloak(activity, bolao);
                        }

                        @Override
                        public void NegativeButton(DialogInterface dialog, int which) {
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
                    }
            );

            if (!activity.isFinishing() && !activity.isDestroyed()) {
                alertDialog.show();
            }

        }catch (Exception e){}
    }

    public static void showSessaoExpirada(Activity activity, Runnable onEntendi) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        dismissDialog();
        alertDialog = DialogUtils.dialogEntendiReturn(activity,
                activity.getString(R.string.sessao_expirada),
                (dialog, which) -> {
                    dismissDialog();
                    onEntendi.run();
                });
        if (alertDialog != null) {
            alertDialog.show();
        }
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
