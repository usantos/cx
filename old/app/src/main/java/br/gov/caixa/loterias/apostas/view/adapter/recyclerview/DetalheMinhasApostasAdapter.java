package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.RowItem;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.config.LotteryGridStyleConfig;
import br.gov.caixa.loterias.apostas.model.enums.RewardMatchMode;
import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.RowUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.mapper.LotteryMapper;

public class DetalheMinhasApostasAdapter extends RecyclerView.Adapter<DetalheMinhasApostasAdapter.ViewHolder> {

    public static final int ADAPTER_SIZE = 0;
    private final List<ItemMinhasApostasDTO> items;
    private final Context context;
    private EstiloModalidadeMKP estilo;

    private static final String CONST_WIDGET = Button.class.getName();

    public DetalheMinhasApostasAdapter(Context context, List<ItemMinhasApostasDTO> items) {
        this.context = context;
        this.items = items;
        validateIfOnlyOneItem(items);
        this.estilo = new EstiloModalidadeMKP(items.get(0).getModalidade());
    }

    private static void validateIfOnlyOneItem(List<ItemMinhasApostasDTO> items) {
        if (items.size() == 1 && items.get(0).isBolao()) {
            items.get(0).setExpanded(true);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minhas_apostas_accordion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemMinhasApostasDTO item = items.get(position);

        desabilitaTodos(holder);

        boolean isExpanded = item.isExpanded();
        handleAccessibility(items.size(), item, holder.itemView, position);

        if (isExpanded) {
            holder.itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());
                notifyItemChanged(position);
                delayedAnnounce(v, item);
            });
        }

        //holder.contentlayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivExpandCollapse.setRotation(isExpanded ? 180 : 0);
        holder.ivExpandCollapse.setColorFilter(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.setExpanded(isExpanded);


        //if (!item.isApurado()) {
        //holder.headerlayout.setVisibility(View.GONE);
        //holder.contentlayout.setVisibility(View.VISIBLE);
        desabilitaTodos(holder);

        isExpanded = item.isExpanded();

        //if(!item.isApurado()) {
        if (!item.isTemAcordeon()) {
            if (item.getNumerosSorteados() == null) {
                montaAcordeonCorpoSemResultado(holder, item);
            } else {
                montaAcordeonCorpo(holder, item);
            }
            return;
        }

        if (item.isBolao()) {
            montaAcordeonCabecaBolao(holder, position, item);
        } else {
            montaAcordeonCabeca(holder, position, item);
        }

        if (isExpanded) {
            if (item.getNumerosSorteados() == null) {
                montaAcordeonCorpoSemResultado(holder, item);
            } else {
                montaAcordeonCorpo(holder, item);
            }
        }
//        }
    }

    private static void delayedAnnounce(View v, ItemMinhasApostasDTO item) {
        v.postDelayed(() -> {
            v.announceForAccessibility(item.isExpanded() ? "Expandido" : "Recolhido");
        }, 100);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void desabilitaTodos(ViewHolder holder) {
        holder.clAcordeonCorpo.setVisibility(View.GONE);
        holder.clAcordeonCabeca.setVisibility(View.GONE);
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

    private void montaAcordeonCabeca(ViewHolder holder, int position, ItemMinhasApostasDTO item) {
        holder.clAcordeonCorpo.setVisibility(holder.isExpanded() ? View.VISIBLE : View.GONE);
        holder.ivExpandCollapse.setRotation(holder.isExpanded() ? 180 : 0);
        holder.ivExpandCollapse.setColorFilter(context.getColor(estilo.getCorFonteFundoEscuro()));

        //Cabecalho Accordion
        holder.clAcordeonCabeca.setVisibility(View.VISIBLE);
        holder.tvResultadoConcurso.setText(context.getString(R.string.resultado_concurso) + " " + item.getConcurso());
        holder.tvResultadoConcurso.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvLabelSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        if (item.getSituacao().equals(context.getString(R.string.label_concurso_nao_premiado)) || item.getSituacao().equals(context.getString(R.string.label_concurso_premio_pago))) {
            holder.tvSituacao.setText(item.getSituacao());
        } else {
            holder.tvSituacao.setText(ViewUtils.textCaixaSTDBold(context, context.getString(R.string.texto_caixaStd_bold, item.getSituacao())));
        }
        holder.tvSituacao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion2.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setVisibility(holder.isExpanded() ? View.GONE : View.VISIBLE);

        holder.clAcordeonCabeca.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            holder.setExpanded(item.isExpanded());
            notifyItemChanged(position);
            delayedAnnounce(v, item);
        });
    }

    private void montaAcordeonCabecaBolao(ViewHolder holder, int position, ItemMinhasApostasDTO item) {
        holder.clAcordeonCorpo.setVisibility(holder.isExpanded() ? View.VISIBLE : View.GONE);
        holder.ivExpandCollapse.setRotation(holder.isExpanded() ? 180 : 0);
        holder.ivExpandCollapse.setColorFilter(context.getColor(estilo.getCorFonteFundoEscuro()));

        //Cabecalho Accordion Bolao
        holder.clAcordeonCabeca.setVisibility(View.VISIBLE);
        holder.tvResultadoConcurso.setVisibility(View.INVISIBLE);
        holder.tvLabelSituacao.setVisibility(View.INVISIBLE);
        holder.tvSituacao.setVisibility(View.INVISIBLE);
        holder.tvApostaNumeroBolao.setVisibility(View.VISIBLE);
        holder.tvApostaNumeroBolao.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvApostaNumeroBolao.setText(context.getString(R.string.label_aposta_maiuscula) + " " + (position + 1));

        if (item.isSurpresinha()) {
            holder.tvApresentaSurpresinha.setVisibility(View.VISIBLE);
            holder.tvApresentaSurpresinha.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
            holder.ivApresentaSurpresinha.setVisibility(View.VISIBLE);
            //holder.ivApresentaSurpresinha.setImageDrawable(context.getDrawable(estilo.getCorFonteFundoEscuro()));
            holder.ivApresentaSurpresinha.setColorFilter(context.getColor(estilo.getCorFonteFundoEscuro()));
        }

        holder.vLinhaAccordion1.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion2.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.vLinhaAccordion1.setVisibility(holder.isExpanded() ? View.GONE : View.VISIBLE);

        holder.clAcordeonCabeca.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            holder.setExpanded(item.isExpanded());
            notifyItemChanged(position);
            delayedAnnounce(v, item);
        });
    }

    private void montaAcordeonCorpo(ViewHolder holder, ItemMinhasApostasDTO item) {
        //desabilitaTodos(holder);

        //holder.clAcordeonCorpo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.clAcordeonCorpo.setVisibility(View.VISIBLE);

        //Todos
        holder.tvLabelNumerosSorteados.setVisibility(View.VISIBLE);
        holder.rvNumberSortedos.setVisibility(View.VISIBLE);
        holder.vDivisorTrevo.setVisibility(View.VISIBLE);
        holder.tvLabelMeusNumeros.setVisibility(View.VISIBLE);
        holder.rvMeusNumeros.setVisibility(View.VISIBLE);

        holder.tvLabelNumerosSorteados.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvLabelMeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

        holder.ivIconeCentro.setImageDrawable(context.getDrawable(estilo.getTrevoFundoClaro()));
        holder.vLinhaAccordion2.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));

        //Acertos
        holder.llAcertos.setVisibility(View.VISIBLE);
        holder.tvLabelAcertos.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvAcertos.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        holder.tvAcertos.setText(montaDescricaoAcertos(item)[0]);

        //Accessibility
        holder.llAcertos.setContentDescription(holder.tvLabelAcertos.getText() + " " + holder.tvAcertos.getText());

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
                holder.llMesSorte.setContentDescription(holder.tvLabelTimeMesSorteado.getText() + " " + item.getTimeMesSorteado());
                holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                holder.tvLabelMeuTimeMes.setText(R.string.label_time_do_coracao);
                holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.llCoracao.setContentDescription(holder.tvLabelMeuTimeMes.getText() + " " + item.getSeuTimeMes());
                holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                setupLotteryGrid(holder.rvNumberSortedos, 6, item.getNumerosSorteados(), null, LotteryGridStyleConfig.noShape(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        item.getNumerosSorteados(), LotteryGridStyleConfig.circular(context, estilo));
                break;
            case DIA_DE_SORTE:
                holder.tvLabelTimeMesSorteado.setVisibility(View.VISIBLE);
                holder.tvTimeMesSorteado.setVisibility(View.VISIBLE);
                holder.tvLabelMeuTimeMes.setVisibility(View.VISIBLE);
                holder.tvMeuTimeMes.setVisibility(View.VISIBLE);

                holder.tvLabelTimeMesSorteado.setText(R.string.label_mes_sorteado_);
                holder.tvLabelTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.llMesSorte.setContentDescription(holder.tvLabelTimeMesSorteado.getText() + " " + item.getTimeMesSorteado());
                holder.tvTimeMesSorteado.setText(item.getTimeMesSorteado());
                holder.tvTimeMesSorteado.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                holder.tvLabelMeuTimeMes.setText(R.string.label_mes_de_sorte);
                holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.llCoracao.setContentDescription(holder.tvLabelMeuTimeMes.getText() + " " + item.getSeuTimeMes());
                holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                setupLotteryGrid(holder.rvNumberSortedos, 6, item.getNumerosSorteados(),
                        null, LotteryGridStyleConfig.noShape(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        item.getNumerosSorteados(), LotteryGridStyleConfig.circular(context, estilo));
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

                //Accessibility
                holder.llAcertos1.setContentDescription(holder.tvLabelAcertos1.getText() + " " + holder.tvAcertos1.getText());

                setupLotteryGrid(holder.rvNumberSortedos, 6, item.getNumerosSorteados(), null, LotteryGridStyleConfig.noShape(context, estilo));
                setupLotteryGrid(holder.rvNumberSortedos2, 6, item.getNumerosSorteados2(), null, LotteryGridStyleConfig.noShape(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(), item.getNumerosSorteados(), LotteryGridStyleConfig.circular(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros2, 6, item.getSeusNumeros2(), item.getNumerosSorteados2(), LotteryGridStyleConfig.circular(context, estilo));
                break;
            case SUPER_7:
                holder.rvSuper7Sorteados.setVisibility(View.VISIBLE);
                holder.rvSuper7MeusNumeros.setVisibility(View.VISIBLE);

                ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));

                setupLotteryGrid(holder.rvSuper7Sorteados, 7, listCabecalho1a7, null, LotteryGridStyleConfig.rectangular7(context, estilo));
                setupLotteryGrid(holder.rvNumberSortedos, item.getNumerosSorteados(), null, LotteryGridStyleConfig.rectangular(context, estilo));
                setupLotteryGrid(holder.rvSuper7MeusNumeros, 7, listCabecalho1a7, null, LotteryGridStyleConfig.rectangular7(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, item.getSeusNumeros(), item.getNumerosSorteados(), LotteryGridStyleConfig.circular7(context, estilo));
                break;
            case MAIS_MILIONARIA:
                holder.tvLabelNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                holder.rvNumberSortedos2.setVisibility(View.VISIBLE);
                holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                holder.rvMeusNumeros2.setVisibility(View.VISIBLE);

                holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvLabelNumerosSegundoSorteio.setText(R.string.label_trevos_sorteados);
                holder.tvLabelNumerosSegundoSorteio.setContentDescription("Trevos sorteados");
                ViewCompat.setAccessibilityHeading(holder.tvLabelNumerosSegundoSorteio, true);

                holder.tvLabelMeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvLabelMeusNumerosSegundoSorteio.setText(R.string.label_trevos);
                holder.tvLabelMeusNumerosSegundoSorteio.setContentDescription("Trevos");
                ViewCompat.setAccessibilityHeading(holder.tvLabelMeusNumerosSegundoSorteio, true);

                setupLotteryGrid(holder.rvNumberSortedos, 6, item.getNumerosSorteados(),
                        null, LotteryGridStyleConfig.noShape(context, estilo));

                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        item.getNumerosSorteados(), LotteryGridStyleConfig.circular(context, estilo));

                setupLotteryGrid(holder.rvNumberSortedos2, 2, item.getTrevosSorteados(),
                        item.getTrevosSorteados(), LotteryGridStyleConfig.trevo(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros2, 2, item.getSeusTrevos(),
                        item.getTrevosSorteados(), LotteryGridStyleConfig.trevo(context, estilo));
                break;
            case LOTOFACIL:
            case QUINA:
            case LOTOMANIA:
            case MEGA_SENA:
            default:
                setupLotteryGrid(holder.rvNumberSortedos, 6, item.getNumerosSorteados(), null, LotteryGridStyleConfig.noShape(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(), item.getNumerosSorteados(), LotteryGridStyleConfig.circular(context, estilo));
                break;
        }
    }

    private void montaAcordeonCorpoSemResultado(ViewHolder holder, ItemMinhasApostasDTO item) {
        //desabilitaTodos(holder);

        //holder.clAcordeonCorpo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.clAcordeonCorpo.setVisibility(View.VISIBLE);

        holder.tvLabelMeusNumeros.setVisibility(View.VISIBLE);
        holder.rvMeusNumeros.setVisibility(View.VISIBLE);

        holder.tvLabelMeusNumeros.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
        //holder.vLinhaAccordion2.setBackgroundColor(context.getColor(estilo.getCorFonteFundoEscuro()));

        switch (item.getModalidade()) {
            case TIMEMANIA:
                holder.tvLabelMeuTimeMes.setVisibility(View.VISIBLE);
                holder.tvMeuTimeMes.setVisibility(View.VISIBLE);

                holder.tvLabelMeuTimeMes.setText(R.string.label_time_do_coracao);
                holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.llCoracao.setContentDescription(holder.tvLabelMeuTimeMes.getText() + " " + item.getSeuTimeMes());
                ViewCompat.setAccessibilityHeading(holder.llCoracao, true);
                holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        null, LotteryGridStyleConfig.circular(context, estilo));
                break;

            case DIA_DE_SORTE:
                holder.tvLabelMeuTimeMes.setVisibility(View.VISIBLE);
                holder.tvMeuTimeMes.setVisibility(View.VISIBLE);

                holder.tvLabelMeuTimeMes.setText(R.string.label_mes_de_sorte);
                holder.tvLabelMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.llCoracao.setContentDescription(holder.tvLabelMeuTimeMes.getText() + " " + item.getSeuTimeMes());
                ViewCompat.setAccessibilityHeading(holder.llCoracao, true);
                holder.tvMeuTimeMes.setText(item.getSeuTimeMes());
                holder.tvMeuTimeMes.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        null, LotteryGridStyleConfig.circular(context, estilo));
                break;

            case DUPLA_SENA:
                holder.tvLabelMeusNumerosPrimeiroSorteio.setVisibility(View.VISIBLE);
                holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                holder.rvMeusNumeros2.setVisibility(View.VISIBLE);

                //holder.tvLabelNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                //holder.tvLabelNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvLabelMeusNumerosPrimeiroSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvLabelMeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));

                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(), null, LotteryGridStyleConfig.circular(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros2, 6, item.getSeusNumeros2(), null, LotteryGridStyleConfig.circular(context, estilo));
                break;

            case SUPER_7:
                holder.rvSuper7MeusNumeros.setVisibility(View.VISIBLE);

                ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));

                setupLotteryGrid(holder.rvSuper7MeusNumeros, 7, listCabecalho1a7, null, LotteryGridStyleConfig.rectangular7(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros, 7, item.getSeusNumeros(), null, LotteryGridStyleConfig.circular7(context, estilo));
                break;

            case MAIS_MILIONARIA:
                holder.tvLabelMeusNumerosSegundoSorteio.setVisibility(View.VISIBLE);
                holder.rvMeusNumeros2.setVisibility(View.VISIBLE);

                holder.tvLabelMeusNumerosSegundoSorteio.setTextColor(context.getColor(estilo.getCorFonteFundoEscuro()));
                holder.tvLabelMeusNumerosSegundoSorteio.setText(R.string.label_trevos);

                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(),
                        null, LotteryGridStyleConfig.circular(context, estilo));
                setupLotteryGrid(holder.rvMeusNumeros2, 2, item.getSeusTrevos(),
                        null, LotteryGridStyleConfig.trevo(context, estilo));
                break;

            case LOTOFACIL:
            case QUINA:
            case LOTOMANIA:
            case MEGA_SENA:
            default:
                setupLotteryGrid(holder.rvMeusNumeros, 6, item.getSeusNumeros(), null, LotteryGridStyleConfig.circular(context, estilo));
                break;
        }
    }

    private void handleAccessibility(int size, ItemMinhasApostasDTO dto, View itemView, int position) {
        //Aciona acessibilidade como expandable somente se houver mais de um item no adapter
        if (size > ADAPTER_SIZE) {
            ViewCompat.setAccessibilityDelegate(itemView, new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(@NotNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(host, info);

                    info.setClassName(CONST_WIDGET);

                    String shouldAdd = "";
                    if (dto.isSurpresinha()) {
                        shouldAdd = "Surpresinha";
                    }

                    String prefixo = "Aposta: " + Integer.valueOf(position + 1).toString() + " de " + getItemCount() + " " + shouldAdd;
                    if (!dto.isBolao()) {
                        prefixo = "Resultado do concurso: " + dto.getConcurso() + " situação " + dto.getSituacao() + " " + Integer.valueOf(position + 1).toString() + " de " + getItemCount();
                    }

                    if (dto.isExpanded()) {
                        info.setContentDescription(prefixo + context.getString(R.string.expandablelistview_expanded_text));
                    } else {
                        info.setContentDescription(prefixo + context.getString(R.string.expandablelistview_retracted_text));
                    }
                }
            });
        }
    }

    //Aqui adiciona colunas
    private void setupLotteryGrid(RecyclerView recyclerView, int columns, List<String> mNumbers, List<String> mSortedNumbers, LotteryGridStyleConfig config) {
        setupRecyclerview(recyclerView);

        List<RowItem> rowItems = buildUiModels(columns, mNumbers, mSortedNumbers, config.isAnnounceColumns(), RewardMatchMode.ANY_POSITION);
        NumerosGridAdapterBackup adapter = new NumerosGridAdapterBackup(config);

        adapter.submitList(rowItems);
        recyclerView.setAdapter(adapter);
    }

    //Somente chamado na supersete, onde há uma diferença no comportamento
    private void setupLotteryGrid(RecyclerView recyclerView, List<String> mNumbers, List<String> mSortedNumbers, LotteryGridStyleConfig config) {
        setupRecyclerview(recyclerView);

        List<RowItem> rowItems = buildUiModels(7, mNumbers, mSortedNumbers, config.isAnnounceColumns(), RewardMatchMode.SAME_COLUMN_REPEATING);
        NumerosGridAdapterBackup adapter = new NumerosGridAdapterBackup(config);

        adapter.submitList(rowItems);
        recyclerView.setAdapter(adapter);
    }

    private void setupRecyclerview(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setNestedScrollingEnabled(false);
    }

    private static List<RowItem> buildUiModels(int columns, List<String> numbers, List<String> rewarded, boolean shouldAddColumn, RewardMatchMode matchMode) {
        List<LotteryNumberUiModel> uiModels = LotteryMapper.buildUiList(numbers, rewarded, matchMode);
        List<List<LotteryNumberUiModel>> rows = RowUtils.chunkBySpan(uiModels, columns);
        List<LotteryNumberUiModel> normalized = Collections.emptyList();
        List<RowItem> rowItems = new ArrayList<>();

        for (List<LotteryNumberUiModel> row : rows) {
            if (shouldAddColumn) {
                normalized = addColumnToAccessibility(row);
            }

            rowItems.add(new RowItem(shouldAddColumn ? normalized : row));
        }

        return rowItems;
    }

    private static List<LotteryNumberUiModel> addColumnToAccessibility(List<LotteryNumberUiModel> row) {
        List<LotteryNumberUiModel> list = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            LotteryNumberUiModel item = row.get(i);

            list.add(new LotteryNumberUiModel(
                    item.getNumber(),
                    item.isRewarded(),
                    "Coluna " + (i + 1) + ", " + item.getAccessibility()
            ));
        }
        return list;
    }

    private String[] montaDescricaoAcertos(ItemMinhasApostasDTO item) {
        int numerosAcertados = 0;
        String acertos = context.getString(R.string.label_acertos_nenhum);
        String acertosDuplaSena = context.getString(R.string.label_acertos_nenhum);

        List<String> numerosSorteados = item.getNumerosSorteados();
        List<String> seusNumeros = item.getSeusNumeros();

        for (int i = 0; i < seusNumeros.size(); i++) {
            String numero = seusNumeros.get(i);
            if (numero != null) {
                if (item.getModalidade() == ModalidadeEnum.SUPER_7) {
                    int indice = i % 7;
                    if (numero != null && numerosSorteados.get(indice) != null && numerosSorteados.get(indice).equals(numero)) {
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
            acertos = numerosAcertados + " número" + (numerosAcertados > 1 ? "s" : "");
        }

        switch (item.getModalidade()) {
            case TIMEMANIA:
                if (item.getSeuTimeMes().equalsIgnoreCase(item.getTimeMesSorteado())) {
                    acertos += " + Time do coração";
                }
                break;
            case DIA_DE_SORTE:
                if (item.getSeuTimeMes().equalsIgnoreCase(item.getTimeMesSorteado())) {
                    acertos += " + Mes da Sorte";
                }
                break;
            case MAIS_MILIONARIA:
                int trevosAcertados = 0;
                List<String> trevosSorteados = item.getTrevosSorteados();
                List<String> seusTrevos = item.getSeusTrevos();
                for (int i = 0; i < seusTrevos.size(); i++) {
                    String numero = seusTrevos.get(i);
                    if (numero != null && trevosSorteados.contains(numero)) {
                        trevosAcertados++;
                    }
                }
                if (trevosAcertados > 0) {
                    acertos += " + " + trevosAcertados + " trevo" + (trevosAcertados > 1 ? "s" : "");
                }
                break;
            case DUPLA_SENA:
                int duplaAcertados = 0;
                List<String> numerosSorteados2 = item.getNumerosSorteados2();
                List<String> seusNumeros2 = item.getSeusNumeros2();
                for (int i = 0; i < seusNumeros2.size(); i++) {
                    String numero = seusNumeros2.get(i);
                    if (numero != null && numerosSorteados2.contains(numero)) {
                        duplaAcertados++;
                    }
                }
                acertosDuplaSena = acertos;
                if (duplaAcertados > 0) {
                    acertos = duplaAcertados + " número" + (duplaAcertados > 1 ? "s" : "");
                }
                break;
            default:
                break;
        }

        return new String[]{acertos, acertosDuplaSena};
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvApostaNumeroBolao, tvApresentaSurpresinha, tvResultadoConcurso, tvLabelSituacao, tvSituacao, tvLabelNumerosSorteados, tvLabelNumerosPrimeiroSorteio, tvLabelNumerosSegundoSorteio;
        TextView tvLabelTimeMesSorteado, tvTimeMesSorteado, tvLabelMeusNumeros, tvLabelMeusNumerosPrimeiroSorteio, tvLabelMeusNumerosSegundoSorteio;
        TextView tvLabelMeuTimeMes, tvMeuTimeMes;
        TextView tvLabelPremio, tvPremio, tvLabelAcertos, tvLabelAcertos1, tvAcertos, tvAcertos1;
        ImageView ivExpandCollapse, ivIconeCentro;
        LinearLayout clAcordeonCorpo, llPremio, llAcertos, llAcertos1;
        LinearLayout llCoracao, llMesSorte;
        ConstraintLayout clAcordeonCabeca;
        RecyclerView rvNumberSortedos, rvNumberSortedos2, rvMeusNumeros, rvMeusNumeros2, rvSuper7Sorteados, rvSuper7MeusNumeros;
        View vLinhaAccordion1, vLinhaAccordion2, vDivisorTrevo;
        ImageView ivApresentaSurpresinha;
        private boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApostaNumeroBolao = itemView.findViewById(R.id.tv_aposta_numero_bolao);
            tvApresentaSurpresinha = itemView.findViewById(R.id.tv_apresenta_surpresinha);
            ivApresentaSurpresinha = itemView.findViewById(R.id.ivApresentaSurpresinha);
            tvResultadoConcurso = itemView.findViewById(R.id.tv_resultado_concurso);
            tvLabelSituacao = itemView.findViewById(R.id.tv_label_situacao);
            tvSituacao = itemView.findViewById(R.id.tv_situacao);
            ivExpandCollapse = itemView.findViewById(R.id.iv_expand_collapse);
            clAcordeonCorpo = itemView.findViewById(R.id.cl_acordeon_corpo);
            clAcordeonCabeca = itemView.findViewById(R.id.cl_acordeon_cabeca);
            vLinhaAccordion1 = itemView.findViewById(R.id.v_linha_accordion1);
            vLinhaAccordion2 = itemView.findViewById(R.id.v_linha_accordion2);
            ivIconeCentro = itemView.findViewById(R.id.icone_centro);
            llCoracao = itemView.findViewById(R.id.ll_coracao);
            llMesSorte = itemView.findViewById(R.id.ll_mes_sorte);

            tvLabelNumerosSorteados = itemView.findViewById(R.id.tv_label_numeros_sorteados);
            ViewCompat.setAccessibilityHeading(tvLabelNumerosSorteados, true);
            rvNumberSortedos = itemView.findViewById(R.id.rv_numbers_sorteados);
            rvSuper7Sorteados = itemView.findViewById(R.id.rv_super7_sorteados);
            tvLabelNumerosPrimeiroSorteio = itemView.findViewById(R.id.tv_label_numeros_primeiro_sorteio);
            ViewCompat.setAccessibilityHeading(tvLabelNumerosPrimeiroSorteio, true);
            tvLabelNumerosSegundoSorteio = itemView.findViewById(R.id.tv_label_numeros_segundo_sorteio);
            ViewCompat.setAccessibilityHeading(tvLabelNumerosSegundoSorteio, true);
            rvNumberSortedos2 = itemView.findViewById(R.id.rv_numbers_sorteados2);
            tvLabelTimeMesSorteado = itemView.findViewById(R.id.tv_label_time_mes_sorteado);
            tvTimeMesSorteado = itemView.findViewById(R.id.tv_time_mes_sorteado);
            tvLabelMeusNumeros = itemView.findViewById(R.id.tv_label_meus_numeros);
            ViewCompat.setAccessibilityHeading(tvLabelMeusNumeros, true);
            rvMeusNumeros = itemView.findViewById(R.id.rv_meus_numeros);
            rvSuper7MeusNumeros = itemView.findViewById(R.id.rv_super7_meus_numeros);
            tvLabelMeusNumerosPrimeiroSorteio = itemView.findViewById(R.id.tv_label_meus_numeros_primeiro_sorteio);
            ViewCompat.setAccessibilityHeading(tvLabelMeusNumerosPrimeiroSorteio, true);
            tvLabelMeusNumerosSegundoSorteio = itemView.findViewById(R.id.tv_label_meus_numeros_segundo_sorteio);
            ViewCompat.setAccessibilityHeading(tvLabelMeusNumerosSegundoSorteio, true);
            rvMeusNumeros2 = itemView.findViewById(R.id.rv_meus_numeros2);
            tvLabelMeuTimeMes = itemView.findViewById(R.id.tv_label_meu_time_mes);
            ViewCompat.setAccessibilityHeading(tvLabelMeuTimeMes, true);
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

        public void setExpanded(boolean isExpanded) {
            this.isExpanded = isExpanded;
        }

        public boolean isExpanded() {
            return isExpanded;
        }
    }

}
