package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogInputIntTestListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSegmentedControlListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogSituacaoCompraListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogTresBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogUmBotaoListener;
import info.hoang8f.android.segmented.SegmentedGroup;

public class DialogUtils {

    private static AlertDialog dialogAtivo = null;

    private static boolean isDialogAtivo() {
        return dialogAtivo != null && dialogAtivo.isShowing();
    }

    private static void registrarDialogAtivo(AlertDialog dialog) {
        dialogAtivo = dialog;
        // Limpa a referência quando a dialog for fechada
        dialog.setOnDismissListener(d -> dialogAtivo = null);
    }

    private static final String tituloPadrao = "Atenção";
    private static final String btnEntendi = "Entendi";
    private static final String btnFechar = "Fechar";
    private static final String btnConfirmar = "Confirmar";
    private static final String btnCancelar = "Cancelar";
    private static final String btnSim = "Sim";
    private static final String btnNao = "Não";


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
        tvDescricao.setText(ViewUtils.textCaixaSTDBold(context, descricao));

        Button button1       = dialog.findViewById(R.id.btnCustom1);
        if (textBotao1 == null) {
            button1.setVisibility(View.GONE);
        } else {
            button1.setText(textBotao1);
            button1.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null)
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
                if (listener != null)
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
                if (listener != null)
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

