package br.gov.caixa.loterias.apostas.utils.helper;

import android.app.Activity;
import android.app.Dialog;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.BaseBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public final class FavoritarLotericaHelper {

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

        Dialog dialog = ViewUtils.alertTitleNewPositiveNegativeListener(
                activity,
                activity.getString(R.string.label_atencao),
                activity.getString(R.string.cancelar),
                activity.getString(R.string.confirmar),
                mensagem,
                (d, which) -> {
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
                                    ViewUtils.alertEntendiCustom(activity, er.getMensagem());
                                } else {
                                    ViewUtils.alertTitleButton(
                                            activity,
                                            R.string.label_atencao,
                                            activity.getString(R.string.nao_concluiu_operacao),
                                            activity.getString(R.string.ok),
                                            dialogInterface -> {}
                                    );
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ViewUtils.alertTitleButton(
                                        activity,
                                        R.string.label_atencao,
                                        activity.getString(R.string.nao_concluiu_operacao),
                                        activity.getString(R.string.ok),
                                        dialogInterface -> {}
                                );
                            }
                        }
                    };

                    if (estadoAtual) {
                        acoes.excluir(lotericaId, listener);
                    } else {
                        acoes.incluir(lotericaId, listener);
                    }
                },
                (d, which) -> d.dismiss()
        );
        dialog.show();
    }
}
