package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListStringAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogTresBotoesListener;

public class DialogUtils {

    private static final String TITULO_PADRAO = "Atenção";
    private static final String BTN_ENTENDI = "Entendi";
    private static final String BTN_FECHAR = "Fechar";
    private static final String BTN_CONFIRMAR = "Confirmar";
    private static final String BTN_CANCELAR = "Cancelar";
    private static final String BTN_SIM = "Sim";
    private static final String BTN_NAO = "Não";
    private static final float LARGURA_DIALOG_PADRAO = 0.85f; // Define a largura da dialog como 85% da largura da tela

 // PADRONIZAÇÃO DAS DIALOGS

    /*
        DIALOG PADRÃO 1 - Maioria dos casos. Cria e abre a Dialog.
     */
    private static void buildAndShowDialogPadrao(Context context, String titulo, String conteudo,
                                                 String labelBotaoPositivo, DialogInterface.OnClickListener onPositivoListener,
                                                 String labelBotaoNegativo, DialogInterface.OnClickListener onNegativoListener,
                                                 String labelBotaoNeutro, DialogInterface.OnClickListener onNeutroListener) {

        if (isContextInvalido(context)) {
            return;
        }

        //Infla o layout da dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_padrao, null);

        //Inicializa o Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogUtilsTheme);
        builder.setView(dialogView);
        builder.setCancelable(false); // Impede que a dialog seja fechada ao clicar fora dela

        final AlertDialog dialog = builder.create();

        //Configura os elementos da dialog
        TextView tvTitulo = dialogView.findViewById(R.id.titulo_dialog_padrao);
        TextView tvConteudo = dialogView.findViewById(R.id.conteudo_dialog_padrao);
        View dividerDialog = dialogView.findViewById(R.id.divider_dialog_padrao);

        LinearLayout layoutTresBotoes = dialogView.findViewById(R.id.layout_tres_botoes);
        LinearLayout layoutBotoesPadrao = dialogView.findViewById(R.id.layout_botoes_padrao);

        ViewCompat.setAccessibilityHeading(tvTitulo, true);
        tvTitulo.setFocusableInTouchMode(true);
        tvTitulo.requestFocus();

        //Caso com dois botões
        Button btnPositivo = dialogView.findViewById(R.id.button_positivo);
        Button btnNegativo = dialogView.findViewById(R.id.button_negativo);

        //Caso com três botões
        Button btnTop = dialogView.findViewById(R.id.btn_top);
        Button btnCenter = dialogView.findViewById(R.id.btn_center);
        Button btnBottom = dialogView.findViewById(R.id.btn_bottom);

        boolean isTresBotoes = labelBotaoNeutro != null && !labelBotaoNeutro.trim().isEmpty();

        layoutBotoesPadrao.setVisibility(isTresBotoes ? View.GONE : View.VISIBLE);
        layoutTresBotoes.setVisibility(isTresBotoes ? View.VISIBLE : View.GONE);

        boolean temTitulo = titulo != null && !titulo.trim().isEmpty();
        if(temTitulo){
            tvTitulo.setText(titulo);
            tvTitulo.setVisibility(View.VISIBLE);
        } else {
            tvTitulo.setVisibility(View.GONE);
        }

        tvConteudo.setText(conteudo != null ? conteudo : "");
        dividerDialog.setVisibility(temTitulo ? View.VISIBLE : View.GONE);

        if (isTresBotoes) {
            configuraBotao(btnTop, labelBotaoPositivo, DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnCenter, labelBotaoNegativo, DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
            configuraBotao(btnBottom, labelBotaoNeutro, DialogInterface.BUTTON_NEUTRAL, onNeutroListener, dialog);
        } else {
            configuraBotao(btnPositivo, labelBotaoPositivo, DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnNegativo, labelBotaoNegativo, DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
        }

        dialog.setOnShowListener(d -> {
            if (tvTitulo.getVisibility() == View.VISIBLE) {
                tvTitulo.post(() -> tvTitulo.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }
        });

        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            return;
        }

    }

    /*
      DIALOG PADRÃO  2 - Cria a Dialog mas não executa o show(). É retornada a instância da Dialog para controle maior em cada cenário.
     */
    private static Dialog buildDialogReturn(Context context, String titulo, String conteudo,
                                            String labelBotaoPositivo, DialogInterface.OnClickListener onPositivoListener,
                                            String labelBotaoNegativo, DialogInterface.OnClickListener onNegativoListener,
                                            String labelBotaoNeutro, DialogInterface.OnClickListener onNeutroListener) {

        if (isContextInvalido(context)) {
            return null;
        }


        //Infla o layout da dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_padrao, null);

        //Inicializa o Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogUtilsTheme);
        builder.setView(dialogView);
        builder.setCancelable(false); // Impede que a dialog seja fechada ao clicar fora dela

        final AlertDialog dialog = builder.create();

        //Configura os elementos da dialog
        TextView tvTitulo = dialogView.findViewById(R.id.titulo_dialog_padrao);
        TextView tvConteudo = dialogView.findViewById(R.id.conteudo_dialog_padrao);
        View dividerDialog = dialogView.findViewById(R.id.divider_dialog_padrao);
        LinearLayout layoutTresBotoes = dialogView.findViewById(R.id.layout_tres_botoes);
        LinearLayout layoutBotoesPadrao = dialogView.findViewById(R.id.layout_botoes_padrao);

        ViewCompat.setAccessibilityHeading(tvTitulo, true);
        tvTitulo.setFocusableInTouchMode(true);
        tvTitulo.requestFocus();

        //Caso com dois botões
        Button btnPositivo = dialogView.findViewById(R.id.button_positivo);
        Button btnNegativo = dialogView.findViewById(R.id.button_negativo);

        //Caso com três botões
        Button btnTop = dialogView.findViewById(R.id.btn_top);
        Button btnCenter = dialogView.findViewById(R.id.btn_center);
        Button btnBottom = dialogView.findViewById(R.id.btn_bottom);

        boolean isTresBotoes = labelBotaoNeutro != null && !labelBotaoNeutro.isEmpty();
        layoutBotoesPadrao.setVisibility(isTresBotoes ? View.GONE : View.VISIBLE);
        layoutTresBotoes.setVisibility(isTresBotoes ? View.VISIBLE : View.GONE);

        boolean temTitulo = titulo != null && !titulo.trim().isEmpty();
        if(temTitulo){
            tvTitulo.setText(titulo);
            tvTitulo.setVisibility(View.VISIBLE);
        } else {
            tvTitulo.setVisibility(View.GONE);
        }

        tvConteudo.setText(conteudo != null ? conteudo : "");
        dividerDialog.setVisibility(temTitulo ? View.VISIBLE : View.GONE);

        if (isTresBotoes) {
            configuraBotao(btnTop, labelBotaoPositivo, DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnCenter, labelBotaoNegativo, DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
            configuraBotao(btnBottom, labelBotaoNeutro, DialogInterface.BUTTON_NEUTRAL, onNeutroListener, dialog);
        } else {
            configuraBotao(btnPositivo, labelBotaoPositivo, DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnNegativo, labelBotaoNegativo, DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
        }

        dialog.setOnShowListener(d -> tvTitulo.post(() -> tvTitulo.sendAccessibilityEvent(
                AccessibilityEvent.TYPE_VIEW_FOCUSED
        )));

        return dialog;
    }

    /*
          DIALOG PADRÃO 3 - Constrói e exibe a dialog com ListView para escolha de itens, caso especial (teimosinha, qtd numeros, escolha de trevos, entre outros.)
     */
    public static void showDialogListItens(Context context, String titulo, String subtitulo,
                                           List<String> list,
                                           String labelBotaoPositivo, String labelBotaoNegativo,
                                           OnDialogListener dialogListener) {
        if (isContextInvalido(context)) {
            return;
        }

        if (dialogListener == null) {
            Log.w("DialogUtils", "showDialogListItens chamado com dialogListener nulo.");
            return;
        }

        final Dialog dialog = new Dialog(context, R.style.DialogUtilsTheme);
        dialog.setContentView(R.layout.layout_dialog_itens);
        dialog.setCancelable(false);

        final ListView listViewItens = dialog.findViewById(R.id.lista_itens_dialog_itens);

        TextView       tituloDialog  = dialog.findViewById(R.id.titulo_dialog_itens);
        View dividerDialog = dialog.findViewById(R.id.divider_dialog_itens);
        tituloDialog.setText(titulo);

        if (subtitulo != null && !subtitulo.isEmpty()) {
            TextView txtSubtitulo = dialog.findViewById(R.id.subtitulo_dialog_itens);
            txtSubtitulo.setText(subtitulo);
            txtSubtitulo.setVisibility(View.VISIBLE);
        }

        ViewCompat.setAccessibilityHeading(tituloDialog, true);
        tituloDialog.setFocusableInTouchMode(true);
        tituloDialog.requestFocus();

        if (list == null) {
            list = Collections.emptyList();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.item_text_popup_itens, R.id.text_list_itens, list);

        listViewItens.setAdapter(adapter);
        listViewItens.setSelector(R.drawable.select_unselect_bg_popup_itens);

        final int[] lastSelected = {-1};
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            lastSelected[0] = position;
            dialogListener.itemSelecionado(position);
        });

        Button dialogButtonConfirmar = dialog.findViewById(R.id.button_positivo_itens);
        Button dialogButtonCancelar = dialog.findViewById(R.id.button_negativo_itens);

        //Caso seja necessário Personalizar o texto dos botões "Confirmar" e "Cancelar" em cenários específicos,
        // caso contrário, os textos padrão são "Confirmar" e "Cancelar", respectivamente.
        if (labelBotaoPositivo != null && !labelBotaoPositivo.isEmpty()) {
            dialogButtonConfirmar.setText(labelBotaoPositivo);
        }
        if (labelBotaoNegativo != null && !labelBotaoNegativo.isEmpty()) {
            dialogButtonCancelar.setText(labelBotaoNegativo);
        }

        dividerDialog.setVisibility(
                titulo != null && !titulo.trim().isEmpty() ? View.VISIBLE : View.GONE
        );

        dialog.setOnShowListener(dialogInterface -> {
            if (tituloDialog.getVisibility() == View.VISIBLE) {
                tituloDialog.post(() -> tituloDialog.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }
        });

        dialogButtonConfirmar.setOnClickListener(v -> {
            dialogListener.ok(lastSelected[0]);
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialogListener.cancelar();
            dialog.dismiss();
        });

        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            Log.w("DialogUtils", "Não foi possível exibir a dialog.", e);
            return;
        }


    }

    /*
     DIALOG PADRÃO 4 - Dialog Favoritar (Ainda não utilizada, trata-se de proposta de Dialog para a funcionalidade de favoritar carrinhos, unica dialog
     existente no app que ainda não está centralizada neste arquivo. Demanda precisa de análise para implementação)

     */

    public static Dialog buildDialogFavoritar(
            Context context,
            String titulo,
            String hintInput,
            String description,
            OnDialogFavoritarListener listener,
            boolean manterSurpresinhas
    )
    {
        if (isContextInvalido(context)) {
            return null;
        }

        final Dialog dialog = new Dialog(context, R.style.DialogUtilsTheme);
        dialog.setContentView(R.layout.layout_dialog_favoritar);

        dialog.setCancelable(false);

        TextView tituloDialog = dialog.findViewById(R.id.titulo_dialog_favoritar);
        tituloDialog.setText(titulo);

        TextView descriptionDialog = dialog.findViewById(R.id.description_dialog_favoritar);
        descriptionDialog.setText(description);

        EditText editTextFavoritar = dialog.findViewById(R.id.edit_dialog_favoritar);

        LinearLayout layoutManterSurpresinhas = dialog.findViewById(R.id.layout_manter_surpresinhas);
        CheckBox checkManterSurpresinhas = dialog.findViewById(R.id.check_manter_surpresinhas);

        Button btnConfirmar = dialog.findViewById(R.id.btn_positivo);
        Button btnCancelar = dialog.findViewById(R.id.btn_negativo);
        if (!TextUtils.isEmpty(hintInput)) {
            editTextFavoritar.setHint(hintInput);
        }

        layoutManterSurpresinhas.setVisibility(manterSurpresinhas ? View.VISIBLE : View.GONE);

        ViewCompat.setAccessibilityHeading(tituloDialog, true);
        tituloDialog.setFocusableInTouchMode(true);
        tituloDialog.requestFocus();

        btnConfirmar.setOnClickListener(v -> {
            String text = editTextFavoritar.getText().toString();
            boolean isChecked = checkManterSurpresinhas.isChecked();

            if (listener != null) {
                listener.Confirmar(text, isChecked);
            }
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> {
            if (listener != null) {
                listener.Cancelar();
            }
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            editTextFavoritar.setText("");
            checkManterSurpresinhas.setChecked(false);
        });

        layoutManterSurpresinhas.setOnClickListener(v ->
                checkManterSurpresinhas.toggle()
        );

        dialog.setOnShowListener(dialogInterface -> {
            if (tituloDialog.getVisibility() == View.VISIBLE) {
                tituloDialog.post(() -> tituloDialog.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }

        });

        return dialog;
    }


    /*
        MÉTODO AUXILIAR - Configuração dos botões
     */
    private static void configuraBotao(Button button, String label, int whichButton, final DialogInterface.OnClickListener listener, AlertDialog dialog) {

        if (button == null) {
            return;
        }

        if (label != null && !label.trim().isEmpty()) {
            button.setText(label);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(dialog, whichButton);
                }
                dialog.dismiss();
            });
        } else {
            button.setVisibility(View.INVISIBLE);
        }
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


    public static void dialogLegendaLoteca(Context context) {
        if (isContextInvalido(context)) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_legenda_loteca, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogUtilsTheme);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();

        Button btnFechar = view.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(v -> dialog.dismiss());
        TextView tvTitulo = view.findViewById(R.id.legenda);
        ViewCompat.setAccessibilityHeading(tvTitulo, true);
        tvTitulo.setFocusableInTouchMode(true);
        tvTitulo.requestFocus();

        dialog.setOnShowListener(d -> {
            if (tvTitulo.getVisibility() == View.VISIBLE) {
                tvTitulo.post(() -> tvTitulo.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }

        });

        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            return;
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

    /* ---------------------------------------------------------------------------------
    MÉTODOS PÚBLICOS - CENÁRIOS DE USO DAS DIALOGS

    Os métodos públicos seguem uma nomenclatura descritiva baseada no tipo de dialog
    e nas ações disponíveis.

    Padrão de nomenclatura:
    dialog + características da dialog

    Exemplos:
    - dialogEntendi:
      título padrão "Atenção" e botão "Entendi".

    - dialogTituloEntendi:
      título personalizado e botão "Entendi".

    - dialogConfirmar:
      título padrão, botão "Confirmar" com ação e botão "Cancelar" apenas para fechar.

    - dialogSimNao:
      título padrão, botões "Sim" e "Não" com ações personalizadas.

    - dialogDoisBotoesPersonalizados:
      título personalizado, dois botões com labels e ações personalizadas.

    - dialogTresBotoes:
      título personalizado, três botões com labels e ações personalizadas.
 */

    /*
    CENÁRIO 1: Com titulo "Atenção" e um botao Entendi (Sem ação personalizada, apenas fecha a dialog)
     */
    public static void dialogEntendi(Context context, String conteudo) {
        buildAndShowDialogPadrao(context, TITULO_PADRAO, conteudo, BTN_ENTENDI, null, null, null, null, null);
    }

    /*
    CENÁRIO 2: Com titulo "Atenção" e um botao Entendi (com ação personalizada no botão "Entendi")
     */
    public static void dialogEntendiListener(Context context, String conteudo, OnDialogBotaoListener onEntendiListener) {
        buildAndShowDialogUmBotao(context, TITULO_PADRAO, conteudo, BTN_ENTENDI, onEntendiListener);
    }

    /*
    CENÁRIO 3: Com titulo customizado e um botao Entendi (com ação personalizada no botão "Entendi")
     */
    public static void dialogTituloEntendiListener(Context context, String titulo, String conteudo, OnDialogBotaoListener listener) {
        buildAndShowDialogUmBotao(context, titulo, conteudo, BTN_ENTENDI, listener);
    }

    /*
    CENÁRIO 4: Com titulo "Atenção" e um botao customizado (com ação personalizada no botão)
     */
    public static void dialogLabelUmBotaoListener(Context context, String conteudo, String labelBotao, OnDialogBotaoListener listener) {
        buildAndShowDialogUmBotao(context, TITULO_PADRAO, conteudo, labelBotao, listener);
    }

    /*
    CENÁRIO 5: Com titulo "Atenção" e um botao "Fechar" (com ação personalizada no botão "Fechar")
     */
    public static void dialogFecharListener(Context context, String conteudo, OnDialogBotaoListener listener) {
        buildAndShowDialogUmBotao(context, TITULO_PADRAO, conteudo, BTN_FECHAR, listener);
    }

    /*
    CENÁRIO 6: Com titulo personalizado e um botao "Entendi" (Sem ação personalizada, apenas fecha a dialog)
    */
    public static void dialogTituloEntendi(Context context, String titulo, String conteudo) {
        buildAndShowDialogPadrao(context, titulo, conteudo, BTN_ENTENDI, null, null, null, null, null);
    }

    /*
    CENÁRIO 7: Com titulo "Atenção" e dois botões, um "Confirmar" e outro "Cancelar" (Ação personalizada somente no botão "Confirmar", o "Cancelar" fecha a dialog sem ação adicional):
    */
    public static void dialogConfirmar(Context context, String conteudo, OnDialogBotaoListener onPositivoListener) {
        buildAndShowDialogPadrao(context, TITULO_PADRAO, conteudo, BTN_CONFIRMAR, onPositivoListener::onButtonClick, BTN_CANCELAR, null, null, null);
    }

    /*
    CENÁRIO 8: Com titulo "Atenção" e dois botões, um "Confirmar" e outro "Cancelar" (com ações personalizadas em ambos os botões):
     */
    public static void dialogConfirmarCancelar(Context context, String conteudo, OnDialogDoisBotoesListener listener) {
        buildAndShowDialogDoisBotoes(context, TITULO_PADRAO, conteudo, BTN_CONFIRMAR, BTN_CANCELAR, listener);
    }

    /*
    CENÁRIO 9: Com titulo personalizado e dois botões, um "Confirmar" e outro "Cancelar" (Ação personalizada somente no botão "Confirmar", o "Cancelar" fecha a dialog sem ação adicional):
    */
    public static void dialogTituloConfirmar(Context context, String titulo, String conteudo, OnDialogBotaoListener onPositivoListener) {
        buildAndShowDialogPadrao(context, titulo, conteudo, BTN_CONFIRMAR, onPositivoListener::onButtonClick, BTN_CANCELAR, null, null, null);
    }

    /*
    CENÁRIO 10: Com titulo personalizado e dois botões, um "Confirmar" e outro "Cancelar" (com ações personalizadas em ambos os botões):
     */
    public static void dialogTituloConfirmarCancelar(Context context, String titulo, String conteudo, OnDialogDoisBotoesListener listener) {
        buildAndShowDialogDoisBotoes(context, titulo, conteudo, BTN_CONFIRMAR, BTN_CANCELAR, listener);
    }

    /*
    CENÁRIO 11: Com titulo "Atenção" e dois botões, um "Sim" e outro "Não" (Ação personalizada somente no botão "Sim", o "Não" fecha a dialog sem ação adicional):
    */
    public static void dialogSim(Context context, String conteudo, OnDialogBotaoListener onPositivoListener) {
        buildAndShowDialogPadrao(context, TITULO_PADRAO, conteudo, BTN_SIM, onPositivoListener::onButtonClick, BTN_NAO, null, null, null);
    }

    /*
    CENÁRIO 12: Com titulo "Atenção" e dois botões, um "Sim" e outro "Não" (com ações personalizadas em ambos os botões):
     */
    public static void dialogSimNao(Context context, String conteudo, OnDialogDoisBotoesListener listener) {
        buildAndShowDialogDoisBotoes(context, TITULO_PADRAO, conteudo, BTN_SIM, BTN_NAO, listener);
    }

    /*
    CENÁRIO 13: Com titulo personalizado e dois botões, um "Sim" e outro "Não" (Ação personalizada somente no botão "Sim", o "Não" fecha a dialog sem ação adicional):
    */
    public static void dialogTituloSim(Context context, String titulo, String conteudo, OnDialogBotaoListener onPositivoListener) {
        buildAndShowDialogPadrao(context, titulo, conteudo, BTN_SIM, onPositivoListener::onButtonClick, BTN_NAO, null, null, null);
    }

    /*
    CENÁRIO 14: Com titulo personalizado e dois botões, um "Sim" e outro "Não" (com ações personalizadas em ambos os botões):
     */
    public static void dialogTituloSimNao(Context context, String titulo, String conteudo, OnDialogDoisBotoesListener listener) {
        buildAndShowDialogDoisBotoes(context, titulo, conteudo, BTN_SIM, BTN_NAO, listener);
    }

    /*
    CENÁRIO 15: Com titulo personalizado e dois botões, com labels personalizados e ações personalizadas em ambos os botões:
    */
    public static void dialogDoisBotoesPersonalizados(Context context, String titulo, String conteudo, String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
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


    //Cenários específicos, onde precisa retornar a Dialog para controle posterior (ex: AlertDialogExperimenteLogarSingleton e RateUtils).

    public static Dialog dialogTituloDoisBotoesReturn(Context context, String titulo, String conteudo, String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
        return buildDialogReturn(context, titulo, conteudo,
                labelBtnPositivo, listener::PositiveButton,
                labelBtnNegativo, listener::NegativeButton, null, null);
    }
    public static Dialog dialogEntendiReturn(Context context, String conteudo, OnDialogBotaoListener listener) {
        return buildDialogReturn(context, TITULO_PADRAO, conteudo, BTN_ENTENDI,
                listener::onButtonClick, null, null, null, null);
    }

    public static Dialog dialogTituloConfirmarReturn(Context context, String titulo, String conteudo, OnDialogBotaoListener onPositivoListener){
        return buildDialogReturn(context, titulo, conteudo, BTN_CONFIRMAR, onPositivoListener::onButtonClick, BTN_CANCELAR, null, null, null);
    }


    public static void dialogHtml(Context context, String titulo, String conteudo){
        buildAndShowDialogHtml(context, titulo, conteudo, BTN_ENTENDI, null, null, null, null, null);
    }

    public static Dialog dialogRateUtils(Context context, String titulo, String conteudo, String labelBtnPositivo, String labelBtnNegativo, OnDialogDoisBotoesListener listener) {
        return buildDialogReturn(context, titulo, conteudo,
                labelBtnPositivo, listener::PositiveButton,
                labelBtnNegativo, listener::NegativeButton, null, null);
    }

    public static void showDialogListItensNovo(
            Context context,
            String titulo,
            String subtitulo,
            String texto,
            List<String> list,
            String labelBotaoPositivo,
            String labelBotaoNegativo,
            OnDialogListener dialogListener
    ) {
        Dialog dialog = buildDialogListItensNovo(context, titulo, subtitulo, texto, list, labelBotaoPositivo, labelBotaoNegativo, dialogListener);
        if (dialog == null) {
            return;
        }
        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            Log.w("DialogUtils", "Não foi possível exibir a dialog.", e);
        }
    }

    public interface OnCustomDialogClickListener {
        void onClick(Dialog dialog);
    }

    public static Dialog buildCustomDialog(
            Context context,
            int layoutResId,
            boolean cancelable,
            int buttonId,
            OnCustomDialogClickListener listener
    ) {

        if (isContextInvalido(context)) {
            return null;
        }

        Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutResId);
        dialog.setCancelable(cancelable);

        View button = dialog.findViewById(buttonId);

        if (button != null) {
            button.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(dialog);
                }
            });
        }

