package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class AccordionView extends ConstraintLayout {

    private ConstraintLayout container;
    private ConstraintLayout header;
    private ConstraintLayout body;

    private TextView tvHeader;
    private TextView tvBody;

    private ImageView ivIcon;

    private AccordionModel model;

    private void init(){
        inflate(getContext(), R.layout.view_accordion, this);
        container = findViewById(R.id.container);

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

        applyState();
    }

    public void bind(AccordionModel model) {
        this.model = model;
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(model.getModalidadeEnum());
//        header.setBackgroundColor();
//        tvHeader.setTextColor(model.getHeaderTextColor());
//        tvBody.setText(model.getBodyText());
//        tvBody.setTextColor(model.getBodyTextColor());
//        body.setBackgroundColor(model.getBodyBackgroundColor());
//        ivIcon.setImageResource(model.getIconRes());
//        ImageViewCompat.setImageTintList(
//                ivIcon,
//                ColorStateList.valueOf(model.getIconColor())
//        );

        body.setVisibility(model.isExpanded() ? View.VISIBLE: View.GONE);

        ivIcon.setRotation(model.isExpanded() ? 180f : 0);

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
