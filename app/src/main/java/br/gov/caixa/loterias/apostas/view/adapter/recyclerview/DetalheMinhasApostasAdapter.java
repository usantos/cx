package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;

public class DetalheMinhasApostasAdapter extends RecyclerView.Adapter<DetalheMinhasApostasAdapter.ViewHolder> {

    private final List<ItemMinhasApostasDTO> items;
    private final Context context;

    public DetalheMinhasApostasAdapter(Context context, List<ItemMinhasApostasDTO> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minhas_apostas_accordion, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemMinhasApostasDTO item = items.get(position);

        boolean isExpanded = item.isExpanded();
        holder.contentlayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivExpandCollapse.setRotation(isExpanded ? 180 : 0);

        if(!item.isApurado()) {
            holder.headerlayout.setVisibility(View.GONE);
            holder.contentlayout.setVisibility(View.VISIBLE);
            desabilitaTodos(holder);

            holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
            holder.rvSeusNumeros.setVisibility(View.VISIBLE);
            holder.tvLabelSeusNumeros.setTextColor(Color.WHITE); //**Corrigir**

            setupGridNumeros(holder.rvSeusNumeros, Shape.CIRCULO, 6,  item.getSeusNumeros(), Color.WHITE, Color.WHITE, Color.TRANSPARENT);
            return;
        } else {
            holder.headerlayout.setVisibility(View.VISIBLE);
        }

        //*** COMPLETAR ***

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void desabilitaTodos(ViewHolder holder) {
        holder.tvLabelNumerosSorteados.setVisibility(View.GONE);
        holder.rvNumberSortedos.setVisibility(View.GONE);
        holder.tvLabelNumerosSorteados2.setVisibility(View.GONE);
        holder.rvNumberSortedos2.setVisibility(View.GONE);
        holder.tvLabelTimeMesSorteado.setVisibility(View.GONE);
        holder.tvTimeMesSorteado.setVisibility(View.GONE);
        holder.tvLabelSeusNumeros.setVisibility(View.GONE);
        holder.rvSeusNumeros.setVisibility(View.GONE);
        holder.tvLabelSeusNumeros2.setVisibility(View.GONE);
        holder.rvSeusNumeros2.setVisibility(View.GONE);
        holder.tvLabelSeuTimeMes.setVisibility(View.GONE);
        holder.tvSeuTimeMes.setVisibility(View.GONE);
    }
    private void setupGridNumeros(RecyclerView recyclerView, Shape shape, int columns,  List<String> numbers,int textColor, int borderColor, int backgroudColor) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
        recyclerView.setNestedScrollingEnabled(false);
        NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor);
        recyclerView.setAdapter(adapter);
    }
    private void setupGridNumerosWithIntersection(RecyclerView recyclerView, Shape shape,  int columns, List<String> numbers, int textColor, int borderColor, int backgroudColor,
                                                  List<String> intersectionNumbers, int intersectionColumns, int intersectionTextColor, int intersectionBorderColor, int intersectionBackgroudColor) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
        recyclerView.setNestedScrollingEnabled(false);
        NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor,
                intersectionNumbers, intersectionTextColor, intersectionBorderColor, intersectionBackgroudColor);
        recyclerView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResultadoConcurso, tvSituacao, tvLabelNumerosSorteados, tvLabelNumerosSorteados2;
        TextView tvLabelTimeMesSorteado, tvTimeMesSorteado, tvLabelSeusNumeros, tvLabelSeusNumeros2;
        TextView tvLabelSeuTimeMes, tvSeuTimeMes;
        ImageView ivExpandCollapse;
        LinearLayout contentlayout;
        ConstraintLayout headerlayout;
        RecyclerView rvNumberSortedos, rvNumberSortedos2, rvSeusNumeros, rvSeusNumeros2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResultadoConcurso = itemView.findViewById(R.id.tv_resultado_concurso);
            tvSituacao = itemView.findViewById(R.id.tv_situacao);
            ivExpandCollapse = itemView.findViewById(R.id.iv_expand_collapse);
            contentlayout = itemView.findViewById(R.id.content_layout);
            headerlayout = itemView.findViewById(R.id.header_layout);

            tvLabelNumerosSorteados = itemView.findViewById(R.id.tv_label_numeros_sorteados);
            rvNumberSortedos = itemView.findViewById(R.id.rv_numbers_sorteados);
            tvLabelNumerosSorteados2 = itemView.findViewById(R.id.tv_label_numeros_sorteados2);
            rvNumberSortedos2 = itemView.findViewById(R.id.rv_numbers_sorteados2);
            tvLabelTimeMesSorteado = itemView.findViewById(R.id.tv_label_time_mes_sorteado);
            tvTimeMesSorteado = itemView.findViewById(R.id.tv_time_mes_sorteado);
            tvLabelSeusNumeros = itemView.findViewById(R.id.tv_label_seus_numeros);
            rvSeusNumeros = itemView.findViewById(R.id.rv_seus_numeros);
            tvLabelSeusNumeros2 = itemView.findViewById(R.id.tv_label_seus_numeros2);
            rvSeusNumeros2 = itemView.findViewById(R.id.rv_seus_numeros2);
            tvLabelSeuTimeMes = itemView.findViewById(R.id.tv_label_seu_time_mes);
            tvSeuTimeMes = itemView.findViewById(R.id.tv_seu_time_mes);
        }
    }
}
