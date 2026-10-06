package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.holder.LotecaHolder;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;

public class LotecaAdapter extends RecyclerView.Adapter<LotecaHolder> {

    private final List<ParametroPartida> listaPartidas;
    private final ParametroJogoDTO parametroJogo;
    private final LocateAdapterListener listener;
    private RecyclerView recyclerView;

    public LotecaAdapter(ParametroJogoDTO parametroJogo, LocateAdapterListener listener) {
        this.parametroJogo = parametroJogo;
        this.listaPartidas = parametroJogo != null && parametroJogo.getPartidas() != null
                ? parametroJogo.getPartidas()
                : new ArrayList<>();
        this.listener = listener;
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public LotecaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_loteca_novo, parent, false);
        return new LotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotecaHolder holder, int position) {
        ParametroPartida item = listaPartidas.get(position);
        Context context = holder.itemView.getContext();

        holder.bind(item, position);

        configurarClique(holder.timeUm, holder, item, context, TipoSelecao.TIME_UM);
        configurarClique(holder.timeDois, holder, item, context, TipoSelecao.TIME_DOIS);
        configurarClique(holder.empate, holder, item, context, TipoSelecao.EMPATE);
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    private void configurarClique(
            View view,
            LotecaHolder holder,
            ParametroPartida item,
            Context context,
            TipoSelecao tipoSelecao
    ) {
        view.setOnClickListener(v -> {
            if (!verificaAberto(context)) {
                return;
            }

            boolean resultadoJaSelecionado =
                    isResultadoSelecionado(item, tipoSelecao);

            if (!resultadoJaSelecionado
                    && listener != null
                    && !listener.simulaVerificaPodeAdicionar(item)) {
                listener.onLimitePalpitesLotecaAtingido();
                return;
            }

            if (!resultadoJaSelecionado
                    && listener != null
                    && !listener.podeAdicionarResultadoLoteca()) {
                listener.onLimitePalpitesLotecaAtingido();
                return;
            }

            alternarSelecao(item, tipoSelecao);
            holder.bindEstadoVisual(context, item);

            boolean combinacaoAceita = notificarClique(item);

            if (!combinacaoAceita) {
                // A combinacao resultante nao e valida para a modalidade;
                // desfaz a mesma alternancia aplicada acima (o toggle e
                // simetrico) e restaura o visual ao estado anterior.
                alternarSelecao(item, tipoSelecao);
                holder.bindEstadoVisual(context, item);
            }
        });
    }

    private boolean notificarClique(ParametroPartida item) {
        return listener == null || listener.clickEquipeLoteca(item);
    }

    private boolean verificaAberto(Context context) {
        if (parametroJogo != null
                && parametroJogo.getConcurso() != null
                && parametroJogo.getConcurso().getAberto() != null
                && !parametroJogo.getConcurso().getAberto()) {

            DialogUtils.dialogEntendi(
                    context,
                    context.getString(R.string.msg_conc_nao_aberto)
                            .replace(
                                    "{num_concurso}",
                                    parametroJogo.getConcurso().getNumero().toString()
                            )
            );
            return false;
        }
        return true;
    }

    private void alternarSelecao(ParametroPartida item, TipoSelecao tipoSelecao) {
        item.setQtdItensSelecionadosAnterior(item.getQtdItensSelecionados());

        switch (tipoSelecao) {
            case TIME_UM:
                toggleTimeUm(item);
                break;
            case TIME_DOIS:
                toggleTimeDois(item);
                break;
            case EMPATE:
                toggleEmpate(item);
                break;
        }
    }

    private void toggleTimeUm(ParametroPartida item) {
        if (item.getEquipe1().isSelecionado()) {
            item.getEquipe1().setSelecionado(false);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() - 1);

            if (!item.isEmpate() && !item.getEquipe2().isSelecionado()) {
                item.setSelecionado(false);
            }
        } else {
            item.getEquipe1().setSelecionado(true);
            item.setSelecionado(true);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() + 1);
        }
    }

    private void toggleTimeDois(ParametroPartida item) {
        if (item.getEquipe2().isSelecionado()) {
            item.getEquipe2().setSelecionado(false);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() - 1);

            if (!item.isEmpate() && !item.getEquipe1().isSelecionado()) {
                item.setSelecionado(false);
            }
        } else {
            item.getEquipe2().setSelecionado(true);
            item.setSelecionado(true);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() + 1);
        }
    }

    private void toggleEmpate(ParametroPartida item) {
        if (item.isEmpate()) {
            item.setEmpate(false);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() - 1);

            if (!item.getEquipe1().isSelecionado() && !item.getEquipe2().isSelecionado()) {
                item.setSelecionado(false);
            }
        } else {
            item.setEmpate(true);
            item.setSelecionado(true);
            item.setQtdItensSelecionados(item.getQtdItensSelecionados() + 1);
        }
    }

    private boolean isResultadoSelecionado(
            ParametroPartida item,
            TipoSelecao tipoSelecao
    ) {
        if (item == null) {
            return false;
        }

        switch (tipoSelecao) {
            case TIME_UM:
                return item.getEquipe1() != null
                        && item.getEquipe1().isSelecionado();
            case TIME_DOIS:
                return item.getEquipe2() != null
                        && item.getEquipe2().isSelecionado();
            case EMPATE:
                return item.isEmpate();
            default:
                return false;
        }
    }

    public void atualizarEstadoVisual(int position) {
        if (position < 0 || position >= listaPartidas.size()) {
            return;
        }
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView == null) {
            return;
        }

        RecyclerView.ViewHolder viewHolder =
                recyclerView.findViewHolderForAdapterPosition(position);
        if (!(viewHolder instanceof LotecaHolder)) {
            return;
        }
        LotecaHolder holder = (LotecaHolder) viewHolder;
        ParametroPartida item = listaPartidas.get(position);
        Context context = holder.itemView.getContext();
        holder.bindEstadoVisual(context, item);
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }
    private RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private enum TipoSelecao {
        TIME_UM,
        TIME_DOIS,
        EMPATE
    }
}