package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.ImageViewCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class AccordionView extends ConstraintLayout {

    private ConstraintLayout header;
    private ConstraintLayout body;

    private TextView tvHeader;
    private TextView tvBody;

    private ImageView ivIcon;

    private AccordionModel model;

    private void init(){
        inflate(getContext(), R.layout.view_accordion, this);

        header = findViewById(R.id.header);
        body = findViewById(R.id.body);

        tvBody = findViewById(R.id.tv_body);
        tvHeader = findViewById(R.id.tv_header);

        ivIcon = findViewById(R.id.iv_icon);

        setupClicks();
    }

    private void setupClicks() {
        header.setOnClickListener(v -> toggle());
    }

    private void toggle() {
        if (model == null) return;

        model.setExpanded(!model.isExpanded());
        setExpandedDescription();

        applyState();
    }

    private void setExpandedDescription(){
        ViewCompat.setAccessibilityDelegate(header, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(
                    @NonNull View host,
                    @NonNull AccessibilityNodeInfoCompat info
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setContentDescription("Como Jogar");
                info.setClassName(Button.class.getName());
                info.setClickable(true);

                // ação correta
                if (model.isExpanded()) {
                    info.addAction(
                            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_COLLAPSE
                    );
                    info.setStateDescription("Expandido");
                } else {
                    info.addAction(
                            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND
                    );
                    info.setStateDescription("Recolhido");
                }
            }
        });
    }

    public void bind(AccordionModel model) {
        this.model = model;
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(model.getModalidadeEnum());

        tvHeader.setTextColor(getContext().getColor(estilo.getCorFonteFundoEscuro()));
        ivIcon.setColorFilter(getContext().getColor(estilo.getCorFonteFundoEscuro()));

        header.setBackgroundColor(getContext().getColor(estilo.getCorEscura()));
        if (model.isEspecial() && estilo.getImagemSubCabecalho() > 0) {
            header.setBackground(AppCompatResources.getDrawable(getContext(), estilo.getImagemSubCabecalho()));
        }

        setExpandedDescription();
        body.setBackgroundColor(getContext().getColor(R.color.cinzaCx));

        tvBody.setText(model.getBodyText());
        tvBody.setBackgroundColor(getContext().getColor(R.color.transparente));

        applyState();
    }

    private void applyState() {
        TransitionManager.beginDelayedTransition(this);

        body.setVisibility(model.isExpanded() ? View.VISIBLE : View.GONE);

        ivIcon.animate().rotation(model.isExpanded() ? 180f : 0f).setDuration(200).start();
    }

    public AccordionView(@NonNull Context context) {
        super(context);
        init();
    }

    public AccordionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccordionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AccordionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
