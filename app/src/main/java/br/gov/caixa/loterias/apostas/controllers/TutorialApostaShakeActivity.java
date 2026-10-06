package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import br.gov.caixa.loterias.apostas.utils.shake.TutorialApostaShakeGifCrop;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.shake.ApostaShakePreferences;

/** Tutorial com os GIFs fornecidos e controles de navegação nativos. */
public class TutorialApostaShakeActivity extends LoteriasBaseAppActivity {
    private static final int[] TITULOS = {R.string.shake_tutorial_titulo_1, R.string.shake_tutorial_titulo_2,
            R.string.shake_tutorial_titulo_3, R.string.shake_tutorial_titulo_4,
            R.string.shake_tutorial_titulo_5, R.string.shake_tutorial_titulo_6};
    private static final int[] DESTAQUES = {R.string.shake_tutorial_destaque_1, R.string.shake_tutorial_destaque_2,
            R.string.shake_tutorial_destaque_3, R.string.shake_tutorial_destaque_4,
            R.string.shake_tutorial_destaque_5, R.string.shake_tutorial_destaque_6};
    private static final int[] GIFS = {R.raw.aposta_shake_apresentacao, R.raw.aposta_shake_abrir_volante,
            R.raw.aposta_shake_agitar, R.raw.aposta_shake_preencher,
            R.raw.aposta_shake_menu, R.raw.aposta_shake_final};
    private static final int[] DESCRICOES = {R.string.shake_tutorial_descricao_1, R.string.shake_tutorial_descricao_2,
            R.string.shake_tutorial_descricao_3, R.string.shake_tutorial_descricao_4,
            R.string.shake_tutorial_descricao_5, R.string.shake_tutorial_descricao_6};
    private int pagina;
    private boolean concluindo;
    private float inicioX, inicioY;
    private boolean toqueEmControle;
    private boolean gestoValido;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_tutorial_aposta_shake);
        pagina = state == null ? 0 : Math.max(0, Math.min(TITULOS.length - 1, state.getInt("pagina")));
        findViewById(R.id.shakeTutorialPrevious).setOnClickListener(v -> anterior());
        findViewById(R.id.shakeTutorialNext).setOnClickListener(v -> proxima());
        findViewById(R.id.shakeTutorialSkip).setOnClickListener(v -> concluir());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { if (pagina > 0) anterior(); else concluir(); }
        });
        apresentar();
    }
    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        View root = findViewById(R.id.shakeTutorialRoot);
        if (root == null) return super.dispatchTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            inicioX = event.getRawX(); inicioY = event.getRawY();
            toqueEmControle = atingiuControle(R.id.shakeTutorialSkip, event)
                    || atingiuControle(R.id.shakeTutorialPrevious, event)
                    || atingiuControle(R.id.shakeTutorialNext, event);
            gestoValido = true;
        }
        // Os botões mantêm seu clique nativo, inclusive acessibilidade.
        if (toqueEmControle) return super.dispatchTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_CANCEL) gestoValido = false;
        if (event.getActionMasked() == MotionEvent.ACTION_UP && gestoValido) {
            float dx = event.getRawX() - inicioX, dy = event.getRawY() - inicioY;
            if (Math.abs(dx) >= dp(48) && Math.abs(dx) > Math.abs(dy)) {
                if (dx < 0) proxima(); else anterior();
            } else if (Math.hypot(dx, dy) <= ViewConfiguration.get(this).getScaledTouchSlop()) {
                int[] position = new int[2];
                root.getLocationOnScreen(position);
                if (event.getRawX() - position[0] >= root.getWidth() / 2f) proxima(); else anterior();
            }
            gestoValido = false;
        }
        return true;
    }
    private boolean atingiuControle(int id, MotionEvent event) {
        View control = findViewById(id);
        Rect bounds = new Rect();
        return control != null && control.isShown() && control.getGlobalVisibleRect(bounds)
                && bounds.contains((int) event.getRawX(), (int) event.getRawY());
    }
    private void anterior() { if (pagina > 0) { pagina--; apresentar(); } }
    private void proxima() { if (pagina == TITULOS.length - 1) concluir(); else { pagina++; apresentar(); } }
    private void concluir() {
        if (concluindo) return;
        concluindo = true;
        ApostaShakePreferences.concluirTutorial();
        setResult(RESULT_OK);
        finish();
    }
    private void apresentar() {
        TextView title = findViewById(R.id.shakeTutorialTitle);
        String texto = getString(TITULOS[pagina]);
        String destaque = getString(DESTAQUES[pagina]);
        SpannableString formatted = new SpannableString(texto);
        int inicio = texto.indexOf(destaque);
        if (!destaque.isEmpty() && inicio >= 0) {
            formatted.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_caixa)), inicio, inicio + destaque.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            formatted.setSpan(new StyleSpan(Typeface.BOLD), inicio, inicio + destaque.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        title.setText(formatted);
        ImageView image = findViewById(R.id.shakeTutorialImage);
        // Glide anima os quadros e acompanha pausa e destruição da Activity.
        RequestBuilder<GifDrawable> animation = Glide.with(this).asGif().load(GIFS[pagina]);
        if (pagina == 1 || pagina == 4) animation.transform(new TutorialApostaShakeGifCrop());
        animation.into(image);
        image.setContentDescription(getString(DESCRICOES[pagina]));
        findViewById(R.id.shakeTutorialPrevious).setVisibility(pagina == 0 ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.shakeTutorialNext).setContentDescription(getString(pagina == 5 ? R.string.shake_tutorial_fechar : R.string.shake_tutorial_proximo));
        Button skip = findViewById(R.id.shakeTutorialSkip);
        skip.setText(pagina == 5 ? R.string.shake_tutorial_fechar : R.string.shake_tutorial_pular);
        skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LinearLayout dots = findViewById(R.id.shakeTutorialDots);
        dots.removeAllViews();
        for (int i = 0; i < TITULOS.length; i++) {
            View dot = new View(this);
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(i == pagina ? getResources().getColor(R.color.blue_caixa) : 0xFFFFFFFF);
            shape.setCornerRadius(dp(8));
            dot.setBackground(shape);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(i == pagina ? 24 : 7), dp(7));
            params.setMargins(dp(3), 0, dp(3), 0);
            dots.addView(dot, params);
        }
        dots.setContentDescription(getString(R.string.shake_tutorial_etapa, pagina + 1, TITULOS.length));
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    @Override protected void onSaveInstanceState(Bundle outState) { outState.putInt("pagina", pagina); super.onSaveInstanceState(outState); }
}
