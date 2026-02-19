package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

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
        holder.ivExpandCollapse.setColorFilter(context.getColor(estilo.getCorFonteFundoEscuro()));


        if(!item.isApurado()) {
            holder.headerlayout.setVisibility(View.GONE);
            holder.contentlayout.setVisibility(View.VISIBLE);
            desabilitaTodos(holder);

            holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
            holder.rvSeusNumeros.setVisibility(View.VISIBLE);
            holder.tvLabelSeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

            setupGridNumeros(holder.rvSeusNumeros, Shape.CIRCULO, 6,  item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()));
            return;
        }

        //Cabecalho Accordion
        holder.headerlayout.setVisibility(View.VISIBLE);
        holder.tvResultadoConcurso.setText(context.getString(R.string.resultado_concurso) + " " + item.getConcurso());
        holder.tvResultadoConcurso.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvLabelSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvSituacao.setText(item.getSituacao());
        holder.tvSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion2.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        holder.headerlayout.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });

        if (isExpanded) {
            desabilitaTodos(holder);

            //Todos
            holder.tvLabelNumerosSorteados.setVisibility(View.VISIBLE);
            holder.rvNumberSortedos.setVisibility(View.VISIBLE);
            holder.tvLabelSeusNumeros.setVisibility(View.VISIBLE);
            holder.rvSeusNumeros.setVisibility(View.VISIBLE);

            holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
            holder.tvLabelSeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

            holder.ivIconeCentro.setImageDrawable(context.getDrawable(estilo.getTrevoFundoClaro()));

            if (item.getValorPremio() != null) {
                holder.llPremio.setVisibility(View.VISIBLE);
                holder.tvLabelPremio.setVisibility(View.VISIBLE);
                holder.tvLabelPremio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvPremio.setVisibility(View.VISIBLE);
                holder.tvPremio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvPremio.setText(ViewUtils.getMoedaFormat(item.getValorPremio()));
            }

            switch (item.getModalidade()) {
                case TIMEMANIA:
                    holder.tvLabelTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvLabelSeuTimeMes.setVisibility(View.VISIBLE);
                    holder.tvSeuTimeMes.setVisibility(View.VISIBLE);

                    holder.tvLabelTimeMesSorteado.setText(R.string.label_time_sorteado);
                    holder.tvLabelTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                    holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    holder.tvLabelSeuTimeMes.setText(R.string.label_time_do_coracao);
                    holder.tvLabelSeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvSeuTimeMes.setText(item.getSeuTimeMes());
                    holder.tvSeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case DIA_DE_SORTE:
                    holder.tvLabelTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvLabelSeuTimeMes.setVisibility(View.VISIBLE);
                    holder.tvSeuTimeMes.setVisibility(View.VISIBLE);

                    holder.tvLabelTimeMesSorteado.setText(R.string.label_mes_sorteado_);
                    holder.tvLabelTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                    holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    holder.tvLabelSeuTimeMes.setText(R.string.label_mes_de_sorte);
                    holder.tvLabelSeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvSeuTimeMes.setText(item.getSeuTimeMes());
                    holder.tvSeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case DUPLA_SENA:
                    holder.tvLabelNumerosPrimeiroSorteio.setVisibility(View.VISIBLE);
                    holder.tvLabelNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumerosPrimeiroSorteio.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros2.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));


                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumeros(holder.rvNumberSortedos2, Shape.NO_SHAPE, 6, item.getNumerosSorteados2(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros2, Shape.CIRCULO, 6, item.getSeusNumeros2(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados2(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case SUPER_7:
                    holder.rvSuper7Sorteados.setVisibility(View.VISIBLE);
                    holder.rvSuper7SeusNumeros.setVisibility(View.VISIBLE);

                    ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1","2","3","4","5","6","7"));

                    setupGridNumeros(holder.rvSuper7Sorteados, Shape.RETANGULO, 7, listCabecalho1a7, context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorClara()), context.getColor(estilo.getCorClara()));
                    setupGridNumeros(holder.rvNumberSortedos, Shape.RETANGULO, 7, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumeros(holder.rvSuper7SeusNumeros, Shape.RETANGULO, 7, listCabecalho1a7, context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorClara()), context.getColor(estilo.getCorClara()));
                    setupGridNumerosWithIntersectionS7(holder.rvSeusNumeros, Shape.CIRCULO, 7, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case MAIS_MILIONARIA:
                    holder.tvLabelNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                    holder.tvLabelSeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvSeusNumeros2.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelNumerosSegundoSorteio.setText(R.string.label_trevos_sorteados);

                    holder.tvLabelSeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelSeusNumerosSegundoSorteio.setText(R.string.label_trevos);

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);

                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumerosWithIntersection(holder.rvNumberSortedos2, Shape.TREVO, 2, item.getTrevosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()),  Color.TRANSPARENT,  Color.TRANSPARENT,
                            item.getTrevosSorteados(), context.getColor(estilo.getCorEscura()),  Color.TRANSPARENT,  Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros2, Shape.TREVO, 2, item.getSeusTrevos(), context.getColor(estilo.getCorFonteFundoEscuro()),  Color.TRANSPARENT,  Color.TRANSPARENT,
                            item.getTrevosSorteados(), context.getColor(estilo.getCorEscura()),  Color.TRANSPARENT,  Color.TRANSPARENT);
                    break;
                case LOTOFACIL:
                case QUINA:
                case LOTOMANIA:
                case MEGA_SENA:
                default:
                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvSeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
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
        holder.rvSuper7Sorteados.setVisibility(View.GONE);
        holder.tvLabelNumerosPrimeiroSorteio.setVisibility(View.GONE);
        holder.tvLabelNumerosSegundoSorteio.setVisibility(View.GONE);
        holder.rvNumberSortedos2.setVisibility(View.GONE);
        holder.tvLabelTimeMesSorteado.setVisibility(View.GONE);
        holder.tvTimeMesSorteado.setVisibility(View.GONE);
        holder.tvLabelSeusNumeros.setVisibility(View.GONE);
        holder.rvSeusNumeros.setVisibility(View.GONE);
        holder.rvSuper7SeusNumeros.setVisibility(View.GONE);
        holder.tvLabelSeusNumerosPrimeiroSorteio.setVisibility(View.GONE);
        holder.tvLabelSeusNumerosSegundoSorteio.setVisibility(View.GONE);
        holder.rvSeusNumeros2.setVisibility(View.GONE);
        holder.tvLabelSeuTimeMes.setVisibility(View.GONE);
        holder.tvSeuTimeMes.setVisibility(View.GONE);
        holder.llPremio.setVisibility(View.GONE);
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
    private void setupGridNumerosWithIntersectionS7(RecyclerView recyclerView, Shape shape,  int columns, List<String> numbers, int textColor, int borderColor, int backgroudColor,
                                                  List<String> intersectionNumbers, int intersectionTextColor, int intersectionBorderColor, int intersectionBackgroudColor) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
        recyclerView.setNestedScrollingEnabled(false);
        NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor,
                intersectionNumbers, intersectionTextColor, intersectionBorderColor, intersectionBackgroudColor, true);
        recyclerView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResultadoConcurso, tvLabelSituacao, tvSituacao, tvLabelNumerosSorteados, tvLabelNumerosPrimeiroSorteio, tvLabelNumerosSegundoSorteio;
        TextView tvLabelTimeMesSorteado, tvTimeMesSorteado, tvLabelSeusNumeros, tvLabelSeusNumerosPrimeiroSorteio, tvLabelSeusNumerosSegundoSorteio;
        TextView tvLabelSeuTimeMes, tvSeuTimeMes;
        TextView tvLabelPremio, tvPremio;
        ImageView ivExpandCollapse, ivIconeCentro;
        LinearLayout contentlayout, llPremio;
        ConstraintLayout headerlayout;
        RecyclerView rvNumberSortedos, rvNumberSortedos2, rvSeusNumeros, rvSeusNumeros2, rvSuper7Sorteados, rvSuper7SeusNumeros;
        View vLinhaAccordion1, vLinhaAccordion2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResultadoConcurso = itemView.findViewById(R.id.tv_resultado_concurso);
            tvLabelSituacao = itemView.findViewById(R.id.tv_label_situacao);
            tvSituacao = itemView.findViewById(R.id.tv_situacao);
            ivExpandCollapse = itemView.findViewById(R.id.iv_expand_collapse);
            contentlayout = itemView.findViewById(R.id.content_layout);
            headerlayout = itemView.findViewById(R.id.header_layout);
            vLinhaAccordion1 = itemView.findViewById(R.id.v_linha_accordion1);
            vLinhaAccordion2 = itemView.findViewById(R.id.v_linha_accordion2);
            ivIconeCentro = itemView.findViewById(R.id.icone_centro);

            tvLabelNumerosSorteados = itemView.findViewById(R.id.tv_label_numeros_sorteados);
            rvNumberSortedos = itemView.findViewById(R.id.rv_numbers_sorteados);
            rvSuper7Sorteados = itemView.findViewById(R.id.rv_super7_sorteados);
            tvLabelNumerosPrimeiroSorteio = itemView.findViewById(R.id.tv_label_numeros_primeiro_sorteio);
            tvLabelNumerosSegundoSorteio = itemView.findViewById(R.id.tv_label_numeros_segundo_sorteio);
            rvNumberSortedos2 = itemView.findViewById(R.id.rv_numbers_sorteados2);
            tvLabelTimeMesSorteado = itemView.findViewById(R.id.tv_label_time_mes_sorteado);
            tvTimeMesSorteado = itemView.findViewById(R.id.tv_time_mes_sorteado);
            tvLabelSeusNumeros = itemView.findViewById(R.id.tv_label_seus_numeros);
            rvSeusNumeros = itemView.findViewById(R.id.rv_seus_numeros);
            rvSuper7SeusNumeros = itemView.findViewById(R.id.rv_super7_seus_numeros);
            tvLabelSeusNumerosPrimeiroSorteio = itemView.findViewById(R.id.tv_label_seus_numeros_primeiro_sorteio);
            tvLabelSeusNumerosSegundoSorteio = itemView.findViewById(R.id.tv_label_seus_numeros_segundo_sorteio);
            rvSeusNumeros2 = itemView.findViewById(R.id.rv_seus_numeros2);
            tvLabelSeuTimeMes = itemView.findViewById(R.id.tv_label_seu_time_mes);
            tvSeuTimeMes = itemView.findViewById(R.id.tv_seu_time_mes);
            llPremio = itemView.findViewById(R.id.ll_premio);
            tvLabelPremio = itemView.findViewById(R.id.tv_label_premio);
            tvPremio = itemView.findViewById(R.id.tv_premio);
        }
    }
}
