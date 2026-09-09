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
    private boolean userScrolling;

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
    }

    public void setupRecyclerForWheel() {
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        if (animator != null) animator.setChangeDuration(0);

        recyclerView.setClipToPadding(false);

        // O primeiro item é apenas a referência visual e não representa uma seleção.
        recyclerView.post(() -> {
            if (!recyclerView.isAttachedToWindow()) return;
            if (adapter == null || adapter.getItemCount() == 0) return;

            int startPos = adapter.getSelectedPosition() == RecyclerView.NO_POSITION
                    ? 0
                    : adapter.getSelectedPosition();

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

            });
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    userScrolling = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE && userScrolling) {
                    selectSnappedAndNotify(true);
                    userScrolling = false;
                }
            }
        });
    }

    public void onItemClicked(int position) {
        if (adapter == null || adapter.getItemCount() == 0) return;

        int pos = Math.min(Math.max(position, 0), adapter.getItemCount() - 1);
        adapter.setSelectedPosition(pos);
        dialogListener.itemSelecionado(pos);
        smoothScrollToCenter(pos);
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
            return;
        }

        int pos = recyclerView.getChildAdapterPosition(snap);
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }

        if (pos != adapter.getSelectedPosition()) {
            adapter.setSelectedPosition(pos);
        }
        if (notifyListener) {
            dialogListener.itemSelecionado(adapter.getSelectedPosition());
        }
    }
}