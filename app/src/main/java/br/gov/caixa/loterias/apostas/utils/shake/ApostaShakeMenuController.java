package br.gov.caixa.loterias.apostas.utils.shake;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

/** Controles do menu e aviso explicativo; a preferência só muda após a confirmação. */
public final class ApostaShakeMenuController {
    private final Activity activity;
    private final Runnable atualizar;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable fecharTooltip = this::ocultarTooltip;
    private PopupWindow tooltip;
    private Dialog confirmacao;
    private boolean automatica;

    public ApostaShakeMenuController(Activity activity, Runnable atualizar) {
        this.activity = activity;
        this.atualizar = atualizar;
    }
    public void alterarAtivacao() {
        ocultarTooltip();
        if (confirmacao != null && confirmacao.isShowing()) return;
        final boolean ativar = !ApostaShakePreferences.isAtiva();
        if (ApostaShakePreferences.isAvisoOculto()) { aplicar(ativar); return; }
        AppCompatCheckBox ocultar = new AppCompatCheckBox(activity);
        ocultar.setText(R.string.shake_menu_ocultar_aviso);
        ocultar.setTextColor(activity.getResources().getColor(R.color.cinza110));
        ocultar.setTextSize(14);
        androidx.core.widget.CompoundButtonCompat.setButtonTintList(ocultar, android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(activity, R.color.blue_caixa)));
        ocultar.setGravity(Gravity.CENTER);
        confirmacao = DialogUtils.dialogTituloDoisBotoesReturn(activity,
                activity.getString(ativar ? R.string.shake_menu_ativar : R.string.shake_menu_desativar),
                activity.getString(ativar ? R.string.shake_menu_confirmar_ativar : R.string.shake_menu_confirmar_desativar),
                activity.getString(R.string.aposta_shake_sim), activity.getString(R.string.aposta_shake_nao),
                new OnDialogDoisBotoesListener() {
                    @Override public void PositiveButton(DialogInterface dialog, int which) {
                        ApostaShakePreferences.setAvisoOculto(ocultar.isChecked());
                        aplicar(ativar);
                    }
                    @Override public void NegativeButton(DialogInterface dialog, int which) { atualizar.run(); }
                });
        if (confirmacao == null) return;
        confirmacao.setOnDismissListener(dialog -> { confirmacao = null; atualizar.run(); });
        confirmacao.show();
        TextView mensagem = confirmacao.findViewById(R.id.conteudo_dialog_padrao);
        if (mensagem != null) mensagem.setGravity(Gravity.CENTER);
        View buttons = confirmacao.findViewById(R.id.layout_botoes_padrao);
        if (buttons != null && buttons.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) buttons.getParent();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            parent.addView(ocultar, parent.indexOfChild(buttons), params);
        }
    }
    private void aplicar(boolean ativa) { ApostaShakePreferences.setAtiva(ativa); atualizar.run(); }
    public boolean mostrarTooltip(View anchor, boolean automatico) {
        if (anchor == null || !anchor.isShown() || anchor.getWidth() == 0 || activity.isFinishing() || activity.isDestroyed()) return false;
        ocultarTooltip();
        automatica = automatico;
        View content = LayoutInflater.from(activity).inflate(R.layout.aposta_shake_tooltip, null);
        View pointer = content.findViewById(R.id.shakeTooltipPointer);
        int accent = androidx.core.content.ContextCompat.getColor(activity,
                br.gov.caixa.loterias.apostas.utils.EspecialUtils.isOutubroRosa()
                        ? R.color.outubro_rosa_secundario : R.color.blue_caixa);
        ((android.widget.ImageView) pointer).setImageTintList(
                android.content.res.ColorStateList.valueOf(accent));
        content.findViewById(R.id.shakeTooltipText).setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(accent));
        View info = anchor.findViewById(R.id.shakeMenuInfo);
        if (info != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) pointer.getLayoutParams();
            params.setMarginStart(Math.max(0, info.getLeft() + info.getWidth() / 2 - params.width / 2));
            pointer.setLayoutParams(params);
        }
        tooltip = new PopupWindow(content, anchor.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, false);
        tooltip.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tooltip.setOutsideTouchable(true);
        tooltip.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) ocultarTooltip();
            return false;
        });
        tooltip.setOnDismissListener(() -> { handler.removeCallbacks(fecharTooltip); tooltip = null; automatica = false; });
        tooltip.showAsDropDown(anchor, 0,
                -Math.round(8 * activity.getResources().getDisplayMetrics().density));
        content.announceForAccessibility(activity.getString(R.string.shake_menu_tooltip));
        if (automatico) handler.postDelayed(fecharTooltip, 5000);
        return true;
    }
    public void aoTocarTela() { if (automatica) ocultarTooltip(); }
    public void ocultarTooltip() {
        handler.removeCallbacks(fecharTooltip);
        if (tooltip != null) tooltip.dismiss();
        tooltip = null;
        automatica = false;
    }
    public void fechar() { ocultarTooltip(); if (confirmacao != null) confirmacao.dismiss(); }
}
