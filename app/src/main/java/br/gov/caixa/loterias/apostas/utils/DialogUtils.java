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

    public static void showDialogCompraCancelada(Context context, OnDialogListener dialogListener){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_compra_cancelada);

        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCompraCanceladaCancelar);
        Button dialogButtonAjuda    = dialog.findViewById(R.id.dialogButtonCompraCanceladaAjuda);
        Button dialogButtonNovamente= dialog.findViewById(R.id.dialogButtonCompraCanceladaNovamente);

        dialogButtonCancelar.setOnClickListener(v -> {
            //dialogListener.cancelar();
            dialog.dismiss();
        });

        dialogButtonAjuda.setOnClickListener(v -> {
            //dialogListener.cancelar();
            dialog.dismiss();
        });

        dialogButtonNovamente.setOnClickListener(v -> {
            //dialogListener.ok();
            dialog.dismiss();
        });


        dialog.show();
    }

}
