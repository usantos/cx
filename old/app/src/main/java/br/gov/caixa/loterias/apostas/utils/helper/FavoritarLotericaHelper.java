package br.gov.caixa.loterias.apostas.utils.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.BaseBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public final class FavoritarLotericaHelper {

    private static final String TAG = "FavoritarLotericaHelper";

    private FavoritarLotericaHelper() {}

    public interface Acoes {
        void incluir(Long lotericaId, OnSilceListener<RetornoPadraoResponse> listener);
        void excluir(Long lotericaId, OnSilceListener<RetornoPadraoResponse> listener);
    }

    public interface EstadoUi {
        void atualizar(boolean isFavorita);
    }

    public static void confirmar(
            Activity activity,
            Long lotericaId,
            String nomeFantasia,
            boolean estadoAtual,
            Acoes acoes,
            EstadoUi estadoUi
    ) {
        String mensagem = estadoAtual
                ? activity.getString(R.string.msg_excluir_favorita, nomeFantasia)
                : activity.getString(R.string.msg_incluir_favorita, nomeFantasia);

        DialogUtils.dialogConfirmar(activity,
                mensagem,
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        AlertDialogUtils.show(activity);
                        OnSilceListener<RetornoPadraoResponse> listener = new OnSilceListener<RetornoPadraoResponse>() {
                            @Override
                            public void success(RetornoPadraoResponse payload) {
                                AlertDialogUtils.dismiss();
                                // Atualiza UI somente após sucesso
                                boolean novoEstado = !estadoAtual;
                                estadoUi.atualizar(novoEstado);

                            }

                            @Override
                            public void error(VolleyError error) {
                                AlertDialogUtils.dismiss();
                                try {
                                    ErrorResponse er = BaseBO.transformaErroEmErrorResponse(error);
                                    if ("002024".equals(er.getCodigo())) {
                                        DialogUtils.dialogEntendi(activity, er.getMensagem());
                                    } else {
                                        DialogUtils.dialogEntendi(activity,
                                                activity.getString(R.string.nao_concluiu_operacao));
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Erro ao processar resposta de favoritar", e);
                                    DialogUtils.dialogEntendi(
                                            activity,
                                            activity.getString(R.string.nao_concluiu_operacao));

                                }
                            }
                        };

                        if (estadoAtual) {
                            acoes.excluir(lotericaId, listener);
                        } else {
                            acoes.incluir(lotericaId, listener);
                        }

                    }
                }
        );

    }
}
