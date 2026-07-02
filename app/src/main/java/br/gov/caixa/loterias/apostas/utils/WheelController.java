package br.gov.caixa.loterias.apostas.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListStringAdapter;

public class WheelController {

    private final RecyclerView recyclerView;
    private final LinearLayoutManager layoutManager;
    private final LinearSnapHelper snapHelper;
    private final OnDialogListener dialogListener;

    private ListStringAdapter adapter;

    public WheelController(@NonNull RecyclerView recyclerView,
                           @NonNull LinearLayoutManager layoutManager,
                           @NonNull LinearSnapHelper snapHelper,
                           @NonNull OnDialogListener dialogListener) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
        this.snapHelper = snapHelper;
        this.dialogListener = dialogListener;
    }

    public void attachAdapter(@NonNull ListStringAdapter adapter) {
        this.adapter = adapter;
        this.adapter.initSelectionIfNeeded();
    }

    public void setupRecyclerForWheel() {
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        if (animator != null) animator.setChangeDuration(0);

        recyclerView.setClipToPadding(false);

        // seleção/scroll inicial antes de calcular padding/snap
        recyclerView.post(() -> {
            if (!recyclerView.isAttachedToWindow()) return;
            if (adapter == null || adapter.getItemCount() == 0) return;

            int startPos = adapter.getSafeSelectedPosition();
            adapter.setSelectedPosition(startPos);

            // garante que existe child pra medir e snapar
            recyclerView.scrollToPosition(startPos);

            // tenta calcular padding quando tiver view
            recyclerView.post(() -> {
                if (!recyclerView.isAttachedToWindow()) return;

                View first = layoutManager.findViewByPosition(startPos);
                if (first == null) {
                    // fallback: tenta posição 0
                    first = layoutManager.findViewByPosition(0);
                }
                if (first == null) {
                    // ainda não tem child -> tenta snapar depois do próximo layout
                    recyclerView.post(() -> selectSnappedAndNotify(false));
                    return;
                }

                int itemH = first.getHeight();
                int rvH = recyclerView.getHeight();
                int pad = Math.max(0, (rvH / 2) - (itemH / 2));

                recyclerView.setPadding(
                        recyclerView.getPaddingLeft(),
                        pad,
                        recyclerView.getPaddingRight(),
                        pad
                );

                selectSnappedAndNotify(false);
            });
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    selectSnappedAndNotify(true);
                }
            }
        });
    }

    public void onItemClicked(int position) {
        if (adapter == null || adapter.getItemCount() == 0) return;

        int pos = Math.min(Math.max(position, 0), adapter.getItemCount() - 1);
        smoothScrollToCenter(pos);

        recyclerView.post(() -> {
            if (!recyclerView.isAttachedToWindow()) return;
            selectSnappedAndNotify(true);
        });
    }

    private void smoothScrollToCenter(int position) {
        View target = layoutManager.findViewByPosition(position);

        if (target != null) {
            int[] dist = snapHelper.calculateDistanceToFinalSnap(layoutManager, target);
            if (dist != null) {
                recyclerView.smoothScrollBy(dist[0], dist[1]);
                return;
            }
        }
        recyclerView.smoothScrollToPosition(position);
    }

    private void selectSnappedAndNotify(boolean notifyListener) {
        if (adapter == null || adapter.getItemCount() == 0) return;

        View snap = snapHelper.findSnapView(layoutManager);
        if (snap == null) {
            // fallback: usa a seleção atual segura
            int safe = adapter.getSafeSelectedPosition();
            adapter.setSelectedPosition(safe);
            if (notifyListener) dialogListener.itemSelecionado(safe);
            return;
        }

        int pos = recyclerView.getChildAdapterPosition(snap);
        if (pos == RecyclerView.NO_POSITION) {
            int safe = adapter.getSafeSelectedPosition();
            adapter.setSelectedPosition(safe);
            if (notifyListener) dialogListener.itemSelecionado(safe);
            return;
        }

        if (pos != adapter.getSelectedPosition()) {
            adapter.setSelectedPosition(pos);
        }
        if (notifyListener) {
            dialogListener.itemSelecionado(adapter.getSafeSelectedPosition());
        }
    }
}