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
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class DetalheMinhasApostasAdapter extends RecyclerView.Adapter<DetalheMinhasApostasAdapter.ViewHolder> {

    private final List<ItemMinhasApostasDTO> items;
    private final Context context;

    private EstiloModalidadeMKP estilo;

    public DetalheMinhasApostasAdapter(Context context, List<ItemMinhasApostasDTO> items) {
        this.context = context;
        this.items = items;
        this.estilo = new EstiloModalidadeMKP(items.get(0).getModalidade());
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
            holder.tvLabelSeusNumeros.setTextColor(estilo.getCorFonteFundoEscuro());

            setupGridNumeros(holder.rvSeusNumeros, Shape.CIRCULO, 6,  item.getSeusNumeros(), Color.WHITE, Color.WHITE, Color.TRANSPARENT);
            return;
        } else {
            holder.headerlayout.setVisibility(View.VISIBLE);
        }

        //Cabecalho Accordion
        holder.tvResultadoConcurso.setText(context.getString(R.string.resultado_concurso) + " " + item.getConcurso());
        holder.tvResultadoConcurso.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvSituacao.setText(item.getSituacao());
        holder.tvSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setBackgroundColor(Color.WHITE);
        holder.vLinhaAccordion2.setBackgroundColor(Color.WHITE);
        holder.vLinhaAccordion1.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        holder.headerlayout.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });

        if (isExpanded) {
            desabilitaTodos(holder);
            switch (item.getModalidade()) {
                case TIMEMANIA:
                    holder.tvLabelNumerosSorteados.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros.setVisibility(View.VISIBLE);

                    holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvLabelSeuTimeMes.setVisibility(View.VISIBLE);
                    holder.tvSeuTimeMes.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), estilo.getCorEscura(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));

                    break;
                case DUPLA_SENA:
                    holder.tvLabelNumerosSorteados.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSorteados2.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumeros2.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros2.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumeros(holder.rvNumberSortedos2, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT,
                            item.getNumerosSorteados(), Color.BLACK, Color.WHITE, Color.WHITE);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros2, Shape.CIRCULO, 6, item.getSeusNumeros(), Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT,
                            item.getNumerosSorteados2(), Color.BLACK, Color.WHITE, Color.WHITE);
                    break;
                case MEGA_SENA:
                case QUINA:
                case LOTOFACIL:
                default:
                    holder.tvLabelNumerosSorteados.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), estilo.getCorEscura(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
            }
        }
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
                                                  List<String> intersectionNumbers, int intersectionTextColor, int intersectionBorderColor, int intersectionBackgroudColor) {
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
        View vLinhaAccordion1, vLinhaAccordion2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResultadoConcurso = itemView.findViewById(R.id.tv_resultado_concurso);
            tvSituacao = itemView.findViewById(R.id.tv_situacao);
            ivExpandCollapse = itemView.findViewById(R.id.iv_expand_collapse);
            contentlayout = itemView.findViewById(R.id.content_layout);
            headerlayout = itemView.findViewById(R.id.header_layout);
            vLinhaAccordion1 = itemView.findViewById(R.id.v_linha_accordion1);
            vLinhaAccordion2 = itemView.findViewById(R.id.v_linha_accordion2);

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