        if (dialog.getWindow() != null) {
            int screenWidth = context.getResources()
                    .getDisplayMetrics()
                    .widthPixels;

            int dialogWidth =
                    (int) (screenWidth * LARGURA_DIALOG_PADRAO);

            dialog.getWindow().setLayout(
                    dialogWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            dialog.getWindow().setBackgroundDrawableResource(
                    R.drawable.rounded_dialog_background
            );
        }

        return dialog;
    }

    public static Dialog buildDialogListItensNovo(
            Context context,
            String titulo,
            String subtitulo,
            String texto,
            List<String> list,
            String labelBotaoPositivo,
            String labelBotaoNegativo,
            OnDialogListener dialogListener
    ) {
        if (isContextInvalido(context)) {
            return null;
        }

        if (dialogListener == null) {
            Log.w("DialogUtils", "showDialogListItens chamado com dialogListener nulo.");
            return null;
        }

        final Dialog dialog = new Dialog(context, R.style.DialogUtilsTheme);
        dialog.setContentView(R.layout.layout_dialog_itens_novo);
        dialog.setCancelable(false);

        TextView tituloDialog = dialog.findViewById(R.id.titulo_dialog_itens);
        tituloDialog.setText(titulo);

        TextView textoDialog = dialog.findViewById(R.id.texto_dialog_itens);

        if (texto != null && !texto.isEmpty()) {
            textoDialog.setText(texto);
            textoDialog.setVisibility(View.VISIBLE);
        } else {
            textoDialog.setVisibility(View.GONE);
        }

        if (subtitulo != null && !subtitulo.isEmpty()) {
            TextView txtSubtitulo = dialog.findViewById(R.id.subtitulo_dialog_itens);
            txtSubtitulo.setText(subtitulo);
            txtSubtitulo.setVisibility(View.VISIBLE);
        }

        ViewCompat.setAccessibilityHeading(tituloDialog, true);
        tituloDialog.setFocusableInTouchMode(true);
        tituloDialog.requestFocus();

        if (list == null) {
            list = Collections.emptyList();
        }

        RecyclerView picker = dialog.findViewById(R.id.picker_itens_dialog);

        if (picker == null) {
            return null;
        }
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        picker.setLayoutManager(layoutManager);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(picker);

        Button dialogButtonConfirmar = dialog.findViewById(R.id.button_positivo_itens);
        Button dialogButtonCancelar = dialog.findViewById(R.id.button_negativo_itens);

        //Caso seja necessário Personalizar o texto dos botões "Confirmar" e "Cancelar" em cenários específicos,
        // caso contrário, os textos padrão são "Confirmar" e "Cancelar", respectivamente.
        if (labelBotaoPositivo != null && !labelBotaoPositivo.isEmpty()) {
            dialogButtonConfirmar.setText(labelBotaoPositivo);
        }
        if (labelBotaoNegativo != null && !labelBotaoNegativo.isEmpty()) {
            dialogButtonCancelar.setText(labelBotaoNegativo);
        }

        ColorStateList confirmarTextColor = dialogButtonConfirmar.getTextColors();
        dialogButtonConfirmar.setEnabled(false);
        dialogButtonConfirmar.setTextColor(ContextCompat.getColor(context, R.color.cinza_button));
        dialogButtonConfirmar.setAlpha(0.6f);

        WheelController wheelController =
                new WheelController(picker, layoutManager, snapHelper, new OnDialogListener() {
                    @Override
                    public void itemSelecionado(int position) {
                        dialogButtonConfirmar.setEnabled(true);
                        dialogButtonConfirmar.setTextColor(confirmarTextColor);
                        dialogButtonConfirmar.setAlpha(1f);
                        dialogListener.itemSelecionado(position);
                    }

                    @Override
                    public void ok(int position) {
                        dialogListener.ok(position);
                    }

                    @Override
                    public void cancelar() {
                        dialogListener.cancelar();
                    }
                });

        ListStringAdapter adapter = new ListStringAdapter(list, wheelController::onItemClicked);
        wheelController.attachAdapter(adapter);
        picker.setAdapter(adapter);
        wheelController.setupRecyclerForWheel();

        dialog.setOnShowListener(dialogInterface -> {
            if (tituloDialog.getVisibility() == View.VISIBLE) {
                tituloDialog.post(() -> tituloDialog.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }
        });

        dialogButtonConfirmar.setOnClickListener(v -> {
            int selectedPosition = adapter.getSelectedPosition();
            if (selectedPosition == RecyclerView.NO_POSITION) return;
            dialogListener.ok(selectedPosition);
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            dialogListener.cancelar();
            dialog.dismiss();
        });


        return dialog;
    }

    private static void buildAndShowDialogHtml(Context context, String titulo, String conteudo,
                                                 String labelBotaoPositivo, DialogInterface.OnClickListener onPositivoListener,
                                                 String labelBotaoNegativo, DialogInterface.OnClickListener onNegativoListener,
                                                 String labelBotaoNeutro, DialogInterface.OnClickListener onNeutroListener){

        if (isContextInvalido(context)) {
            return ;
        }

        //Infla o layout da dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_padrao, null);

        //Inicializa o Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogUtilsTheme);
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

        boolean isTresBotoes = labelBotaoNeutro !=null && !labelBotaoNeutro.trim().isEmpty();

        layoutBotoesPadrao.setVisibility(isTresBotoes ? View.GONE : View.VISIBLE);
        layoutTresBotoes.setVisibility(isTresBotoes ? View.VISIBLE : View.GONE);

        if(titulo != null && !titulo.isEmpty()){
            tvTitulo.setText(titulo);
            tvTitulo.setVisibility(View.VISIBLE);
        } else {
            tvTitulo.setVisibility(View.GONE);
        }

        tvConteudo.setText(
                HtmlCompat.fromHtml(
                conteudo != null ? conteudo : "",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                )
        );

        if(isTresBotoes){
            configuraBotao(btnTop, labelBotaoPositivo, DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnCenter, labelBotaoNegativo, DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
            configuraBotao(btnBottom, labelBotaoNeutro, DialogInterface.BUTTON_NEUTRAL, onNeutroListener, dialog);
        }else{
            configuraBotao(btnPositivo, labelBotaoPositivo,DialogInterface.BUTTON_POSITIVE, onPositivoListener, dialog);
            configuraBotao(btnNegativo, labelBotaoNegativo,DialogInterface.BUTTON_NEGATIVE, onNegativoListener, dialog);
        }

        dialog.setOnShowListener(d -> {
            if (tvTitulo.getVisibility() == View.VISIBLE){
                tvTitulo.post(() -> tvTitulo.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
                ));
            }

        });

        try {
            if (!isContextInvalido(context)) {
                dialog.show();
            }
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            return;
        }

    }
}
