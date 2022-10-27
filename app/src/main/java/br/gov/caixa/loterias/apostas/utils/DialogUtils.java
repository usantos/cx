package br.gov.caixa.loterias.apostas.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSituacaoCompraListener;

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

    public static void showDialogDebitoNaoAutorizado(Context context, String nomeBotao, OnDialogSituacaoCompraListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_compra_cancelada);

        Button dialogButtonFechar    = dialog.findViewById(R.id.dialogButtonCompraCanceladaCancelar);
        Button dialogButtonAjuda     = dialog.findViewById(R.id.dialogButtonCompraCanceladaAjuda);
        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonCompraCanceladaNovamente);
        dialogButtonOk.setText(nomeBotao);

        dialogButtonFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

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

}