    public static void modalJogoresponsavel(Activity ctx) {
        // custom dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.custom_dialog_padrao);

        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(ctx.getResources().getString(R.string.jogo_responsavel));
        TextView descricaoView= dialog.findViewById(R.id.descricaoDialog);

        descricaoView.setText(R.string.voce_sera_redirecionado_para_o_site_jogo_responsavel);

        Button dialogButtonConfirmar = dialog.findViewById(R.id.rightButton);
        dialogButtonConfirmar.setBackground(ctx.getDrawable(R.drawable.button_rounded_verdeazul));
        dialogButtonConfirmar.setTextColor(ctx.getResources().getColor(R.color.branco));
        dialogButtonConfirmar.setText(R.string.confirmar);

        Button dialogButtonCancelar = dialog.findViewById(R.id.leftButton);
        dialogButtonCancelar.setBackground(ctx.getDrawable(R.drawable.background_border_verde));
        dialogButtonCancelar.setTextColor(ctx.getResources().getColor(R.color.verdeazul));
        dialogButtonCancelar.setText(R.string.cancelar);


        dialogButtonConfirmar.setOnClickListener(v -> {
            Utils.abreUrl(ctx, R.string.url_jogo_responsavel);
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

 // PADRONIZAÇÃO DAS DIALOGS

    /*
        DIALOG PADRÃO 1 - Constrói e exibe a dialog (mais de 90% dos casos).
     */
    private static void buildAndShowDialogPadrao(Context context, String titulo, String conteudo,
                                           String labelBotaoPositivo, DialogInterface.OnClickListener onPositivoListener,
                                           String labelBotaoNegativo, DialogInterface.OnClickListener onNegativoListener,
                                           String labelBotaoNeutro, DialogInterface.OnClickListener onNeutroListener){

        if (isContextInvalido(context)) {
            return;
        }


        //Infla o layout da dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_padrao, null);

        //Inicializa o Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false); // Impede que a dialog seja fechada ao clicar fora dela

        final AlertDialog dialog = builder.create();

        //Configura os elementos da dialog
        TextView tvTitulo = dialogView.findViewById(R.id.titulo_dialog_padrao);
        TextView tvConteudo = dialogView.findViewById(R.id.conteudo_dialog_padrao);
        LinearLayout layoutTresBotoes = dialogView.findViewById(R.id.layout_tres_botoes);
        LinearLayout layoutBotoesPadrao = dialogView.findViewById(R.id.layout_botoes_padrao);

        ViewCompat.setAccessibilityHeading(tvTitulo,true);
        tvTitulo.setFocusableInTouchMode(true);
        tvTitulo.requestFocus();

        //Caso com dois botões
        Button btnPositivo = dialogView.findViewById(R.id.button_positivo);
        Button btnNegativo = dialogView.findViewById(R.id.button_negativo);

        //Caso com três botões
        Button btnTop = dialogView.findViewById(R.id.btn_top);
        Button btnCenter = dialogView.findViewById(R.id.btn_center);
        Button btnBottom = dialogView.findViewById(R.id.btn_bottom);

        boolean isTresBotoes = labelBotaoNeutro !=null && !labelBotaoNeutro.isEmpty();
        layoutBotoesPadrao.setVisibility(isTresBotoes ? View.GONE : View.VISIBLE);
        layoutTresBotoes.setVisibility(isTresBotoes ? View.VISIBLE : View.GONE);

        if(titulo != null && !titulo.isEmpty()){
            tvTitulo.setText(titulo);
            tvTitulo.setVisibility(View.VISIBLE);
        } else {
            tvTitulo.setVisibility(View.GONE);
        }

        tvConteudo.setText(conteudo != null ? conteudo : "");

        if(isTresBotoes){
            configuraBotao(btnTop, labelBotaoPositivo, onPositivoListener, dialog);
            configuraBotao(btnCenter, labelBotaoNegativo, onNegativoListener, dialog);
            configuraBotao(btnBottom, labelBotaoNeutro, onNeutroListener, dialog);
        }else{
            configuraBotao(btnPositivo, labelBotaoPositivo, onPositivoListener, dialog);
            configuraBotao(btnNegativo, labelBotaoNegativo, onNegativoListener, dialog);
        }

        dialog.setOnShowListener(d -> {
            tvTitulo.post(() -> {
                tvTitulo.performAccessibilityAction(
                        android.view.accessibility.AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
                        null
                );
            });
        });

        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            dialogAtivo = null;
            return;
        }

        if(dialog.getWindow() != null){
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }

    }

  /*
        DIALOG PADRÃO 2 - layout e comportamento distinto - Constrói e exibe a dialog com ListView para escolha de itens, caso especial (teimosinha, qtd numeros, escolha de trevos, entre outros.)
   */
    public static void showDialogListItens(Context context,String titulo, String subtitulo,
                                           List<String> list,
                                           String labelBotaoPositivo, String labelBotaoNegativo,
                                           OnDialogListener dialogListener){
        if (isContextInvalido(context)) {
            return;
        }

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog_itens);

        final ListView listViewItens = dialog.findViewById(R.id.lista_itens_dialog_itens);

        TextView       tituloDialog  = dialog.findViewById(R.id.titulo_dialog_itens);
        tituloDialog.setText(titulo);

        if (subtitulo != null && !subtitulo.isEmpty()){
            TextView txtSubtitulo = dialog.findViewById(R.id.subtitulo_dialog_itens);
            txtSubtitulo.setText(subtitulo);
            txtSubtitulo.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.item_text_popup_itens, R.id.text_list_itens, list);

        listViewItens.setAdapter(adapter);
        listViewItens.setSelector(R.drawable.select_unselect_bg_popup_itens);

        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            dialogListener.itemSelecionado(position);
        });

        Button dialogButtonConfirmar       = dialog.findViewById(R.id.button_positivo_itens);
        Button dialogButtonCancelar = dialog.findViewById(R.id.button_negativo_itens);

        //Caso seja necessário Personalizar o texto dos botões "Confirmar" e "Cancelar" em cenários específicos,
        // caso contrário, os textos padrão são "Confirmar" e "Cancelar", respectivamente.
        if(labelBotaoPositivo != null && !labelBotaoPositivo.isEmpty()) {
            dialogButtonConfirmar.setText(labelBotaoPositivo);
        }
        if(labelBotaoNegativo != null && !labelBotaoNegativo.isEmpty()) {
            dialogButtonCancelar.setText(labelBotaoNegativo);
        }

        dialogButtonConfirmar.setOnClickListener(v -> {
            dialogListener.ok();
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialogListener.cancelar();
            dialog.dismiss();
        });

