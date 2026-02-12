package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int layoutId;
        switch (shape) {
            case CIRCULO:
                layoutId = R.layout.item_numeros_grid_circulo;
                break;
            case RETANGULO:
                layoutId = R.layout.item_numeros_grid_retangulo;
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

        boolean isIntersection = intersectionNumbers != null && intersectionNumbers.contains(number);

        if (isIntersection) {
            holder.textView.setTextColor(intersectionTextColor);
        } else {
            holder.textView.setTextColor(textColor);
        }

        if (shape != Shape.NO_SHAPE) {
            int currentBorderColor = isIntersection ? intersectionBorderColor : borderColor;
            int currentBackgroundColor = isIntersection ? intersectionBackgroudColor : backgroudColor;
            GradientDrawable background = (GradientDrawable) holder.textView.getBackground().mutate();
            background.setStroke(3, currentBorderColor);
            background.setColor(currentBackgroundColor);
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
