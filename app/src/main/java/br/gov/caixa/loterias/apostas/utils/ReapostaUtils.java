package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.view.listener.OnReapostaListener;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ReapostaUtils {

    public static void showDialogReaposta(Activity activity, ApostaDTO aposta, OnReapostaListener listener) {
        final Dialog dialogSegmented = new Dialog(activity);
        dialogSegmented.setContentView(R.layout.custom_dialog_segmented_control);

        TextView tituloDialog    = dialogSegmented.findViewById(R.id.tv_titulo);
        TextView descricaoDialog = dialogSegmented.findViewById(R.id.tv_descricao);

        SegmentedGroup sgOpcoes = dialogSegmented.findViewById(R.id.sg_opcoes);

        RadioButton opcao1 = dialogSegmented.findViewById(R.id.rb_1);
        RadioButton opcao2 = dialogSegmented.findViewById(R.id.rb_2);
        RadioButton opcao3 = dialogSegmented.findViewById(R.id.rb_3);
        Button btnOk       = dialogSegmented.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogSegmented.findViewById(R.id.btn_cancelar);

        String descricao = activity.getString(R.string.carrinho_fav_segmented_desc);
        descricao = descricao.replace("{modalidade}", aposta.getModalidade().name());
        descricaoDialog.setText(descricao);

        sgOpcoes.check(R.id.rb_1);
        opcao1.setText("Ambos");

        opcao2.setText(ModalidadeEnum.getDescricao(aposta.getModalidade()));
        opcao3.setText(ModalidadeEnum.getDescricaoEspecial(aposta.getModalidade()));
        tituloDialog.setText(R.string.label_atencao);

        btnOk.setOnClickListener(v -> {
            int opcaoSelecionada = sgOpcoes.indexOfChild(dialogSegmented.findViewById(sgOpcoes.getCheckedRadioButtonId()));
            listener.ok(opcaoSelecionada);
        });

        btnCancelar.setOnClickListener(v -> dialogSegmented.dismiss());

        dialogSegmented.show();
    }

    public static ApostaDTO temDoisConcurosAbertos(List<ApostaDTO> list) {
        int countModalidade;

        List<ParametroSimulacao> parametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
        if (list != null) {
            for (ApostaDTO aposta : list) {
                countModalidade = 0;
                for (int param = 0; param < parametros.size() - 1; param++) {
                    ModalidadeEnum modalidade = parametros.get(param).getParametroJogo().getConcurso().getModalidade();
                    if (aposta.getModalidade().equals(modalidade)) {
                        countModalidade++;
                    }
                }
                if (countModalidade >= 2) {
                    return aposta;
                }
            }
        }
        return null;
    }

    public static void chamaTransformarCarrinho(int opcaoSelecionada) {
        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
//        ApostaBO_.getInstance_(context).transformarCarrinho(idCarrinhoAtual, opcaoSelecionada, new RequestListener<CarrinhoDTOResponse>() {
//            @Override
//            public void onResponse(CarrinhoDTOResponse result) {
//                salvarCarrinhoFavoritoLocal(idCarrinhoAtual);
//                AlertDialogUtils.dismiss();
//                if (result.getRedirect() != null) {
//                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
//                }
//                if (dialogSegmented != null && dialogSegmented.isShowing()) {
//                    dialogSegmented.dismiss();
//                }
//                activity.startActivity(new Intent(activity, CarrinhoActivity.class));
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                AlertDialogUtils.dismiss();
//                RedirectNetwork.checkRedirect(error, activity);
//            }
//        });
    }


}
