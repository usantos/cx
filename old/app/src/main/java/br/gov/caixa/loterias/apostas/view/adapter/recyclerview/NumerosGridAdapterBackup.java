package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.RowItem;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.model.config.LotteryGridStyleConfig;
import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;

public class NumerosGridAdapterBackup extends ListAdapter<RowItem, NumerosGridAdapterBackup.ViewHolder> {

    private final LotteryGridStyleConfig styleConfig;


    private static final DiffUtil.ItemCallback<RowItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RowItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull RowItem oldItem, @NonNull RowItem newItem) {
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(@NonNull RowItem oldItem, @NonNull RowItem newItem) {
                    return oldItem.getItems().equals(newItem.getItems());
                }
            };

    public NumerosGridAdapterBackup(LotteryGridStyleConfig styleConfig) {
        super(DIFF_CALLBACK);
        this.styleConfig = styleConfig;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), styleConfig);
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout rowContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowContainer = itemView.findViewById(R.id.rowContainer);
        }

        public void bind(RowItem rowItem, LotteryGridStyleConfig styleConfig) {
            rowContainer.removeAllViews();

            Context context = itemView.getContext();
            int layoutId = resolveLayout(styleConfig.getShape());

            StringBuilder contentDescription = new StringBuilder();

            for (LotteryNumberUiModel model : rowItem.getItems()) {
                View cellView = LayoutInflater.from(context)
                        .inflate(layoutId, rowContainer, false);

                TextView cellText = cellView.findViewById(R.id.tv_number);

                if (model.getNumber() == null) {
                    cellView.setVisibility(View.INVISIBLE);
                } else {
                    cellText.setText(model.getNumber());
                    applyTextColor(cellText, model, styleConfig);
                    applyBackground(cellText, model, styleConfig);
                }

                cellView.setImportantForAccessibility(
                        View.IMPORTANT_FOR_ACCESSIBILITY_NO
                );

                rowContainer.addView(cellView);

                if (model.getNumber() != null && !TextUtils.isEmpty(model.getAccessibility())) {
                    if (contentDescription.length() > 0) {
                        contentDescription.append(", ");
                    }
                    contentDescription.append(model.getAccessibility());
                }
            }

            itemView.setContentDescription(contentDescription.toString());
            itemView.setFocusable(true);
            itemView.setImportantForAccessibility(
                    View.IMPORTANT_FOR_ACCESSIBILITY_YES
            );
        }

        private void applyBackground(TextView cellText, LotteryNumberUiModel model, LotteryGridStyleConfig styleConfig) {
            Shape shape = styleConfig.getShape();

            if (shape == Shape.NO_SHAPE) {
                return;
            }

            if (shape == Shape.TREVO) {
                if (model.isRewarded()) {
                    cellText.setBackgroundResource(R.drawable.ic_item_trevo_selecionado);
                } else {
                    cellText.setBackgroundResource(R.drawable.ic_item_trevo_branco);
                }

                return;
            }

            int borderColor = model.isRewarded()
                    ? styleConfig.getIntersection().getBorderColor()
                    : styleConfig.getNormal().getBorderColor();

            int backgroundColor = model.isRewarded()
                    ? styleConfig.getIntersection().getBackgroundColor()
                    : styleConfig.getNormal().getBackgroundColor();

            GradientDrawable background = (GradientDrawable) cellText.getBackground().mutate();

            background.setStroke(3, borderColor);
            background.setColor(backgroundColor);
        }

        private void applyTextColor(TextView cellText, LotteryNumberUiModel model, LotteryGridStyleConfig styleConfig) {
            int color = model.isRewarded()
                    ? styleConfig.getIntersection().getTextColor()
                    : styleConfig.getNormal().getTextColor();

            cellText.setTextColor(color);
        }

        private int resolveLayout(Shape shape) {
            switch (shape) {
                case CIRCULO:
                    return R.layout.item_numeros_grid_circulo_clone;
                case RETANGULO:
                    return R.layout.item_numeros_grid_retangulo_clone;
                case TREVO:
                    return R.layout.item_numeros_grid_trevo_clone;
                case NO_SHAPE:
                default:
                    return R.layout.item_numeros_grid_noshape_clone;
            }
        }
    }
}
