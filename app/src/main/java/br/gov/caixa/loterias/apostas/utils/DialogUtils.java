package br.gov.caixa.loterias.apostas.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
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
                                              AdapterView.OnItemClickListener itemClickListener,
                                              OnDialogListener dialogListener){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);

        TextView       tituloDialog  = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(titulo);

        TextView txtSubtitulo = dialog.findViewById(R.id.subtituloDialog);
        txtSubtitulo.setText(subtitulo);
        txtSubtitulo.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                                          R.layout.item_text_popup, R.id.textList, list);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener(itemClickListener);

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

}