        dialog.show();

        if(dialog.getWindow() != null){

            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }
    }


    /*
     DIALOG PADRÃO 3 - Dialog Favoritar
     */

    public static Dialog buildDialogFavoritar(Context context, String titulo, OnDialogFavoritarListener listener, boolean manterSurpresinhas) {

        if (isContextInvalido(context)) {
            return null;
        }

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog_favoritar);

        TextView       tituloDialog  = dialog.findViewById(R.id.titulo_dialog_favoritar);
        tituloDialog.setText(titulo);

        EditText editTextFavoritar = dialog.findViewById(R.id.edit_dialog_favoritar);

        LinearLayout layoutManterSurpresinhas = dialog.findViewById(R.id.layout_manter_surpresinhas);

        Button btnConfirmar = dialog.findViewById(R.id.btn_positivo);
        Button btnCancelar = dialog.findViewById(R.id.btn_negativo);

        if(manterSurpresinhas){
            layoutManterSurpresinhas.setVisibility(View.VISIBLE);
        } else {
            layoutManterSurpresinhas.setVisibility(View.GONE);
        }

        btnConfirmar.setOnClickListener(v -> {
            String text = editTextFavoritar.getText().toString();
            listener.Confirmar(text);
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> {
            listener.Cancelar();
            dialog.dismiss();
        });

        dialog.setOnDismissListener( dialogInterface -> {
            editTextFavoritar.setText("");
        });

        if(dialog.getWindow() != null){

            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.65); // Define a largura da dialog como 85% da largura da tela

            dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }

        return dialog;
    }


