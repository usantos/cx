package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogInputIntTestListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSegmentedControlListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSituacaoCompraListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogUmBotaoListener;
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

    public static void showDialogDebitoNaoAutorizado(Context context, String descricao, boolean showBtnOk, String nomeBotao, boolean showButtonAjuda, OnDialogSituacaoCompraListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_compra_cancelada);

        TextView descricaoTxt           = dialog.findViewById(R.id.texto_popup);
        Button dialogButtonFechar    = dialog.findViewById(R.id.dialogButtonCompraCanceladaCancelar);
        Button dialogButtonAjuda     = dialog.findViewById(R.id.dialogButtonCompraCanceladaAjuda);
        Button dialogButtonOk        = dialog.findViewById(R.id.dialogButtonCompraCanceladaNovamente);
        dialogButtonOk.setText(nomeBotao);

        if (descricao != null && !descricao.isEmpty()){
            descricaoTxt.setText(descricao);
        }

        dialogButtonFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (!showBtnOk){
            dialogButtonOk.setVisibility(View.GONE);
        }
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

    public static void showDialog3Botoes(Context context, int layoutlitulo, String titulo, String descricao,
                                         String botao1, String botao2, String botao3, OnDialog3ButtonsListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_tres_botoes);

        ConstraintLayout layoutTitulo       = dialog.findViewById(R.id.layoutTitulo);
        TextView tituloView                 = dialog.findViewById(R.id.tituloDialogCustom);
        TextView descricaoView              = dialog.findViewById(R.id.descricaoDialog);
        Button close                        = dialog.findViewById(R.id.closePopup);
        Button buttonLeft                   = dialog.findViewById(R.id.dialogButton1);
        Button buttonCenter                 = dialog.findViewById(R.id.dialogButton2);
        Button buttonRigth                  = dialog.findViewById(R.id.dialogButton3);

        layoutTitulo.setBackground(AppCompatResources.getDrawable(context, layoutlitulo));
        tituloView.setText(titulo);
        descricaoView.setText(descricao);

        buttonLeft.setText(ViewUtils.textCaixaSTDBoldTitle(context, botao1));
        buttonCenter.setText(ViewUtils.textCaixaSTDBoldTitle(context, botao2));
        buttonRigth.setText(ViewUtils.textCaixaSTDBoldTitle(context, botao3));

        close.setOnClickListener(v -> dialog.dismiss());
        buttonLeft.setOnClickListener(v -> listener.leftButtom());
        buttonCenter.setOnClickListener(v -> listener.centerButtom());
        buttonRigth.setOnClickListener(v -> listener.rightButtom());

        dialog.show();
    }


    public static void showDialogGetIntTest(Context context, String titulo, String orientacao, OnDialogInputIntTestListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_edittext_teste_simulacao);

        TextView tituloView                 = dialog.findViewById(R.id.tituloDialogCustom);
        TextView descricaoView              = dialog.findViewById(R.id.descricaoDialog);
        EditText edtNumero                  = dialog.findViewById(R.id.edtNumero);
        Button btnOk                        = dialog.findViewById(R.id.btnOk);

        tituloView.setText(titulo);
        descricaoView.setText(orientacao);


        btnOk.setOnClickListener(v -> {
            try {
                Integer integer = Integer.valueOf(edtNumero.getText().toString());
                listener.onResult(integer.intValue() * 1000);
                dialog.dismiss();
            } catch (Exception e){
                TestVisao.toast("O número precisa ser um número interio válido!");
            }
        });

        dialog.show();
    }

    public static Dialog showDialogCustom(Context context, String titulo, String descricao,
                                          String textBotao1, String textBotao2, String textBotao3,
                                          OnDialog3ButtonsListener listener, boolean cancelable) {

        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(cancelable);
        dialog.setContentView(R.layout.custom_dialog_show);

        TextView tvTitulo    = dialog.findViewById(R.id.tituloCustom);
        tvTitulo.setText(titulo);

        TextView tvDescricao = dialog.findViewById(R.id.textCustom);
        tvDescricao.setText(descricao);

        Button button1       = dialog.findViewById(R.id.btnCustom1);
        if (textBotao1 == null) {
            button1.setVisibility(View.GONE);
        } else {
            button1.setText(textBotao1);
            button1.setOnClickListener(v -> {
                dialog.dismiss();
                listener.leftButtom();
            });
        }

        Button button2       = dialog.findViewById(R.id.btnCustom2);
        if (textBotao2 == null) {
            button2.setVisibility(View.GONE);
        } else {
            button2.setText(textBotao2);
            button2.setOnClickListener(v -> {
                dialog.dismiss();
                listener.centerButtom();
            });
        }

        Button button3 = dialog.findViewById(R.id.btnCustom3);
        if (textBotao3 == null) {
            button3.setVisibility(View.GONE);
        } else {
            button3.setText(textBotao3);
            button3.setOnClickListener(v -> {
                dialog.dismiss();
                listener.rightButtom();
            });
        }

        dialog.show();

        return dialog;
    }


    public static void showDialogDefault(Context context, int layoutlitulo, String titulo, String descricao,
                                         String botao1, String botao2, OnDialogListenerLeftRight listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_padrao);

        ConstraintLayout layoutTitulo       = dialog.findViewById(R.id.layoutTitulo);
        TextView tituloView                 = dialog.findViewById(R.id.tituloDialogCustom);
        TextView descricaoView              = dialog.findViewById(R.id.descricaoDialog);
        Button buttonLeft                   = dialog.findViewById(R.id.leftButton);
        Button buttonRigth                  = dialog.findViewById(R.id.rightButton);

        if (layoutlitulo != 0){
            layoutTitulo.setBackground(AppCompatResources.getDrawable(context, layoutlitulo));
        }
        tituloView.setText(titulo);
        descricaoView.setText(descricao);

        buttonLeft.setText(ViewUtils.textCaixaSTDBoldTitle(context, botao1));
        buttonRigth.setText(ViewUtils.textCaixaSTDBoldTitle(context, botao2));

        buttonLeft.setOnClickListener(v -> {
            dialog.dismiss();
            listener.left();
        });
        buttonRigth.setOnClickListener(v -> {
            dialog.dismiss();
            listener.right();
        });

        dialog.show();
    }


    public static void showDialog1Botao(Context contexto, String titulo, String descricao, String botao, OnDialogUmBotaoListener listener) {
        showDialogCustom(contexto,  titulo, descricao, null, null, botao, onDialogListener1Botao(listener), false);
    }

    private static OnDialog3ButtonsListener onDialogListener1Botao(OnDialogUmBotaoListener listener) {
        return new OnDialog3ButtonsListener() {
            @Override
            public void leftButtom() {
            }

            @Override
            public void centerButtom() {
            }

            @Override
            public void rightButtom() {

                listener.ok();
            }
        };
    }

    public static void modalNaoSeiCep(Activity ctx) {
        // custom dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.custom_dialog_padrao);

        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(ctx.getResources().getString(R.string.nao_sei_cep));
        TextView descricaoView= dialog.findViewById(R.id.descricaoDialog);

        descricaoView.setText(R.string.voce_sera_redirecionado_para_o_site_dos_correios);

        Button dialogButtonConfirmar = dialog.findViewById(R.id.rightButton);
        dialogButtonConfirmar.setBackground(ctx.getDrawable(R.drawable.button_rounded_verdeazul));
        dialogButtonConfirmar.setTextColor(ctx.getResources().getColor(R.color.branco));
        dialogButtonConfirmar.setText(R.string.confirmar);

        Button dialogButtonCancelar = dialog.findViewById(R.id.leftButton);
        dialogButtonCancelar.setBackground(ctx.getDrawable(R.drawable.background_border_verde));
        dialogButtonCancelar.setTextColor(ctx.getResources().getColor(R.color.verdeazul));
        dialogButtonCancelar.setText(R.string.cancelar);


        dialogButtonConfirmar.setOnClickListener(v -> {
            Utils.abreUrl(ctx, R.string.Url_Correios);
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}
