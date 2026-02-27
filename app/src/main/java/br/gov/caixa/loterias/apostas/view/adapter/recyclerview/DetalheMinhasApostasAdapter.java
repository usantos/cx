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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SituacaoCompraUtil;
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

            holder.tvLabelMeusNumeros.setVisibility(View.VISIBLE);
            holder.rvMeusNumeros.setVisibility(View.VISIBLE);
            holder.tvLabelMeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

            setupGridNumeros(holder.rvMeusNumeros, Shape.CIRCULO, 6,  item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()));
            return;
        }

        //Cabecalho Accordion
        holder.headerlayout.setVisibility(View.VISIBLE);
        holder.tvResultadoConcurso.setText(context.getString(R.string.resultado_concurso) + " " + item.getConcurso());
        holder.tvResultadoConcurso.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvLabelSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        if (item.getSituacao().equals(context.getString(R.string.label_concurso_nao_premiado))) {
            holder.tvSituacao.setText(item.getSituacao());
        } else {
            holder.tvSituacao.setText(ViewUtils.textCaixaSTDBold(context, context.getString(R.string.texto_caixaStd_bold, item.getSituacao())));
        }
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
            holder.vDivisorTrevo.setVisibility(View.VISIBLE);
            holder.tvLabelMeusNumeros.setVisibility(View.VISIBLE);
            holder.rvMeusNumeros.setVisibility(View.VISIBLE);

            holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
            holder.tvLabelMeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

            holder.ivIconeCentro.setImageDrawable(context.getDrawable(estilo.getTrevoFundoClaro()));

            //Acertos
            holder.llAcertos.setVisibility(View.VISIBLE);
            holder.tvLabelAcertos.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
            holder.tvAcertos.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
            holder.tvAcertos.setText(montaDescricaoAcertos(item)[0]);

            if (item.getValorPremio() != null) {
                holder.llPremio.setVisibility(View.VISIBLE);
                holder.tvLabelPremio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvPremio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvPremio.setText(ViewUtils.getMoedaFormat(item.getValorPremio()));
            }

            switch (item.getModalidade()) {
                case TIMEMANIA:
                    holder.tvLabelTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvLabelMeuTimeMes.setVisibility(View.VISIBLE);
                    holder.tvMeuTimeMes.setVisibility(View.VISIBLE);

                    holder.tvLabelTimeMesSorteado.setText(R.string.label_time_sorteado);
                    holder.tvLabelTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                    holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    holder.tvLabelMeuTimeMes.setText(R.string.label_time_do_coracao);
                    holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                    holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case DIA_DE_SORTE:
                    holder.tvLabelTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                    holder.tvLabelMeuTimeMes.setVisibility(View.VISIBLE);
                    holder.tvMeuTimeMes.setVisibility(View.VISIBLE);

                    holder.tvLabelTimeMesSorteado.setText(R.string.label_mes_sorteado_);
                    holder.tvLabelTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                    holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    holder.tvLabelMeuTimeMes.setText(R.string.label_mes_de_sorte);
                    holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                    holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case DUPLA_SENA:
                    holder.tvLabelNumerosPrimeiroSorteio.setVisibility(View.VISIBLE);
                    holder.tvLabelNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                    holder.tvLabelMeusNumerosPrimeiroSorteio.setVisibility(View.VISIBLE);
                    holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvMeusNumeros2.setVisibility(View.VISIBLE);
                    holder.llAcertos1.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelMeusNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelMeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                    //Acertos
                    holder.tvLabelAcertos1.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvAcertos1.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvAcertos1.setText(montaDescricaoAcertos(item)[1]);

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumeros(holder.rvNumberSortedos2, Shape.NO_SHAPE, 6, item.getNumerosSorteados2(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros2, Shape.CIRCULO, 6, item.getSeusNumeros2(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados2(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case SUPER_7:
                    holder.rvSuper7Sorteados.setVisibility(View.VISIBLE);
                    holder.rvSuper7MeusNumeros.setVisibility(View.VISIBLE);

                    ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1","2","3","4","5","6","7"));

                    setupGridNumeros(holder.rvSuper7Sorteados, Shape.RETANGULO, 7, listCabecalho1a7, context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorClara()), context.getColor(estilo.getCorClara()));
                    setupGridNumeros(holder.rvNumberSortedos, Shape.RETANGULO, 7, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumeros(holder.rvSuper7MeusNumeros, Shape.RETANGULO, 7, listCabecalho1a7, context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorClara()), context.getColor(estilo.getCorClara()));
                    setupGridNumerosWithIntersectionS7(holder.rvMeusNumeros, Shape.CIRCULO, 7, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));
                    break;
                case MAIS_MILIONARIA:
                    holder.tvLabelNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                    holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                    holder.rvMeusNumeros2.setVisibility(View.VISIBLE);

                    holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelNumerosSegundoSorteio.setText(R.string.label_trevos_sorteados);

                    holder.tvLabelMeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                    holder.tvLabelMeusNumerosSegundoSorteio.setText(R.string.label_trevos);

                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);

                    setupGridNumerosWithIntersection(holder.rvMeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
                            item.getNumerosSorteados(), context.getColor(estilo.getCorEscura()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()));

                    setupGridNumerosWithIntersection(holder.rvNumberSortedos2, Shape.TREVO, 2, item.getTrevosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()),  Color.TRANSPARENT,  Color.TRANSPARENT,
                            item.getTrevosSorteados(), context.getColor(estilo.getCorEscura()),  Color.TRANSPARENT,  Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros2, Shape.TREVO, 2, item.getSeusTrevos(), context.getColor(estilo.getCorFonteFundoEscuro()),  Color.TRANSPARENT,  Color.TRANSPARENT,
                            item.getTrevosSorteados(), context.getColor(estilo.getCorEscura()),  Color.TRANSPARENT,  Color.TRANSPARENT);
                    break;
                case LOTOFACIL:
                case QUINA:
                case LOTOMANIA:
                case MEGA_SENA:
                default:
                    setupGridNumeros(holder.rvNumberSortedos, Shape.NO_SHAPE, 6, item.getNumerosSorteados(), context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);
                    setupGridNumerosWithIntersection(holder.rvMeusNumeros, Shape.CIRCULO, 6, item.getSeusNumeros(), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorFonteFundoEscuro()), context.getColor(estilo.getCorEscura()),
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
        holder.tvLabelMeusNumeros.setVisibility(View.GONE);
        holder.rvMeusNumeros.setVisibility(View.GONE);
        holder.rvSuper7MeusNumeros.setVisibility(View.GONE);
        holder.tvLabelMeusNumerosPrimeiroSorteio.setVisibility(View.GONE);
        holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.GONE);
        holder.rvMeusNumeros2.setVisibility(View.GONE);
        holder.tvLabelMeuTimeMes.setVisibility(View.GONE);
        holder.tvMeuTimeMes.setVisibility(View.GONE);
        holder.llAcertos1.setVisibility(View.GONE);
        holder.llAcertos.setVisibility(View.GONE);
        holder.llPremio.setVisibility(View.GONE);
        holder.vDivisorTrevo.setVisibility(View.GONE);
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

    private String[] montaDescricaoAcertos(ItemMinhasApostasDTO item) {
        int numerosAcertados = 0;
        String acertos = context.getString(R.string.label_acertos_nenhum);
        String acertosDuplaSena = context.getString(R.string.label_acertos_nenhum);

        List<String> numerosSorteados = item.getNumerosSorteados();
        List<String> seusNumeros = item.getSeusNumeros();

        for (int i=0; i<seusNumeros.size(); i++) {
            String numero = seusNumeros.get(i);
            if (numero != null) {
                if (item.getModalidade() == ModalidadeEnum.SUPER_7) {
                    if (numero != null && numerosSorteados.get(i) != null && numerosSorteados.get(i).equals(numero)) {
                        numerosAcertados++;
                    }
                } else {
                    if (numero != null && numerosSorteados.contains(numero)) {
                        numerosAcertados++;
                    }
                }
            }
        }

        if (numerosAcertados > 0) {
            acertos = numerosAcertados + " número" + (numerosAcertados>1 ? "s" : "");
        }

        switch (item.getModalidade()) {
            case TIMEMANIA:
                item.getSeuTimeMes().equalsIgnoreCase(item.getSeuTimeMes());
                acertos += " + Time do coração";
                break;
            case DIA_DE_SORTE:
                item.getSeuTimeMes().equalsIgnoreCase(item.getSeuTimeMes());
                acertos += " + Mes da Sorte";
                break;
            case MAIS_MILIONARIA:
                int trevosAcertados = 0;
                List<String> trevosSorteados = item.getTrevosSorteados();
                List<String> seusTrevos = item.getSeusTrevos();
                for (int i=0; i<seusTrevos.size(); i++) {
                    String numero = seusTrevos.get(i);
                    if (numero != null && trevosSorteados.contains(numero)) {
                        trevosAcertados++;
                    }
                }
                if (trevosAcertados > 0) {
                    acertos += trevosAcertados + " trevo" + (trevosAcertados>1 ? "s" : "");
                }
                break;
            case DUPLA_SENA:
                int duplaAcertados = 0;
                List<String> numerosSorteados2 = item.getNumerosSorteados2();
                List<String> seusNumeros2 = item.getSeusNumeros2();
                for (int i=0; i<seusNumeros2.size(); i++) {
                    String numero = seusNumeros2.get(i);
                    if (numero != null && numerosSorteados2.contains(numero)) {
                        duplaAcertados++;
                    }
                }
                acertosDuplaSena = acertos;
                if (duplaAcertados > 0) {
                    acertos = duplaAcertados + " número" + (duplaAcertados>1 ? "s" : "");
                }
                break;
            default:
                break;
        }

        return new String[] {acertos, acertosDuplaSena};
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResultadoConcurso, tvLabelSituacao, tvSituacao, tvLabelNumerosSorteados, tvLabelNumerosPrimeiroSorteio, tvLabelNumerosSegundoSorteio;
        TextView tvLabelTimeMesSorteado, tvTimeMesSorteado, tvLabelMeusNumeros, tvLabelMeusNumerosPrimeiroSorteio, tvLabelMeusNumerosSegundoSorteio;
        TextView tvLabelMeuTimeMes, tvMeuTimeMes;
        TextView tvLabelPremio, tvPremio, tvLabelAcertos, tvLabelAcertos1, tvAcertos, tvAcertos1;
        ImageView ivExpandCollapse, ivIconeCentro;
        LinearLayout contentlayout, llPremio, llAcertos, llAcertos1;
        ConstraintLayout headerlayout;
        RecyclerView rvNumberSortedos, rvNumberSortedos2, rvMeusNumeros, rvMeusNumeros2, rvSuper7Sorteados, rvSuper7MeusNumeros;
        View vLinhaAccordion1, vLinhaAccordion2, vDivisorTrevo;

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
            tvLabelMeusNumeros = itemView.findViewById(R.id.tv_label_meus_numeros);
            rvMeusNumeros = itemView.findViewById(R.id.rv_meus_numeros);
            rvSuper7MeusNumeros = itemView.findViewById(R.id.rv_super7_meus_numeros);
            tvLabelMeusNumerosPrimeiroSorteio = itemView.findViewById(R.id.tv_label_meus_numeros_primeiro_sorteio);
            tvLabelMeusNumerosSegundoSorteio = itemView.findViewById(R.id.tv_label_meus_numeros_segundo_sorteio);
            rvMeusNumeros2 = itemView.findViewById(R.id.rv_meus_numeros2);
            tvLabelMeuTimeMes = itemView.findViewById(R.id.tv_label_meu_time_mes);
            tvMeuTimeMes = itemView.findViewById(R.id.tv_meu_time_mes);
            llAcertos1 = itemView.findViewById(R.id.ll_acertos1);
            tvLabelAcertos1 = itemView.findViewById(R.id.tv_label_acertos1);
            tvAcertos1 = itemView.findViewById(R.id.tv_acertos1);
            llAcertos = itemView.findViewById(R.id.ll_acertos);
            tvLabelAcertos = itemView.findViewById(R.id.tv_label_acertos);
            tvAcertos = itemView.findViewById(R.id.tv_acertos);
            llPremio = itemView.findViewById(R.id.ll_premio);
            tvLabelPremio = itemView.findViewById(R.id.tv_label_premio);
            tvPremio = itemView.findViewById(R.id.tv_premio);
            vDivisorTrevo = itemView.findViewById(R.id.divisor_trevo);
        }
    }
}