//    private void setaDialogFavoritar() {
//        dialogFavoritar = new Dialog(this);
//        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);
//
//        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
//        tituloDialog.setText("Carrinho Favorito");
//
//        etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);
//
//        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
//        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);
//
//        btnOk.setOnClickListener(v -> {
//            String text = etCarrinho.getText().toString();
//            if (text != null && !text.isEmpty()) {
//                validarCarrinhoFavorito(text);
//            }
//        });
//        dialogFavoritar.setOnDismissListener(dialogInterface -> {
//            etCarrinho.setText("");
//        });
//        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
//    }

    /*
        MÉTODO AUXILIAR - Configuração dos botões
     */
    private static void configuraBotao(Button button, String label, final DialogInterface.OnClickListener listener, AlertDialog dialog){
        if(label != null && !label.isEmpty()){
            button.setText(label);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> {
                if(listener != null){
                    listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
                dialog.dismiss();
            });
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    /*
         MÉTODO AUXILIAR: Constrói e exibe a dialog para os cenários com apenas ação em um botão.
    */
    private static void buildAndShowDialogUmBotao(Context context, String titulo, String conteudo,
                                                  String labelBotaoPositivo,
                                                  OnDialogBotaoListener listener) {
        buildAndShowDialogPadrao(context, titulo, conteudo,
                labelBotaoPositivo, listener::onButtonClick,
                null, null, null, null);
    }



    /*
        MÉTODO AUXILIAR: Constrói e exibe a dialog para os cenários com ações (positivo e negativo) em dois botões.
     */
    private static void buildAndShowDialogDoisBotoes(Context context, String titulo, String conteudo,
                                                 String labelBotaoPositivo,
                                                 String labelBotaoNegativo,
                                                 OnDialogDoisBotoesListener listener) {
        buildAndShowDialogPadrao(context, titulo, conteudo,
                labelBotaoPositivo, listener::PositiveButton,
                labelBotaoNegativo, listener::NegativeButton, null, null);
    }

    /*
        MÉTODO AUXILIAR: Constrói e exibe a dialog para os cenários com ações (positivo, negativo e neutro) nos três botões.
     */
    private static void buildAndShowDialogTresBotoes(Context context, String titulo, String conteudo,
                                                     String labelBotaoTop,
                                                     String labelBotaoCenter,
                                                     String labelBotaoBottom,
                                                     OnDialogTresBotoesListener listener) {
        buildAndShowDialogPadrao(context, titulo, conteudo,
                labelBotaoTop, listener::TopButton,
                labelBotaoCenter, listener::CenterButton,
                labelBotaoBottom, listener::BottomButton);
    }

    /*
        Prevenção de Context inválido (Activity finalizada ou destruída) para evitar crashes ao tentar exibir a dialog.
    */
    private static boolean isContextInvalido(Context context) {
        if (context == null) {
            return true;
        }

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (activity.isFinishing()) {
                return true;
            }

            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && activity.isDestroyed();
        }

        return false;
    }

    /*
    Nome dos métodos, com aplicação direta, em cada cenário abaixo segue o seguinte padrão:
    - alert +:
        - Titulo (se tiver titulo customizado, em caso contrário, o título padrão é "Atenção")
        - Confirmar/Sim (se tiver ação no botão positivo)
        - Cancelar/Nao (se tiver ação no botão negativo)
        - TresBotoes (três botões com ações)

     */

    /*
    CENÁRIO 1: Com titulo "Atenção" e um botao Entendi (Sem ação personalizada, apenas fecha a dialog)
     */
    public static void dialogEntendi(Context context, String conteudo){
        buildAndShowDialogPadrao(context, tituloPadrao, conteudo, btnEntendi, null, null, null, null, null);
    }

    /*
    CENÁRIO 2: Com titulo "Atenção" e um botao Entendi (com ação personalizada no botão "Entendi")
     */
    public static void dialogEntendiListener(Context context, String conteudo, OnDialogBotaoListener onEntendiListener){
        buildAndShowDialogUmBotao(context, tituloPadrao, conteudo, btnEntendi, onEntendiListener);
    }

    /*
    CENÁRIO 3: Com titulo customizado e um botao Entendi (com ação personalizada no botão "Entendi")
     */
    public static void dialogTituloEntendiListener(Context context, String titulo, String conteudo, OnDialogBotaoListener listener) {
         buildAndShowDialogUmBotao(context, titulo, conteudo, btnEntendi, listener);
    }

    /*
    CENÁRIO 4: Com titulo "Atenção" e um botao customizado (com ação personalizada no botão)
     */
    public static void dialogLabelUmBotaoListener(Context context, String conteudo, String labelBotao, OnDialogBotaoListener listener) {
         buildAndShowDialogUmBotao(context, tituloPadrao, conteudo, labelBotao, listener);
    }

    /*
    CENÁRIO 5: Com titulo "Atenção" e um botao "Fechar" (com ação personalizada no botão "Fechar")
     */
    public static void dialogFecharListener(Context context, String conteudo, OnDialogBotaoListener listener) {
         buildAndShowDialogUmBotao(context, tituloPadrao, conteudo, btnFechar, listener);
    }
    /*
    CENÁRIO 6: Com titulo personalizado e um botao "Entendi" (Sem ação personalizada, apenas fecha a dialog)
    */
    public static void dialogTituloEntendi(Context context, String titulo, String conteudo){
         buildAndShowDialogPadrao(context, titulo, conteudo, btnEntendi, null, null, null, null, null);
    }
    /*
    CENÁRIO 7: Com titulo "Atenção" e dois botões, um "Confirmar" e outro "Cancelar" (Ação personalizada somente no botão "Confirmar", o "Cancelar" fecha a dialog sem ação adicional):
    */
    public static void dialogConfirmar(Context context, String conteudo, OnDialogBotaoListener onPositivoListener){
         buildAndShowDialogPadrao(context, tituloPadrao, conteudo, btnConfirmar, onPositivoListener::onButtonClick, btnCancelar, null, null, null);
    }
    /*
    CENÁRIO 8: Com titulo "Atenção" e dois botões, um "Confirmar" e outro "Cancelar" (com ações personalizadas em ambos os botões):
     */
    public static void dialogConfirmarCancelar(Context context, String conteudo, OnDialogDoisBotoesListener listener) {
         buildAndShowDialogDoisBotoes(context, tituloPadrao, conteudo, btnConfirmar, btnCancelar, listener);
    }
    /*
    CENÁRIO 9: Com titulo personalizado e dois botões, um "Confirmar" e outro "Cancelar" (Ação personalizada somente no botão "Confirmar", o "Cancelar" fecha a dialog sem ação adicional):
    */
    public static void dialogTituloConfirmar(Context context, String titulo, String conteudo, OnDialogBotaoListener onPositivoListener){
         buildAndShowDialogPadrao(context, titulo, conteudo, btnConfirmar, onPositivoListener::onButtonClick, btnCancelar, null, null, null);
    }
    /*
    CENÁRIO 10: Com titulo personalizado e dois botões, um "Confirmar" e outro "Cancelar" (com ações personalizadas em ambos os botões):
     */
    public static void dialogTituloConfirmarCancelar(Context context, String titulo, String conteudo, OnDialogDoisBotoesListener listener) {
         buildAndShowDialogDoisBotoes(context, titulo, conteudo, btnConfirmar, btnCancelar, listener);
    }
    /*
    CENÁRIO 11: Com titulo "Atenção" e dois botões, um "Sim" e outro "Não" (Ação personalizada somente no botão "Sim", o "Não" fecha a dialog sem ação adicional):
    */
    public static void dialogSim(Context context, String conteudo, OnDialogBotaoListener onPositivoListener){
         buildAndShowDialogPadrao(context, tituloPadrao, conteudo, btnSim, onPositivoListener::onButtonClick, btnNao, null, null, null);
    }
    /*
    CENÁRIO 12: Com titulo "Atenção" e dois botões, um "Sim" e outro "Não" (com ações personalizadas em ambos os botões):
     */
    public static void dialogSimNao(Context context, String conteudo, OnDialogDoisBotoesListener listener) {
         buildAndShowDialogDoisBotoes(context, tituloPadrao, conteudo, btnSim, btnNao, listener);
    }
    /*
    CENÁRIO 13: Com titulo personalizado e dois botões, um "Sim" e outro "Não" (Ação personalizada somente no botão "Sim", o "Não" fecha a dialog sem ação adicional):
    */
    public static void dialogTituloSim(Context context, String titulo, String conteudo, OnDialogBotaoListener onPositivoListener){
         buildAndShowDialogPadrao(context, titulo, conteudo, btnSim, onPositivoListener::onButtonClick, btnNao, null, null, null);
    }
    /*
    CENÁRIO 14: Com titulo personalizado e dois botões, um "Sim" e outro "Não" (com ações personalizadas em ambos os botões):
     */
    public static void dialogTituloSimNao(Context context, String titulo, String conteudo, OnDialogDoisBotoesListener listener) {
         buildAndShowDialogDoisBotoes(context, titulo, conteudo, btnSim, btnNao, listener);
    }

    /*
    CENÁRIO 15: Com titulo personalizado e dois botões, com labels personalizados e ações personalizadas em ambos os botões:
    */
    public static void dialogDoisBotoesPersonalizados(Context context, String titulo, String conteudo,String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
        buildAndShowDialogDoisBotoes(context, titulo, conteudo, labelBtnPositivo, labelBtnNegativo, listener);
    }

    /*
    CENÁRIO 16: Com titulo personalizado e três botões, com labels personalizados e ações personalizadas em todos os botões:
     */
    public static void dialogTresBotoes(Context context, String titulo, String conteudo,
                                       String labelTopBtn, String labelCenterBtn, String labelBottomBtn,
                                       OnDialogTresBotoesListener listener) {
         buildAndShowDialogTresBotoes(context, titulo, conteudo,
                labelTopBtn,
                labelCenterBtn,
                labelBottomBtn, listener);
    }


    /*
      CENÁRIOS ESPECÍFICOS QUE FOGEM DO PADRÃO ---------------------------------------------------------------------------------------
    */

    /*
       Constrói e exibe a dialog com comportamento específico a cada caso.
     */
    private static Dialog buildAndShowDialogReturn(Context context, String titulo, String conteudo,
                                                   String labelBotaoPositivo, DialogInterface.OnClickListener onPositivoListener,
                                                   String labelBotaoNegativo, DialogInterface.OnClickListener onNegativoListener,
                                                   String labelBotaoNeutro, DialogInterface.OnClickListener onNeutroListener){

        if (isContextInvalido(context)) {
            return null;
        }

        if (isDialogAtivo()) return null;

        //Infla o layout da dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_padrao, null);

        //Inicializa o Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false); // Impede que a dialog seja fechada ao clicar fora dela

        final AlertDialog dialog = builder.create();

        //Configura os elementos da dialog
        TextView tvTitulo = dialogView.findViewById(R.id.titulo_dialog_padrao);
        TextView tvConteudo = dialogView.findViewById(R.id.conteudo_dialog_padrao);
        LinearLayout layoutTresBotoes = dialogView.findViewById(R.id.layout_tres_botoes);
        LinearLayout layoutBotoesPadrao = dialogView.findViewById(R.id.layout_botoes_padrao);

        ViewCompat.setAccessibilityHeading(tvTitulo,true);
        tvTitulo.setFocusableInTouchMode(true);
        tvTitulo.requestFocus();

        //Caso com dois botões
        Button btnPositivo = dialogView.findViewById(R.id.button_positivo);
        Button btnNegativo = dialogView.findViewById(R.id.button_negativo);

        //Caso com três botões
        Button btnTop = dialogView.findViewById(R.id.btn_top);
        Button btnCenter = dialogView.findViewById(R.id.btn_center);
        Button btnBottom = dialogView.findViewById(R.id.btn_bottom);

        boolean isTresBotoes = labelBotaoNeutro !=null && !labelBotaoNeutro.isEmpty();
        layoutBotoesPadrao.setVisibility(isTresBotoes ? View.GONE : View.VISIBLE);
        layoutTresBotoes.setVisibility(isTresBotoes ? View.VISIBLE : View.GONE);

        if(titulo != null && !titulo.isEmpty()){
            tvTitulo.setText(titulo);
            tvTitulo.setVisibility(View.VISIBLE);
        } else {
            tvTitulo.setVisibility(View.GONE);
        }

        tvConteudo.setText(conteudo != null ? conteudo : "");

        if(isTresBotoes){
            configuraBotao(btnTop, labelBotaoPositivo, onPositivoListener, dialog);
            configuraBotao(btnCenter, labelBotaoNegativo, onNegativoListener, dialog);
            configuraBotao(btnBottom, labelBotaoNeutro, onNeutroListener, dialog);
        }else{
            configuraBotao(btnPositivo, labelBotaoPositivo, onPositivoListener, dialog);
            configuraBotao(btnNegativo, labelBotaoNegativo, onNegativoListener, dialog);
        }

        dialog.setOnShowListener(d -> {
            tvTitulo.post(() -> {
                tvTitulo.performAccessibilityAction(
                        android.view.accessibility.AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
                        null
                );
            });
        });

        // Registra como ativa ANTES de show() para evitar race condition.
        // O OnDismissListener interno limpa dialogAtivo quando o chamador fechar via dismiss().
        // ATENÇÃO: não defina outro setOnDismissListener externamente — será sobrescrito.
        dialogAtivo = dialog;
        dialog.setOnDismissListener(d -> dialogAtivo = null);

        if(dialog.getWindow() != null){

            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }
        return dialog;
    }

    public static Dialog dialogTituloDoisBotoesReturn(Context context, String titulo, String conteudo,String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
        return buildAndShowDialogReturn(context, titulo, conteudo,
                labelBtnPositivo, listener::PositiveButton,
                labelBtnNegativo, listener::NegativeButton, null, null);
    }

    public static Dialog dialogRateUtils(Context context, String titulo, String conteudo,String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
        return buildAndShowDialogReturn(context, titulo, conteudo,
                labelBtnPositivo, listener::PositiveButton,
                labelBtnNegativo, listener::NegativeButton, null, null);
    }
}
