package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;

public class NumerosGridAdapter extends RecyclerView.Adapter<NumerosGridAdapter.ViewHolder> {

    private Shape shape;
    private List<String> numbers;
    private int textColor;
    private int borderColor;
    private int backgroudColor;
    private List<String> intersectionNumbers;
    private int intersectionTextColor;
    private int intersectionBorderColor;
    private int intersectionBackgroudColor;
    private boolean intersectionS7;

    private Context context;

    public NumerosGridAdapter(Shape shape, List<String> numbers, int textColor, int borderColor, int backgroudColor) {
        this.shape = shape;
        this.numbers = numbers;
        this.textColor = textColor;
        this.borderColor = borderColor;
        this.backgroudColor = backgroudColor;
    }
    public NumerosGridAdapter(Shape shape, List<String> numbers, int textColor, int borderColor, int backgroudColor,
                              List<String> intersectionNumbers, int intersectionTtextColor, int intersectionBorderColor, int intersectionBackgroudColor) {
        this(shape, numbers, textColor, borderColor, backgroudColor);
        this.intersectionNumbers = intersectionNumbers;
        this.intersectionTextColor = intersectionTtextColor;
        this.intersectionBorderColor = intersectionBorderColor;
        this.intersectionBackgroudColor = intersectionBackgroudColor;
    }
    public NumerosGridAdapter(Shape shape, List<String> numbers, int textColor, int borderColor, int backgroudColor,
                              List<String> intersectionNumbers, int intersectionTtextColor, int intersectionBorderColor, int intersectionBackgroudColor, boolean intersectionS7) {
        this(shape, numbers, textColor, borderColor, backgroudColor);
        this.intersectionNumbers = intersectionNumbers;
        this.intersectionTextColor = intersectionTtextColor;
        this.intersectionBorderColor = intersectionBorderColor;
        this.intersectionBackgroudColor = intersectionBackgroudColor;
        this.intersectionS7 = intersectionS7;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();
        int layoutId;
        switch (shape) {
            case CIRCULO:
                layoutId = R.layout.item_numeros_grid_circulo;
                break;
            case RETANGULO:
                layoutId = R.layout.item_numeros_grid_retangulo;
                break;
            case RETANGULO_CAROUSEL:
                layoutId = R.layout.item_numeros_grid_retangulo_carrossel;
                break;
            case TREVO:
                layoutId = R.layout.item_numeros_grid_trevo;
                break;
            case NO_SHAPE:
            default:
                layoutId = R.layout.item_numeros_grid_noshape;
                break;
        }
        view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String number = numbers.get(position);
        holder.textView.setText(number);

        if (number == null || number.isEmpty()) {
            GradientDrawable background = (GradientDrawable) holder.textView.getBackground().mutate();
            background.setStroke(3, Color.TRANSPARENT);
            background.setColor(Color.TRANSPARENT);
            return;
        }

        boolean isIntersection;
        if (intersectionS7) {
            int indice = position % 7;
            isIntersection = intersectionNumbers != null && intersectionNumbers.get(indice).equals(number);
        } else {
            isIntersection = intersectionNumbers != null && intersectionNumbers.contains(number);
        }

        if (isIntersection) {
            //Accessibility
            holder.textView.setContentDescription(number + context.getString(R.string.txt_rewarded));
            holder.textView.setTextColor(intersectionTextColor);
        } else {
            holder.textView.setTextColor(textColor);
        }

        if (shape != Shape.NO_SHAPE) {
            if (shape != Shape.TREVO) {
                int currentBorderColor = isIntersection ? intersectionBorderColor : borderColor;
                int currentBackgroundColor = isIntersection ? intersectionBackgroudColor : backgroudColor;
                GradientDrawable background = (GradientDrawable) holder.textView.getBackground().mutate();
                background.setStroke(3, currentBorderColor);
                background.setColor(currentBackgroundColor);
            } else {
                if (isIntersection) {
                    //Apenas para acessibilidade
                    holder.textView.setContentDescription(holder.textView.getText() + context.getString(R.string.txt_rewarded));
                    holder.textView.setBackgroundResource(R.drawable.ic_item_trevo_selecionado);
                } else {
                    holder.textView.setBackgroundResource(R.drawable.ic_item_trevo_branco);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_number);
        }
    }
}
