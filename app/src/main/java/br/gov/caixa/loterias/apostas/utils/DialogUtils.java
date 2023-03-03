package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSegmentedControlListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSituacaoCompraListener;
import info.hoang8f.android.segmented.SegmentedGroup;

public class DialogUtils {

    public static void showDialogListaEscolha(Context context, String titulo, String subtitulo,
                                              List<String> list,
                                              OnDialogListener dialogListener){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);

        TextView       tituloDialog  = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(titulo);

        if (subtitulo != null && !subtitulo.isEmpty()){
            TextView txtSubtitulo = dialog.findViewById(R.id.subtituloDialog);
            txtSubtitulo.setText(subtitulo);
            txtSubtitulo.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                                          R.layout.item_text_popup, R.id.textList, list);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            dialogListener.itemSelecionado(position);
        });

        Button dialogButtonOk       = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        dialogButtonOk.setOnClickListener(v -> {
            dialogListener.ok();
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialogListener.cancelar();
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void showDialogDebitoNaoAutorizado(Context context, String nomeBotao, boolean showButtonAjuda, OnDialogSituacaoCompraListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_compra_cancelada);

        Button dialogButtonFechar    = dialog.findViewById(R.id.dialogButtonCompraCanceladaCancelar);
        Button dialogButtonAjuda     = dialog.findViewById(R.id.dialogButtonCompraCanceladaAjuda);
        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonCompraCanceladaNovamente);
        dialogButtonOk.setText(nomeBotao);

        dialogButtonFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (!showButtonAjuda){
            dialogButtonAjuda.setVisibility(View.GONE);
        }
        dialogButtonAjuda.setOnClickListener(v -> {
            dialog.dismiss();
            listener.ajuda();
        });

        dialogButtonOk.setOnClickListener(v -> {
            dialog.dismiss();
            listener.ok();
        });

        dialog.show();
    }

    public static void showDialogCarrinhoVazio(Context context, String nomeBotao) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_carrinho_vazio);

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonCarrinhoVazio);
        dialogButtonOk.setText(nomeBotao);

        dialogButtonOk.setOnClickListener(v -> {
            dialog.dismiss();
            //            listener.ok();
        });

        dialog.show();
    }

    public static Dialog showDialogSegmentedControl(Activity act, String modalidadeNormal, String modalidadeEspecial, OnDialogSegmentedControlListener listener) {
        Dialog dialogSegmented = new Dialog(act);
        dialogSegmented.setContentView(R.layout.custom_dialog_segmented_control);

        TextView tituloDialog    = dialogSegmented.findViewById(R.id.tv_titulo);
        TextView descricaoDialog = dialogSegmented.findViewById(R.id.tv_descricao);

        SegmentedGroup sgOpcoes = dialogSegmented.findViewById(R.id.sg_opcoes);

        RadioButton opcao1 = dialogSegmented.findViewById(R.id.rb_1);
        RadioButton opcao2 = dialogSegmented.findViewById(R.id.rb_2);
        RadioButton opcao3      = dialogSegmented.findViewById(R.id.rb_3);
        Button      btnOk       = dialogSegmented.findViewById(R.id.btn_ok);
        Button      btnCancelar = dialogSegmented.findViewById(R.id.btn_cancelar);

        String descricao = act.getString(R.string.carrinho_fav_segmented_desc);
        descricao = descricao.replace("{modalidade}", modalidadeNormal);
        descricaoDialog.setText(descricao);

        sgOpcoes.check(R.id.rb_1);
        opcao1.setText("Ambos");

        opcao2.setText(modalidadeNormal);
        opcao3.setText(modalidadeEspecial);
        tituloDialog.setText("Carrinho Favorito");

        btnOk.setOnClickListener(v -> {
            int opcaoSelecionada = sgOpcoes.indexOfChild(dialogSegmented.findViewById(sgOpcoes.getCheckedRadioButtonId()));
            listener.ok(opcaoSelecionada);
        });

        btnCancelar.setOnClickListener(v -> dialogSegmented.dismiss());
        dialogSegmented.show();
        return dialogSegmented;
    }


    public static void showDialogLeftRight(Context context, String titulo, String subtitulo,
                                           String legendaBtnLeft, String legendaBtnRight,
                                           OnDialogListenerLeftRight listenerLeftRight) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_nao_sim);

        dialog.setCanceledOnTouchOutside(false);

        TextView txtTitulo = dialog.findViewById(R.id.titulo_left_right);
        if (titulo == null) {
            txtTitulo.setVisibility(View.GONE);
        } else {
            txtTitulo.setVisibility(View.VISIBLE);
            txtTitulo.setText(titulo);
        }

        TextView txtSubtitulo = dialog.findViewById(R.id.mensagem_left_right);
        if (subtitulo == null) {
            txtSubtitulo.setVisibility(View.GONE);
        } else {
            txtSubtitulo.setVisibility(View.VISIBLE);
            txtSubtitulo.setText(subtitulo);
        }

        Button btnLeft  = dialog.findViewById(R.id.btn_left);
        Button btnRight = dialog.findViewById(R.id.btn_right);

        if (legendaBtnLeft != null) {
            btnLeft.setText(legendaBtnLeft);
        }

        if (legendaBtnRight != null) {
            btnRight.setText(legendaBtnRight);
        }

        btnLeft.setOnClickListener(v -> {
            listenerLeftRight.left();
            dialog.dismiss();
        });

        btnRight.setOnClickListener(v -> {
            listenerLeftRight.right();
            dialog.dismiss();
        });

        dialog.show();
    }

}
